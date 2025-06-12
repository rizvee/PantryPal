package com.example.pantrypal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Example Data class for UI State
data class MainUiState(
    val isLoading: Boolean = false,
    val data: String = "Hello from MainViewModel!"
    // Add other relevant UI state properties here
)

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    // Private MutableStateFlow that can be updated from this ViewModel
    private val _uiState = MutableStateFlow(MainUiState())
    // Public immutable StateFlow that UI can observe
    val uiState: StateFlow<MainUiState> = _uiState

    init {
        // Example: Load initial data or perform setup
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            // Simulate data loading
            kotlinx.coroutines.delay(2000) // Simulate network call or heavy operation
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                data = "Data loaded successfully!"
            )
        }
    }

    fun updateData(newData: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(data = newData, isLoading = false)
        }
    }

    // Add other business logic methods here that interact with data sources
    // and update the _uiState accordingly.
}
