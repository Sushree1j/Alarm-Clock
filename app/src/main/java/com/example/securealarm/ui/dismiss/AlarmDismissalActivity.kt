package com.example.securealarm.ui.dismiss

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.securealarm.SecureAlarmApplication
import com.example.securealarm.R
import com.example.securealarm.alarm.AlarmConstants
import com.example.securealarm.receiver.SecurityAlertReceiver
import com.example.securealarm.security.AuthMethod
import com.example.securealarm.service.AlarmService
import com.example.securealarm.ui.theme.SecureAlarmTheme
import kotlinx.coroutines.launch

class AlarmDismissalActivity : AppCompatActivity() {

    private val viewModel: AlarmDismissalViewModel by viewModels {
        val app = application as SecureAlarmApplication
        AlarmDismissalViewModelFactory(app.container.alarmRepository, app.container.authenticationManager)
    }

    private var biometricPrompt: BiometricPrompt? = null
    private var biometricPromptInfo: BiometricPrompt.PromptInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val alarmId = intent.getLongExtra(AlarmConstants.EXTRA_ALARM_ID, -1L)
        if (alarmId <= 0L) {
            finish()
            return
        }
        viewModel.load(alarmId)
        setupBiometricPrompt()
        setContent {
            SecureAlarmTheme {
                DismissalRoute(
                    viewModel = viewModel,
                    onSuccess = { completeDismissal(alarmId) },
                    onFingerprintRequest = { showBiometricPrompt() },
                    onSecurityAlert = { attempts -> triggerSecurityAlert(alarmId, attempts) }
                )
            }
        }
    }

    private fun completeDismissal(alarmId: Long) {
    val intent = Intent(this, AlarmService::class.java).apply {
            action = AlarmConstants.ACTION_AUTHORIZED_DISMISS
            putExtra(AlarmConstants.EXTRA_ALARM_ID, alarmId)
        }
        ContextCompat.startForegroundService(this, intent)
        finishAffinity()
    }

    private fun setupBiometricPrompt() {
        val executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                viewModel.recordFingerprintSuccess()
                val alarmId = intent.getLongExtra(AlarmConstants.EXTRA_ALARM_ID, -1L)
                if (alarmId > 0) {
                    completeDismissal(alarmId)
                }
            }
        })
        biometricPromptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Authenticate to dismiss")
            .setSubtitle("Fingerprint required")
            .setNegativeButtonText("Cancel")
            .build()
    }

    private fun showBiometricPrompt() {
        val manager = BiometricManager.from(this)
        val canAuthenticate = manager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)
        if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
            biometricPromptInfo?.let { info ->
                biometricPrompt?.authenticate(info)
            }
        }
    }

    private fun triggerSecurityAlert(alarmId: Long, attempts: Int) {
        val alertIntent = Intent(AlarmConstants.ACTION_SECURITY_ALERT).apply {
            setPackage(packageName)
            putExtra(AlarmConstants.EXTRA_ALARM_ID, alarmId)
            putExtra(SecurityAlertReceiver.EXTRA_ATTEMPTS, attempts)
        }
        sendBroadcast(alertIntent)
    }
}

@Composable
private fun DismissalRoute(
    viewModel: AlarmDismissalViewModel,
    onSuccess: () -> Unit,
    onFingerprintRequest: () -> Unit,
    onSecurityAlert: (Int) -> Unit
) {
    val state by viewModel.state.collectAsState()
    Surface(modifier = Modifier.fillMaxSize()) {
        when (val current = state) {
            DismissalState.Loading -> Loading()
            is DismissalState.Error -> Error(message = current.message)
            is DismissalState.Data -> DismissalContent(
                state = current,
                viewModel = viewModel,
                onSuccess = onSuccess,
                onFingerprintRequest = onFingerprintRequest,
                onSecurityAlert = onSecurityAlert
            )
        }
    }
}

@Composable
private fun DismissalContent(
    state: DismissalState.Data,
    viewModel: AlarmDismissalViewModel,
    onSuccess: () -> Unit,
    onFingerprintRequest: () -> Unit,
    onSecurityAlert: (Int) -> Unit
) {
    var input by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(R.string.authenticate_to_dismiss))
        when (state.alarm.authMethod) {
            AuthMethod.FINGERPRINT -> {
                val fingerprintDescription = stringResource(R.string.fingerprint_button_description)
                Button(
                    onClick = onFingerprintRequest,
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = fingerprintDescription }
                ) {
                    Text(text = stringResource(R.string.use_fingerprint))
                }
            }
            else -> {
                val credentialDescription = stringResource(R.string.credential_input_label)
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    label = { Text(text = credentialDescription) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = credentialDescription },
                    keyboardOptions = when (state.alarm.authMethod) {
                        AuthMethod.PIN -> KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                        AuthMethod.PASSWORD -> KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password)
                        AuthMethod.PATTERN -> KeyboardOptions.Default
                        AuthMethod.FINGERPRINT -> KeyboardOptions.Default
                    },
                    visualTransformation = when (state.alarm.authMethod) {
                        AuthMethod.PIN, AuthMethod.PASSWORD -> PasswordVisualTransformation()
                        else -> VisualTransformation.None
                    }
                )
                val verifyDescription = stringResource(R.string.verify_credential_action_description)
                Button(
                    onClick = {
                        scope.launch {
                            when (val result = viewModel.verify(input)) {
                                is VerificationResult.Success -> onSuccess()
                                is VerificationResult.Failure -> {
                                    message = context.getString(R.string.incorrect_credential_attempts, result.attempts)
                                    if (result.attempts == 3) {
                                        onSecurityAlert(result.attempts)
                                    }
                                }
                                is VerificationResult.Error -> message = result.message
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = verifyDescription }
                ) {
                    Text(text = stringResource(R.string.verify))
                }
            }
        }
        message?.let { Text(text = it) }
        if (state.failureCount >= 3) {
            Text(text = stringResource(R.string.multiple_failed_attempts))
        }
    }
}

@Composable
private fun Loading() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun Error(message: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = message)
    }
}
