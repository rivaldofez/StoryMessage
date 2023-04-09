package com.rivaldofez.storymessage.page.maps

import androidx.lifecycle.ViewModel
import androidx.paging.ExperimentalPagingApi
import com.rivaldofez.storymessage.data.UserDataRepository
import com.rivaldofez.storymessage.data.StoryRepository
import com.rivaldofez.storymessage.data.remote.response.StoriesResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ExperimentalPagingApi
@HiltViewModel
class MapsViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val storyRepository: StoryRepository) :
    ViewModel() {

    fun getStoriesWithLocation(token: String): Flow<Result<StoriesResponse>> =
        storyRepository.getStoriesWithLocation(token = token)

    fun getAuthenticationToken(): Flow<String?> = userDataRepository.getAuthenticationToken()
}