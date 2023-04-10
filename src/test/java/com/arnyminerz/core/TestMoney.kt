package com.arnyminerz.core

import com.arnyminerz.core.utils.divideMoney
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TestMoney {
    @Test
    fun test_divideMoney() {
        // We want to divide 53.17 monetary units in packages of 5, 2, 1, 0.2, 0.1, 0.02 and 0.01
        val result = divideMoney(
            53.17,
            listOf(
                "five" to 5.0,
                "two" to 2.0,
                "one" to 1.0,
                "p_two" to 0.2,
                "p_one" to 0.1,
                "pz_two" to 0.02,
                "pz_one" to 0.01,
            ),
        )
        assertEquals(10, result["five"])
        assertEquals(1, result["two"])
        assertEquals(1, result["one"])
        assertNull(result["p_two"])
        assertEquals(1, result["p_one"])
        assertEquals(3, result["pz_two"])
        assertEquals(1, result["pz_one"])
    }

    @Test
    fun test_divideMoney_fill() {
        // We want to divide 53.17 monetary units in packages of 5, 2, 1, 0.2, 0.1, 0.02 and 0.01
        val result = divideMoney(
            53.17,
            listOf(
                "five" to 5.0,
                "two" to 2.0,
                "one" to 1.0,
                "p_two" to 0.2,
                "p_one" to 0.1,
                "pz_two" to 0.02,
                "pz_one" to 0.01,
            ),
            fillNulls = true,
        )
        assertEquals(10, result["five"])
        assertEquals(1, result["two"])
        assertEquals(1, result["one"])
        assertEquals(0, result["p_two"])
        assertEquals(1, result["p_one"])
        assertEquals(3, result["pz_two"])
        assertEquals(1, result["pz_one"])
    }
}