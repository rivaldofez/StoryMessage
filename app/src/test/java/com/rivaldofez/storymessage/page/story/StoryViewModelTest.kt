package com.rivaldofez.storymessage.page.story

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.rivaldofez.storymessage.data.StoryRepository
import com.rivaldofez.storymessage.data.UserDataRepository
import com.rivaldofez.storymessage.data.local.entity.StoryEntity
import com.rivaldofez.storymessage.utils.CoroutineTestRule
import com.rivaldofez.storymessage.utils.DataDummy.generateDummyListStory
import com.rivaldofez.storymessage.utils.PagedTestDataSource
import com.rivaldofez.storymessage.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@ExperimentalPagingApi
@RunWith(MockitoJUnitRunner::class)
class StoryViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    @Mock
    private lateinit var userDataRepository: UserDataRepository

    private lateinit var storyViewModel: StoryViewModel
    private val dummyToken = "authentication_token"

    @Before
    fun setUp(){
        storyViewModel = StoryViewModel(userDataRepository, storyRepository)
    }

    @Test
    fun `Get all stories failed with zero data`() =  runTest {
        val dummyStories = ArrayList<StoryEntity>()
        val data = PagedTestDataSource.snapshot(dummyStories)

        val stories = MutableLiveData<PagingData<StoryEntity>>()
        stories.value = data

        `when`(storyRepository.getStories(dummyToken)).thenReturn(stories.asFlow())
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DiffCallback,
            updateCallback = noopListUpdateCallback,
            mainDispatcher = coroutinesTestRule.testDispatcher,
            workerDispatcher = coroutinesTestRule.testDispatcher
        )

        val actualStories = storyViewModel.getStories(dummyToken).getOrAwaitValue()
        differ.submitData(actualStories)

        advanceUntilIdle()

        assertNotNull(differ.snapshot())
        assertEquals(0, differ.snapshot().size)
    }

    @Test
    fun `Get all stories successfully`() = runTest {
        val dummyStories = generateDummyListStory()
        val data = PagedTestDataSource.snapshot(dummyStories)

        val stories = MutableLiveData<PagingData<StoryEntity>>()
        stories.value = data

        `when`(storyRepository.getStories(dummyToken)).thenReturn(stories.asFlow())
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DiffCallback,
            updateCallback = noopListUpdateCallback,
            mainDispatcher = coroutinesTestRule.testDispatcher,
            workerDispatcher = coroutinesTestRule.testDispatcher
        )

        val actualStories = storyViewModel.getStories(dummyToken).getOrAwaitValue()
        differ.submitData(actualStories)

        advanceUntilIdle()

        assertNotNull(differ.snapshot())
        assertEquals(dummyStories.size, differ.snapshot().size)
        assertEquals(differ.snapshot().first(), dummyStories.first())
    }


    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }
}