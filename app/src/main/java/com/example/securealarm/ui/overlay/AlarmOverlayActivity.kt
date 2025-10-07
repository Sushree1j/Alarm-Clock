package com.example.securealarm.ui.overlay

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.securealarm.R
import com.example.securealarm.SecureAlarmApplication
import com.example.securealarm.alarm.AlarmConstants
import com.example.securealarm.data.local.AlarmEntity
import com.example.securealarm.service.AlarmService
import com.example.securealarm.ui.dismiss.AlarmDismissalActivity
import com.example.securealarm.ui.theme.SecureAlarmTheme
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.StateFlow

class AlarmOverlayActivity : ComponentActivity() {

    private val viewModel: AlarmOverlayViewModel by viewModels {
        AlarmOverlayViewModelFactory((application as SecureAlarmApplication).container.alarmRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val alarmId = intent.getLongExtra(AlarmConstants.EXTRA_ALARM_ID, -1L)
        if (alarmId <= 0L) {
            finish()
            return
        }
        viewModel.load(alarmId)
        setContent {
            SecureAlarmTheme {
                OverlayRoute(
                    stateFlow = viewModel.state,
                    alarmId = alarmId,
                    onDismiss = { openDismissal(alarmId) },
                    onSnooze = { requestSnooze(alarmId) }
                )
            }
        }
    }

    private fun openDismissal(alarmId: Long) {
        val intent = Intent(this, AlarmDismissalActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(AlarmConstants.EXTRA_ALARM_ID, alarmId)
        }
        startActivity(intent)
    }

    private fun requestSnooze(alarmId: Long) {
        val serviceIntent = Intent(this, AlarmService::class.java).apply {
            action = AlarmConstants.ACTION_SNOOZE_REQUEST
            putExtra(AlarmConstants.EXTRA_ALARM_ID, alarmId)
        }
    ContextCompat.startForegroundService(this, serviceIntent)
        finish()
    }
}

@Composable
private fun OverlayRoute(
    stateFlow: StateFlow<AlarmOverlayState>,
    alarmId: Long,
    onDismiss: () -> Unit,
    onSnooze: () -> Unit
) {
    val state by stateFlow.collectAsState()
    Surface(modifier = Modifier.fillMaxSize()) {
        when (val current = state) {
            AlarmOverlayState.Loading -> Loading()
            is AlarmOverlayState.Error -> Error(message = current.message)
            is AlarmOverlayState.Data -> AlarmOverlayContent(alarm = current.alarm, onDismiss = onDismiss, onSnooze = onSnooze)
        }
    }
}

@Composable
private fun AlarmOverlayContent(alarm: AlarmEntity, onDismiss: () -> Unit, onSnooze: () -> Unit) {
    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")
    val dateTime = Instant.ofEpochMilli(alarm.triggerAtMillis).atZone(ZoneId.systemDefault())
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically)
    ) {
        Text(text = stringResource(R.string.alarm_active_title), style = MaterialTheme.typography.headlineLarge)
        Text(text = dateTime.format(timeFormatter), style = MaterialTheme.typography.displayMedium)
        alarm.label?.let {
            Text(text = it, style = MaterialTheme.typography.bodyLarge)
        }
        val dismissDescription = stringResource(R.string.dismiss_alarm_action_description)
        Button(
            onClick = onDismiss,
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = dismissDescription }
        ) {
            Text(text = stringResource(R.string.dismiss))
        }
        val snoozeDescription = stringResource(R.string.snooze_alarm_action_description)
        Button(
            onClick = onSnooze,
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = snoozeDescription }
        ) {
            Text(text = stringResource(R.string.snooze))
        }
    }
}

@Composable
private fun Loading() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun Error(message: String) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = message)
    }
}
