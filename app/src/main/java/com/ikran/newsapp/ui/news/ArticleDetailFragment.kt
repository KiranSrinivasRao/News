package com.ikran.newsapp.ui.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.ikran.newsapp.R
import com.ikran.newsapp.util.Constants

/**
 * A simple [Fragment] subclass.
 */
class ArticleDetailFragment : Fragment() {

    lateinit var viewModel: NewsViewModel
    lateinit var webView: WebView
    lateinit var webUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.onBackPressedDispatcher?.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (shouldInterceptBackPress()) {
                        val topNewsFragment = TopNewsFragment()
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.container, topNewsFragment).commitNow()
                    } else {
                        isEnabled = false
                        activity?.onBackPressed()
                    }
                }
            }
        )
    }

    private fun shouldInterceptBackPress() = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_article_detail, container, false)
        webView = view.findViewById(R.id.webView)
        webUrl = requireArguments().getString(Constants.URL_KEY) ?: "https://www.google.com"

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).viewModel

        webView.apply {
            webViewClient = WebViewClient()
            loadUrl(webUrl)
        }
    }

    companion object {
        val logTag: String = ArticleDetailFragment::class.java.simpleName
    }
}
