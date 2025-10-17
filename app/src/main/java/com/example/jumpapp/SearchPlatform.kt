package com.example.simplejump

import androidx.compose.ui.graphics.Color

/**
 * 搜索平台枚举
 */
enum class SearchPlatform(
    val displayName: String,
    val icon: String,
    val packageName: String,
    val primaryColor: Color,
    val hotSearches: List<String>
) {
    XIAOHONGSHU(
        displayName = "小红书",
        icon = "📕",
        packageName = "com.xingin.xhs",
        primaryColor = Color(0xFFe74c3c),
        hotSearches = listOf("美食", "穿搭", "护肤", "旅游")
    ),

    ZHIHU(
        displayName = "知乎",
        icon = "🤔",
        packageName = "com.zhihu.android",
        primaryColor = Color(0xFF0084FF),
        hotSearches = listOf("编程", "科技", "职场", "学习")
    )
}