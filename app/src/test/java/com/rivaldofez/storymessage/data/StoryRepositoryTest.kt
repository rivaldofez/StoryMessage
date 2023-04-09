package com.rivaldofez.storymessage.data

import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.ListUpdateCallback
import com.rivaldofez.storymessage.data.local.room.StoryDatabase
import com.rivaldofez.storymessage.data.remote.ApiService
import com.rivaldofez.storymessage.data.remote.response.StoriesResponse
import com.rivaldofez.storymessage.page.story.StoryAdapter
import com.rivaldofez.storymessage.utils.CoroutineTestRule
import com.rivaldofez.storymessage.utils.PagedTestDataSource
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@ExperimentalPagingApi
@RunWith(MockitoJUnitRunner::class)
class StoryRepositoryTest {

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    @Mock
    private lateinit var storyDatabase: StoryDatabase

    @Mock
    private lateinit var apiService: ApiService

    @Mock
    private lateinit var storyRepositoryMock: StoryRepository

    private lateinit var storyRepository: StoryRepository

    private val dummyToken = "authentication_token"
    private val dummyMultipart = com.rivaldofez.storymessage.utils.DataDummy.generateDummyMultipartFile()
    private val dummyDescription = com.rivaldofez.storymessage.utils.DataDummy.generateDummyRequestBody()
    private val dummyStoriesResponse = com.rivaldofez.storymessage.utils.DataDummy.generateDummyStoriesResponse()

    @Before
    fun setup() {
        storyRepository = StoryRepository(apiService, storyDatabase)
    }

    @Test
    fun `Get stories with pager - successfully`() = runTest {
        val dummyStories = com.rivaldofez.storymessage.utils.DataDummy.generateDummyListStory()
        val data = PagedTestDataSource.snapshot(dummyStories)

        val expectedResult = flowOf(data)

        Mockito.`when`(storyRepositoryMock.getStories(dummyToken)).thenReturn(expectedResult)

        storyRepositoryMock.getStories(dummyToken).collect { actualResult ->
            val differ = AsyncPagingDataDiffer(
                diffCallback = StoryAdapter.DiffCallback,
                updateCallback = noopListUpdateCallback,
                mainDispatcher = coroutinesTestRule.testDispatcher,
                workerDispatcher = coroutinesTestRule.testDispatcher
            )
            differ.submitData(actualResult)

            assertNotNull(differ.snapshot())
            assertEquals(
                dummyStoriesResponse.stories.size,
                differ.snapshot().size
            )
        }

    }

    @Test
    fun `Get stories with location - successfully`() = runTest {
        val expectedResult = flowOf(Result.success(dummyStoriesResponse))

        Mockito.`when`(storyRepositoryMock.getStoriesWithLocation(dummyToken)).thenReturn(expectedResult)

        storyRepositoryMock.getStoriesWithLocation(dummyToken).collect { resultResponse ->
            Assert.assertTrue(resultResponse.isSuccess)
            Assert.assertFalse(resultResponse.isFailure)

            resultResponse.onSuccess { actualResponse ->
                Assert.assertNotNull(actualResponse)
                Assert.assertEquals(dummyStoriesResponse, actualResponse)
            }
        }
    }

    @Test
    fun `Get stories with location - with exception`() = runTest {
        val expectedResponse = flowOf<Result<StoriesResponse>>(Result.failure(Exception("failed")))

        Mockito.`when`(storyRepositoryMock.getStoriesWithLocation(dummyToken)).thenReturn(
            expectedResponse
        )

        storyRepositoryMock.getStoriesWithLocation(dummyToken).collect { resultResponse ->
            Assert.assertFalse(resultResponse.isSuccess)
            Assert.assertTrue(resultResponse.isFailure)

            resultResponse.onFailure { actualResponse ->
                Assert.assertNotNull(actualResponse)
            }
        }
    }

    @Test
    fun `Add story - successfully`() = runTest {
        val expectedResponse = com.rivaldofez.storymessage.utils.DataDummy.generateDummyFileUploadResponse()

        Mockito.`when`(
            apiService.addStory(
                dummyToken.generateBearerToken(),
                dummyMultipart,
                dummyDescription,
                null,
                null
            )
        ).thenReturn(expectedResponse)

        storyRepository.addStory(dummyToken, dummyMultipart, dummyDescription, null, null)
            .collect { resultResponse ->
                Assert.assertTrue(resultResponse.isSuccess)
                Assert.assertFalse(resultResponse.isFailure)

                resultResponse.onSuccess { actualResponse ->
                    Assert.assertEquals(expectedResponse, actualResponse)
                }
            }

        Mockito.verify(apiService)
            .addStory(
                dummyToken.generateBearerToken(),
                dummyMultipart,
                dummyDescription,
                null,
                null
            )
        Mockito.verifyNoInteractions(storyDatabase)
    }

    @Test
    fun `Upload image file - throw exception`() = runTest {

        Mockito.`when`(
            apiService.addStory(
                dummyToken.generateBearerToken(),
                dummyMultipart,
                dummyDescription,
                null,
                null
            )
        ).then { throw Exception() }

        storyRepository.addStory(dummyToken, dummyMultipart, dummyDescription, null, null)
            .collect { resultResponse ->
                Assert.assertFalse(resultResponse.isSuccess)
                Assert.assertTrue(resultResponse.isFailure)

                resultResponse.onFailure { actualResponse ->
                    Assert.assertNotNull(actualResponse)
                }
            }

        Mockito.verify(apiService).addStory(
            dummyToken.generateBearerToken(),
            dummyMultipart,
            dummyDescription,
            null,
            null
        )
    }

    private fun String.generateBearerToken(): String {
        return "Bearer $this"
    }

    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }

}