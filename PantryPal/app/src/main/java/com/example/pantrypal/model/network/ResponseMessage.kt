package com.example.pantrypal.model.network

import com.google.gson.annotations.SerializedName

data class ResponseMessage(
    @SerializedName("role")
    val role: String?, // e.g., "assistant"

    @SerializedName("content")
    val content: String? // This is the main text we want to extract
)
