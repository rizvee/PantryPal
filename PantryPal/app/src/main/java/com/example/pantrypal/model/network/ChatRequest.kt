package com.example.pantrypal.model.network

import com.google.gson.annotations.SerializedName

data class ChatRequest(
    @SerializedName("model")
    val model: String, // e.g., "deepseek/deepseek-chat" or "openai/gpt-3.5-turbo"

    @SerializedName("messages")
    val messages: List<ChatMessage> // Renamed from Message to ChatMessage to avoid potential conflicts
)
