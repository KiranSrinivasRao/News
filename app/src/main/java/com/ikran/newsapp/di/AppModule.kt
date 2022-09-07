package com.ikran.newsapp.di

import com.ikran.newsapp.api.RetrofitInstance
import com.ikran.newsapp.repository.NewsRepository
import org.koin.dsl.module

val appModule = module {

    single {
        RetrofitInstance.api
    }
    single {
        NewsRepository()
    }
}
