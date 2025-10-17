package com.example.simplejump.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import com.example.simplejump.data.ActionType
import com.example.simplejump.data.SearchResult

/**
 * 剪贴板工具类
 * 提供剪贴板读写功能
 */
object ClipboardUtils {

    private const val TAG = "ClipboardUtils"

    /**
     * 复制文本到剪贴板
     * @param context 上下文
     * @param text 要复制的文本
     * @param label 剪贴板标签（可选）
     * @return 操作结果
     */
    fun copyToClipboard(
        context: Context,
        text: String,
        label: String = "SimpleJump"
    ): SearchResult {
        return try {
            if (text.isBlank()) {
                return SearchResult.failure(
                    message = "❌ 复制内容不能为空",
                    actionType = ActionType.COPY_TEXT,
                    errorCode = "EMPTY_TEXT"
                )
            }

            val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText(label, text)
            clipboardManager.setPrimaryClip(clipData)

            Log.d(TAG, "Text copied to clipboard: $text")

            SearchResult.success(
                message = "✅ 已复制到剪贴板：
                {if (text.length > 20) "..." else ""}",
                        actionType = ActionType.COPY_TEXT,
                data = mapOf(
                    "text" to text,
                    "label" to label,
                    "length" to text.length
                )
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error copying to clipboard", e)
            SearchResult.failure(
                message = "❌ 复制失败：${e.message}",
                actionType = ActionType.COPY_TEXT,
                errorCode = "COPY_ERROR",
                data = mapOf("exception" to e.message.orEmpty())
            )
        }
    }

    /**
     * 从剪贴板读取文本
     * @param context 上下文
     * @return 剪贴板中的文本，如果没有则返回null
     */
    fun getFromClipboard(context: Context): String? {
        return try {
            val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = clipboardManager.primaryClip

            if (clipData != null && clipData.itemCount > 0) {
                val text = clipData.getItemAt(0).text?.toString()
                Log.d(TAG, "Text read from clipboard: $text")
                text
            } else {
                Log.d(TAG, "No text in clipboard")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error reading from clipboard", e)
            null
        }
    }

    /**
     * 检查剪贴板是否有内容
     * @param context 上下文
     * @return 是否有内容
     */
    fun hasClipboardContent(context: Context): Boolean {
        return try {
            val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = clipboardManager.primaryClip
            clipData != null && clipData.itemCount > 0 &&
                    !clipData.getItemAt(0).text.isNullOrBlank()
        } catch (e: Exception) {
            Log.e(TAG, "Error checking clipboard content", e)
            false
        }
    }

    /**
     * 清空剪贴板
     * @param context 上下文
     * @return 操作结果
     */
    fun clearClipboard(context: Context): SearchResult {
        return try {
            val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val emptyClip = ClipData.newPlainText("", "")
            clipboardManager.setPrimaryClip(emptyClip)

            Log.d(TAG, "Clipboard cleared")

            SearchResult.success(
                message = "✅ 剪贴板已清空",
                actionType = ActionType.COPY_TEXT
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing clipboard", e)
            SearchResult.failure(
                message = "❌ 清空剪贴板失败：${e.message}",
                actionType = ActionType.COPY_TEXT,
                errorCode = "CLEAR_ERROR",
                data = mapOf("exception" to e.message.orEmpty())
            )
        }
    }

    /**
     * 获取剪贴板内容信息
     * @param context 上下文
     * @return 剪贴板信息
     */
    fun getClipboardInfo(context: Context): Map {
        return try {
            val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = clipboardManager.primaryClip

            if (clipData != null && clipData.itemCount > 0) {
                val item = clipData.getItemAt(0)
                val text = item.text?.toString() ?: ""

                mapOf(
                    "hasContent" to true,
                    "text" to text,
                    "length" to text.length,
                    "label" to (clipData.description.label?.toString() ?: ""),
                    "mimeType" to clipData.description.getMimeType(0),
                    "itemCount" to clipData.itemCount,
                    "timestamp" to System.currentTimeMillis()
                )
            } else {
                mapOf(
                    "hasContent" to false,
                    "text" to "",
                    "length" to 0,
                    "timestamp" to System.currentTimeMillis()
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting clipboard info", e)
            mapOf(
                "hasContent" to false,
                "error" to e.message.orEmpty(),
                "timestamp" to System.currentTimeMillis()
            )
        }
    }

    /**
     * 智能复制文本（自动去除首尾空格，处理特殊字符）
     * @param context 上下文
     * @param text 要复制的文本
     * @param label 剪贴板标签
     * @return 操作结果
     */
    fun smartCopyToClipboard(
        context: Context,
        text: String,
        label: String = "SimpleJump搜索"
    ): SearchResult {
        val cleanText = text.trim()
            .replace(Regex("//s+"), " ")  // 多个空格替换为单个空格
            .replace(Regex("[//r//n]+"), " ")  // 换行替换为空格

        return copyToClipboard(context, cleanText, label)
    }
}