package com.example.simplejump.platform.bilibili

/**
 * 哔哩哔哩URL Schemes配置
 */
object BilibiliSchemes {

    // 基础协议
    const val BASE_SCHEME = "bilibili://"
    const val BILI_SCHEME = "bili://"

    // 搜索相关
    object Search {
        const val SEARCH_RESULT = "bilibili://search?keyword="
        const val SEARCH_PAGE = "bilibili://search"
        const val BILI_SEARCH = "bili://search?keyword="

        // 备用搜索链接
        val FALLBACK_SEARCH_SCHEMES = listOf(
            "bilibili://search/",
            "bili://search",
            "bilibili://main/search",
            "bilibili://search/all"
        )
    }

    // 主页相关
    object Main {
        const val HOME = "bilibili://main"
        const val BILI_MAIN = "bili://main"
        const val INDEX = "bilibili://"

        // 备用主页链接
        val FALLBACK_MAIN_SCHEMES = listOf(
            "bilibili://home",
            "bili://",
            "bilibili://feed",
            "bilibili://main/home"
        )
    }

    // 分类内容
    object Category {
        const val RECOMMEND = "bilibili://main/home"
        const val FOLLOWING = "bilibili://main/dynamic"
        const val LIVE = "bilibili://live"
        const val ANIME = "bilibili://main/bangumi"
        const val GAME = "bilibili://game"
        const val MUSIC = "bilibili://music"
        const val KNOWLEDGE = "bilibili://main/knowledge"
    }

    // 特殊页面
    object Special {
        const val PROFILE = "bilibili://space/"
        const val VIDEO_DETAIL = "bilibili://video/"
        const val BANGUMI = "bilibili://bangumi/season/"
        const val ARTICLE = "bilibili://article/"
        const val LIVE_ROOM = "bilibili://live/"
    }

    // 网页版链接
    object Web {
        const val BASE_URL = "https://www.bilibili.com"
        const val SEARCH_URL = "https://search.bilibili.com/all?keyword="
        const val VIDEO_URL = "$BASE_URL/video/"
        const val BANGUMI_URL = "$BASE_URL/bangumi/"
        const val LIVE_URL = "https://live.bilibili.com/"
    }

    /**
     * 构建搜索URL
     */
    fun buildSearchUrls(keyword: String, encoded: Boolean = false): List<String> {
        val query = if (encoded) keyword else java.net.URLEncoder.encode(keyword, "UTF-8")
        return listOf(
            Search.SEARCH_RESULT + query,
            Search.BILI_SEARCH + query
        ) + Search.FALLBACK_SEARCH_SCHEMES
    }

    /**
     * 构建搜索页面URL列表
     */
    fun buildSearchPageUrls(): List<String> {
        return listOf(
            Search.SEARCH_PAGE,
            "bili://search"
        ) + Search.FALLBACK_SEARCH_SCHEMES
    }

    /**
     * 构建主页URL列表
     */
    fun buildMainPageUrls(): List<String> {
        return listOf(
            Main.HOME,
            Main.BILI_MAIN,
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