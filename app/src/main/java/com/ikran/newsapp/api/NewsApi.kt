package com.ikran.newsapp.api

import com.ikran.newsapp.data.NewsApiResponse
import com.ikran.newsapp.util.Constants.Companion.NEWS_API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {

    @GET("v2/top-headlines")
    suspend fun getTopNews(
        @Query("country")
        countryCode: String = "in",
        @Query("page")
        page: Int = 1,
        @Query("apiKey")
        apiKey: String = NEWS_API_KEY
    ): Response<NewsApiResponse>

    @GET("v2/everything")
    suspend fun searchForNews(
        @Query("q")
        searchQuery: String,
        @Query("page")
        page: Int = 1,
        @Query("apiKey")
        apiKey: String = NEWS_API_KEY
    ): Response<NewsApiResponse>
}
