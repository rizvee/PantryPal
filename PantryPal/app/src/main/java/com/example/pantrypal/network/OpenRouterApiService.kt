package com.example.pantrypal.network

import com.example.pantrypal.model.network.ChatRequest
import com.example.pantrypal.model.network.ChatResponse
import retrofit2.Response // Import Retrofit's Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface OpenRouterApiService {

    /**
     * Sends a chat completion request to the OpenRouter API.
     *
     * @param apiKey The Authorization token (e.g., "Bearer YOUR_OPENROUTER_API_KEY").
     *               This will be provided by the repository, including the "Bearer " prefix.
     * @param requestBody The ChatRequest object containing the model and messages.
     * @return A Retrofit Response object wrapping the ChatResponse.
     *         Using Response<T> allows checking for HTTP success/failure codes.
     */
    @POST("chat/completions") // The specific endpoint path
    suspend fun getChatCompletion(
        @Header("Authorization") apiKey: String,
        @Body requestBody: ChatRequest
    ): Response<ChatResponse> // Using Retrofit's Response wrapper
}
