package com.example.simplejump.data

import androidx.compose.ui.graphics.Color

/**
 * 平台配置数据类
 * 将平台的详细配置从枚举中抽离，便于管理和扩展
 */
data class PlatformConfig(
    val displayName: String,                 // 显示名称
    val icon: String,                        // 图标emoji
    val packageName: String,                 // 应用包名
    val primaryColor: Color,                 // 主题色
    val hotSearches: List<String>,           // 热门搜索
    val schemes: PlatformSchemes,            // URL schemes配置
    val features: PlatformFeatures,          // 功能支持配置
    val marketInfo: MarketInfo               // 应用市场信息
)

/**
 * 平台URL Schemes配置
 */
data class PlatformSchemes(
    val searchSchemes: List<String>,         // 搜索schemes
    val searchPageSchemes: List<String>,     // 搜索页面schemes
    val mainPageSchemes: List<String>,       // 主页schemes
    val fallbackSchemes: List<String> = emptyList()  // 备用schemes
) {
    companion object {
        /**
         * 创建基础schemes配置
         */
        fun basic(
            searchScheme: String,
            searchPageScheme: String,
            mainPageScheme: String
        ) = PlatformSchemes(
            searchSchemes = listOf(searchScheme),
            searchPageSchemes = listOf(searchPageScheme),
            mainPageSchemes = listOf(mainPageScheme)
        )
    }
}

/**
 * 平台功能支持配置
 */
data class PlatformFeatures(
    val supportAutoPaste: Boolean = true,          // 支持自动粘贴搜索
    val supportModeSearch: Boolean = false,        // 支持分类搜索
    val supportDeepLink: Boolean = true,           // 支持深度链接
    val supportDirectSearch: Boolean = true,       // 支持直接搜索
    val requiresSpecialHandling: Boolean = false   // 需要特殊处理
)

/**
 * 应用市场信息
 */
data class MarketInfo(
    val playStoreUrl: String? = null,        // Google Play下载链接
    val appStoreUrl: String? = null,         // App Store下载链接
    val officialWebsite: String? = null,     // 官方网站
    val description: String = ""             // 应用描述
)

/**
 * 预定义平台配置
 */
object PlatformConfigs {

    val XIAOHONGSHU = PlatformConfig(
        displayName = "小红书",
        icon = "📕",
        packageName = "com.xingin.xhs",
        primaryColor = Color(0xFFe74c3c),
        hotSearches = listOf("美食", "穿搭", "护肤", "旅游", "减肥", "化妆", "家居", "读书"),
        schemes = PlatformSchemes.basic(
            searchScheme = "xhsdiscover://search/result?keyword=",
            searchPageScheme = "xhsdiscover://search",
            mainPageScheme = "xhsdiscover://main"
        ),
        features = PlatformFeatures(
            supportAutoPaste = true,
            supportModeSearch = false,
            supportDeepLink = true,
            supportDirectSearch = true
        ),
        marketInfo = MarketInfo(
            playStoreUrl = "https://play.google.com/store/apps/details?id=com.xingin.xhs",
            officialWebsite = "https://www.xiaohongshu.com",
            description = "标记我的生活"
        )
    )

    val ZHIHU = PlatformConfig(
        displayName = "知乎",
        icon = "🧠",
        packageName = "com.zhihu.android",
        primaryColor = Color(0xFF0084ff),
        hotSearches = listOf("编程", "投资", "心理学", "历史", "科技", "哲学", "电影", "读书"),
        schemes = PlatformSchemes.basic(
            searchScheme = "zhihu://search?q=",
            searchPageScheme = "zhihu://search",
            mainPageScheme = "zhihu://main"
        ),
        features = PlatformFeatures(
            supportAutoPaste = true,
            supportModeSearch = true,
            supportDeepLink = true,
            supportDirectSearch = true
        ),
        marketInfo = MarketInfo(
            playStoreUrl = "https://play.google.com/store/apps/details?id=com.zhihu.android",
            officialWebsite = "https://www.zhihu.com",
            description = "有问题，就会有答案"
        )
    )

    val BILIBILI = PlatformConfig(
        displayName = "哔哩哔哩",
        icon = "📺",
        packageName = "tv.danmaku.bili",
        primaryColor = Color(0xFFfb7299),
        hotSearches = listOf("番剧", "游戏", "科技", "音乐", "舞蹈", "美食", "动画", "鬼畜"),
        schemes = PlatformSchemes.basic(
            searchScheme = "bilibili://search?keyword=",
            searchPageScheme = "bilibili://search",
            mainPageScheme = "bilibili://main"
        ),
        features = PlatformFeatures(
            supportAutoPaste = true,
            supportModeSearch = true,
            supportDeepLink = true,
            supportDirectSearch = true
        ),
        marketInfo = MarketInfo(
            playStoreUrl = "https://play.google.com/store/apps/details?id=tv.danmaku.bili",
            officialWebsite = "https://www.bilibili.com",
            description = "哔哩哔哩 (゜-゜)つロ 干杯~"
        )
    )

    val WEIBO = PlatformConfig(
        displayName = "微博",
        icon = "🌊",
        packageName = "com.sina.weibo",
        primaryColor = Color(0xFFff8200),
        hotSearches = listOf("热搜", "明星", "娱乐", "新闻", "体育", "搞笑", "情感", "时尚"),
        schemes = PlatformSchemes.basic(
            searchScheme = "sinaweibo://searchall?q=",
            searchPageScheme = "sinaweibo://search",
            mainPageScheme = "sinaweibo://main"
        ),
        features = PlatformFeatures(
            supportAutoPaste = true,
            supportModeSearch = false,
            supportDeepLink = true,
            supportDirectSearch = true
        ),
        marketInfo = MarketInfo(
            playStoreUrl = "https://play.google.com/store/apps/details?id=com.sina.weibo",
            officialWebsite = "https://weibo.com",
            description = "随时随地发现新鲜事"
        )
    )

    val DOUYIN = PlatformConfig(
        displayName = "抖音",
        icon = "🎵",
        packageName = "com.ss.android.ugc.aweme",
        primaryColor = Color(0xFF000000),
        hotSearches = listOf("搞笑", "美食", "舞蹈", "音乐", "宠物", "旅游", "健身", "化妆"),
        schemes = PlatformSchemes.basic(
            searchScheme = "snssdk1128://search?keyword=",
            searchPageScheme = "snssdk1128://search",
            mainPageScheme = "snssdk1128://main"
        ),
        features = PlatformFeatures(
            supportAutoPaste = true,
            supportModeSearch = false,
            supportDeepLink = true,
            supportDirectSearch = true,
            requiresSpecialHandling = true  // 抖音可能需要特殊处理
        ),
        marketInfo = MarketInfo(
            playStoreUrl = "https://play.google.com/store/apps/details?id=com.ss.android.ugc.aweme",
            officialWebsite = "https://www.douyin.com",
            description = "记录美好生活"
        )
    )

    /**
     * 获取所有配置
     * 获取所有配置
     */
    fun getAllConfigs(): List<PlatformConfig> = listOf(
        XIAOHONGSHU, ZHIHU, BILIBILI, WEIBO, DOUYIN
    )

    /**
     * 根据包名获取配置
     */
    fun getConfigByPackageName(packageName: String): PlatformConfig? {
        return getAllConfigs().find { it.packageName == packageName }
    }
}