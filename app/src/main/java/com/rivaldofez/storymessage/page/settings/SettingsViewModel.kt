package com.rivaldofez.storymessage.page.settings

import androidx.lifecycle.ViewModel
import com.rivaldofez.storymessage.data.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository
): ViewModel() {

    fun getThemeSetting(): Flow<String?> = userDataRepository.getThemeSetting()

    suspend fun saveThemeSetting(theme: String) = userDataRepository.saveThemeSetting(theme = theme)
}