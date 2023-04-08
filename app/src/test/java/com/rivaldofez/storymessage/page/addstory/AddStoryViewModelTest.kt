package com.rivaldofez.storymessage.page.addstory

import androidx.paging.ExperimentalPagingApi
import com.rivaldofez.storymessage.data.StoryRepository
import com.rivaldofez.storymessage.data.UserDataRepository
import com.rivaldofez.storymessage.data.remote.response.AddStoryResponse
import com.rivaldofez.storymessage.util.DataDummy
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalPagingApi
@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class AddStoryViewModelTest {
    @Mock
    private lateinit var userDataRepository: UserDataRepository

    @Mock
    private lateinit var storyRepository: StoryRepository

    @Mock
    private lateinit var addStoryViewModel: AddStoryViewModel

    private val dummyToken = "authentication_token"
    private val dummyUploadResponse = DataDummy.generateDummyFileUploadResponse()
    private val dummyMultipart = DataDummy.generateDummyMultipartFile()
    private val dummyDescription = DataDummy.generateDummyRequestBody()

    @Before
    fun setup() {
        addStoryViewModel = AddStoryViewModel(userDataRepository, storyRepository)
    }

    @Test
    fun `Get authentication token successfully`() = runTest {
        val expectedToken = flowOf(dummyToken)

        Mockito.`when`(addStoryViewModel.getAuthenticationToken()).thenReturn(expectedToken)

        addStoryViewModel.getAuthenticationToken().collect { actualToken ->
            assertNotNull(actualToken)
            assertEquals(dummyToken, actualToken)
        }

        Mockito.verify(userDataRepository).getAuthenticationToken()
        Mockito.verifyNoInteractions(storyRepository)
    }

    @Test
    fun `Get authentication token successfully but null`() = runTest {
        val expectedToken = flowOf(null)

        Mockito.`when`(addStoryViewModel.getAuthenticationToken()).thenReturn(expectedToken)

        addStoryViewModel.getAuthenticationToken().collect { actualToken ->
            Assert.assertNull(actualToken)
        }

        Mockito.verify(userDataRepository).getAuthenticationToken()
        Mockito.verifyNoInteractions(storyRepository)
    }

    @Test
    fun `Add story successfully`() = runTest {
        val expectedResponse = flowOf(Result.success(dummyUploadResponse))

        Mockito.`when`(
            addStoryViewModel.addStory(
                dummyToken,
                dummyMultipart,
                dummyDescription,
                null,
                null
            )
        ).thenReturn(expectedResponse)

        addStoryViewModel.addStory(dummyToken, dummyMultipart, dummyDescription, null, null)
            .collect { resultResponse ->

                Assert.assertTrue(resultResponse.isSuccess)
                Assert.assertFalse(resultResponse.isFailure)

                resultResponse.onSuccess { actualResponse ->
                    Assert.assertNotNull(actualResponse)
                    Assert.assertSame(dummyUploadResponse, actualResponse)
                }
            }

        Mockito.verify(storyRepository)
            .addStory(dummyToken, dummyMultipart, dummyDescription, null, null)
        Mockito.verifyNoInteractions(userDataRepository)
    }

    @Test
    fun `Add story failed`(): Unit = runTest {
        val expectedResponse: Flow<Result<AddStoryResponse>> =
            flowOf(Result.failure(Exception("failed")))

        Mockito.`when`(
            addStoryViewModel.addStory(
                dummyToken,
                dummyMultipart,
                dummyDescription,
                null,
                null
            )
        ).thenReturn(expectedResponse)

        addStoryViewModel.addStory(dummyToken, dummyMultipart, dummyDescription, null, null)
            .collect { resultResponse ->
                Assert.assertFalse(resultResponse.isSuccess)
                Assert.assertTrue(resultResponse.isFailure)

                resultResponse.onFailure { actualResponse ->
                    Assert.assertNotNull(actualResponse)
                }
            }

    }
}