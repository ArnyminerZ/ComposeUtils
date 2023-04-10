package com.arnyminerz.core.security

import com.arnyminerz.core.Logger
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

abstract class AESEncryptionModel {
    companion object {
        private const val ITERATIONS = 10_000
        private const val KEY_LENGTH = 256
    }

    protected abstract val salt: String
    protected abstract val iv: String
    protected abstract val secretKey: String

    protected abstract fun decodeBase64(input: String): ByteArray

    protected abstract fun encodeBase64ToString(input: ByteArray): String

    private val ivBase64: ByteArray
        get() = decodeBase64(iv)

    init {
        if (ivBase64.size != 16)
            throw IllegalArgumentException("IV length is not correct. Must be exactly 16 bytes long.")
    }

    private data class Cypher(val cipher: Cipher, val secretKey: SecretKeySpec, val ivParameterSpec: IvParameterSpec)

    private fun getCypher(): Cypher {
        val ivParameterSpec = IvParameterSpec(decodeBase64(iv))

        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val spec = PBEKeySpec(
            secretKey.toCharArray(),
            decodeBase64(salt),
            ITERATIONS,
            KEY_LENGTH
        )
        val tmp = factory.generateSecret(spec)
        val secretKey = SecretKeySpec(tmp.encoded, "AES")

        return Cypher(
            Cipher.getInstance("AES/CBC/PKCS5PADDING"),
            secretKey,
            ivParameterSpec,
        )
    }

    fun encrypt(strToEncrypt: String): String? =
        try {
            val (cipher, secretKey, ivParameterSpec) = getCypher()
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec)
            encodeBase64ToString(
                cipher.doFinal(strToEncrypt.toByteArray(Charsets.UTF_8)),
            )
        } catch (e: Exception) {
            Logger.e(e, "Error while encrypting:")
            null
        }

    fun decrypt(strToDecrypt: String): String? =
        try {
            val (cipher, secretKey, ivParameterSpec) = getCypher()
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
            String(cipher.doFinal(decodeBase64(strToDecrypt)))
        } catch (e: Exception) {
            Logger.e(e, "Error while decrypting:")
            null
        }
}
