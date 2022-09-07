package com.ikran.newsapp.di

import com.ikran.newsapp.ui.news.NewsViewModel
import org.koin.dsl.module

val activityModule = module {

    single {
        NewsViewModel(get(), get())
    }

}