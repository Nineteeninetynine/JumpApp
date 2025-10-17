package com.example.simplejump.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.example.simplejump.SearchPlatform
import com.example.simplejump.data.ActionType
import com.example.simplejump.data.SearchResult
import java.net.URLEncoder

/**
 * Intent工具类
 * 提供各种Intent跳转功能
 */
object IntentUtils {

    private const val TAG = "IntentUtils"

    /**
     * 尝试打开URL Scheme
     * @param context 上下文
     * @param uri URI字符串
     * @param actionType 操作类型
     * @return 操作结果
     */
    fun tryOpenScheme(
        context: Context,
        uri: String,
        actionType: ActionType = ActionType.SEARCH
    ): SearchResult {
        return try {
            Log.d(TAG, "Trying to open scheme: $uri")

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            // 检查是否有应用可以处理这个Intent
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
                SearchResult.success(
                    message = "✅ 成功跳转到目标页面",
                    actionType = actionType,
                    data = mapOf("uri" to uri)
                )
            } else {
                SearchResult.failure(
                    message = "❌ 没有应用可以处理此操作",
                    actionType = actionType,
                    errorCode = "NO_HANDLER_APP",
                    data = mapOf("uri" to uri)
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error opening scheme: $uri", e)
            SearchResult.failure(
                message = "❌ 跳转失败：${e.message}",
                actionType = actionType,
                errorCode = "SCHEME_ERROR",
                data = mapOf("uri" to uri, "exception" to e.message.orEmpty())
            )
        }
    }

    /**
     * 尝试多个URL Scheme，直到成功或全部失败
     * @param context 上下文
     * @param uris URI列表
     * @param actionType 操作类型
     * @return 操作结果
     */
    fun tryMultipleSchemes(
        context: Context,
        uris: List,
        actionType: ActionType = ActionType.SEARCH
    ): SearchResult {
        if (uris.isEmpty()) {
            return SearchResult.failure(
                message = "❌ 没有可用的跳转方案",
                actionType = actionType,
                errorCode = "NO_SCHEMES"
            )
        }

        val failedSchemes = mutableListOf()

        for (uri in uris) {
            val result = tryOpenScheme(context, uri, actionType)
            if (result.success) {
                return result.copy(
                    data = result.data?.plus(
                        mapOf(
                            "totalSchemes" to uris.size,
                            "successScheme" to uri,
                            "failedSchemes" to failedSchemes
                        )
                    )
                )
            } else {
                failedSchemes.add(uri)
                Log.d(TAG, "Scheme failed: $uri")
            }
        }

        return SearchResult.failure(
            message = "❌ 所有跳转方案都失败了，可能应用未安装",
            actionType = actionType,
            errorCode = "ALL_SCHEMES_FAILED",
            data = mapOf(
                "totalSchemes" to uris.size,
                "failedSchemes" to failedSchemes
            )
        )
    }

    /**
     * 智能搜索 - 尝试直接跳转到搜索结果
     * @param context 上下文
     * @param platform 搜索平台
     * @param query 搜索内容
     * @return 操作结果
     */
    fun smartSearch(
        context: Context,
        platform: SearchPlatform,
        query: String
    ): SearchResult {
        if (query.isBlank()) {
            return SearchResult.failure(
                message = "❌ 搜索内容不能为空",
                actionType = ActionType.SEARCH,
                errorCode = "EMPTY_QUERY"
            )
        }

        // 检查应用是否安装
        if (!AppUtils.isAppInstalled(context, platform.packageName)) {
            return SearchResult.failure(
                message = "❌ ${platform.displayName}未安装，请先安装应用",
                actionType = ActionType.SEARCH,
                errorCode = "APP_NOT_INSTALLED",
                data = mapOf(
                    "platform" to platform.displayName,
                    "packageName" to platform.packageName
                )
            )
        }

        try {
            val encodedQuery = URLEncoder.encode(query, "UTF-8")
            val searchSchemes = generateSearchSchemes(platform, encodedQuery)

            Log.d(TAG, "Smart search for
                    query")
                    Log.d(TAG, "Generated schemes: $searchSchemes")

            return tryMultipleSchemes(context, searchSchemes, ActionType.SEARCH).let { result ->
                if (result.success) {
                    result.copy(
                        message = "✅ 成功在
                                中
                                搜
                                索
                                「
                                query」",
                                data = result.data?.plus(
                                mapOf(
                                    "platform" to platform.displayName,
                                    "query" to query,
                                    "encodedQuery" to encodedQuery
                                )
                                )
                    )
                } else {
                    result.copy(
                        message = "❌ 在${platform.displayName}中搜索失败，尝试打开搜索页面"
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in smart search", e)
            return SearchResult.failure(
                message = "❌ 搜索过程中发生错误：${e.message}",
                actionType = ActionType.SEARCH,
                errorCode = "SEARCH_ERROR",
                data = mapOf("exception" to e.message.orEmpty())
            )
        }
    }

    /**
     * 打开搜索页面
     * @param context 上下文
     * @param platform 搜索平台
     * @return 操作结果
     */
    fun openSearchPage(
        context: Context,
        platform: SearchPlatform
    ): SearchResult {
        // 检查应用是否安装
        if (!AppUtils.isAppInstalled(context, platform.packageName)) {
            return SearchResult.failure(
                message = "❌ ${platform.displayName}未安装，请先安装应用",
                actionType = ActionType.OPEN_SEARCH_PAGE,
                errorCode = "APP_NOT_INSTALLED"
            )
        }

        val searchPageSchemes = generateSearchPageSchemes(platform)

        return tryMultipleSchemes(context, searchPageSchemes, ActionType.OPEN_SEARCH_PAGE).let { result ->
            if (result.success) {
                result.copy(
                    message = "✅ 成功打开${platform.displayName}搜索页面"
                )
            } else {
                // 如果打开搜索页面失败，尝试打开应用主页
                AppUtils.openAppMainPage(context, platform.packageName)
            }
        }
    }

    /**
     * 复制内容并打开应用
     * @param context 上下文
     * @param platform 搜索平台
     * @param content 要复制的内容
     * @return 操作结果
     */
    fun copyAndOpen(
        context: Context,
        platform: SearchPlatform,
        content: String
    ): SearchResult {
        if (content.isBlank()) {
            return SearchResult.failure(
                message = "❌ 复制内容不能为空",
                actionType = ActionType.COPY_AND_OPEN,
                errorCode = "EMPTY_CONTENT"
            )
        }

        try {
            // 先复制到剪贴板
            val copyResult = ClipboardUtils.smartCopyToClipboard(
                context,
                content,
                "${platform.displayName}搜索内容"
            )

            if (!copyResult.success) {
                return copyResult.copy(actionType = ActionType.COPY_AND_OPEN)
            }

            // 然后打开搜索页面
            val openResult = openSearchPage(context, platform)

            return if (openResult.success) {
                SearchResult.success(
                    message = "✅ 已复制「
                    」
                并
                打
                开
                {platform.displayName}，可直接粘贴搜索",
                actionType = ActionType.COPY_AND_OPEN,
                data = mapOf(
                    "content" to content,
                    "platform" to platform.displayName,
                    "copyResult" to copyResult.success,
                    "openResult" to openResult.success
                )
                )
            } else {
                SearchResult.failure(
                    message = "✅ 内容已复制，但打开${platform.displayName}失败",
                    actionType = ActionType.COPY_AND_OPEN,
                    errorCode = "COPY_SUCCESS_OPEN_FAILED",
                    data = mapOf(
                        "content" to content,
                        "copyResult" to copyResult.success,
                        "openError" to openResult.message
                    )
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in copy and open", e)
            return SearchResult.failure(
                message = "❌ 复制并打开失败：${e.message}",
                actionType = ActionType.COPY_AND_OPEN,
                errorCode = "COPY_AND_OPEN_ERROR",
                data = mapOf("exception" to e.message.orEmpty())
            )
        }
    }

    /**
     * 生成搜索URL Schemes
     * @param platform 平台
     * @param encodedQuery 编码后的查询内容
     * @return URL Schemes列表
     */
    private fun generateSearchSchemes(platform: SearchPlatform, encodedQuery: String): List {
        return when (platform) {
            SearchPlatform.XIAOHONGSHU -> listOf(
                "xhsdiscover://search/result?keyword=$encodedQuery",
                "xhsdiscover://search?keyword=$encodedQuery"
            )
            SearchPlatform.ZHIHU -> listOf(
                "zhihu://search?q=$encodedQuery",
                "zhihu://search?query=$encodedQuery"
            )
            SearchPlatform.BILIBILI -> listOf(
                "bilibili://search?keyword=$encodedQuery",
                "bilibili://search?q=$encodedQuery"
            )
            SearchPlatform.WEIBO -> listOf(
                "sinaweibo://searchall?q=$encodedQuery",
                "sinaweibo://search?q=$encodedQuery"
            )
            SearchPlatform.DOUYIN -> listOf(
                "snssdk1128://search?keyword=$encodedQuery",
                "aweme://search?keyword=$encodedQuery"
            )
        }
    }

    /**
     * 生成搜索页面URL Schemes
     * @param platform 平台
     * @return URL Schemes列表
     */
    private fun generateSearchPageSchemes(platform: SearchPlatform): List {
        return when (platform) {
            SearchPlatform.XIAOHONGSHU -> listOf(
                "xhsdiscover://search",
                "xhsdiscover://search/"
            )
            SearchPlatform.ZHIHU -> listOf(
                "zhihu://search",
                "zhihu://search/"
            )
            SearchPlatform.BILIBILI -> listOf(
                "bilibili://search",
                "bilibili://search/"
            )
            SearchPlatform.WEIBO -> listOf(
                "sinaweibo://search",
                "sinaweibo://searchall"
            )
            SearchPlatform.DOUYIN -> listOf(
                "snssdk1128://search",
                "aweme://search"
            )
        }
    }

    /**
     * 生成主页URL Schemes
     * @param platform 平台
     * @return URL Schemes列表
     */
    private fun generateMainPageSchemes(platform: SearchPlatform): List {
        return when (platform) {
            SearchPlatform.XIAOHONGSHU -> listOf(
                "xhsdiscover://main",
                "xhsdiscover://",
                "xhsdiscover://home"
            )
            SearchPlatform.ZHIHU -> listOf(
                "zhihu://main",
                "zhihu://",
                "zhihu://home"
            )
            SearchPlatform.BILIBILI -> listOf(
                "bilibili://main",
                "bilibili://",
                "bilibili://home"
            )
            SearchPlatform.WEIBO -> listOf(
                "sinaweibo://main",
                "sinaweibo://",
                "sinaweibo://home"
            )
            SearchPlatform.DOUYIN -> listOf(
                "snssdk1128://main",
                "snssdk1128://",
                "aweme://main"
            )
        }
    }

    /**
     * 打开网页版搜索（备用方案）
     * @param context 上下文
     * @param platform 平台
     * @param query 搜索内容
     * @return 操作结果
     */
    fun openWebSearch(
        context: Context,
        platform: SearchPlatform,
        query: String = ""
    ): SearchResult {
        return try {
            val webUrl = when (platform) {
                SearchPlatform.XIAOHONGSHU -> "https://www.xiaohongshu.com/search_result?keyword=${URLEncoder.encode(query, "UTF-8")}"
                SearchPlatform.ZHIHU -> "https://www.zhihu.com/search?q=${URLEncoder.encode(query, "UTF-8")}"
                SearchPlatform.BILIBILI -> "https://search.bilibili.com/all?keyword=${URLEncoder.encode(query, "UTF-8")}"
                SearchPlatform.WEIBO -> "https://s.weibo.com/weibo?q=${URLEncoder.encode(query, "UTF-8")}"
                SearchPlatform.DOUYIN -> "https://www.douyin.com/search/${URLEncoder.encode(query, "UTF-8")}"
            }

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(webUrl)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(intent)

            SearchResult.success(
                message = "✅ 已在浏览器中打开
                {if (query.isNotEmpty()) "搜索「$query」" else ""}",
                        actionType = ActionType.SEARCH,
                data = mapOf(
                    "webUrl" to webUrl,
                    "platform" to platform.displayName,
                    "query" to query
                )
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error opening web search", e)
            SearchResult.failure(
                message = "❌ 打开网页版搜索失败：${e.message}",
                actionType = ActionType.SEARCH,
                errorCode = "WEB_SEARCH_ERROR",
                data = mapOf("exception" to e.message.orEmpty())
            )
        }
    }
}