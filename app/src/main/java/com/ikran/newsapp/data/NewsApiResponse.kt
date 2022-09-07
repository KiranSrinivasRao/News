package com.ikran.newsapp.data

data class NewsApiResponse(
    val articles: MutableList<Article>,
    val status: String,
    val totalResults: Int
)
