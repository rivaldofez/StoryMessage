package com.rivaldofez.storymessage.utils

import com.rivaldofez.storymessage.data.local.entity.StoryEntity
import com.rivaldofez.storymessage.data.remote.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

object DataDummy {
    fun generateDummyStoriesResponse(): StoriesResponse {
        val error = false
        val message = "Stories fetched successfully"
        val listStory = mutableListOf<StoryResponse>()

        for (i in 0 until 10){
            val story = StoryResponse(
                id = "story-FvU4u0Vp2S3PMsFg",
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-1641623658595_dummy-pic.png",
                createdAt = "2022-01-08T06:34:18.598Z",
                name = "Rivaldo",
                description = "Lorem Ipsum",
                lon = -16.002,
                lat = -10.212
            )

            listStory.add(story)
        }

        return StoriesResponse(listStory, error, message)
    }

    fun generateDummyListStory(): List<StoryEntity> {
        val items = arrayListOf<StoryEntity>()

        for(i in 0 until 10){
            val story = StoryEntity(
                id = "story-FvU4u0Vp2S3PMsFg",
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-1641623658595_dummy-pic.png",
                createdAt = "2022-01-08T06:34:18.598Z",
                name = "Bertrand",
                description = "Lorem Ipsum",
                lon = -16.002,
                lat = -10.212
            )
            items.add(story)
        }

        return items
    }

    fun generateDummyLoginResponse(): LoginResponse {
        val loginResult = LoginResultResponse(
            userId = "user-yj5pc_LARC_AgK61",
            name = "Cornelis",
            token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLXlqNXBjX0xBUkNfQWdLNjEiLCJpYXQiOjE2NDE3OTk5NDl9.flEMaQ7zsdYkxuyGbiXjEDXO8kuDTcI__3UjCwt6R_I"
        )

        return LoginResponse(
            loginResult = loginResult,
            error = false,
            message = "success"
        )
    }

    fun generateDummyRegisterResponse(): RegisterResponse {
        return RegisterResponse(
            error = false,
            message = "success"
        )
    }

    fun generateDummyMultipartFile(): MultipartBody.Part {
        val dummyText = "text"
        return MultipartBody.Part.create(dummyText.toRequestBody())
    }

    fun generateDummyRequestBody(): RequestBody {
        val dummyText = "text"
        return dummyText.toRequestBody()
    }

    fun generateDummyFileUploadResponse(): AddStoryResponse {
        return AddStoryResponse(
            error = false,
            message = "success"
        )
    }
}