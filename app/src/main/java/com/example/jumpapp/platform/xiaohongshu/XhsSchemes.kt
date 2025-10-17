package com.example.simplejump.platform.xiaohongshu

/**
 * 小红书URL Schemes配置
 * 包含各种跳转链接的配置
 */
object XhsSchemes {

    // 基础协议
    const val BASE_SCHEME = "xhsdiscover://"

    // 搜索相关
    object Search {
        const val SEARCH_RESULT = "xhsdiscover://search/result?keyword="
        const val SEARCH_PAGE = "xhsdiscover://search"
        const val SEARCH_WITH_KEYWORD = "xhsdiscover://search?keyword="

        // 备用搜索链接
        val FALLBACK_SEARCH_SCHEMES = listOf(
            "xhsdiscover://search/",
            "xhsdiscover://discover/search"
        )
    }

    // 主页相关
    object Main {
        const val HOME = "xhsdiscover://main"
        const val DISCOVER = "xhsdiscover://discover"
        const val INDEX = "xhsdiscover://"

        // 备用主页链接
        val FALLBACK_MAIN_SCHEMES = listOf(
            "xhsdiscover://home",
            "xhsdiscover://index"
        )
    }

    // 分类搜索
    object Category {
        const val BEAUTY = "xhsdiscover://search/result?keyword=美妆"
        const val FASHION = "xhsdiscover://search/result?keyword=穿搭"
        const val FOOD = "xhsdiscover://search/result?keyword=美食"
        const val TRAVEL = "xhsdiscover://search/result?keyword=旅游"
        const val HOME_DECOR = "xhsdiscover://search/result?keyword=家居"
    }

    // 特殊页面
    object Special {
        const val PROFILE = "xhsdiscover://user/"
        const val NOTE_DETAIL = "xhsdiscover://note/"
        const val LIVE = "xhsdiscover://live"
        const val SHOP = "xhsdiscover://shop"
    }

    // 网页版链接
    object Web {
        const val BASE_URL = "https://www.xiaohongshu.com"
        const val SEARCH_URL = "$BASE_URL/search_result?keyword="
        const val EXPLORE_URL = "$BASE_URL/explore"
    }

    /**
     * 构建搜索URL
     * @param keyword 搜索关键词
     * @param encoded 是否已编码
     * @return 搜索URL列表
     */
    fun buildSearchUrls(keyword: String, encoded: Boolean = false): List {
        val query = if (encoded) keyword else java.net.URLEncoder.encode(keyword, "UTF-8")
        return listOf(
            Search.SEARCH_RESULT + query,
            Search.SEARCH_WITH_KEYWORD + query
        ) + Search.FALLBACK_SEARCH_SCHEMES
    }

    /**
     * 构建搜索页面URL列表
     * @return 搜索页面URL列表
     */
    fun buildSearchPageUrls(): List {
        return listOf(Search.SEARCH_PAGE) + Search.FALLBACK_SEARCH_SCHEMES
    }

    /**
     * 构建主页URL列表
     * @return 主页URL列表
     */
    fun buildMainPageUrls(): List {
        return listOf(
            Main.HOME,
            Main.DISCOVER,
            Main.INDEX
        ) + Main.FALLBACK_MAIN_SCHEMES
    }

    /**
     * 构建网页搜索URL
     * @param keyword 搜索关键词
     * @return 网页搜索URL
     */
    fun buildWebSearchUrl(keyword: String): String {
        val encodedKeyword = java.net.URLEncoder.encode(keyword, "UTF-8")
        return Web.SEARCH_URL + encodedKeyword
    }
}