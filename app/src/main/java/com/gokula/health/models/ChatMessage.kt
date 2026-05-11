package com.gokula.health.models

data class ChatMessage(
    val content: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)