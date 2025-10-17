package com.example.simplejump.platform.xiaohongshu

import android.content.Context
import android.util.Log
import com.example.simplejump.data.ActionType
import com.example.simplejump.data.SearchResult
import com.example.simplejump.platform.SearchPlatform
import com.example.simplejump.platform.SearchProvider
import com.example.simplejump.utils.AppUtils
import com.example.simplejump.utils.ClipboardUtils
import com.example.simplejump.utils.IntentUtils

/**
 * 小红书搜索提供者实现
 */
class XhsSearchProvider : SearchProvider {

    override val platform = SearchPlatform.XIAOHONGSHU

    companion object {
        private const val TAG = "XhsSearchProvider"
    }

    override suspend fun search(context: Context, query: String): SearchResult {
        return try {
            Log.d(TAG, "Searching in Xiaohongshu: $query")

            if (query.isBlank()) {
                return SearchResult.failure(
                    message = "❌ 搜索内容不能为空",
                    actionType = ActionType.SEARCH,
                    errorCode = "EMPTY_QUERY"
                )
            }

            if (!isAppInstalled(context)) {
                return SearchResult.failure(
                    message = "❌ 小红书未安装，请先安装应用",
                    actionType = ActionType.SEARCH,
                    errorCode = "APP_NOT_INSTALLED"
                )
            }

            val searchUrls = XhsSchemes.buildSearchUrls(query)
            val result = IntentUtils.tryMultipleSchemes(context, searchUrls, ActionType.SEARCH)

            if (result.success) {
                result.copy(
                    message = "✅ 成功在小红书中搜索「$query」",
                    data = result.data?.plus(
                        mapOf(
                            "platform" to platform.displayName,
                            "query" to query,
                            "method" to "direct_search"
                        )
                    )
                )
            } else {
                // 如果直接搜索失败，尝试打开搜索页面
                Log.w(TAG, "Direct search failed, trying search page")
                openSearchPage(context).let { searchPageResult ->
                    if (searchPageResult.success) {
                        SearchResult.success(
                            message = "✅ 已打开小红书搜索页面，请手动输入「$query」",
                            actionType = ActionType.SEARCH,
                            data = mapOf(
                                "platform" to platform.displayName,
                                "query" to query,
                                "method" to "search_page_fallback"
                            )
                        )
                    } else {
                        result
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in search", e)
            SearchResult.failure(
                message = "❌ 小红书搜索失败：${e.message}",
                actionType = ActionType.SEARCH,
                errorCode = "SEARCH_ERROR",
                data = mapOf("exception" to e.message.orEmpty())
            )
        }
    }

    override suspend fun openSearchPage(context: Context): SearchResult {
        return try {
            Log.d(TAG, "Opening Xiaohongshu search page")

            if (!isAppInstalled(context)) {
                return SearchResult.failure(
                    message = "❌ 小红书未安装，请先安装应用",
                    actionType = ActionType.OPEN_SEARCH_PAGE,
                    errorCode = "APP_NOT_INSTALLED"
                )
            }

            val searchPageUrls = XhsSchemes.buildSearchPageUrls()
            val result = IntentUtils.tryMultipleSchemes(context, searchPageUrls, ActionType.OPEN_SEARCH_PAGE)

            if (result.success) {
                result.copy(
                    message = "✅ 成功打开小红书搜索页面"
                )
            } else {
                // 如果打开搜索页面失败，尝试打开主页
                Log.w(TAG, "Opening search page failed, trying main page")
                openMainPage(context)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error opening search page", e)
            SearchResult.failure(
                message = "❌ 打开小红书搜索页面失败：${e.message}",
                actionType = ActionType.OPEN_SEARCH_PAGE,
                errorCode = "OPEN_SEARCH_PAGE_ERROR",
                data = mapOf("exception" to e.message.orEmpty())
            )
        }
    }

    override suspend fun openMainPage(context: Context): SearchResult {
        return try {
            Log.d(TAG, "Opening Xiaohongshu main page")

            if (!isAppInstalled(context)) {
                return SearchResult.failure(
                    message = "❌ 小红书未安装，请先安装应用",
                    actionType = ActionType.OPEN_MAIN_PAGE,
                    errorCode = "APP_NOT_INSTALLED"
                )
            }

            val mainPageUrls = XhsSchemes.buildMainPageUrls()
            val result = IntentUtils.tryMultipleSchemes(context, mainPageUrls, ActionType.OPEN_MAIN_PAGE)

            if (result.success) {
                result.copy(
                    message = "✅ 成功打开小红书"
                )
            } else {
                // 使用系统默认的应用启动方式
                AppUtils.openAppMainPage(context, platform.packageName)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error opening main page", e)
            SearchResult.failure(
                message = "❌ 打开小红书失败：${e.message}",
                actionType = ActionType.OPEN_MAIN_PAGE,
                errorCode = "OPEN_MAIN_PAGE_ERROR",
                data = mapOf("exception" to e.message.orEmpty())
            )
        }
    }

    override suspend fun copyAndOpenSearch(context: Context, content: String): SearchResult {
        return try {
            Log.d(TAG, "Copy and open search: $content")

            if (content.isBlank()) {
                return SearchResult.failure(
                    message = "❌ 复制内容不能为空",
                    actionType = ActionType.COPY_AND_OPEN,
                    errorCode = "EMPTY_CONTENT"
                )
            }

            // 复制到剪贴板
            val copyResult = ClipboardUtils.smartCopyToClipboard(
                context,
                content,
                "小红书搜索内容"
            )

            if (!copyResult.success) {
                return copyResult.copy(actionType = ActionType.COPY_AND_OPEN)
            }

            // 打开搜索页面
            val openResult = openSearchPage(context)

            if (openResult.success) {
                SearchResult.success(
                    message = "✅ 已复制「$content」并打开小红书搜索页面",
                    actionType = ActionType.COPY_AND_OPEN,
                    data = mapOf(
                        "content" to content,
                        "platform" to platform.displayName,
                        "copySuccess" to true,
                        "openSuccess" to true
                    )
                )
            } else {
                SearchResult.success(
                    message = "✅ 已复制「$content」，但打开小红书失败",
                    actionType = ActionType.COPY_AND_OPEN,
                    data = mapOf(
                        "content" to content,
                        "copySuccess" to true,
                        "openSuccess" to false,
                        "openError" to openResult.message
                    )
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in copy and open search", e)
            SearchResult.failure(
                message = "❌ 复制并打开失败：${e.message}",
                actionType = ActionType.COPY_AND_OPEN,
                errorCode = "COPY_AND_OPEN_ERROR",
                data = mapOf("exception" to e.message.orEmpty())
            )
        }
    }

    override fun isAppInstalled(context: Context): Boolean {
        return AppUtils.isAppInstalled(context, platform.packageName)
    }

    override fun getAppInfo(context: Context): Map {
        val baseInfo = mapOf(
            "platform" to platform.displayName,
            "packageName" to platform.packageName,
            "icon" to platform.icon,
            "primaryColor" to platform.primaryColor.value,
            "hotSearches" to platform.hotSearches
        )

        return baseInfo + AppUtils.getAppInstallationStatus(context, platform.packageName)
    }

    override suspend fun jumpToStore(context: Context): SearchResult {
        return AppUtils.jumpToAppStore(context, platform.packageName).let { result ->
            if (result.success) {
                result.copy(
                    message = "✅ 已跳转到应用商店下载小红书"
                )
            } else {
                result
            }
        }
    }

    override suspend fun openWebVersion(context: Context, query: String): SearchResult {
        return try {
            val webUrl = if (query.isNotEmpty()) {
                XhsSchemes.buildWebSearchUrl(query)
            } else {
                XhsSchemes.Web.BASE_URL
            }

            val result = IntentUtils.tryOpenScheme(context, webUrl, ActionType.SEARCH)

            if (result.success) {
                result.copy(
                    message = "✅ 已在浏览器中打开小红书query」" else ""}",
            data = result.data?.plus(
                mapOf(
                    "webUrl" to webUrl,
                    "query" to query,
                    "method" to "web_version"
                )
            )
            )
        } else {
            result
        }
    } catch (e: Exception) {
        Log.e(TAG, "Error opening web version", e)
        SearchResult.failure(
            message = "❌ 打开小红书网页版失败：${e.message}",
            actionType = ActionType.SEARCH,
            errorCode = "WEB_VERSION_ERROR",
            data = mapOf("exception" to e.message.orEmpty())
        )
    }
}
}