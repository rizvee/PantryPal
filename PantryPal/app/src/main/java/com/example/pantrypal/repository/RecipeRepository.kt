package com.example.pantrypal.repository

import com.example.pantrypal.model.network.ChatMessage
import com.example.pantrypal.model.network.ChatRequest
import com.example.pantrypal.network.OpenRouterApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

// Result class for handling success/failure, similar to Kotlin's Result but more explicit for demo
sealed class NetworkResult<out T> {
    data class Success<out T>(val data: T) : NetworkResult<T>()
    data class Error(val message: String, val code: Int? = null) : NetworkResult<Nothing>()
}

@Singleton // This repository can be a singleton if it doesn't hold request-specific state
class RecipeRepository @Inject constructor(
    private val openRouterApiService: OpenRouterApiService,
    @Named("openRouterApiKey") private val apiKey: String // Injected API key from AppModule
) {

    // You can define constants for models if you switch often or want them configurable
    private val defaultChatModel = "deepseek/deepseek-chat" // Example model

    suspend fun generateRecipe(
        ingredients: List<String>,
        cuisineType: String? = null, // e.g., "Italian", "Mexican"
        dietaryRestrictions: List<String>? = null, // e.g., "vegetarian", "gluten-free"
        cookingTime: String? = null // e.g., "30 minutes", "1 hour"
    ): NetworkResult<String> {
        // Construct the prompt for the AI
        val ingredientsString = ingredients.joinToString(", ")
        var prompt = "Generate a recipe using the following ingredients: $ingredientsString."
        cuisineType?.let { prompt += " The recipe should be for $it cuisine." }
        dietaryRestrictions?.takeIf { it.isNotEmpty() }?.let {
            prompt += " It must adhere to these dietary restrictions: ${it.joinToString(", ")}."
        }
        cookingTime?.let { prompt += " The total cooking time should be around $it." }
        prompt += " Please provide the recipe with a title, list of ingredients (including quantities for a typical serving, e.g., 2-4 people), step-by-step instructions, and estimated prep and cook times."

        val messages = listOf(
            ChatMessage(role = "system", content = "You are a helpful kitchen assistant that generates recipes based on user-provided ingredients and preferences. Only provide the recipe itself, without conversational fluff before or after."),
            ChatMessage(role = "user", content = prompt)
        )

        val requestBody = ChatRequest(
            model = defaultChatModel,
            messages = messages
        )

        // Ensure network call is on IO dispatcher
        return withContext(Dispatchers.IO) {
            try {
                val fullApiKey = "Bearer $apiKey" // Add "Bearer " prefix
                val response = openRouterApiService.getChatCompletion(fullApiKey, requestBody)

                if (response.isSuccessful) {
                    val chatResponse = response.body()
                    val recipeContent = chatResponse?.choices?.firstOrNull()?.message?.content
                    if (recipeContent != null) {
                        NetworkResult.Success(recipeContent.trim())
                    } else {
                        NetworkResult.Error("Failed to parse recipe from response or content is empty.")
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    NetworkResult.Error("API Error: ${response.code()} - $errorBody", response.code())
                }
            } catch (e: Exception) {
                // Handle exceptions like network errors, timeouts, etc.
                NetworkResult.Error("Network request failed: ${e.message ?: "Unknown exception"}")
            }
        }
    }
}
