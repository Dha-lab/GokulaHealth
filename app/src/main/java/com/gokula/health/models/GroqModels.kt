package com.gokula.health.models

data class GroqRequest(
    val model: String = "llama-3.3-70b-versatile",
    val messages: List<GroqMessage>,
    val temperature: Double = 0.4,
    val max_tokens: Int = 1024
)

data class GroqMessage(
    val role: String,
    val content: String
)

data class GroqResponse(
    val id: String = "",
    val choices: List<GroqChoice> = emptyList()
)

data class GroqChoice(
    val message: GroqMessage = GroqMessage("", ""),
    val finish_reason: String = ""
)