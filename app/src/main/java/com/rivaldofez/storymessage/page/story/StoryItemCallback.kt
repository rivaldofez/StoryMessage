package com.rivaldofez.storymessage.page.story

import com.rivaldofez.storymessage.data.local.entity.StoryEntity
import com.rivaldofez.storymessage.databinding.ItemStoryBinding

interface StoryItemCallback {
    fun onStoryClicked(story: StoryEntity, itemBinding: ItemStoryBinding)
}