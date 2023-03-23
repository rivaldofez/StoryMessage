package com.rivaldofez.storymessage.data.remote

import com.rivaldofez.storymessage.data.remote.response.StoriesResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class StoryRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun getStories(token: String, page: Int?, size: Int?): Flow<Result<StoriesResponse>> = flow{
        try {
            val bearerToken = "Bearer $token"
            val response = apiService.getStories(token = bearerToken, page = page, size = size)
            emit(Result.success(response))
        } catch (e: Exception){
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)
}