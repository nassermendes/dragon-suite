package com.example.chatgptlauncher.util

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.io.IOException
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class RetryManagerTest {
    private val retryManager = RetryManager()

    @Test
    fun `retry should succeed on first attempt`() = runTest {
        var attempts = 0
        val result = retryManager.retry {
            attempts++
            "success"
        }

        assertThat(result).isInstanceOf(RetryManager.RetryResult.Success::class.java)
        val success = result as RetryManager.RetryResult.Success
        assertThat(success.data).isEqualTo("success")
        assertThat(success.attempts).isEqualTo(1)
        assertThat(attempts).isEqualTo(1)
    }

    @Test
    fun `retry should succeed after multiple attempts`() = runTest {
        var attempts = 0
        val result = retryManager.retry(
            strategy = RetryManager.RetryStrategy.ExponentialBackoff(
                maxAttempts = 3,
                initialDelay = 0.seconds // For testing
            )
        ) {
            attempts++
            if (attempts < 2) throw IOException("Simulated failure")
            "success"
        }

        assertThat(result).isInstanceOf(RetryManager.RetryResult.Success::class.java)
        val success = result as RetryManager.RetryResult.Success
        assertThat(success.data).isEqualTo("success")
        assertThat(success.attempts).isEqualTo(2)
        assertThat(attempts).isEqualTo(2)
    }

    @Test
    fun `retry should fail after max attempts`() = runTest {
        var attempts = 0
        val result = retryManager.retry(
            strategy = RetryManager.RetryStrategy.ExponentialBackoff(
                maxAttempts = 3,
                initialDelay = 0.seconds // For testing
            )
        ) {
            attempts++
            throw IOException("Simulated failure")
        }

        assertThat(result).isInstanceOf(RetryManager.RetryResult.Failure::class.java)
        val failure = result as RetryManager.RetryResult.Failure
        assertThat(failure.exception).isInstanceOf(IOException::class.java)
        assertThat(failure.attempts).isEqualTo(3)
        assertThat(attempts).isEqualTo(3)
    }

    @Test
    fun `retry should respect shouldRetry predicate`() = runTest {
        var attempts = 0
        val result = retryManager.retry(
            strategy = RetryManager.RetryStrategy.ExponentialBackoff(
                maxAttempts = 3,
                initialDelay = 0.seconds
            ),
            shouldRetry = { it !is IllegalArgumentException }
        ) {
            attempts++
            throw IllegalArgumentException("Should not retry")
        }

        assertThat(result).isInstanceOf(RetryManager.RetryResult.Failure::class.java)
        assertThat(attempts).isEqualTo(1)
    }

    @Test
    fun `retryNetworkOperation should handle network errors`() = runTest {
        var attempts = 0
        val result = runCatching {
            retryManager.retryNetworkOperation(
                maxAttempts = 3,
                operation = {
                    attempts++
                    throw IOException("Network error")
                }
            )
        }

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(IOException::class.java)
        assertThat(attempts).isEqualTo(3)
    }

    @Test
    fun `retryWithRateLimit should handle rate limits`() = runTest {
        var attempts = 0
        val result = runCatching {
            retryManager.retryWithRateLimit(
                maxAttempts = 2,
                rateLimitDelay = 0.seconds,
                operation = {
                    attempts++
                    throw retrofit2.HttpException(
                        retrofit2.Response.error<Any>(
                            429,
                            okhttp3.ResponseBody.create(null, "Rate limited")
                        )
                    )
                }
            )
        }

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(retrofit2.HttpException::class.java)
        assertThat(attempts).isEqualTo(2)
    }
}
