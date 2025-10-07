package com.example.securealarm.security

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthenticationManagerInstrumentedTest {

    @Test
    fun createAndVerifyPinCredential() {
        val context: Context = ApplicationProvider.getApplicationContext()
        val securityManager = SecurityManager(context)
        val authenticationManager = AuthenticationManager(securityManager)

        val encrypted = authenticationManager.createCredential(AuthMethod.PIN, "1234")

        assertTrue(authenticationManager.verifyCredential(AuthMethod.PIN, encrypted, "1234"))
        assertFalse(authenticationManager.verifyCredential(AuthMethod.PIN, encrypted, "9999"))
    }
}
