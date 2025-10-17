package com.example.simplejump.platform

import android.content.Context
import com.example.simplejump.data.SearchResult

/**
 * 搜索提供者接口
 * 定义各平台必须实现的搜索功能
 */
interface SearchProvider {

    /**
     * 平台标识
     */
    val platform: SearchPlatform

    /**
     * 智能搜索 - 直接跳转到搜索结果页面
     * @param context 上下文
     * @param query 搜索关键词
     * @return 搜索结果
     */
    suspend fun search(context: Context, query: String): SearchResult

    /**
     * 打开搜索页面 - 跳转到搜索输入页面
     * @param context 上下文
     * @return 操作结果
     */
    suspend fun openSearchPage(context: Context): SearchResult

    /**
     * 打开应用主页
     * @param context 上下文
     * @return 操作结果
     */
    suspend fun openMainPage(context: Context): SearchResult

    /**
     * 复制内容并打开搜索页面
     * @param context 上下文
     * @param content 要复制的内容
     * @return 操作结果
     */
    suspend fun copyAndOpenSearch(context: Context, content: String): SearchResult

    /**
     * 检查应用是否已安装
     * @param context 上下文
     * @return 是否已安装
     */
    fun isAppInstalled(context: Context): Boolean

    /**
     * 获取应用详细信息
     * @param context 上下文
     * @return 应用信息
     */
    fun getAppInfo(context: Context): Map<String, Any>

    /**
     * 跳转到应用商店下载
     * @param context 上下文
     * @return 操作结果
     */
    suspend fun jumpToStore(context: Context): SearchResult

    /**
     * 打开网页版搜索（备用方案）
     * @param context 上下文
     * @param query 搜索关键词（可选）
     * @return 操作结果
     */
    suspend fun openWebVersion(context: Context, query: String = ""): SearchResult
}