package com.example.simplejump.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import com.example.simplejump.platform.SearchPlatform
import com.example.simplejump.data.ActionType
import com.example.simplejump.data.SearchResult

/**
 * 应用相关工具类
 * 提供应用安装检查、跳转等功能
 */
object AppUtils {

    private const val TAG = "AppUtils"

    /**
     * 检查应用是否已安装
     * @param context 上下文
     * @param packageName 包名
     * @return 是否已安装
     */
    fun isAppInstalled(context: Context, packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            Log.d(TAG, "App not installed: $packageName")
            false
        }
    }

    /**
     * 获取应用安装状态详情
     * @param context 上下文
     * @param packageName 包名
     * @return 安装状态信息
     */
    fun getAppInstallationStatus(context: Context, packageName: String): Map<String, Any?> {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(packageName, 0)
            val appInfo = context.packageManager.getApplicationInfo(packageName, 0)
            val appName = context.packageManager.getApplicationLabel(appInfo).toString()

            mapOf(
                "installed" to true,
                "packageName" to packageName,
                "appName" to appName,
                "versionName" to packageInfo.versionName,
                "versionCode" to packageInfo.longVersionCode,
                "firstInstallTime" to packageInfo.firstInstallTime,
                "lastUpdateTime" to packageInfo.lastUpdateTime,
                "enabled" to appInfo.enabled
            )
        } catch (e: PackageManager.NameNotFoundException) {
            Log.d(TAG, "App not found: $packageName")
            mapOf(
                "installed" to false,
                "packageName" to packageName,
                "error" to "Package not found"
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error getting app status for $packageName", e)
            mapOf(
                "installed" to false,
                "packageName" to packageName,
                "error" to e.message.orEmpty()
            )
        }
    }

    /**
     * 打开应用主页面
     * @param context 上下文
     * @param packageName 包名
     * @return 操作结果
     */
    fun openAppMainPage(context: Context, packageName: String): SearchResult {
        return try {
            if (!isAppInstalled(context, packageName)) {
                return SearchResult.failure(
                    message = "❌ 应用未安装，无法打开",
                    actionType = ActionType.OPEN_MAIN_PAGE,
                    errorCode = "APP_NOT_INSTALLED"
                )
            }

            val intent = context.packageManager.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                context.startActivity(intent)

                SearchResult.success(
                    message = "✅ 成功打开应用",
                    actionType = ActionType.OPEN_MAIN_PAGE,
                    data = mapOf("packageName" to packageName)
                )
            } else {
                SearchResult.failure(
                    message = "❌ 无法启动应用",
                    actionType = ActionType.OPEN_MAIN_PAGE,
                    errorCode = "NO_LAUNCH_INTENT"
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error opening app: $packageName", e)
            SearchResult.failure(
                message = "❌ 打开应用失败：${e.message}",
                actionType = ActionType.OPEN_MAIN_PAGE,
                errorCode = "OPEN_APP_ERROR",
                data = mapOf("exception" to e.message.orEmpty())
            )
        }
    }

    /**
     * 跳转到应用商店下载页面
     * @param context 上下文
     * @param packageName 包名
     * @return 操作结果
     */
    fun jumpToAppStore(context: Context, packageName: String): SearchResult {
        return try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("market://details?id=$packageName")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            // 检查是否有应用商店可以处理这个intent
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
                SearchResult.success(
                    message = "✅ 已跳转到应用商店",
                    actionType = ActionType.INSTALL_APP,
                    data = mapOf("packageName" to packageName)
                )
            } else {
                // 如果没有应用商店，尝试用浏览器打开Play Store网页
                val webIntent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(webIntent)

                SearchResult.success(
                    message = "✅ 已在浏览器中打开下载页面",
                    actionType = ActionType.INSTALL_APP,
                    data = mapOf("packageName" to packageName, "method" to "browser")
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error jumping to app store for $packageName", e)
            SearchResult.failure(
                message = "❌ 跳转应用商店失败：${e.message}",
                actionType = ActionType.INSTALL_APP,
                errorCode = "JUMP_STORE_ERROR",
                data = mapOf("exception" to e.message.orEmpty())
            )
        }
    }
//
//    /**
//     * 分析应用信息
//     * @param context 上下文
//     * @param platform 平台
//     * @return 操作结果
//     */
//    fun analyzeApp(context: Context, platform: SearchPlatform): SearchResult {
//        val packageName = platform.packageName
//        val status = getAppInstallationStatus(context, packageName)
//
//        val isInstalled = status["installed"] as Boolean
//
//        val analysisResult = buildString {
//            append("📱 ${platform.displayName} 应用分析/n/n")
//            append("包名：$packageName/n")
//
//            if (isInstalled) {
//                append("状态：✅ 已安装/n")
//                status["appName"]?.let { append("应用名：$it/n") }
//                status["versionName"]?.let { append("版本：$it/n") }
//                status["enabled"]?.let {
//                    append("启用状态：${if (it as Boolean) "已启用" else "已禁用"}/n")
//                }
//
//                val firstInstallTime = status["firstInstallTime"] as? Long
//                firstInstallTime?.let {
//                    val date = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
//                        .format(java.util.Date(it))
//                    append("首次安装：$date/n")
//                }
//            } else {
//                append("状态：❌ 未安装/n")
//                append("建议：点击安装按钮前往应用商店下载/n")
//            }
//
//            append("/n支持功能：/n")
//            append("• 智能搜索跳转/n")
//            append("• 搜索页面打开/n")
//            append("• 剪贴板内容搜索/n")
//            if (platform.hotSearches.isNotEmpty()) {
//                append("• 热门搜索：${platform.hotSearches.take(3).joinToString("、")}/n")
//            }
//        }
//
//        return SearchResult.success(
//            message = analysisResult,
//            actionType = ActionType.ANALYZE_APP,
//            data = status
//        )
//    }
//
//    /**
//     * 检查多个应用的安装状态
//     * @param context 上下文
//     * @param packageNames 包名列表
//     * @return 安装状态映射
//     */
//    fun checkMultipleAppsStatus(context: Context, packageNames: List): Map {
//        return packageNames.associateWith { packageName ->
//            isAppInstalled(context, packageName)
//        }
//    }
//
//    /**
//     * 获取设备上所有已安装的相关应用
//     * @param context 上下文
//     * @return 已安装的平台列表
//     */
//    fun getInstalledPlatforms(context: Context): List {
//        return SearchPlatform.values().filter { platform ->
//            isAppInstalled(context, platform.packageName)
//        }
//    }
//
//    /**
//     * 获取推荐安装的应用
//     * @param context 上下文
//     * @return 未安装的平台列表
//     */
//    fun getRecommendedApps(context: Context): List {
//        return SearchPlatform.values().filter { platform ->
//            !isAppInstalled(context, platform.packageName)
//        }
//    }
}