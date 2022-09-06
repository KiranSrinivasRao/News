package com.ikran.newsapp.ui.news

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.ikran.newsapp.R
import com.ikran.newsapp.repository.NewsRepository
import com.ikran.newsapp.util.BackPressHandler
import com.ikran.newsapp.util.getTopFragment

class NewsActivity : AppCompatActivity() {

    lateinit var viewModel: NewsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, TopNewsFragment.newInstance())
                .commitNow()
        }
        val repository = NewsRepository()

        val viewModelProviderFactory = NewsViewModelProviderFactory(application, repository)
        viewModel = ViewModelProvider(this,viewModelProviderFactory ).get(NewsViewModel::class.java)
    }

    fun isTopFragmentConsumedBackPress() = getTopFragment<BackPressHandler>()?.onBackPressed() == true


}