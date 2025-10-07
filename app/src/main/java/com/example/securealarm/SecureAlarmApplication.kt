package com.example.securealarm

import android.app.Application
import com.example.securealarm.alarm.AlarmScheduler
import com.example.securealarm.data.AlarmRepository
import com.example.securealarm.data.SecurityEventRepository
import com.example.securealarm.data.local.AlarmDatabase
import com.example.securealarm.security.AuthenticationManager
import com.example.securealarm.security.SecurityManager
import timber.log.Timber

class SecureAlarmApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        if ((applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
            Timber.plant(Timber.DebugTree())
        }
        container = AppContainerImpl(this)
    }
}

interface AppContainer {
    val alarmRepository: AlarmRepository
    val authenticationManager: AuthenticationManager
    val securityManager: SecurityManager
    val alarmScheduler: AlarmScheduler
    val securityEventRepository: SecurityEventRepository
}

private class AppContainerImpl(app: Application) : AppContainer {

    private val database by lazy { AlarmDatabase.getInstance(app) }

    override val securityManager: SecurityManager by lazy { SecurityManager(app) }

    override val authenticationManager: AuthenticationManager by lazy {
        AuthenticationManager(securityManager)
    }

    override val alarmRepository: AlarmRepository by lazy {
        AlarmRepository(database.alarmDao())
    }

    override val alarmScheduler: AlarmScheduler by lazy { AlarmScheduler(app) }

    override val securityEventRepository: SecurityEventRepository by lazy {
        SecurityEventRepository(database.securityEventDao())
    }
}
