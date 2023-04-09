package com.rivaldofez.storymessage.page.register

import com.rivaldofez.storymessage.data.UserDataRepository
import com.rivaldofez.storymessage.data.remote.response.RegisterResponse
import com.rivaldofez.storymessage.utils.DataDummy
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

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class RegisterViewModelTest {

    @Mock
    private lateinit var userDataRepository: UserDataRepository
    private lateinit var registerViewModel: RegisterViewModel

    private val dummyRegisterResponse = DataDummy.generateDummyRegisterResponse()
    private val dummyName = "Rivaldo Fernandes"
    private val dummyEmail = "rivaldofez@gmail.com"
    private val dummyPassword = "password"

    @Before
    fun setup() {
        registerViewModel = RegisterViewModel(userDataRepository)
    }

    @Test
    fun `Registration successfully - result success`(): Unit = runTest {
        val expectedResponse = flowOf(Result.success(dummyRegisterResponse))

        Mockito.`when`(registerViewModel.registerUser(dummyName, dummyEmail, dummyPassword)).thenReturn(
            expectedResponse
        )

        registerViewModel.registerUser(dummyName, dummyEmail, dummyPassword).collect { resultResponse ->

            assertTrue(resultResponse.isSuccess)
            assertFalse(resultResponse.isFailure)

            resultResponse.onSuccess { actualResponse ->
                assertNotNull(actualResponse)
                assertSame(dummyRegisterResponse, actualResponse)
            }
        }

        Mockito.verify(userDataRepository).userRegister(dummyName, dummyEmail, dummyPassword)
    }

    @Test
    fun `Registration failed - result with exception`(): Unit = runTest {
        val expectedResponse: Flow<Result<RegisterResponse>> =
            flowOf(Result.failure(Exception("failed")))

        Mockito.`when`(registerViewModel.registerUser(dummyName, dummyEmail, dummyPassword)).thenReturn(
            expectedResponse
        )

        registerViewModel.registerUser(dummyName, dummyEmail, dummyPassword).collect { resultResponse ->

            assertFalse(resultResponse.isSuccess)
            assertTrue(resultResponse.isFailure)

            resultResponse.onFailure { actualResponse ->
                assertNotNull(actualResponse)
            }
        }
    }

}