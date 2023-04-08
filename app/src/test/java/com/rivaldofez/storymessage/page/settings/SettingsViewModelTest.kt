package com.rivaldofez.storymessage.page.settings

import com.rivaldofez.storymessage.data.UserDataRepository
import com.rivaldofez.storymessage.utils.CoroutineTestRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class SettingsViewModelTest {

    @get:Rule
    var coroutineTestRule = CoroutineTestRule()

    @Mock
    private lateinit var userDataRepository: UserDataRepository
    private lateinit var settingsViewModel: SettingsViewModel

    private val dummyToken = "authentication_token"

    @Before
    fun setup() {
        settingsViewModel = SettingsViewModel(userDataRepository)
    }

    @Test
    fun `Save authentication token successfully`(): Unit = runTest {
        settingsViewModel.removeAuthenticationToken()
        Mockito.verify(userDataRepository).removeAuthenticationToken()
    }
}