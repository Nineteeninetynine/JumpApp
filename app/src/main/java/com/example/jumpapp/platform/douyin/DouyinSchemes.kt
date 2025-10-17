package com.example.simplejump.platform.douyin

/**
 * 抖音URL Schemes配置
 */
object DouyinSchemes {

    // 基础协议
    const val BASE_SCHEME = "snssdk1128://"
    const val AWEME_SCHEME = "aweme://"

    // 搜索相关
    object Search {
        const val SEARCH_RESULT = "snssdk1128://search?keyword="
        const val SEARCH_PAGE = "snssdk1128://search"
        const val AWEME_SEARCH = "aweme://search?keyword="

        // 备用搜索链接
        val FALLBACK_SEARCH_SCHEMES = listOf(
            "snssdk1128://search/",
            "aweme://search",
            "snssdk1128://discover/search"
        )
    }

    // 主页相关
    object Main {
        const val HOME = "snssdk1128://main"
        const val AWEME_MAIN = "aweme://main"
        const val INDEX = "snssdk1128://"

        // 备用主页链接
        val FALLBACK_MAIN_SCHEMES = listOf(
            "snssdk1128://home",
            "aweme://",
            "snssdk1128://feed"
        )
    }

    // 分类内容
    object Category {
        const val RECOMMEND = "snssdk1128://feed/recommend"
        const val FOLLOWING = "snssdk1128://feed/following"
        const val LIVE = "snssdk1128://live"
        const val NEARBY = "snssdk1128://nearby"
    }

    // 特殊页面
    object Special {
        const val PROFILE = "snssdk1128://user/"
        const val VIDEO_DETAIL = "snssdk1128://video/"
        const val CHALLENGE = "snssdk1128://challenge/"
        const val MUSIC = "snssdk1128://music/"
    }

    // 网页版链接
    object Web {
        const val BASE_URL = "https://www.douyin.com"
        const val SEARCH_URL = "$BASE_URL/search/"
        const val VIDEO_URL = "$BASE_URL/video/"
    }

    /**
     * 构建搜索URL
     */
    fun buildSearchUrls(keyword: String, encoded: Boolean = false): List<String> {
        val query = if (encoded) keyword else java.net.URLEncoder.encode(keyword, "UTF-8")
        return listOf(
            Search.SEARCH_RESULT + query,
            Search.AWEME_SEARCH + query
        ) + Search.FALLBACK_SEARCH_SCHEMES
    }

    /**
     * 构建搜索页面URL列表
     */
    fun buildSearchPageUrls(): List<String> {
        return listOf(
            Search.SEARCH_PAGE,
            "aweme://search"
        ) + Search.FALLBACK_SEARCH_SCHEMES
    }

    /**
     * 构建主页URL列表
     */
    fun buildMainPageUrls(): List<String> {
        return listOf(
            Main.HOME,
            Main.AWEME_MAIN,
            Main.INDEX
        ) + Main.FALLBACK_MAIN_SCHEMES
    }

    /**
     * 构建网页搜索URL
     */
    fun buildWebSearchUrl(keyword: String): String {
        val encodedKeyword = java.net.URLEncoder.encode(keyword, "UTF-8")
        return Web.SEARCH_URL + encodedKeyword
    }
}