package com.arnyminerz.core

import com.arnyminerz.core.utils.dniLetter
import com.arnyminerz.core.utils.isValidDni
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TestDniValidation {
    @Test
    fun test_dniLetter() {
        assertEquals('Z', "12345678Z".dniLetter)
    }

    @Test
    fun test_dniValid() {
        assertTrue("12345678Z".isValidDni)
    }

    @Test
    fun test_dniInvalid() {
        assertFalse("12345678Y".isValidDni)
        assertFalse("".isValidDni)
    }
}