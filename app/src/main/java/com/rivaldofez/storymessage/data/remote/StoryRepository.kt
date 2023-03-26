package com.rivaldofez.storymessage.data.remote

import com.rivaldofez.storymessage.data.remote.response.AddStoryResponse
import com.rivaldofez.storymessage.data.remote.response.StoriesResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import okhttp3.RequestBody
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


    suspend fun addStory(token: String, file: MultipartBody.Part, description: RequestBody)
    : Flow<Result<AddStoryResponse>> = flow {
        try {
            val bearerToken = "Bearer $token"
            val response = apiService.addStory(token = bearerToken, file = file, description = description)
            emit(Result.success(response))
        } catch (e: Exception){
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }
}