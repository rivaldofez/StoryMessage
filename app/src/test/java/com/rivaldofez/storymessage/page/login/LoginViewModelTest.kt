package com.rivaldofez.storymessage.page.login

import com.rivaldofez.storymessage.data.UserDataRepository
import com.rivaldofez.storymessage.data.remote.response.LoginResponse
import com.rivaldofez.storymessage.util.DataDummy
import com.rivaldofez.storymessage.utils.CoroutineTestRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
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
class LoginViewModelTest {

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    @Mock
    private lateinit var userDataRepository: UserDataRepository
    private lateinit var loginViewModel: LoginViewModel

    private val dummyLoginResponse = DataDummy.generateDummyLoginResponse()
    private val dummyToken = "authentication_token"
    private val dummyEmail = "email@mail.com"
    private val dummyPassword = "password"

    @Before
    fun setup() {
        loginViewModel = LoginViewModel(userDataRepository)
    }

    @Test
    fun `Login successfully - result success`(): Unit = runTest {
        val expectedResponse = flow {
            emit(Result.success(dummyLoginResponse))
        }

        Mockito.`when`(loginViewModel.userLogin(dummyEmail, dummyPassword)).thenReturn(expectedResponse)

        loginViewModel.userLogin(dummyEmail, dummyPassword).collect { result ->

            assertTrue(result.isSuccess)
            assertFalse(result.isFailure)

            result.onSuccess { actualResponse ->
                assertNotNull(actualResponse)
                assertSame(dummyLoginResponse, actualResponse)
            }
        }

        Mockito.verify(userDataRepository).userLogin(dummyEmail, dummyPassword)
    }

    @Test
    fun `Login failed - result failure with exception`(): Unit = runTest {
        val expectedResponse: Flow<Result<LoginResponse>> =
            flowOf(Result.failure(Exception("login failed")))

        Mockito.`when`(loginViewModel.userLogin(dummyEmail, dummyPassword)).thenReturn(expectedResponse)

        loginViewModel.userLogin(dummyEmail, dummyPassword).collect { result ->

            assertFalse(result.isSuccess)
            assertTrue(result.isFailure)

            result.onFailure {
                assertNotNull(it)
            }
        }

        Mockito.verify(userDataRepository).userLogin(dummyEmail, dummyPassword)
    }

    @Test
    fun `Save authentication token successfully`(): Unit = runTest {
        loginViewModel.saveAuthenticationToken(dummyToken)
        Mockito.verify(userDataRepository).saveAuthenticationToken(dummyToken)
    }
}