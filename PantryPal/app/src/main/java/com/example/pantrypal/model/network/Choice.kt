package com.example.pantrypal.model.network

import com.google.gson.annotations.SerializedName

data class Choice(
    @SerializedName("message")
    val message: ResponseMessage,

    @SerializedName("finish_reason")
    val finishReason: String? // e.g., "stop", "length"
    // Potentially "index", "logprobs" if needed
)
