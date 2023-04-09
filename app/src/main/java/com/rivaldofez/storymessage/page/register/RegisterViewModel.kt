package com.rivaldofez.storymessage.page.register

import androidx.lifecycle.ViewModel
import com.rivaldofez.storymessage.data.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository
): ViewModel() {

    suspend fun registerUser(name: String, email: String, password: String) =
        userDataRepository.userRegister(name = name, email = email, password = password)
}