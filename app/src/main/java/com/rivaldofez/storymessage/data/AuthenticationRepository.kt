package com.rivaldofez.storymessage.data

import com.rivaldofez.storymessage.data.local.AuthenticationLocalDataSource
import com.rivaldofez.storymessage.data.remote.ApiService
import com.rivaldofez.storymessage.data.remote.response.LoginResponse
import com.rivaldofez.storymessage.data.remote.response.RegisterResponse
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

    suspend fun userRegister(email: String, password: String, name: String
    ): Flow<Result<RegisterResponse>> = flow {
        try {
            val response = apiService.userRegister(email = email, password = password, name = name)
            emit(Result.success(response))
        } catch (e: Exception){
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun saveAuthenticationToken(token: String){
        authenticationLocalDataSource.saveAuthenticationToken(token)
    }

    suspend fun removeAuthenticationToken(){
        authenticationLocalDataSource.removeAuthenticationToken()
    }

    fun getAuthenticationToken(): Flow<String?> = authenticationLocalDataSource.getAuthenticationToken()
}