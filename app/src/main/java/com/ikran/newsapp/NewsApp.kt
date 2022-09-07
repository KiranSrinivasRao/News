package com.ikran.newsapp

import android.app.Application
import com.ikran.newsapp.di.activityModule
import com.ikran.newsapp.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class NewsApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@NewsApp)
            modules(appModule, activityModule)
        }
    }
}
