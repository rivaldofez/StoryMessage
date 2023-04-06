package com.rivaldofez.storymessage.page.maps

import androidx.lifecycle.ViewModel
import androidx.paging.ExperimentalPagingApi
import com.rivaldofez.storymessage.data.StoryRepository
import com.rivaldofez.storymessage.data.remote.response.StoriesResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ExperimentalPagingApi
@HiltViewModel
class MapsViewModel @Inject constructor(private val storyRepository: StoryRepository) :
    ViewModel() {

    fun getAllStories(token: String): Flow<Result<StoriesResponse>> =
        storyRepository.getStoriesWithLocation(token)
}