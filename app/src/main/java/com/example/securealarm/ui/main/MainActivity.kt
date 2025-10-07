package com.example.securealarm.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.securealarm.R
import com.example.securealarm.SecureAlarmApplication
import com.example.securealarm.data.local.AlarmEntity
import com.example.securealarm.ui.setup.AlarmSetupActivity
import com.example.securealarm.ui.theme.SecureAlarmTheme
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory((application as SecureAlarmApplication).container.alarmRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SecureAlarmTheme {
                MainRoute(
                    viewModel = viewModel,
                    onCreateAlarm = { startActivity(Intent(this, AlarmSetupActivity::class.java)) }
                )
            }
        }
    }
}

@Composable
private fun MainRoute(
    viewModel: MainViewModel,
    onCreateAlarm: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    MainScreen(state = state, onCreateAlarm = onCreateAlarm)
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun MainScreen(
    state: MainUiState,
    onCreateAlarm: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = stringResource(id = R.string.app_name)) })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateAlarm) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_alarm),
                    contentDescription = stringResource(id = R.string.create_alarm)
                )
            }
        }
    ) { paddingValues ->
        when (state) {
            MainUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            MainUiState.Empty -> {
                Box(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = stringResource(id = R.string.no_alarms), style = MaterialTheme.typography.bodyLarge)
                }
            }
            is MainUiState.HasAlarms -> {
                LazyColumn(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.alarms, key = { it.id }) { alarm ->
                        AlarmCard(alarm = alarm)
                    }
                    item { Spacer(modifier = Modifier.padding(bottom = 64.dp)) }
                }
            }
        }
    }
}

@Composable
private fun AlarmCard(alarm: AlarmEntity) {
    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")
    val dateTime = Instant.ofEpochMilli(alarm.triggerAtMillis).atZone(ZoneId.systemDefault())
    val authLabel = stringResource(id = R.string.authentication_prefix, alarm.authMethod.name)
    val contentDescriptionText = "${dateTime.format(timeFormatter)}, $authLabel"
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .semantics {
                contentDescription = contentDescriptionText
            },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = dateTime.format(timeFormatter), style = MaterialTheme.typography.headlineMedium)
            alarm.label?.let {
                Text(text = it, style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier.padding(4.dp))
            Text(text = authLabel, style = MaterialTheme.typography.bodySmall)
        }
    }
}
