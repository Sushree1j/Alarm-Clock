package com.example.securealarm.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.securealarm.data.AlarmRepository
import com.example.securealarm.data.local.AlarmEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class MainViewModel(private val repository: AlarmRepository) : ViewModel() {

    val uiState: StateFlow<MainUiState> = repository.observeAlarms()
        .map { alarms ->
            if (alarms.isEmpty()) MainUiState.Empty else MainUiState.HasAlarms(alarms)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MainUiState.Loading
        )
}

sealed interface MainUiState {
    data object Loading : MainUiState
    data object Empty : MainUiState
    data class HasAlarms(val alarms: List<AlarmEntity>) : MainUiState
}
