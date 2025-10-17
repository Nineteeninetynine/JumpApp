package com.example.simplejump

/**
 * 小红书URL Scheme配置
 */
object XiaoHongShuSchemes {
    const val PACKAGE_NAME = "com.xingin.xhs"

    val SEARCH_WITH_KEYWORD = listOf(
        "xhsdiscover://search/result?keyword=%s",
        "xhsdiscover://search/result?q=%s",
        "xhsdiscover://search/recommend?keyword=%s",
        "xhsdiscover://search/recommend?q=%s",
        "xhsdiscover://search?keyword=%s",
        "xhsdiscover://search?q=%s"
    )

    val SEARCH_PAGE = listOf(
        "xhsdiscover://search/recommend",
        "xhsdiscover://search",
        "xhsdiscover://global_search",
        "xhsdiscover://discover"
    )

    val MAIN_PAGE = listOf(
        "xhsdiscover://",
        "xhsdiscover://home",
        "xhsdiscover://main",
        "xhsdiscover://discover",
        "xiaohongshu://",
        "xiaohongshu://home",
        "xhs://",
        "xhs://home"
    )
}

/**
 * 知乎URL Scheme配置
 */
object ZhiHuSchemes {
    const val PACKAGE_NAME = "com.zhihu.android"

    val SEARCH_WITH_KEYWORD = listOf(
        "zhihu://search?q=%s",
        "zhihu://search?keyword=%s",
        "zhihu://search?query=%s"
    )

    val SEARCH_PAGE = listOf(
        "zhihu://search",
        "zhihu://explore/search",
        "zhihu://main/search"
    )

    val MAIN_PAGE = listOf(
        "zhihu://",
        "zhihu://main",
        "zhihu://home"
    )
}