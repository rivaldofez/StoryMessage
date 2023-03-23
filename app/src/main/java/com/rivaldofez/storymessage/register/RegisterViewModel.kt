package com.rivaldofez.storymessage.register

import androidx.lifecycle.ViewModel
import com.rivaldofez.storymessage.data.remote.AuthenticationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
): ViewModel() {

    suspend fun registerUser(name: String, email: String, password: String) =
        authenticationRepository.userRegister(name = name, email = email, password = password)
}