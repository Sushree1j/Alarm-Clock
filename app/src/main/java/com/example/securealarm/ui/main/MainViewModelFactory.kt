package com.example.securealarm.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.securealarm.data.AlarmRepository

class MainViewModelFactory(private val repository: AlarmRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
