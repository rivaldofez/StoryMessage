package com.rivaldofez.storymessage.page.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rivaldofez.storymessage.data.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository
): ViewModel() {

    fun getThemeSetting(): Flow<String?> = userDataRepository.getThemeSetting()

    fun saveThemeSetting(themeId: Int) {
        viewModelScope.launch {
            userDataRepository.saveThemeSetting(themeId = themeId)
        }
    }
}