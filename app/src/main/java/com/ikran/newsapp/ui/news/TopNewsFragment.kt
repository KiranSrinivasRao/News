package com.ikran.newsapp.ui.news

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ikran.newsapp.R
import com.ikran.newsapp.adapter.NewsAdapter
import com.ikran.newsapp.adapter.TopNewsAdapter
import com.ikran.newsapp.util.Constants
import com.ikran.newsapp.util.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TopNewsFragment : Fragment() {

    companion object {
        fun newInstance() = TopNewsFragment()
        val logTag:String = TopNewsFragment::class.java.simpleName
    }

    lateinit var viewModel: NewsViewModel
    private lateinit var recyclerView: RecyclerView
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
        val searchItem:MenuItem = menu.findItem(R.id.menu_search_item)
        val searchView = searchItem.actionView as SearchView

        var job: Job? = null

        searchView.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    Log.i(logTag, "onQueryTextSubmit: $query")
                    //hideKeyboard() & hit api
                    job?.cancel()
                    job = MainScope().launch{
                        delay(500L)
                        if (!query.isNullOrBlank() ) {
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
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_top_news, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewPopularNews)
        recyclerView.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
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
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles)
                        topNewsAdapter.differ.submitList(newsResponse.articles)
                        //Log.i(logTag, newsResponse.toString())

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
                            Log.e(logTag, message)
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Log.e(logTag, "Error Fragment --- " + e.message)
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
                //else -> {}
            }
        }


        viewModel.searchNews.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles)

                        val searchNewsFragment = SearchNewsFragment()
                        parentFragmentManager.apply {
                            beginTransaction().replace(R.id.container, searchNewsFragment).commit()
                        }

                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
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
                    showProgressBar()
                }
                //else -> {}
            }
        }

    }

    private fun showProgressBar() {
        Log.e(logTag, "showProgressBar")
    }

    private fun hideProgressBar() {
        Log.e(logTag, "hideProgressBar")
    }

    /*   override fun onActivityCreated(savedInstanceState: Bundle?) {
           super.onActivityCreated(savedInstanceState)

           //  Use the ViewModel
       }*/

}