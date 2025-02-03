package com.example.chatgptlauncher

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SimpleTest {
    @Test
    fun `simple test should pass`() {
        assertTrue(true, "Basic assertion should pass")
    }

    @Test
    fun `simple coroutine test should pass`() = runTest {
        var counter = 0
        val result = runCatching {
            repeat(3) {
                counter++
            }
        }
        assertEquals(3, counter, "Counter should increment three times")
        assertTrue(result.isSuccess, "Operation should succeed")
    }
}
