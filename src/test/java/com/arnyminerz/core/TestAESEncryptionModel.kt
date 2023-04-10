package com.arnyminerz.core

import com.arnyminerz.core.security.AESEncryptionModel
import java.util.Base64
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class TestAESEncryptionModel {
    private object AESEncryption: AESEncryptionModel() {
        override val iv: String = "0123456789abcdef"

        override val salt: String = "0123456789abcdef"

        override val secretKey: String = "123456789abcdefghijklmnopqrstuvwxyz"

        override fun encodeBase64ToString(input: ByteArray): String =
            Base64.getMimeEncoder().encodeToString(input)

        override fun decodeBase64(input: String): ByteArray =
            Base64.getMimeDecoder().decode(input)
    }

    @Test
    fun test_encrypt_decrypt() {
        val original = "test-encryption"

        val encrypted = AESEncryption.encrypt(original)
        assertNotNull(encrypted)

        val decrypted = AESEncryption.decrypt(encrypted!!)
        assertEquals(original, decrypted)
    }
}