package com.ikran.newsapp.util

class Constants {

    companion object {
        const val URL_KEY: String = "URL_KEY"
        const val NEWS_API_KEY = "015ebaaf8d2b49cbbc1896815fcc1f10"
        const val BASE_URL = "https://newsapi.org/"
        const val QUERY_PAGE_COUNT =
            1 // if 20 is queried then there is no response due to limit for free version
    }
}
