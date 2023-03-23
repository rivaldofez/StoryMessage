package com.rivaldofez.storymessage.splash

import androidx.lifecycle.ViewModel
import com.rivaldofez.storymessage.data.remote.AuthenticationRepository
import kotlinx.coroutines.flow.Flow

class SplashViewModel constructor(private val authenticationRepository: AuthenticationRepository): ViewModel() {
    fun getAuthenticationToken(): Flow<String?> = authenticationRepository.getAuthenticationToken()
}