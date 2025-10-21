package com.example.simplejump.platform.zhihu

/**
 * 知乎URL Schemes配置
 */
object ZhihuSchemes {

    // 基础协议
    const val BASE_SCHEME = "zhihu://"

    // 搜索相关
    object Search {
        const val SEARCH_RESULT = "zhihu://search?q="
        const val SEARCH_PAGE = "zhihu://search"
        const val SEARCH_WITH_QUERY = "zhihu://search?query="

        // 备用搜索链接
        val FALLBACK_SEARCH_SCHEMES = listOf(
            "zhihu://search/",
            "zhihu://discover/search"
        )
    }

    // 主页相关
    object Main {
        const val HOME = "zhihu://main"
        const val DISCOVER = "zhihu://discover"
        const val INDEX = "zhihu://"

        // 备用主页链接
        val FALLBACK_MAIN_SCHEMES = listOf(
            "zhihu://home",
            "zhihu://index",
            "zhihu://feed"
        )
    }

    // 分类搜索
    object Category {
        const val QUESTION = "zhihu://search?q=问题"
        const val ARTICLE = "zhihu://search?q=文章"
        const val PEOPLE = "zhihu://search?q=用户"
        const val TOPIC = "zhihu://search?q=话题"
    }

    // 特殊页面
    object Special {
        const val HOT = "zhihu://explore/hot"
        const val RECOMMENDATIONS = "zhihu://explore/recommendations"
        const val FOLLOWING = "zhihu://following"
        const val PROFILE = "zhihu://people/"
    }

    // 网页版链接
    object Web {
        const val BASE_URL = "https://www.zhihu.com"
        const val SEARCH_URL = "$BASE_URL/search?q="
        const val EXPLORE_URL = "$BASE_URL/explore"
        const val HOT_URL = "$BASE_URL/hot"
    }

    /**
     * 构建搜索URL
     */
    fun buildSearchUrls(keyword: String, encoded: Boolean = false): List<String> {
        val query = if (encoded) keyword else java.net.URLEncoder.encode(keyword, "UTF-8")
        return listOf(
            Search.SEARCH_RESULT + query,
            Search.SEARCH_WITH_QUERY + query
        ) + Search.FALLBACK_SEARCH_SCHEMES
    }

    /**
     * 构建搜索页面URL列表
     */
    fun buildSearchPageUrls(): List<String> {
        return listOf(Search.SEARCH_PAGE) + Search.FALLBACK_SEARCH_SCHEMES
    }

    /**
     * 构建主页URL列表
     */
    fun buildMainPageUrls(): List<String> {
        return listOf(
            Main.HOME,
            Main.DISCOVER,
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