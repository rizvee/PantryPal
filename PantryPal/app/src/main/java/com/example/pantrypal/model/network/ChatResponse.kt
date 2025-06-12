package com.example.pantrypal.model.network

import com.google.gson.annotations.SerializedName

data class ChatResponse(
    @SerializedName("id")
    val id: String?, // Can be nullable if not always present or not needed

    @SerializedName("choices")
    val choices: List<Choice>,

    // Include other fields if needed, e.g., "created", "model", "object", "usage"
    // For example:
    // @SerializedName("created")
    // val created: Long?,
    // @SerializedName("model")
    // val model: String?,
    // @SerializedName("usage")
    // val usage: UsageStats?
)
