package com.rivaldofez.storymessage.page.maps

import androidx.paging.ExperimentalPagingApi
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.filters.MediumTest
import com.rivaldofez.storymessage.data.remote.ApiConfig
import com.rivaldofez.storymessage.utils.JsonConverter
import com.rivaldofez.storymessage.utils.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.rivaldofez.storymessage.R

@ExperimentalPagingApi
@MediumTest
@HiltAndroidTest
class MapsFragmentTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private val mockWebServer = MockWebServer()

    @Before
    fun setup() {
        mockWebServer.start(8080)
        ApiConfig.API_BASE_URL_MOCK = "http://127.0.0.1:8080/"
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun launchLocationFragment_Success() {
        launchFragmentInHiltContainer<MapsFragment>()

        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(JsonConverter.readStringFromFile("success_response.json"))
        mockWebServer.enqueue(mockResponse)

        Espresso.onView(ViewMatchers.withId(R.id.map))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}