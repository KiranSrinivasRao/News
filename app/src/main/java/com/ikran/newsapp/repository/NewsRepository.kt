package com.ikran.newsapp.repository

import com.ikran.newsapp.api.RetrofitInstance

class NewsRepository {

    suspend fun getTopNews(country: String, page: Int) =
        RetrofitInstance.api.getTopNews(countryCode = country, page = page)

    suspend fun searchNews(query: String, page: Int) =
        RetrofitInstance.api.searchForNews(searchQuery = query, page = page)
}
