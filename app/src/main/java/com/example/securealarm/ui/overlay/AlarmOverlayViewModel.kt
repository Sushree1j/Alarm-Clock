package com.example.securealarm.ui.overlay

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.securealarm.data.AlarmRepository
import com.example.securealarm.data.local.AlarmEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AlarmOverlayViewModel(private val repository: AlarmRepository) : ViewModel() {

    private val _state = MutableStateFlow<AlarmOverlayState>(AlarmOverlayState.Loading)
    val state: StateFlow<AlarmOverlayState> = _state

    fun load(alarmId: Long) {
        viewModelScope.launch {
            val alarm = repository.getAlarm(alarmId)
            _state.value = if (alarm != null) {
                AlarmOverlayState.Data(alarm)
            } else {
                AlarmOverlayState.Error("Alarm not found")
            }
        }
    }
}

sealed interface AlarmOverlayState {
    data object Loading : AlarmOverlayState
    data class Data(val alarm: AlarmEntity) : AlarmOverlayState
    data class Error(val message: String) : AlarmOverlayState
}
