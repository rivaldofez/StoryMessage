package com.rivaldofez.storymessage.data

import com.rivaldofez.storymessage.data.local.UserDataLocalDataSource
import com.rivaldofez.storymessage.data.remote.ApiService
import com.rivaldofez.storymessage.data.remote.response.LoginResponse
import com.rivaldofez.storymessage.data.remote.response.RegisterResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class UserDataRepository @Inject constructor(
    private val apiService: ApiService,
    private val userDataLocalDataSource: UserDataLocalDataSource) {

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
        userDataLocalDataSource.saveAuthenticationToken(token)
    }

    suspend fun removeAuthenticationToken(){
        userDataLocalDataSource.removeAuthenticationToken()
    }

    suspend fun saveThemeSetting(theme: String){
        userDataLocalDataSource.saveThemeSetting(theme)
    }

    fun getAuthenticationToken(): Flow<String?> = userDataLocalDataSource.getAuthenticationToken()

    fun getThemeSetting(): Flow<String?> = userDataLocalDataSource.getThemeSetting()

}