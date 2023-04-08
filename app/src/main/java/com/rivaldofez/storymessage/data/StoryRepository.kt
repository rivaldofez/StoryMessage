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
import com.rivaldofez.storymessage.util.wrapEspressoIdlingResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@ExperimentalPagingApi
class StoryRepository @Inject constructor(
    private val apiService: ApiService,
    private val storyDatabase: StoryDatabase
) {

    fun getStories(token: String): Flow<PagingData<StoryEntity>> {
        val bearerToken = generateBearerToken(token)
        return Pager(
            config = PagingConfig(pageSize = 15),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, bearerToken),
            pagingSourceFactory = { storyDatabase.storyDao().getAllStories() }
        ).flow
    }

    fun getStoriesWithLocation(token: String): Flow<Result<StoriesResponse>> = flow {
        wrapEspressoIdlingResource {
            try {
                val bearerToken = generateBearerToken(token)
                val response =
                    apiService.getStories(token = bearerToken, size = 30, location = 1, page = null)
                emit(Result.success(response))
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Result.failure(e))
            }
        }
    }


    suspend fun addStory(token: String,
                         file: MultipartBody.Part,
                         description: RequestBody,
                         lat: RequestBody?,
                         lon: RequestBody?
    ) : Flow<Result<AddStoryResponse>> = flow {
        try {
            val bearerToken = generateBearerToken(token)
            val response = apiService.addStory(token = bearerToken, file = file, description = description, lat = lat, lon = lon)
            emit(Result.success(response))
        } catch (e: Exception){
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }

    private fun generateBearerToken(token : String): String = "Bearer $token"
}