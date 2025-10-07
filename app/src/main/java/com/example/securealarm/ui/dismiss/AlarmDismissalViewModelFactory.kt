package com.example.securealarm.ui.dismiss

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.securealarm.data.AlarmRepository
import com.example.securealarm.security.AuthenticationManager

class AlarmDismissalViewModelFactory(
    private val repository: AlarmRepository,
    private val authenticationManager: AuthenticationManager
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlarmDismissalViewModel::class.java)) {
            return AlarmDismissalViewModel(repository, authenticationManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
