package com.ikran.newsapp.ui.news

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ikran.newsapp.R
import com.ikran.newsapp.adapter.NewsAdapter
import com.ikran.newsapp.adapter.TopNewsAdapter
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
        //setHasOptionsMenu(true)
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

   /* override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_bookbark_menu, menu)
        val searchItem:MenuItem = menu.findItem(R.id.menu_search_item)
        val searchView = searchItem.actionView as SearchView

        searchView.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    Log.i(logTag, "onQueryTextSubmit: $query")
                    //hideKeyboard() & hit api
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    Log.i(logTag, "onQueryTextChange: $newText")
                    return true
                }

            })
            // Todo
            setOnSearchClickListener {
                searchView.setQuery("TODO", false)
            }
        }
    }*/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_search_news, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewSearchNews)
        recyclerView.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).viewModel
        viewModel.searchNews.observe(viewLifecycleOwner, Observer { response ->
            when(response){
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles)

                        //Log.i(logTag, newsResponse.toString())

                        val articleDetailFragment = ArticleDetailFragment()


                        newsAdapter.setOnClickListener { newsArticle ->
                            val args = Bundle()
                            args.putString(Constants.URL_KEY, newsArticle.url)
                            articleDetailFragment.arguments = args

                            parentFragmentManager.apply {
                                beginTransaction().replace(R.id.container, articleDetailFragment ).commit()
                            }
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    try{
                        response.message?.let { message ->
                            Log.e(TopNewsFragment.logTag, message)
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    }catch (e: Exception){
                        Log.e(TopNewsFragment.logTag,"Error Fragment --- "+ e.message)
                    }
                }
                is Resource.Loading -> { showProgressBar()}
                //else -> {}
            }
        })
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