package com.rivaldofez.storymessage.story

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rivaldofez.storymessage.data.remote.response.StoryResponse
import com.rivaldofez.storymessage.databinding.ItemStoryBinding
import com.rivaldofez.storymessage.extension.setImageFromUrl
import javax.inject.Inject
import javax.inject.Singleton

class StoryAdapter (private val callback: StoryItemCallback): ListAdapter<StoryResponse, StoryAdapter.ViewHolder>(DiffCallback) {


    inner class ViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(context: Context, story: StoryResponse) {
            binding.apply {
                tvName.text = story.name
                tvDescription.text = story.description
                imgStory.setImageFromUrl(context, url = story.photoUrl)
                tvDate.text = story.createdAt

                // On item clicked
                root.setOnClickListener{
                    callback.onStoryClicked(story = story, itemBinding = binding)
                }

            }
        }
    }


    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<StoryResponse>() {
            override fun areItemsTheSame(oldItem: StoryResponse, newItem: StoryResponse): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: StoryResponse, newItem: StoryResponse): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = getItem(position)
        holder.bind(holder.itemView.context, story)
    }

}