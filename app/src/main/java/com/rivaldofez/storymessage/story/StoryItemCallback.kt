package com.rivaldofez.storymessage.story

import com.rivaldofez.storymessage.data.remote.response.StoryResponse

interface StoryItemCallback {
    fun onStoryClicked(story: StoryResponse)
}