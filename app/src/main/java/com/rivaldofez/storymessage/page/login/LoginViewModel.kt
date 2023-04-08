package com.rivaldofez.storymessage.page.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rivaldofez.storymessage.data.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository
) : ViewModel() {

    suspend fun userLogin(email: String, password: String) =
        userDataRepository.userLogin(email = email, password = password)

    fun saveAuthenticationToken(token: String){
        viewModelScope.launch {
            userDataRepository.saveAuthenticationToken(token = token)
        }
    }
}