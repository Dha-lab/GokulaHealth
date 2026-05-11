package com.gokula.health.utils

import android.util.Log
import com.gokula.health.BuildConfig
import com.gokula.health.models.GroqMessage
import com.gokula.health.models.GroqRequest
import com.gokula.health.models.GroqResponse
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException
import java.util.concurrent.TimeUnit

object GroqService {
    private const val TAG = "GroqService"
    private const val BASE_URL = "https://api.groq.com/openai/v1/chat/completions"
    private val gson = Gson()

    private val client: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private val systemMessage = GroqMessage(
        role = "system",
        content = "You are an expert AI assistant for Gokula-Health app. " +
                "You specialize in dairy cattle health, disease prevention (FMD, BQ, HS), " +
                "milk production, vaccination schedules, and cattle nutrition. " +
                "Provide concise, practical, and friendly advice for dairy farmers."
    )

    fun sendMessage(
        messages: List<GroqMessage>,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val apiKey = BuildConfig.GROQ_API_KEY
        if (apiKey.isEmpty() || apiKey == "null") {
            onError("Groq API key not configured. Please add it to local.properties")
            return
        }

        val allMessages = mutableListOf(systemMessage).apply { addAll(messages) }
        val request = GroqRequest(
            model = "llama-3.3-70b-versatile",
            messages = allMessages,
            temperature = 0.4,
            max_tokens = 1024
        )
        val json = gson.toJson(request)
        val body = json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val httpRequest = Request.Builder()
            .url(BASE_URL)
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .post(body)
            .build()

        client.newCall(httpRequest).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Network failure: ${e.message}")
                onError("Network error: Please check your internet connection")
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.use { responseBody ->
                    val bodyStr = responseBody.string()
                    if (response.isSuccessful) {
                        try {
                            val groqResponse = gson.fromJson(bodyStr, GroqResponse::class.java)
                            val content = groqResponse.choices.firstOrNull()?.message?.content
                            if (!content.isNullOrEmpty()) {
                                onSuccess(content.trim())
                            } else {
                                onError("Empty response from AI")
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Parse error: ${e.message}")
                            onError("Failed to parse AI response")
                        }
                    } else {
                        Log.e(TAG, "API Error ${response.code}: $bodyStr")
                        when (response.code) {
                            401 -> onError("Invalid API key. Check your Groq API key")
                            429 -> onError("Rate limit exceeded. Please wait a moment")
                            else -> onError("API Error ${response.code}")
                        }
                    }
                }
            }
        })
    }
}