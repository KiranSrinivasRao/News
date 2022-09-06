package com.ikran.newsapp.data

data class NewsApiResponse(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)