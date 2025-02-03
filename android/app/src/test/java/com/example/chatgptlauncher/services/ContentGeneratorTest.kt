package com.example.chatgptlauncher.services

import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.client.OpenAI
import com.example.chatgptlauncher.config.SecureConfig
import com.example.chatgptlauncher.model.SocialPlatform
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ContentGeneratorTest {
    private lateinit var contentGenerator: ContentGenerator
    private lateinit var secureConfig: SecureConfig
    private val mockOpenAI = mockk<OpenAI>()
    private val mockChatCompletion = mockk<ChatCompletion>()

    @Before
    fun setup() {
        secureConfig = mockk {
            every { getOpenAIApiKey() } returns "test-api-key"
        }
        contentGenerator = ContentGenerator(secureConfig)
    }

    @Test
    fun `generateContent should handle YouTube format correctly`() = runTest {
        val response = """
            Title: Amazing Video Title
            Description: This is a great description
            Tags: #viral #youtube #trending
        """.trimIndent()

        setupMockResponse(response)

        val result = contentGenerator.generateContent(
            platform = SocialPlatform.YOUTUBE,
            isCharityAccount = false,
            videoContext = "Test video"
        )

        assertThat(result.title).isEqualTo("Amazing Video Title")
        assertThat(result.caption).isEqualTo("This is a great description")
        assertThat(result.hashtags).containsExactly("#viral", "#youtube", "#trending")
    }

    @Test
    fun `generateContent should handle Instagram format correctly`() = runTest {
        val response = """
            Caption: Check out this amazing content! 🔥
            Hashtags: #instagram #content #viral
        """.trimIndent()

        setupMockResponse(response)

        val result = contentGenerator.generateContent(
            platform = SocialPlatform.INSTAGRAM,
            isCharityAccount = false,
            videoContext = "Test video"
        )

        assertThat(result.caption).isEqualTo("Check out this amazing content! 🔥")
        assertThat(result.hashtags).containsExactly("#instagram", "#content", "#viral")
        assertThat(result.title).isNull()
    }

    @Test
    fun `generateContent should handle TikTok format correctly`() = runTest {
        val response = """
            Caption: Viral moment! 🎵
            Hashtags: #fyp #viral #trending
        """.trimIndent()

        setupMockResponse(response)

        val result = contentGenerator.generateContent(
            platform = SocialPlatform.TIKTOK,
            isCharityAccount = false,
            videoContext = "Test video"
        )

        assertThat(result.caption).isEqualTo("Viral moment! 🎵")
        assertThat(result.hashtags).containsExactly("#fyp", "#viral", "#trending")
        assertThat(result.title).isNull()
    }

    @Test(expected = ContentGenerator.GenerationError.NoApiKey::class)
    fun `generateContent should throw when API key is missing`() = runTest {
        every { secureConfig.getOpenAIApiKey() } returns ""

        contentGenerator.generateContent(
            platform = SocialPlatform.YOUTUBE,
            isCharityAccount = false,
            videoContext = "Test video"
        )
    }

    @Test
    fun `generateContent should include charity context when isCharityAccount is true`() = runTest {
        var capturedPrompt = ""
        coEvery {
            mockOpenAI.chatCompletion(any())
        } answers {
            capturedPrompt = firstArg<ChatCompletionRequest>()
                .messages
                .last()
                .content
            mockChatCompletion
        }

        setupMockResponse("Title: Test\nDescription: Test\nTags: #test")

        contentGenerator.generateContent(
            platform = SocialPlatform.YOUTUBE,
            isCharityAccount = true,
            videoContext = "Test video"
        )

        assertThat(capturedPrompt).contains("charity account")
        assertThat(capturedPrompt).contains("impact")
        assertThat(capturedPrompt).contains("community")
    }

    private fun setupMockResponse(response: String) {
        val mockChoice = mockk<ChatCompletion.Choice> {
            every { message } returns ChatMessage(
                role = com.aallam.openai.api.chat.ChatRole.Assistant,
                content = response
            )
        }
        
        every { mockChatCompletion.choices } returns listOf(mockChoice)
        
        coEvery {
            mockOpenAI.chatCompletion(any())
        } returns mockChatCompletion
    }
}
