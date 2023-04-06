package com.rivaldofez.storymessage.page.splash

import androidx.lifecycle.ViewModel
import com.rivaldofez.storymessage.data.AuthenticationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(private val authenticationRepository: AuthenticationRepository): ViewModel() {
    fun getAuthenticationToken(): Flow<String?> = authenticationRepository.getAuthenticationToken()
}