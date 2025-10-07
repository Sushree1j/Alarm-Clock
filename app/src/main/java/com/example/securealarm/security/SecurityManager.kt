package com.example.securealarm.security

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import kotlin.random.Random

private const val KEY_ALIAS = "secure_alarm_aes_key"
private const val ANDROID_KEYSTORE = "AndroidKeyStore"

class SecurityManager(context: Context) {

    private val keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }

    private val applicationContext = context.applicationContext

    init {
        getOrCreateKey()
    }

    fun encrypt(plaintext: ByteArray): EncryptedPayload {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey())
        val iv = cipher.iv
        val encrypted = cipher.doFinal(plaintext)
        return EncryptedPayload(
            cipherText = encrypted,
            initializationVector = iv
        )
    }

    fun decrypt(payload: EncryptedPayload): ByteArray {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val spec = GCMParameterSpec(128, payload.initializationVector)
        cipher.init(Cipher.DECRYPT_MODE, getOrCreateKey(), spec)
        return cipher.doFinal(payload.cipherText)
    }

    fun generateSalt(): ByteArray {
        val salt = ByteArray(32)
        Random.nextBytes(salt)
        return salt
    }

    private fun getOrCreateKey(): SecretKey {
        val existing = keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry
        if (existing != null) {
            return existing.secretKey
        }
        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
        val parameterSpec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setUserAuthenticationRequired(false)
            .build()
        keyGenerator.init(parameterSpec)
        return keyGenerator.generateKey()
    }
}

class EncryptedPayload(
    val cipherText: ByteArray,
    val initializationVector: ByteArray
)
