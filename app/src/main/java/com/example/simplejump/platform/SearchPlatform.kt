package com.example.simplejump.platform

import androidx.compose.ui.graphics.Color
import com.example.simplejump.data.PlatformConfig
import com.example.simplejump.data.PlatformConfigs

/**
 * 搜索平台枚举
 * 定义支持的所有搜索平台
 */
enum class SearchPlatform(
    val displayName: String,
    val icon: String,
    val packageName: String,
    val primaryColor: Color
) {
    XIAOHONGSHU(
        displayName = "小红书",
        icon = "📕",
        packageName = "com.xingin.xhs",
        primaryColor = Color(0xFFe74c3c)
    ),

    ZHIHU(
        displayName = "知乎",
        icon = "🧠",
        packageName = "com.zhihu.android",
        primaryColor = Color(0xFF0084ff)
    ),

    BILIBILI(
        displayName = "哔哩哔哩",
        icon = "📺",
        packageName = "tv.danmaku.bili",
        primaryColor = Color(0xFFfb7299)
    ),

    WEIBO(
        displayName = "微博",
        icon = "🌊",
        packageName = "com.sina.weibo",
        primaryColor = Color(0xFFff8200)
    ),

    DOUYIN(
        displayName = "抖音",
        icon = "🎵",
        packageName = "com.ss.android.ugc.aweme",
        primaryColor = Color(0xFF000000)
    );

    /**
     * 获取平台配置
     */
    fun getConfig(): PlatformConfig {
        return when (this) {
            XIAOHONGSHU -> PlatformConfigs.XIAOHONGSHU
            ZHIHU -> PlatformConfigs.ZHIHU
            BILIBILI -> PlatformConfigs.BILIBILI
            WEIBO -> PlatformConfigs.WEIBO
            DOUYIN -> PlatformConfigs.DOUYIN
        }
    }

    /**
     * 热门搜索
     * 修复：为 List 指定泛型类型 <String>
     */
    val hotSearches: List<String>
        get() = getConfig().hotSearches

    companion object {
        /**
         * 根据包名获取平台
         */
        fun fromPackageName(packageName: String): SearchPlatform? {
            return values().find { it.packageName == packageName }
        }

        /**
         * 获取所有平台的包名
         * 修复：为 List 指定泛型类型 <String>
         */
        fun getAllPackageNames(): List<String> {
            return values().map { it.packageName }
        }
    }
}
