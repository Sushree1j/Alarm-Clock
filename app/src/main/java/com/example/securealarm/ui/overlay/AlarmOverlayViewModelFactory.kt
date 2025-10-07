package com.example.securealarm.ui.overlay

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.securealarm.data.AlarmRepository

class AlarmOverlayViewModelFactory(private val repository: AlarmRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlarmOverlayViewModel::class.java)) {
            return AlarmOverlayViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
