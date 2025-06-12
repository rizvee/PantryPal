package com.example.pantrypal.repository

import android.util.Log // Ensure Log is imported
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
    private val defaultChatModel = "deepseek/deepseek-r1-0528:free" // Updated model

    suspend fun generateRecipe(
        ingredients: List<String>,
        cuisineType: String? = null, // e.g., "Italian", "Mexican"
        dietaryRestrictions: List<String>? = null, // e.g., "vegetarian", "gluten-free"
        cookingTime: String? = null // e.g., "30 minutes", "1 hour"
    ): NetworkResult<String> {
        // Input validation
        if (ingredients.isEmpty()) {
            return NetworkResult.Error("Ingredients list cannot be empty.")
        }

        // Construct the prompt for the AI
        val ingredientsString = ingredients.joinToString(", ")
        var userPrompt = "I have the following ingredients: $ingredientsString."
        cuisineType?.takeIf { it.isNotBlank() }?.let { userPrompt += " I'd like to make something in the $it cuisine style." }
        dietaryRestrictions?.takeIf { it.isNotEmpty() }?.let {
            userPrompt += " Please ensure the recipe is ${it.joinToString(" and ")}."
        }
        cookingTime?.takeIf { it.isNotBlank() }?.let { userPrompt += " I have about $it to cook." }
        userPrompt += " Could you generate a unique recipe for me?"

        val systemMessageContent = """
        You are 'PantryPal Culinary AI', a specialized assistant for generating creative recipes.
        When a user provides ingredients and preferences, your task is to output a complete recipe.
        The recipe should include:
        1. Title: A catchy name for the dish.
        2. Introduction: (Optional) A brief, engaging description of the dish (1-2 sentences).
        3. Servings: Estimated number of servings (e.g., "Serves 2-4").
        4. Prep Time: Estimated preparation time (e.g., "15 minutes").
        5. Cook Time: Estimated cooking time (e.g., "30 minutes").
        6. Ingredients: A list of all ingredients with precise quantities. Include the ones provided by the user and any additional ones needed.
        7. Instructions: Clear, step-by-step cooking instructions. Number each step.
        8. Tips/Variations: (Optional) Any helpful tips or possible variations.

        Respond only with the recipe in this structured format. Do not include any conversational phrases, greetings, or any text outside of this recipe structure.
        Start directly with the "Title:".
        """.trimIndent()

        val messages = listOf(
            ChatMessage(role = "system", content = systemMessageContent),
            ChatMessage(role = "user", content = userPrompt)
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
                    if (!recipeContent.isNullOrBlank()) { // Updated condition
                        NetworkResult.Success(recipeContent.trim())
                    } else {
                        Log.w("RecipeRepository", "API response content is null or blank. Full response: ${response.raw()}")
                        NetworkResult.Error("Failed to parse recipe from response or content is empty.")
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    Log.e("RecipeRepository", "API Error: ${response.code()} - $errorBody. Request: $requestBody")
                    NetworkResult.Error("API Error ${response.code()}: Failed to fetch recipe. Please try again.", response.code())
                }
            } catch (e: Exception) {
                // Handle exceptions like network errors, timeouts, etc.
                Log.e("RecipeRepository", "Network request failed: ${e.message}", e)
                NetworkResult.Error("Network request failed: ${e.message ?: "Unknown exception"}")
            }
        }
    }
}
