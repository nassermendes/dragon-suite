package com.example.dragonsuite.ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.ScrollView
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.example.dragonsuite.R
import com.example.dragonsuite.service.ChatGPTService
import kotlinx.coroutines.launch
import androidx.appcompat.app.AppCompatDialog

class ChatGPTDialog(context: Context) : AppCompatDialog(context) {
    private val chatGPTService = ChatGPTService()
    private lateinit var messageInput: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var chatOutput: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_chatgpt, null)
        setContentView(view)

        // Initialize views
        messageInput = view.findViewById(R.id.messageInput)
        sendButton = view.findViewById(R.id.sendButton)
        chatOutput = view.findViewById(R.id.chatOutput)

        // Set dialog properties
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        setupListeners()
    }

    private fun setupListeners() {
        messageInput.addTextChangedListener {
            sendButton.isEnabled = !it.isNullOrBlank()
        }

        sendButton.setOnClickListener {
            val message = messageInput.text.toString()
            if (message.isNotBlank()) {
                sendMessage(message)
                messageInput.text.clear()
            }
        }
    }

    private fun sendMessage(message: String) {
        lifecycleScope.launch {
            try {
                chatOutput.append("\nYou: $message\n")
                chatOutput.append("Assistant: ")
                
                // Use streaming response for real-time updates
                chatGPTService.streamResponse(message).collect { response ->
                    chatOutput.append(response)
                }
                chatOutput.append("\n")
            } catch (e: Exception) {
                chatOutput.append("\nError: ${e.message}\n")
            }
        }
    }

    fun updateResponse(response: String) {
        chatOutput.post {
            chatOutput.append("\nAssistant: $response\n")
            // Scroll to the bottom
            (chatOutput.parent as? android.widget.ScrollView)?.fullScroll(android.widget.ScrollView.FOCUS_DOWN)
        }
    }
}
