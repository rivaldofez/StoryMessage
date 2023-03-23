package com.rivaldofez.storymessage.data.remote

import com.rivaldofez.storymessage.data.local.AuthenticationLocalDataSource
import com.rivaldofez.storymessage.data.remote.response.LoginResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AuthenticationRepository constructor(
    private val apiService: ApiService,
    private val authenticationLocalDataSource: AuthenticationLocalDataSource) {



    suspend fun userLogin(email: String, password: String): Flow<Result<LoginResponse>> = flow {
        try {
            val response = apiService.userLogin(email = email, password = password)
            emit(Result.success(response))
        } catch (e: Exception){
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }
}