package com.example.pantrypal.model.network

import com.google.gson.annotations.SerializedName

data class UsageStats(
    @SerializedName("prompt_tokens")
    val promptTokens: Int?,
    @SerializedName("completion_tokens")
    val completionTokens: Int?,
    @SerializedName("total_tokens")
    val totalTokens: Int?
)
