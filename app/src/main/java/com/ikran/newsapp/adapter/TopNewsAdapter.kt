package com.ikran.newsapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.ikran.newsapp.R
import com.ikran.newsapp.data.Article

class TopNewsAdapter : RecyclerView.Adapter<TopNewsAdapter.NewsViewHolder>() {

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val differCallback =

        object : DiffUtil.ItemCallback<Article>() {
            override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
                return oldItem.url == newItem.url
            }

            override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
                return oldItem == newItem
            }
        }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        return NewsViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.top_news_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article = differ.currentList[position]
        val rootView = holder.itemView.rootView
        val head: TextView = rootView.findViewById(R.id.textViewTopNewsHead)
        val description: TextView = rootView.findViewById(R.id.textViewTopNewsDesc)
        val source: Chip = rootView.findViewById(R.id.sourceChip)
        val image: ImageView = rootView.findViewById(R.id.imageViewTopNews)
        holder.itemView.apply {
            Glide.with(this).load(article.urlToImage).into(image)
            head.text = article.title
            description.text = article.description
            source.text = article.source?.name
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}
