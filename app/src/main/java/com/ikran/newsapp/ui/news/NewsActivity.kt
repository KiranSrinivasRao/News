package com.ikran.newsapp.ui.news

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.ikran.newsapp.R
import com.ikran.newsapp.repository.NewsRepository
import com.ikran.newsapp.util.BackPressHandler
import com.ikran.newsapp.util.getTopFragment

class NewsActivity : AppCompatActivity() {

    lateinit var viewModel: NewsViewModel
    // private val vm by viewModel<NewsViewModel>()

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
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(NewsViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setLogo(R.drawable.global_news_text)
        supportActionBar?.setDisplayUseLogoEnabled(true)
        supportActionBar?.setTitle("")
        val colorDrawable = ColorDrawable(Color.parseColor("#D9D9D9"))
        supportActionBar?.setBackgroundDrawable(colorDrawable)
    }

    fun isTopFragmentConsumedBackPress() =
        getTopFragment<BackPressHandler>()?.onBackPressed() == true // ktlint-disable max-line-length
}
