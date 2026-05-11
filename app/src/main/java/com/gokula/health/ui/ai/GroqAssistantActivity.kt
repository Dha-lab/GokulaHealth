package com.gokula.health.ui.ai

import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.gokula.health.adapters.ChatAdapter
import com.gokula.health.databinding.ActivityGroqAssistantBinding
import com.gokula.health.models.ChatMessage
import com.gokula.health.models.GroqMessage
import com.gokula.health.utils.GroqService

class GroqAssistantActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGroqAssistantBinding
    private lateinit var adapter: ChatAdapter
    private val chatMessages = mutableListOf<ChatMessage>()
    private val groqHistory = mutableListOf<GroqMessage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroqAssistantBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ChatAdapter()
        binding.recyclerChat.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        binding.recyclerChat.adapter = adapter

        // Initial AI message
        addAiMessage(getString(com.gokula.health.R.string.ai_greeting))

        binding.btnBack.setOnClickListener { finish() }
        binding.btnSend.setOnClickListener { sendMessage() }

        // Suggestion chips
        binding.chipSug1.setOnClickListener {
            binding.etMessage.setText("What are the signs of mastitis in dairy cows?")
            sendMessage()
        }
        binding.chipSug2.setOnClickListener {
            binding.etMessage.setText("Tell me about FMD vaccine for cattle?")
            sendMessage()
        }
        binding.chipSug3.setOnClickListener {
            binding.etMessage.setText("How can I increase my cow's milk yield naturally?")
            sendMessage()
        }
        binding.chipSug4.setOnClickListener {
            binding.etMessage.setText("How much feed does a lactating cow need daily?")
            sendMessage()
        }
    }

    private fun sendMessage() {
        val text = binding.etMessage.text.toString().trim()
        if (text.isEmpty()) return

        binding.etMessage.text?.clear()
        hideKeyboard()

        addUserMessage(text)
        groqHistory.add(GroqMessage("user", text))

        binding.layoutTyping.visibility = android.view.View.VISIBLE
        binding.btnSend.isEnabled = false

        val messagesToSend = if (groqHistory.size > 10) groqHistory.takeLast(10) else groqHistory.toList()

        GroqService.sendMessage(
            messagesToSend,
            onSuccess = { response ->
                runOnUiThread {
                    binding.layoutTyping.visibility = android.view.View.GONE
                    binding.btnSend.isEnabled = true
                    addAiMessage(response)
                    groqHistory.add(GroqMessage("assistant", response))
                }
            },
            onError = { error ->
                runOnUiThread {
                    binding.layoutTyping.visibility = android.view.View.GONE
                    binding.btnSend.isEnabled = true
                    addAiMessage("Sorry, I encountered an error: $error")
                    Toast.makeText(this, error, Toast.LENGTH_LONG).show()
                    // Remove failed user message from history
                    if (groqHistory.lastOrNull()?.role == "user") {
                        groqHistory.removeLast()
                    }
                }
            }
        )
    }

    private fun addUserMessage(text: String) {
        chatMessages.add(ChatMessage(text, isUser = true))
        adapter.submit(chatMessages.toList())
        binding.recyclerChat.scrollToPosition(adapter.itemCount - 1)
    }

    private fun addAiMessage(text: String) {
        chatMessages.add(ChatMessage(text, isUser = false))
        adapter.submit(chatMessages.toList())
        binding.recyclerChat.scrollToPosition(adapter.itemCount - 1)
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etMessage.windowToken, 0)
    }
}