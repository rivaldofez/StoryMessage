package com.rivaldofez.storymessage.page.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rivaldofez.storymessage.data.UserDataRepository
import com.rivaldofez.storymessage.data.StoryRepository
import com.rivaldofez.storymessage.data.local.entity.StoryEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ExperimentalPagingApi
@HiltViewModel
class StoryViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val storyRepository: StoryRepository
): ViewModel() {

    fun getStories(token: String): LiveData<PagingData<StoryEntity>> =
        storyRepository.getStories(token = token).cachedIn(viewModelScope).asLiveData()

    fun getAuthenticationToken(): Flow<String?> = userDataRepository.getAuthenticationToken()


}