package com.rivaldofez.storymessage.page.maps

import androidx.paging.ExperimentalPagingApi
import com.rivaldofez.storymessage.data.StoryRepository
import com.rivaldofez.storymessage.data.UserDataRepository
import com.rivaldofez.storymessage.data.remote.response.StoriesResponse
import com.rivaldofez.storymessage.util.DataDummy
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalPagingApi
@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MapsViewModelTest {

    @Mock
    private lateinit var userDataRepository: UserDataRepository

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var mapsViewModel: MapsViewModel

    private val dummyStoriesResponse = DataDummy.generateDummyStoriesResponse()
    private val dummyToken = "authentication_token"

    @Before
    fun setup() {
        mapsViewModel = MapsViewModel(userDataRepository, storyRepository)
    }

    @Test
    fun `Get story with location successfully - result success`(): Unit = runTest {

        val expectedResponse = flowOf(Result.success(dummyStoriesResponse))

        Mockito.`when`(mapsViewModel.getStoriesWithLocation(dummyToken)).thenReturn(expectedResponse)

        mapsViewModel.getStoriesWithLocation(dummyToken).collect { actualResponse ->

            assertTrue(actualResponse.isSuccess)
            assertFalse(actualResponse.isFailure)

            actualResponse.onSuccess { storiesResponse ->
                assertNotNull(storiesResponse)
                assertSame(storiesResponse, dummyStoriesResponse)
            }
        }

        Mockito.verify(storyRepository).getStoriesWithLocation(dummyToken)
    }

    @Test
    fun `Get story with location failed - result failure with exception`(): Unit = runTest {

        val expectedResponse: Flow<Result<StoriesResponse>> =
            flowOf(Result.failure(Exception("Failed")))

        Mockito.`when`(mapsViewModel.getStoriesWithLocation(dummyToken)).thenReturn(expectedResponse)

        mapsViewModel.getStoriesWithLocation(dummyToken).collect { actualResponse ->

            assertFalse(actualResponse.isSuccess)
            assertTrue(actualResponse.isFailure)

            actualResponse.onFailure {
                assertNotNull(it)
            }
        }

        Mockito.verify(storyRepository).getStoriesWithLocation(dummyToken)
    }
}