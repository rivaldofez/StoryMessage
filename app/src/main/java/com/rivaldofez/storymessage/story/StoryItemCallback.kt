package com.rivaldofez.storymessage.story

import com.rivaldofez.storymessage.data.remote.response.StoryResponse
import com.rivaldofez.storymessage.databinding.ItemStoryBinding

interface StoryItemCallback {
    fun onStoryClicked(story: StoryResponse, itemBinding: ItemStoryBinding)
}