package com.arnyminerz.core

import com.arnyminerz.core.resources.ResourcesProperties
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test

class TestResourcesProperties {
    private val resourcesProperties = ResourcesProperties("test.properties")

    @Test
    fun test_get() {
        assertEquals(4, resourcesProperties.size)

        assertEquals("value", resourcesProperties["key"])
        assertEquals("example-value", resourcesProperties["example-key"])
        assertEquals("this-Is=a difficult..value", resourcesProperties["complicated"])
        assertEquals("Quotes removed", resourcesProperties["quoted"])

        assertNull(resourcesProperties["missing"])
        assertThrows(NoSuchElementException::class.java) { resourcesProperties.getValue("missing") }
    }

    @Test
    fun test_set_forbidden() {
        assertThrows(UnsupportedOperationException::class.java) { resourcesProperties["illegal"] = "value" }
    }

    @Test
    fun test_setMemory_forbidden() {
        assertThrows(UnsupportedOperationException::class.java) { resourcesProperties.setMemory("illegal", "value") }
    }

    @Test
    fun test_clear_forbidden() {
        assertThrows(UnsupportedOperationException::class.java) { resourcesProperties.clear("key") }
    }
}
