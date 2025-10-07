package com.example.securealarm.ui.setup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.securealarm.R
import com.example.securealarm.SecureAlarmApplication
import com.example.securealarm.alarm.AlarmScheduler
import com.example.securealarm.data.AlarmRepository
import com.example.securealarm.data.local.AlarmEntity
import com.example.securealarm.security.AuthMethod
import com.example.securealarm.security.AuthenticationManager
import com.example.securealarm.ui.theme.SecureAlarmTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.ZoneId

class AlarmSetupActivity : ComponentActivity() {

    private val container by lazy { (application as SecureAlarmApplication).container }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SecureAlarmTheme {
                AlarmSetupScreen(
                    repository = container.alarmRepository,
                    authenticationManager = container.authenticationManager,
                    scheduler = container.alarmScheduler,
                    onFinished = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AlarmSetupScreen(
    repository: AlarmRepository,
    authenticationManager: AuthenticationManager,
    scheduler: AlarmScheduler,
    onFinished: () -> Unit
) {
    var label by rememberSaveable { mutableStateOf("") }
    var repeatOption by rememberSaveable { mutableStateOf(RepeatOption.Once) }
    var authMethod by rememberSaveable { mutableStateOf(AuthMethod.PIN) }
    var secret by rememberSaveable { mutableStateOf("") }
    var showTimePicker by remember { mutableStateOf(false) }
    var selectedHour by rememberSaveable { mutableStateOf(7) }
    var selectedMinute by rememberSaveable { mutableStateOf(0) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSaving by remember { mutableStateOf(false) }

    val timePickerState = rememberTimePickerState(selectedHour, selectedMinute, false)

    LaunchedEffect(showTimePicker) {
        if (showTimePicker) {
            timePickerState.hour = selectedHour
            timePickerState.minute = selectedMinute
        }
    }

    val scope = rememberCoroutineScope()

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = stringResource(R.string.create_alarm))
            val timePickerDescription = stringResource(R.string.time_picker_action_description)
            Text(
                text = String.format("%02d:%02d", selectedHour, selectedMinute),
                modifier = Modifier
                    .clickable { showTimePicker = true }
                    .semantics { contentDescription = timePickerDescription }
                    .padding(vertical = 8.dp)
            )
            OutlinedTextField(
                value = label,
                onValueChange = { label = it },
                label = { Text(stringResource(R.string.label_optional)) },
                modifier = Modifier.fillMaxWidth()
            )
            RepeatPicker(selected = repeatOption) { repeatOption = it }
            AuthMethodPicker(method = authMethod, onMethodChange = {
                authMethod = it
                if (it == AuthMethod.FINGERPRINT) {
                    secret = ""
                }
            })
            if (authMethod != AuthMethod.FINGERPRINT) {
                val credentialDescription = stringResource(R.string.credential_input_label)
                OutlinedTextField(
                    value = secret,
                    onValueChange = { secret = it },
                    label = { Text(credentialDescription) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = credentialDescription },
                    keyboardOptions = when (authMethod) {
                        AuthMethod.PIN -> KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                        AuthMethod.PASSWORD -> KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password)
                        AuthMethod.PATTERN -> KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text)
                        AuthMethod.FINGERPRINT -> KeyboardOptions.Default
                    },
                    visualTransformation = when (authMethod) {
                        AuthMethod.PIN, AuthMethod.PASSWORD -> PasswordVisualTransformation()
                        else -> androidx.compose.ui.text.input.VisualTransformation.None
                    }
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            val saveButtonDescription = stringResource(R.string.save_alarm_action_description)
            Button(
                onClick = {
                    if (isSaving) return@Button
                    val validation = validateInputs(authMethod, secret)
                    if (validation != null) {
                        errorMessage = validation
                        return@Button
                    }
                    isSaving = true
                    errorMessage = null
                    scope.launch {
                        val result = runCatching {
                            withContext(Dispatchers.IO) {
                                saveAlarm(
                                    repository = repository,
                                    authenticationManager = authenticationManager,
                                    scheduler = scheduler,
                                    label = label,
                                    authMethod = authMethod,
                                    secret = secret.takeIf { authMethod != AuthMethod.FINGERPRINT },
                                    repeatOption = repeatOption,
                                    hour = selectedHour,
                                    minute = selectedMinute
                                )
                            }
                        }
                        result.onSuccess {
                            onFinished()
                        }.onFailure { throwable ->
                            errorMessage = throwable.message ?: "Couldn't save alarm"
                        }
                        isSaving = false
                    }
                },
                enabled = !isSaving,
                modifier = Modifier.semantics {
                    contentDescription = saveButtonDescription
                }
            ) {
                Text(text = if (isSaving) stringResource(R.string.saving) else stringResource(R.string.save_alarm))
            }
            errorMessage?.let { Text(text = it) }
        }
    }

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedHour = timePickerState.hour
                        selectedMinute = timePickerState.minute
                        showTimePicker = false
                    }
                ) { Text(stringResource(android.R.string.ok)) }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text(stringResource(android.R.string.cancel)) }
            },
            text = {
                TimePicker(
                    state = timePickerState,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        )
    }
}

private suspend fun saveAlarm(
    repository: AlarmRepository,
    authenticationManager: AuthenticationManager,
    scheduler: AlarmScheduler,
    label: String,
    authMethod: AuthMethod,
    secret: String?,
    repeatOption: RepeatOption,
    hour: Int,
    minute: Int
) {
    val triggerAt = computeNextTrigger(hour, minute)
    val authData = authenticationManager.createCredential(authMethod, secret)
    val repeatPattern = when (repeatOption) {
        RepeatOption.Once -> null
        RepeatOption.Daily -> "DAILY"
    }
    val alarm = AlarmEntity(
        triggerAtMillis = triggerAt,
        repeatPattern = repeatPattern,
        label = label.ifBlank { null },
        soundUri = null,
        authMethod = authMethod,
        authData = authData,
        snoozeMinutes = 10
    )
    val id = repository.createAlarm(alarm)
    scheduler.schedule(alarm.copy(id = id))
}

private fun computeNextTrigger(hour: Int, minute: Int): Long {
    val now = LocalDateTime.now()
    var target = now.withHour(hour).withMinute(minute).withSecond(0).withNano(0)
    if (target.isBefore(now)) {
        target = target.plusDays(1)
    }
    return target.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
}

private fun validateInputs(method: AuthMethod, secret: String): String? {
    return when (method) {
        AuthMethod.PIN -> if (secret.length in 4..6 && secret.all { it.isDigit() }) null else "PIN must be 4-6 digits"
        AuthMethod.PASSWORD -> if (secret.length >= 6) null else "Password must be at least 6 characters"
        AuthMethod.PATTERN -> if (secret.length >= 4) null else "Pattern description too short"
        AuthMethod.FINGERPRINT -> null
    }
}

private enum class RepeatOption { Once, Daily }

@Composable
private fun RepeatPicker(selected: RepeatOption, onSelect: (RepeatOption) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = stringResource(R.string.repeat_label))
        RepeatOption.values().forEach { option ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = option.name)
                RadioButton(selected = selected == option, onClick = { onSelect(option) })
            }
        }
    }
}

@Composable
private fun AuthMethodPicker(method: AuthMethod, onMethodChange: (AuthMethod) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = stringResource(R.string.authentication_label))
        AuthMethod.values().forEach { value ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = value.name.lowercase().replaceFirstChar { it.titlecase() })
                RadioButton(selected = method == value, onClick = { onMethodChange(value) })
            }
        }
    }
}
