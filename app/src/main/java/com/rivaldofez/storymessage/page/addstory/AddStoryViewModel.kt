package com.rivaldofez.storymessage.page.addstory

import androidx.lifecycle.ViewModel
import androidx.paging.ExperimentalPagingApi
import com.rivaldofez.storymessage.data.AuthenticationRepository
import com.rivaldofez.storymessage.data.StoryRepository
import com.rivaldofez.storymessage.data.remote.response.AddStoryResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@ExperimentalPagingApi
@HiltViewModel
class AddStoryViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val storyRepository: StoryRepository
): ViewModel() {

    suspend fun addStory(token: String, file: MultipartBody.Part, description: RequestBody):
            Flow<Result<AddStoryResponse>> = storyRepository.addStory(token = token, file = file, description = description)

    fun getAuthenticationToken(): Flow<String?> = authenticationRepository.getAuthenticationToken()
}