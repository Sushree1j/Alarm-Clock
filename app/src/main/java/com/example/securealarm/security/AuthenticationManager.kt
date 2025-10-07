package com.example.securealarm.security

import android.util.Base64
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.security.MessageDigest

private const val PBKDF_ITERATIONS = 2048

class AuthenticationManager(private val securityManager: SecurityManager) {

    private val json = Json { ignoreUnknownKeys = true }

    fun createCredential(method: AuthMethod, secret: String?): String {
        val record = when (method) {
            AuthMethod.PIN, AuthMethod.PASSWORD -> {
                require(!secret.isNullOrBlank()) { "Secret required for $method" }
                val salt = securityManager.generateSalt()
                val hash = hashSecret(secret, salt)
                CredentialRecord(
                    method = method,
                    salt = encode(salt),
                    hash = encode(hash)
                )
            }
            AuthMethod.PATTERN -> {
                require(!secret.isNullOrBlank()) { "Pattern data required" }
                val salt = securityManager.generateSalt()
                val hash = hashSecret(secret, salt)
                CredentialRecord(
                    method = method,
                    salt = encode(salt),
                    hash = encode(hash)
                )
            }
            AuthMethod.FINGERPRINT -> CredentialRecord(method = method, salt = null, hash = null)
        }
        val serialized = json.encodeToString(record)
        val payload = securityManager.encrypt(serialized.toByteArray())
        return encodePayload(payload)
    }

    fun verifyCredential(method: AuthMethod, encryptedData: String, attempt: String?): Boolean {
        val payload = decodePayload(encryptedData)
        val decrypted = securityManager.decrypt(payload).decodeToString()
        val record = json.decodeFromString<CredentialRecord>(decrypted)
        if (record.method != method) return false
        return when (method) {
            AuthMethod.PIN, AuthMethod.PASSWORD, AuthMethod.PATTERN -> {
                val salt = decode(record.salt ?: return false)
                val expectedHash = decode(record.hash ?: return false)
                val attemptHash = hashSecret(attempt.orEmpty(), salt)
                expectedHash.contentEquals(attemptHash)
            }
            AuthMethod.FINGERPRINT -> true
        }
    }

    private fun hashSecret(secret: String, salt: ByteArray): ByteArray {
        var result = secret.toByteArray() + salt
        repeat(PBKDF_ITERATIONS) {
            val digest = MessageDigest.getInstance("SHA-256")
            result = digest.digest(result)
        }
        return result
    }

    private fun encode(bytes: ByteArray): String = Base64.encodeToString(bytes, Base64.NO_WRAP)

    private fun decode(value: String): ByteArray = Base64.decode(value, Base64.NO_WRAP)

    private fun encodePayload(payload: EncryptedPayload): String {
        val combined = payload.initializationVector + payload.cipherText
        return encode(combined)
    }

    private fun decodePayload(data: String): EncryptedPayload {
        val bytes = decode(data)
        val iv = bytes.copyOfRange(0, 12)
        val cipherText = bytes.copyOfRange(12, bytes.size)
        return EncryptedPayload(cipherText = cipherText, initializationVector = iv)
    }

    @Serializable
    private data class CredentialRecord(
        val method: AuthMethod,
        val salt: String?,
        val hash: String?
    )
}
