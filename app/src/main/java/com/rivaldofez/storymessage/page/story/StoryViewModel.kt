package com.rivaldofez.storymessage.page.story

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rivaldofez.storymessage.data.remote.AuthenticationRepository
import com.rivaldofez.storymessage.data.remote.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoryViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val storyRepository: StoryRepository
): ViewModel() {

    suspend fun getStories(token: String, page: Int? = null, size: Int? = null) =
        storyRepository.getStories(token = token, page = page, size = size)

    fun getAuthenticationToken(): Flow<String?> = authenticationRepository.getAuthenticationToken()

    fun saveAuthenticationToken(token: String){
        viewModelScope.launch {
            authenticationRepository.saveAuthenticationToken(token = token)
        }
    }

}