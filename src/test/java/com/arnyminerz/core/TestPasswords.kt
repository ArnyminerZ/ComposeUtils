package com.arnyminerz.core

import com.arnyminerz.core.security.PasswordSafety
import org.junit.Assert.assertEquals
import org.junit.Test

class TestPasswords {
    private object Passwords : com.arnyminerz.core.security.Passwords() {
        override val topics: List<String> = listOf("forbidden")
    }

    @Test
    fun test_passwordSafety() {
        assertEquals(PasswordSafety.Forbidden, Passwords.isSafePassword("123Forbidden123"))
        assertEquals(PasswordSafety.AllNumbers, Passwords.isSafePassword("123456789"))
        assertEquals(PasswordSafety.AllCaps, Passwords.isSafePassword("QWERTYUIOP"))
        assertEquals(PasswordSafety.AllLowercase, Passwords.isSafePassword("qwertyuiop"))
        assertEquals(PasswordSafety.Short, Passwords.isSafePassword("qwerty"))
        assertEquals(PasswordSafety.Unsafe, Passwords.isSafePassword("Acidburn"))

        assertEquals(PasswordSafety.Safe, Passwords.isSafePassword(Passwords.generateRandomPassword()))
    }
}
