package com.rivaldofez.storymessage.data

import android.provider.ContactsContract.Data
import androidx.datastore.preferences.preferencesDataStore
import com.rivaldofez.storymessage.data.local.UserDataLocalDataSource
import com.rivaldofez.storymessage.data.remote.ApiService
import com.rivaldofez.storymessage.util.DataDummy
import com.rivaldofez.storymessage.utils.CoroutineTestRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner


@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class UserDataRepositoryTest {
    @get:Rule
    var coroutineTestRule = CoroutineTestRule()

    @Mock
    private lateinit var userDataLocalDataSource: UserDataLocalDataSource

    @Mock
    private lateinit var apiService: ApiService
    private lateinit var userDataRepository: UserDataRepository

    private val dummyName = "Name"
    private val dummyEmail = "mail@gmail.com"
    private val dummyPassword = "password"
    private val dummyToken = "authentication_token"

    @Before
    fun setup(){
        userDataRepository = UserDataRepository(apiService, userDataLocalDataSource)
    }

    @Test
    fun `User Login Successfully`(): Unit = runTest {
        val expectedResponse = DataDummy.generateDummyLoginResponse()

        `when`(apiService.userLogin(dummyEmail, dummyPassword)).thenReturn(expectedResponse)

        userDataRepository.userLogin(dummyEmail, dummyPassword).collect { resultResponse ->
            Assert.assertTrue(resultResponse.isSuccess)
            Assert.assertFalse(resultResponse.isFailure)

            resultResponse.onSuccess { actualResponse ->
                Assert.assertNotNull(actualResponse)
                Assert.assertEquals(expectedResponse, actualResponse)
            }

            resultResponse.onFailure { actualResponse ->
                Assert.assertNull(actualResponse)
            }
        }
    }

    @Test
    fun `User Login failed - with exception`(): Unit = runTest {
        `when`(apiService.userLogin(dummyEmail, dummyPassword)).then { throw Exception() }

        userDataRepository.userLogin(dummyEmail, dummyPassword).collect { resultResponse ->
            Assert.assertTrue(resultResponse.isFailure)
            Assert.assertFalse(resultResponse.isSuccess)

            resultResponse.onFailure { actualResponse ->
                Assert.assertNotNull(actualResponse)
            }
        }
    }

    @Test
    fun `User register successfully`(): Unit = runTest {
        val expectedResponse = DataDummy.generateDummyRegisterResponse()

        `when`(apiService.userRegister(dummyName, dummyEmail, dummyPassword)).thenReturn(
            expectedResponse
        )

        userDataRepository.userRegister(dummyName, dummyEmail, dummyPassword).collect { result ->
            Assert.assertTrue(result.isSuccess)
            Assert.assertFalse(result.isFailure)

            result.onSuccess { actualResponse ->
                Assert.assertNotNull(actualResponse)
                Assert.assertEquals(expectedResponse, actualResponse)
            }

            result.onFailure {
                Assert.assertNull(it)
            }
        }
    }

    @Test
    fun `User Register failed - with exception`(): Unit = runTest {
        `when`(apiService.userRegister(dummyName, dummyEmail, dummyPassword)).then { throw Exception() }

        userDataRepository.userRegister(dummyName, dummyEmail, dummyPassword).collect { resultResponse ->
            Assert.assertTrue(resultResponse.isFailure)
            Assert.assertFalse(resultResponse.isSuccess)

            resultResponse.onFailure { actualResponse ->
                Assert.assertNotNull(actualResponse)
            }
        }
    }

    @Test
    fun `Save authentication token successfully`() = runTest {
        userDataRepository.saveAuthenticationToken(dummyToken)
        Mockito.verify(userDataLocalDataSource).saveAuthenticationToken(dummyToken)
    }

    @Test
    fun `Get authentication token successfully`() = runTest {
        val expectedToken = flowOf(dummyToken)

        `when`(userDataLocalDataSource.getAuthenticationToken()).thenReturn(expectedToken)

        userDataLocalDataSource.getAuthenticationToken().collect { actualToken ->
            Assert.assertNotNull(actualToken)
            Assert.assertEquals(dummyToken, actualToken)
        }

        Mockito.verify(userDataLocalDataSource).getAuthenticationToken()
    }
}