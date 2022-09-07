package com.ikran.newsapp.ui.news

import android.os.Bundle
import android.util.Log
import android.view.* // ktlint-disable no-wildcard-imports
import android.widget.AbsListView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ikran.newsapp.R
import com.ikran.newsapp.adapter.NewsAdapter
import com.ikran.newsapp.adapter.TopNewsAdapter
import com.ikran.newsapp.util.Constants
import com.ikran.newsapp.util.Constants.Companion.QUERY_PAGE_COUNT
import com.ikran.newsapp.util.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TopNewsFragment : Fragment() {

    companion object {
        fun newInstance() = TopNewsFragment()
        val logTag: String = TopNewsFragment::class.java.simpleName
    }

    lateinit var viewModel: NewsViewModel
    private lateinit var popularNewsRecyclerView: RecyclerView
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var topNewsRecyclerView: RecyclerView
    private lateinit var topNewsAdapter: TopNewsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        newsAdapter = NewsAdapter()
        topNewsAdapter = TopNewsAdapter()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_bookbark_menu, menu)
        val searchItem: MenuItem = menu.findItem(R.id.menu_search_item)
        val searchView = searchItem.actionView as SearchView

        var job: Job? = null

        searchView.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    Log.i(logTag, "onQueryTextSubmit: $query")
                    // hideKeyboard() & hit api
                    job?.cancel()
                    job = MainScope().launch {
                        delay(500L)
                        if (!query.isNullOrBlank()) {
                            viewModel.searchNews(query, 1)
                        }
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    Log.i(logTag, "onQueryTextChange: $newText")
                    /*job?.cancel()
                    job = MainScope().launch{
                        delay(500L)
                        if (!newText.isNullOrBlank() ) {
                            viewModel.searchNews(newText, 1)
                        }
                    }*/
                    return true
                }
            })
            setOnSearchClickListener {
                searchView.setQuery("", false)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_top_news, container, false)
        popularNewsRecyclerView = view.findViewById(R.id.recyclerViewPopularNews)
        popularNewsRecyclerView.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@TopNewsFragment.scrollListener)
        }

        topNewsRecyclerView = view.findViewById(R.id.recyclerViewTopNews)
        topNewsRecyclerView.apply {
            adapter = topNewsAdapter
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).viewModel

        viewModel.topNews.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressSnackBar()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        topNewsAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalPages = newsResponse.totalResults / QUERY_PAGE_COUNT + 2
                        isLastPage = viewModel.topNewsPage == totalPages
                        if (isLastPage) {
                            popularNewsRecyclerView.setPadding(0, 0, 0, 0)
                        }
                        // Log.i(logTag, newsResponse.toString())

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
                    hideProgressSnackBar()
                    try {
                        response.message?.let { message ->
                            Log.e(logTag, message)
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Log.e(logTag, "Error Fragment --- " + e.message)
                    }
                }
                is Resource.Loading -> {
                    showProgressSnackBar()
                }
            }
        }

        viewModel.searchNews.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressSnackBar()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles)

                        val searchNewsFragment = SearchNewsFragment()
                        parentFragmentManager.apply {
                            beginTransaction().replace(R.id.container, searchNewsFragment).commit()
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressSnackBar()
                    try {
                        response.message?.let { message ->
                            Log.e(logTag, message)
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Log.e(logTag, "Error Fragment --- " + e.message)
                    }
                }
                is Resource.Loading -> {
                    showProgressSnackBar()
                }
            }
        }
    }

    private fun showProgressSnackBar() {
        Log.e(logTag, "showProgressBar")
        isLoading = true
    }

    private fun hideProgressSnackBar() {
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
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_COUNT

            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem &&
                isNotAtBeginning && isTotalMoreThanVisible && isScrolling
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
