package com.rivaldofez.storymessage.page.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rivaldofez.storymessage.data.remote.AuthenticationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository) : ViewModel() {

    suspend fun userLogin(email: String, password: String) =
        authenticationRepository.userLogin(email = email, password = password)

    fun saveAuthenticationToken(token: String){
        viewModelScope.launch {
            authenticationRepository.saveAuthenticationToken(token = token)
        }
    }
}