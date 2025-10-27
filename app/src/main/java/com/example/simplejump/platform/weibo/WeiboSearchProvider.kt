package com.example.simplejump.platform.weibo

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
 * 微博搜索提供者实现
 */
class WeiboSearchProvider : SearchProvider {

    override val platform = SearchPlatform.WEIBO

    companion object {
        private const val TAG = "WeiboSearchProvider"
    }

    override suspend fun search(context: Context, query: String): SearchResult {
        return try {
            Log.d(TAG, "Searching in Weibo: $query")

            if (query.isBlank()) {
                return SearchResult.failure(
                    message = "❌ 搜索内容不能为空",
                    actionType = ActionType.SEARCH,
                    errorCode = "EMPTY_QUERY"
                )
            }

            if (!isAppInstalled(context)) {
                return SearchResult.failure(
                    message = "❌ 微博未安装，请先安装应用",
                    actionType = ActionType.SEARCH,
                    errorCode = "APP_NOT_INSTALLED",
                    data = mapOf("platform" to platform.displayName)
                )
            }

            val searchUrls = WeiboSchemes.buildSearchUrls(query)
            val result = IntentUtils.tryMultipleSchemes(context, searchUrls, ActionType.SEARCH)

            if (result.success) {
                result.copy(
                    message = "✅ 成功在微博中搜索「$query」",
                    data = result.data?.plus(
                        mapOf(
                            "platform" to platform.displayName,
                            "query" to query,
                            "method" to "direct_search"
                        )
                    )
                )
            } else {
                Log.w(TAG, "Direct search failed, trying search page")
                openSearchPage(context).let { searchPageResult ->
                    if (searchPageResult.success) {
                        SearchResult.success(
                            message = "✅ 已打开微博搜索页面，请手动输入「$query」",
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
                message = "❌ 微博搜索失败：${e.message}",
                actionType = ActionType.SEARCH,
                errorCode = "SEARCH_ERROR",
                data = mapOf("exception" to e.message.orEmpty())
            )
        }
    }

    override suspend fun openSearchPage(context: Context): SearchResult {
        return try {
            Log.d(TAG, "Opening Weibo search page")

            if (!isAppInstalled(context)) {
                return SearchResult.failure(
                    message = "❌ 微博未安装，请先安装应用",
                    actionType = ActionType.OPEN_SEARCH_PAGE,
                    errorCode = "APP_NOT_INSTALLED"
                )
            }

            val searchPageUrls = WeiboSchemes.buildSearchPageUrls()
            val result = IntentUtils.tryMultipleSchemes(context, searchPageUrls, ActionType.OPEN_SEARCH_PAGE)

            if (result.success) {
                result.copy(
                    message = "✅ 成功打开微博搜索页面"
                )
            } else {
                Log.w(TAG, "Opening search page failed, trying main page")
                openMainPage(context)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error opening search page", e)
            SearchResult.failure(
                message = "❌ 打开微博搜索页面失败：${e.message}",
                actionType = ActionType.OPEN_SEARCH_PAGE,
                errorCode = "OPEN_SEARCH_PAGE_ERROR",
                data = mapOf("exception" to e.message.orEmpty())
            )
        }
    }

    override suspend fun openMainPage(context: Context): SearchResult {
        return try {
            Log.d(TAG, "Opening Weibo main page")

            if (!isAppInstalled(context)) {
                return SearchResult.failure(
                    message = "❌ 微博未安装，请先安装应用",
                    actionType = ActionType.OPEN_MAIN_PAGE,
                    errorCode = "APP_NOT_INSTALLED"
                )
            }

            val mainPageUrls = WeiboSchemes.buildMainPageUrls()
            val result = IntentUtils.tryMultipleSchemes(context, mainPageUrls, ActionType.OPEN_MAIN_PAGE)

            if (result.success) {
                result.copy(
                    message = "✅ 成功打开微博"
                )
            } else {
                AppUtils.openAppMainPage(context, platform.packageName)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error opening main page", e)
            SearchResult.failure(
                message = "❌ 打开微博失败：${e.message}",
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

            val copyResult = ClipboardUtils.smartCopyToClipboard(
                context,
                content,
                "微博搜索内容"
            )

            if (!copyResult.success) {
                return copyResult.copy(actionType = ActionType.COPY_AND_OPEN)
            }

            val openResult = openSearchPage(context)

            if (openResult.success) {
                SearchResult.success(
                    message = "✅ 已复制「$content」并打开微博搜索页面",
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
                    message = "✅ 已复制「$content」，但打开微博失败",
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

    override fun getAppInfo(context: Context): Map<String, Any> {
        val baseInfo = mapOf(
            "platform" to platform.displayName,
            "packageName" to platform.packageName,
            "icon" to platform.icon,
            "primaryColor" to platform.primaryColor.value,
            "hotSearches" to platform.hotSearches,
            "specialFeatures" to listOf("热搜", "微博", "明星", "时事", "直播")
        )

        val installationStatus = AppUtils.getAppInstallationStatus(context, platform.packageName)
        return baseInfo + (installationStatus as Map<String, Any>)
    }

    override suspend fun jumpToStore(context: Context): SearchResult {
        return AppUtils.jumpToAppStore(context, platform.packageName).let { result ->
            if (result.success) {
                result.copy(
                    message = "✅ 已跳转到应用商店下载微博"
                )
            } else {
                result
            }
        }
    }

    override suspend fun openWebVersion(context: Context, query: String): SearchResult {
        return try {
            val webUrl = if (query.isNotEmpty()) {
                WeiboSchemes.buildWebSearchUrl(query)
            } else {
                WeiboSchemes.Web.BASE_URL
            }

            val result = IntentUtils.tryOpenScheme(context, webUrl, ActionType.SEARCH)

            if (result.success) {
                result.copy(
                    message = buildString {
                        append("✅ 已在浏览器中打开微博")
                        if (query.isNotEmpty()) {
                            append("搜索「$query」")
                        }
                    },
                    actionType = ActionType.WEB_SEARCH,
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
                message = "❌ 打开微博网页版失败：${e.message}",
                actionType = ActionType.SEARCH,
                errorCode = "WEB_VERSION_ERROR",
                data = mapOf("exception" to e.message.orEmpty())
            )
        }
    }
}