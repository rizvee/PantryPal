package com.example.pantrypal.model.network

import com.google.gson.annotations.SerializedName

data class ChatMessage(
    @SerializedName("role")
    val role: String, // e.g., "user", "system", "assistant"

    @SerializedName("content")
    val content: String
)
