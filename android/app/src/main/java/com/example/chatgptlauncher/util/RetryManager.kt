package com.example.chatgptlauncher.util

import android.util.Log
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class RetryManager {
    companion object {
        private const val TAG = "RetryManager"
        private val DEFAULT_MAX_ATTEMPTS = 3
        private val DEFAULT_INITIAL_DELAY = 1.seconds
        private val DEFAULT_MAX_DELAY = 10.seconds
        private const val DEFAULT_MULTIPLIER = 2.0
    }

    sealed class RetryStrategy {
        data class ExponentialBackoff(
            val maxAttempts: Int = DEFAULT_MAX_ATTEMPTS,
            val initialDelay: Duration = DEFAULT_INITIAL_DELAY,
            val maxDelay: Duration = DEFAULT_MAX_DELAY,
            val multiplier: Double = DEFAULT_MULTIPLIER
        ) : RetryStrategy()

        data class FixedDelay(
            val maxAttempts: Int,
            val delay: Duration
        ) : RetryStrategy()
    }

    sealed class RetryResult<T> {
        data class Success<T>(
            val data: T,
            val attempts: Int
        ) : RetryResult<T>()

        data class Failure<T>(
            val exception: Exception,
            val attempts: Int
        ) : RetryResult<T>()
    }

    suspend fun <T> retry(
        strategy: RetryStrategy = RetryStrategy.ExponentialBackoff(),
        shouldRetry: (Exception) -> Boolean = { true },
        operation: suspend () -> T
    ): RetryResult<T> {
        var currentDelay = when (strategy) {
            is RetryStrategy.ExponentialBackoff -> strategy.initialDelay
            is RetryStrategy.FixedDelay -> strategy.delay
        }

        val maxAttempts = when (strategy) {
            is RetryStrategy.ExponentialBackoff -> strategy.maxAttempts
            is RetryStrategy.FixedDelay -> strategy.maxAttempts
        }

        var attempts = 0
        var lastException: Exception? = null

        while (attempts < maxAttempts) {
            try {
                attempts++
                return RetryResult.Success(operation(), attempts)
            } catch (e: Exception) {
                lastException = e
                Log.w(TAG, "Attempt $attempts failed", e)

                if (!shouldRetry(e) || attempts >= maxAttempts) {
                    break
                }

                delay(currentDelay)

                when (strategy) {
                    is RetryStrategy.ExponentialBackoff -> {
                        currentDelay = (currentDelay.times(strategy.multiplier))
                            .coerceAtMost(strategy.maxDelay)
                    }
                    is RetryStrategy.FixedDelay -> {
                        // Delay remains constant
                    }
                }
            }
        }

        return RetryResult.Failure(
            lastException ?: IllegalStateException("Unknown error occurred"),
            attempts
        )
    }

    // Utility function for common retry scenarios
    suspend fun <T> retryNetworkOperation(
        operation: suspend () -> T,
        maxAttempts: Int = DEFAULT_MAX_ATTEMPTS,
        shouldRetry: (Exception) -> Boolean = { e ->
            when (e) {
                is java.net.SocketTimeoutException,
                is java.io.IOException,
                is retrofit2.HttpException -> true
                else -> false
            }
        }
    ): T {
        val result = retry(
            strategy = RetryStrategy.ExponentialBackoff(maxAttempts = maxAttempts),
            shouldRetry = shouldRetry,
            operation = operation
        )

        return when (result) {
            is RetryResult.Success -> result.data
            is RetryResult.Failure -> throw result.exception
        }
    }

    // Utility function for handling rate limits
    suspend fun <T> retryWithRateLimit(
        operation: suspend () -> T,
        maxAttempts: Int = DEFAULT_MAX_ATTEMPTS,
        rateLimitDelay: Duration = 60.seconds
    ): T {
        val result = retry(
            strategy = RetryStrategy.FixedDelay(
                maxAttempts = maxAttempts,
                delay = rateLimitDelay
            ),
            shouldRetry = { e ->
                e is retrofit2.HttpException && e.code() == 429 // Rate limit status code
            },
            operation = operation
        )

        return when (result) {
            is RetryResult.Success -> result.data
            is RetryResult.Failure -> throw result.exception
        }
    }
}
