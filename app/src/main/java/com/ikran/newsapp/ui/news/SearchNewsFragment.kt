package com.ikran.newsapp.ui.news

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ikran.newsapp.R
import com.ikran.newsapp.adapter.NewsAdapter
import com.ikran.newsapp.util.Constants
import com.ikran.newsapp.util.Resource

class SearchNewsFragment : Fragment() {

    companion object {
        fun newInstance() = SearchNewsFragment()
        val logTag:String = SearchNewsFragment::class.java.simpleName
    }

    lateinit var viewModel: NewsViewModel
    private lateinit var recyclerView: RecyclerView
    lateinit var newsAdapter: NewsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        newsAdapter = NewsAdapter()
        activity?.onBackPressedDispatcher?.addCallback(this,
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    if(shouldInterceptBackPress()){
                        val topNewsFragment = TopNewsFragment()
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.container, topNewsFragment ).commitNow()
                    }else{
                        isEnabled = false
                        activity?.onBackPressed()
                    }
                }
            })
    }
    private fun shouldInterceptBackPress() = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_search_news, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewSearchNews)
        recyclerView.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@SearchNewsFragment.scrollListener)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).viewModel
        viewModel.searchNews.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalPages = newsResponse.totalResults / Constants.QUERY_PAGE_COUNT + 2
                        isLastPage = viewModel.topNewsPage == totalPages
                        if (isLastPage) {
                            recyclerView.setPadding(0, 0, 0, 0)
                        }

                        val articleDetailFragment = ArticleDetailFragment()


                        newsAdapter.setOnClickListener { newsArticle ->
                            val args = Bundle()
                            args.putString(Constants.URL_KEY, newsArticle.url)
                            articleDetailFragment.arguments = args

                            parentFragmentManager.apply {
                                beginTransaction().replace(R.id.container, articleDetailFragment)
                                    .commit()
                            }
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    try {
                        response.message?.let { message ->
                            Log.e(TopNewsFragment.logTag, message)
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Log.e(TopNewsFragment.logTag, "Error Fragment --- " + e.message)
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
                //else -> {}
            }
        }
    }

    private fun showProgressBar() {
        Log.e(logTag, "showProgressBar")
        isLoading = true
    }

    private fun hideProgressBar() {
        Log.e(logTag, "hideProgressBar")
        isLoading = false
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_COUNT

            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem
                    && isNotAtBeginning && isTotalMoreThanVisible && isScrolling
            if (shouldPaginate) {
                viewModel.getTopNews("in")
                isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }

    /*   override fun onActivityCreated(savedInstanceState: Bundle?) {
           super.onActivityCreated(savedInstanceState)

           //  Use the ViewModel
       }*/

}