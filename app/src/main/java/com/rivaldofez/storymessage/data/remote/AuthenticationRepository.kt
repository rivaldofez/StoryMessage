package com.rivaldofez.storymessage.data.remote

import com.rivaldofez.storymessage.data.local.AuthenticationLocalDataSource
import com.rivaldofez.storymessage.data.remote.response.LoginResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AuthenticationRepository @Inject constructor(
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
    }.flowOn(Dispatchers.IO)

    suspend fun saveAuthenticationToken(token: String){
        authenticationLocalDataSource.saveAuthenticationToken(token)
    }

    fun getAuthenticationToken(): Flow<String?> = authenticationLocalDataSource.getAuthenticationToken()
}