package com.rivaldofez.storymessage.page.story

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rivaldofez.storymessage.data.local.entity.StoryEntity
import com.rivaldofez.storymessage.databinding.ItemStoryBinding
import com.rivaldofez.storymessage.extension.setImageFromUrl
import com.rivaldofez.storymessage.extension.setLocaleDateFormat

class StoryAdapter (private val callback: StoryItemCallback): PagingDataAdapter<StoryEntity, StoryAdapter.ViewHolder>(
    DiffCallback
) {

    inner class ViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(context: Context, story: StoryEntity) {
            binding.apply {
                tvName.text = story.name.lowercase().replaceFirstChar { it.titlecase() }
                tvDescription.text = story.description
                imgStory.setImageFromUrl(context, url = story.photoUrl)
                tvDate.setLocaleDateFormat(story.createdAt)

                root.setOnClickListener{
                    callback.onStoryClicked(story = story, itemBinding = binding)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            holder.bind(holder.itemView.context, story)
        }

    }

    companion object {
         val DiffCallback = object : DiffUtil.ItemCallback<StoryEntity>() {
            override fun areItemsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem == newItem
            }
        }
    }
}