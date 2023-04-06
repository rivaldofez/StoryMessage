package com.rivaldofez.storymessage.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.rivaldofez.storymessage.data.local.entity.StoryEntity
import com.rivaldofez.storymessage.data.local.room.StoryDatabase
import com.rivaldofez.storymessage.data.remote.ApiService
import com.rivaldofez.storymessage.data.remote.StoryRemoteMediator
import com.rivaldofez.storymessage.data.remote.response.AddStoryResponse
import com.rivaldofez.storymessage.data.remote.response.StoriesResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@ExperimentalPagingApi
class StoryRepository @Inject constructor(
    private val apiService: ApiService,
    private val storyDatabase: StoryDatabase
) {

//    suspend fun getStories(token: String, page: Int?, size: Int?): Flow<Result<StoriesResponse>> = flow{
//        try {
//            val bearerToken = "Bearer $token"
//            val response = apiService.getStories(token = bearerToken, page = page, size = size)
//            emit(Result.success(response))
//        } catch (e: Exception){
//            e.printStackTrace()
//            emit(Result.failure(e))
//        }
//    }.flowOn(Dispatchers.IO)

    fun getStories(token: String): Flow<PagingData<StoryEntity>> {
        val bearerToken = "Bearer $token"
        return Pager(
            config = PagingConfig(pageSize = 15),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, bearerToken),
            pagingSourceFactory = { storyDatabase.storyDao().getAllStories() }
        ).flow
    }


    suspend fun addStory(token: String,
                         file: MultipartBody.Part,
                         description: RequestBody,
                         lat: RequestBody?,
                         lon: RequestBody?
    ) : Flow<Result<AddStoryResponse>> = flow {
        try {
            val bearerToken = "Bearer $token"
            val response = apiService.addStory(token = bearerToken, file = file, description = description, lat = lat, lon = lon)
            emit(Result.success(response))
        } catch (e: Exception){
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }
}