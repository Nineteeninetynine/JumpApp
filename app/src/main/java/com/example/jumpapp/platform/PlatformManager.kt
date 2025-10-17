package com.example.simplejump.platform

import android.content.Context
import com.example.simplejump.data.ActionType
import com.example.simplejump.data.SearchResult
import com.example.simplejump.platform.xiaohongshu.XhsSearchProvider
import com.example.simplejump.platform.zhihu.ZhihuSearchProvider
import com.example.simplejump.platform.douyin.DouyinSearchProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 平台管理器 - 搜索功能调度中心
 * 统一管理所有平台的搜索功能
 */
class PlatformManager private constructor() {

    companion object {
        @Volatile
        private var INSTANCE: PlatformManager? = null

        fun getInstance(): PlatformManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: PlatformManager().also { INSTANCE = it }
            }
        }
    }

    // 平台提供者映射
    // 修复：为 Map 指定泛型类型 <SearchPlatform, SearchProvider>
    private val providers: Map<SearchPlatform, SearchProvider> = mapOf(
        SearchPlatform.XIAOHONGSHU to XhsSearchProvider(),
        SearchPlatform.ZHIHU to ZhihuSearchProvider(),
        SearchPlatform.DOUYIN to DouyinSearchProvider()
        // 其他平台可以在这里添加
    )

    /**
     * 智能搜索
     * @param context 上下文
     * @param platform 目标平台
     * @param query 搜索关键词
     * @return 搜索结果
     */
    suspend fun search(
        context: Context,
        platform: SearchPlatform,
        query: String
    ): SearchResult = withContext(Dispatchers.IO) {
        val provider = getProvider(platform)
        provider?.search(context, query) ?: createUnsupportedResult(platform, ActionType.SEARCH)
    }

    /**
     * 打开搜索页面
     * @param context 上下文
     * @param platform 目标平台
     * @return 操作结果
     */
    suspend fun openSearchPage(
        context: Context,
        platform: SearchPlatform
    ): SearchResult = withContext(Dispatchers.IO) {
        val provider = getProvider(platform)
        provider?.openSearchPage(context) ?: createUnsupportedResult(platform, ActionType.OPEN_SEARCH_PAGE)
    }

    /**
     * 打开应用主页
     * @param context 上下文
     * @param platform 目标平台
     * @return 操作结果
     */
    suspend fun openMainPage(
        context: Context,
        platform: SearchPlatform
    ): SearchResult = withContext(Dispatchers.IO) {
        val provider = getProvider(platform)
        provider?.openMainPage(context) ?: createUnsupportedResult(platform, ActionType.OPEN_MAIN_PAGE)
    }

    /**
     * 复制并打开搜索
     * @param context 上下文
     * @param platform 目标平台
     * @param content 要复制的内容
     * @return 操作结果
     */
    suspend fun copyAndOpenSearch(
        context: Context,
        platform: SearchPlatform,
        content: String
    ): SearchResult = withContext(Dispatchers.IO) {
        val provider = getProvider(platform)
        provider?.copyAndOpenSearch(context, content) ?: createUnsupportedResult(platform, ActionType.COPY_AND_OPEN)
    }

    /**
     * 跳转到应用商店
     * @param context 上下文
     * @param platform 目标平台
     * @return 操作结果
     */
    suspend fun jumpToStore(
        context: Context,
        platform: SearchPlatform
    ): SearchResult = withContext(Dispatchers.IO) {
        val provider = getProvider(platform)
        provider?.jumpToStore(context) ?: createUnsupportedResult(platform, ActionType.INSTALL_APP)
    }

    /**
     * 打开网页版
     * @param context 上下文
     * @param platform 目标平台
     * @param query 搜索关键词
     * @return 操作结果
     */
    suspend fun openWebVersion(
        context: Context,
        platform: SearchPlatform,
        query: String = ""
    ): SearchResult = withContext(Dispatchers.IO) {
        val provider = getProvider(platform)
        provider?.openWebVersion(context, query) ?: createUnsupportedResult(platform, ActionType.SEARCH)
    }

    /**
     * 批量检查应用安装状态
     * @param context 上下文
     * @return 安装状态映射
     */
    // 修复：为 Map 指定泛型类型 <SearchPlatform, Boolean>
    fun getInstalledPlatforms(context: Context): Map<SearchPlatform, Boolean> {
        return SearchPlatform.values().associateWith { platform ->
            getProvider(platform)?.isAppInstalled(context) ?: false
        }
    }

    /**
     * 获取已安装的平台列表
     * @param context 上下文
     * @return 已安装平台列表
     */
    fun getAvailablePlatforms(context: Context): List<SearchPlatform> {
        return getInstalledPlatforms(context)
            .filterValues { it }
            .keys
            .toList()
    }

    /**
     * 获取推荐安装的平台
     * @param context 上下文
     * @return 未安装平台列表
     */
    fun getRecommendedPlatforms(context: Context): List<SearchPlatform> {
        return getInstalledPlatforms(context)
            .filterValues { !it }
            .keys
            .toList()
    }

    /**
     * 获取平台详细信息
     * @param context 上下文
     * @param platform 目标平台
     * @return 平台信息
     */
    // 修复：为 Map 指定泛型类型 <String, Any>
    fun getPlatformInfo(context: Context, platform: SearchPlatform): Map<String, Any> {
        val provider = getProvider(platform)
        val baseInfo = mapOf(
            "platform" to platform.displayName,
            "packageName" to platform.packageName,
            "icon" to platform.icon,
            "supported" to (provider != null)
        )

        return if (provider != null) {
            baseInfo + provider.getAppInfo(context)
        } else {
            baseInfo + mapOf("error" to "Platform not supported")
        }
    }

    /**
     * 获取支持的所有平台
     * @return 支持的平台列表
     */
    fun getSupportedPlatforms(): List<SearchPlatform> {
        return providers.keys.toList()
    }

    /**
     * 获取平台提供者
     * @param platform 平台
     * @return 提供者实例
     */
    private fun getProvider(platform: SearchPlatform): SearchProvider? {
        return providers[platform]
    }

    /**
     * 创建不支持平台的错误结果
     * @param platform 平台
     * @param actionType 操作类型
     * @return 错误结果
     */
    private fun createUnsupportedResult(platform: SearchPlatform, actionType: ActionType): SearchResult {
        return SearchResult.failure(
            message = "❌ ${platform.displayName}暂不支持此功能",
            actionType = actionType,
            errorCode = "PLATFORM_NOT_SUPPORTED",
            // 修复：mapOf 会自动推断泛型类型为 <String, String>，符合 SearchResult 中 Map<String, Any> 的要求
            data = mapOf("platform" to platform.displayName)
        )
    }
}
