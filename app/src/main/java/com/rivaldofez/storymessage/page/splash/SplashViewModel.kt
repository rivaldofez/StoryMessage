package com.rivaldofez.storymessage.page.splash

import androidx.lifecycle.ViewModel
import com.rivaldofez.storymessage.data.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(private val userDataRepository: UserDataRepository): ViewModel() {
    fun getAuthenticationToken(): Flow<String?> = userDataRepository.getAuthenticationToken()
}