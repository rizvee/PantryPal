package com.example.pantrypal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pantrypal.repository.NetworkResult
import com.example.pantrypal.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RecipeUiState(
    val isLoading: Boolean = false,
    val recipe: String? = null,
    val error: String? = null,
    val isRecipeGenerated: Boolean = false // To track if a recipe has been successfully generated at least once
)

@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeUiState())
    val uiState: StateFlow<RecipeUiState> = _uiState.asStateFlow()

    fun generateNewRecipe(
        ingredients: List<String>,
        cuisineType: String? = null,
        dietaryRestrictions: List<String>? = null,
        cookingTime: String? = null
    ) {
        viewModelScope.launch {
            _uiState.value = RecipeUiState(isLoading = true) // Reset state, indicate loading

            when (val result = recipeRepository.generateRecipe(
                ingredients,
                cuisineType,
                dietaryRestrictions,
                cookingTime
            )) {
                is NetworkResult.Success -> {
                    _uiState.value = RecipeUiState(
                        isLoading = false,
                        recipe = result.data,
                        error = null,
                        isRecipeGenerated = true
                    )
                }
                is NetworkResult.Error -> {
                    _uiState.value = RecipeUiState(
                        isLoading = false,
                        recipe = null,
                        error = result.message,
                        isRecipeGenerated = false
                    )
                }
            }
        }
    }

    /**
     * Resets the error state, typically called after the error has been shown to the user.
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    /**
     * Resets the entire UI state back to its initial state, including clearing any generated recipe.
     * Useful if the user wants to start a new recipe generation from scratch.
     */
    fun resetRecipeState() {
        _uiState.value = RecipeUiState()
    }
}
