package com.example.chatgptlauncher.services

import com.example.chatgptlauncher.util.RetryManager
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class RateLimitTest {
    private val retryManager = RetryManager()

    @Test
    fun `should handle YouTube rate limits`() = runTest {
        var attempts = 0
        val result = runCatching {
            retryManager.retryWithRateLimit(
                maxAttempts = 3,
                rateLimitDelay = 1.seconds
            ) {
                attempts++
                if (attempts < 3) {
                    throw createRateLimitException("YouTube API quota exceeded")
                }
                "success"
            }
        }

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo("success")
        assertThat(attempts).isEqualTo(3)
    }

    @Test
    fun `should handle Instagram rate limits with exponential backoff`() = runTest {
        var attempts = 0
        val result = retryManager.retry(
            strategy = RetryManager.RetryStrategy.ExponentialBackoff(
                maxAttempts = 4,
                initialDelay = 1.seconds,
                maxDelay = 4.seconds
            )
        ) {
            attempts++
            if (attempts < 3) {
                throw createRateLimitException("Instagram API rate limit")
            }
            "success"
        }

        assertThat(result).isInstanceOf(RetryManager.RetryResult.Success::class.java)
        assertThat((result as RetryManager.RetryResult.Success).data).isEqualTo("success")
        assertThat(attempts).isEqualTo(3)
    }

    @Test
    fun `should handle TikTok API errors with custom retry predicate`() = runTest {
        var attempts = 0
        val result = retryManager.retry(
            strategy = RetryManager.RetryStrategy.ExponentialBackoff(maxAttempts = 3),
            shouldRetry = { e ->
                e is HttpException && e.code() in listOf(429, 500, 502, 503, 504)
            }
        ) {
            attempts++
            when (attempts) {
                1 -> throw createHttpException(502)
                2 -> throw createHttpException(503)
                else -> "success"
            }
        }

        assertThat(result).isInstanceOf(RetryManager.RetryResult.Success::class.java)
        assertThat((result as RetryManager.RetryResult.Success).data).isEqualTo("success")
        assertThat(attempts).isEqualTo(3)
    }

    @Test
    fun `should not retry on permanent errors`() = runTest {
        var attempts = 0
        val result = retryManager.retry(
            strategy = RetryManager.RetryStrategy.ExponentialBackoff(maxAttempts = 3),
            shouldRetry = { e ->
                e is HttpException && e.code() != 401
            }
        ) {
            attempts++
            throw createHttpException(401)
        }

        assertThat(result).isInstanceOf(RetryManager.RetryResult.Failure::class.java)
        assertThat(attempts).isEqualTo(1)
    }

    private fun createRateLimitException(message: String): HttpException {
        return HttpException(
            Response.error<Any>(
                429,
                okhttp3.ResponseBody.create(null, message)
            )
        )
    }

    private fun createHttpException(code: Int): HttpException {
        return HttpException(
            Response.error<Any>(
                code,
                okhttp3.ResponseBody.create(null, "Error $code")
            )
        )
    }
}
