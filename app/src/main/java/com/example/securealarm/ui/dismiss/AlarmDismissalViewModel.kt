package com.example.securealarm.ui.dismiss

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.securealarm.data.AlarmRepository
import com.example.securealarm.data.SecurityEventRepository
import com.example.securealarm.data.local.AlarmEntity
import com.example.securealarm.security.AuthMethod
import com.example.securealarm.security.AuthenticationManager
import com.example.securealarm.security.SecurityEventType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmDismissalViewModel(
    private val repository: AlarmRepository,
    private val authenticationManager: AuthenticationManager,
    private val securityEventRepository: SecurityEventRepository
) : ViewModel() {

    private val _state = MutableStateFlow<DismissalState>(DismissalState.Loading)
    val state: StateFlow<DismissalState> = _state

    private var failureCount = 0

    fun load(alarmId: Long) {
        viewModelScope.launch {
            val alarm = repository.getAlarm(alarmId)
            if (alarm != null) {
                _state.value = DismissalState.Data(alarm, failureCount)
            } else {
                _state.value = DismissalState.Error("Alarm not found")
            }
        }
    }

    fun recordFingerprintSuccess() {
        val current = _state.value
        if (current is DismissalState.Data) {
            _state.value = current.copy(failureCount = failureCount)
        }
    }

    suspend fun verify(input: String?): VerificationResult {
        val current = _state.value
        if (current !is DismissalState.Data) {
            return VerificationResult.Error("Alarm unavailable")
        }
        val alarm = current.alarm
        return when (alarm.authMethod) {
            AuthMethod.FINGERPRINT -> VerificationResult.Error("Fingerprint requires biometric prompt")
            else -> {
                val success = authenticationManager.verifyCredential(alarm.authMethod, alarm.authData, input)
                if (success) {
                    VerificationResult.Success(alarm)
                } else {
                    failureCount += 1
                    _state.value = current.copy(failureCount = failureCount)
                    viewModelScope.launch(Dispatchers.IO) {
                        securityEventRepository.recordEvent(
                            alarmId = alarm.id,
                            type = SecurityEventType.FAILED_CREDENTIAL,
                            message = "Attempt $failureCount failed for ${alarm.label ?: "alarm"}"
                        )
                    }
                    VerificationResult.Failure(failureCount)
                }
            }
        }
    }
}

sealed interface DismissalState {
    data object Loading : DismissalState
    data class Data(val alarm: AlarmEntity, val failureCount: Int) : DismissalState
    data class Error(val message: String) : DismissalState
}

sealed interface VerificationResult {
    data class Success(val alarm: AlarmEntity) : VerificationResult
    data class Failure(val attempts: Int) : VerificationResult
    data class Error(val message: String) : VerificationResult
}
