package com.example.simplejump.platform.weibo

/**
 * 微博URL Schemes配置
 */
object WeiboSchemes {

    // 基础协议
    const val BASE_SCHEME = "sinaweibo://"
    const val WEIBO_SCHEME = "weibo://"

    // 搜索相关
    object Search {
        const val SEARCH_RESULT = "sinaweibo://search?q="
        const val SEARCH_PAGE = "sinaweibo://search"
        const val WEIBO_SEARCH = "weibo://search?q="

        // 备用搜索链接
        val FALLBACK_SEARCH_SCHEMES = listOf(
            "sinaweibo://searchall",
            "weibo://search",
            "sinaweibo://qsearch",
            "sinaweibo://browser?url=https://s.weibo.com"
        )
    }

    // 主页相关
    object Main {
        const val HOME = "sinaweibo://main"
        const val WEIBO_MAIN = "weibo://main"
        const val INDEX = "sinaweibo://"

        // 备用主页链接
        val FALLBACK_MAIN_SCHEMES = listOf(
            "sinaweibo://homepage",
            "weibo://",
            "sinaweibo://feed",
            "sinaweibo://timeline"
        )
    }

    // 分类内容
    object Category {
        const val TIMELINE = "sinaweibo://timeline"           // 时间线
        const val HOT = "sinaweibo://hot"                     // 热搜
        const val DISCOVER = "sinaweibo://discover"           // 发现
        const val MESSAGE = "sinaweibo://message"             // 消息
        const val NEARBY = "sinaweibo://nearby"               // 附近
        const val LIVE = "sinaweibo://live"                   // 直播
        const val VIDEO = "sinaweibo://video"                 // 视频
        const val TOPIC = "sinaweibo://topic"                 // 话题
    }

    // 特殊页面
    object Special {
        const val PROFILE = "sinaweibo://userinfo?uid="       // 用户主页
        const val POST_DETAIL = "sinaweibo://detail?mblogid=" // 微博详情
        const val TOPIC_DETAIL = "sinaweibo://topic?name="    // 话题详情
        const val COMPOSE = "sinaweibo://compose"             // 发微博
        const val HOT_SEARCH = "sinaweibo://hotsearch"        // 热搜榜
        const val STORY = "sinaweibo://story"                 // 微博故事
    }

    // 网页版链接
    object Web {
        const val BASE_URL = "https://weibo.com"
        const val SEARCH_URL = "https://s.weibo.com/weibo/"
        const val HOT_SEARCH_URL = "https://s.weibo.com/top/summary"
        const val MOBILE_URL = "https://m.weibo.cn"
        const val USER_URL = "$BASE_URL/u/"
    }

    /**
     * 构建搜索URL
     */
    fun buildSearchUrls(keyword: String, encoded: Boolean = false): List<String> {
        val query = if (encoded) keyword else java.net.URLEncoder.encode(keyword, "UTF-8")
        return listOf(
            Search.SEARCH_RESULT + query,
            Search.WEIBO_SEARCH + query,
            "sinaweibo://searchall?q=$query"
        ) + Search.FALLBACK_SEARCH_SCHEMES
    }

    /**
     * 构建搜索页面URL列表
     */
    fun buildSearchPageUrls(): List<String> {
        return listOf(
            Search.SEARCH_PAGE,
            "weibo://search",
            "sinaweibo://searchall"
        ) + Search.FALLBACK_SEARCH_SCHEMES
    }

    /**
     * 构建主页URL列表
     */
    fun buildMainPageUrls(): List<String> {
        return listOf(
            Main.HOME,
            Main.WEIBO_MAIN,
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

    /**
     * 构建热搜页面URL列表
     */
    fun buildHotSearchUrls(): List<String> {
        return listOf(
            Special.HOT_SEARCH,
            Category.HOT,
            "sinaweibo://browser?url=${Web.HOT_SEARCH_URL}"
        )
    }

    /**
     * 构建用户主页URL
     */
    fun buildUserProfileUrl(uid: String): String {
        return Special.PROFILE + uid
    }

    /**
     * 构建话题页面URL
     */
    fun buildTopicUrl(topicName: String): String {
        val encodedTopic = java.net.URLEncoder.encode(topicName, "UTF-8")
        return Special.TOPIC_DETAIL + encodedTopic
    }
}