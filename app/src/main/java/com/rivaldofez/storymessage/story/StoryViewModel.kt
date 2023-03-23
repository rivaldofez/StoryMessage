package com.rivaldofez.storymessage.story

import androidx.lifecycle.ViewModel
import com.rivaldofez.storymessage.data.remote.AuthenticationRepository
import com.rivaldofez.storymessage.data.remote.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StoryViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val storyRepository: StoryRepository
): ViewModel() {

    suspend fun getStories(token: String, page: Int? = null, size: Int? = null) =
        storyRepository.getStories(token = token, page = page, size = size)


}