package com.example.simplejump.data

import java.text.SimpleDateFormat
import java.util.*

/**
 * 搜索操作结果统一封装类
 * 用于统一所有平台操作的返回结果格式
 */
data class SearchResult(
    val success: Boolean, // 操作是否成功
    val message: String, // 显示给用户的消息
    val actionType: ActionType, // 操作类型
    val errorCode: String? = null, // 错误码（便于调试）
    // 修复：为 Map 指定泛型类型，通常为 <String, Any>
    val data: Map<String, Any>? = null, // 额外数据
    val timestamp: Long = System.currentTimeMillis() // 时间戳
) {
    companion object {
        /**
         * 创建成功结果
         */
        // 修复：为 Map 指定泛型类型
        fun success(
            message: String,
            actionType: ActionType,
            data: Map<String, Any>? = null
        ) = SearchResult(
            success = true,
            message = message,
            actionType = actionType,
            data = data
        )

        /**
         * 创建失败结果
         */
        // 修复：为 Map 指定泛型类型
        fun failure(
            message: String,
            actionType: ActionType,
            errorCode: String? = null,
            data: Map<String, Any>? = null
        ) = SearchResult(
            success = false,
            message = message,
            actionType = actionType,
            errorCode = errorCode,
            data = data
        )

        /**
         * 创建带有详细信息的成功结果
         */
        fun successWithDetails(
            message: String,
            actionType: ActionType,
            platform: String? = null,
            query: String? = null,
            method: String? = null,
            additionalData: Map<String, Any>? = null // 修复：为 Map 指定泛型类型
        ) = SearchResult(
            success = true,
            message = message,
            actionType = actionType,
            data = buildMap {
                platform?.let { put("platform", it) }
                query?.let { put("query", it) }
                method?.let { put("method", it) }
                additionalData?.let { putAll(it) }
            }.takeIf { it.isNotEmpty() }
        )

        /**
         * 创建带有详细信息的失败结果
         */
        fun failureWithDetails(
            message: String,
            actionType: ActionType,
            errorCode: String? = null,
            platform: String? = null,
            exception: String? = null,
            additionalData: Map<String, Any>? = null // 修复：为 Map 指定泛型类型
        ) = SearchResult(
            success = false,
            message = message,
            actionType = actionType,
            errorCode = errorCode,
            data = buildMap {
                platform?.let { put("platform", it) }
                exception?.let { put("exception", it) }
                additionalData?.let { putAll(it) }
            }.takeIf { it.isNotEmpty() }
        )
    }

    /**
     * 判断是否为成功结果
     */
    fun isSuccess(): Boolean = success

    /**
     * 判断是否为失败结果
     */
    fun isFailure(): Boolean = !success

    /**
     * 获取平台名称
     */
    fun getPlatform(): String? = data?.get("platform") as? String

    /**
     * 获取查询内容
     */
    fun getQuery(): String? = data?.get("query") as? String

    /**
     * 获取错误信息
     */
    fun getException(): String? = data?.get("exception") as? String

    /**
     * 复制并修改结果
     */
    // 修复：为 Map 指定泛型类型
    fun copyWith(
        success: Boolean = this.success,
        message: String = this.message,
        actionType: ActionType = this.actionType,
        errorCode: String? = this.errorCode,
        data: Map<String, Any>? = this.data,
        timestamp: Long = this.timestamp
    ) = SearchResult(
        success = success,
        message = message,
        actionType = actionType,
        errorCode = errorCode,
        data = data,
        timestamp = timestamp
    )

    /**
     * 添加额外数据
     */
    // 修复：为 Map 指定泛型类型
    fun withAdditionalData(additionalData: Map<String, Any>): SearchResult {
        val newData = if (data != null) {
            data + additionalData
        } else {
            additionalData
        }
        return copy(data = newData)
    }

    /**
     * 获取格式化的时间戳
     */
    fun getFormattedTimestamp(): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            .format(Date(timestamp))
    }
}

/**
 * 操作类型枚举
 */
enum class ActionType(val displayName: String, val description: String) {
    SEARCH("智能搜索", "直接跳转到搜索结果页面"),
    OPEN_SEARCH_PAGE("打开搜索页面", "打开应用的搜索输入页面"),
    COPY_AND_OPEN("复制并打开", "复制内容到剪贴板并打开应用"),
    OPEN_MAIN_PAGE("打开主页", "打开应用的主页面"),
    INSTALL_APP("安装应用", "跳转到应用商店安装应用"),
    ANALYZE_APP("分析应用", "分析应用的安装状态和信息"),
    COPY_TEXT("复制文本", "复制文本到剪贴板"),
    CHECK_APP_STATUS("检查应用状态", "检查应用的安装和可用状态"),
    WEB_SEARCH("网页搜索", "在浏览器中打开网页版搜索")
}
