package com.example.pantrypal.view.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack // Ensure ArrowBack is imported
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pantrypal.view.ui.theme.PantryPalTheme
import com.example.pantrypal.viewmodel.RecipeViewModel
import com.example.pantrypal.viewmodel.RecipeUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeGenerationScreen(
    viewModel: RecipeViewModel = hiltViewModel(),
    onNavigateToRecipeDetail: (String) -> Unit, // Callback to navigate with recipe text
    onNavigateBack: (() -> Unit)? = null // Optional: For back navigation from TopAppBar
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    var ingredientsText by remember { mutableStateOf("") }
    var cuisineText by remember { mutableStateOf("") }
    var dietText by remember { mutableStateOf("") }
    var timeText by remember { mutableStateOf("") }

    // Effect to navigate when a recipe is successfully generated
    LaunchedEffect(uiState.isRecipeGenerated, uiState.recipe) {
        if (uiState.isRecipeGenerated && uiState.recipe != null) {
            onNavigateToRecipeDetail(uiState.recipe!!)
            viewModel.resetRecipeState() // Reset state after navigation to prevent re-triggering
        }
    }

    // Effect to show error SnackBar
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            // In a real app, you'd use SnackbarHostState provided by Scaffold
            // For simplicity here, we'll just log or use a Toast, or assume SnackbarHost is available
            android.widget.Toast.makeText(context, it, android.widget.Toast.LENGTH_LONG).show()
            viewModel.clearError() // Clear error after showing
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Generate New Recipe") },
                navigationIcon = {
                    onNavigateBack?.let {
                        IconButton(onClick = it) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()), // Make the column scrollable
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Enter ingredients and preferences to generate a recipe!", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = ingredientsText,
                onValueChange = { ingredientsText = it },
                label = { Text("Ingredients (comma-separated)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next
                ),
                trailingIcon = {
                    if (ingredientsText.isNotEmpty()) {
                        IconButton(onClick = { ingredientsText = "" }) {
                            Icon(Icons.Filled.Clear, "Clear ingredients")
                        }
                    }
                }
            )

            OutlinedTextField(
                value = cuisineText,
                onValueChange = { cuisineText = it },
                label = { Text("Cuisine Type (e.g., Italian, Mexican)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                 trailingIcon = {
                    if (cuisineText.isNotEmpty()) {
                        IconButton(onClick = { cuisineText = "" }) {
                            Icon(Icons.Filled.Clear, "Clear cuisine")
                        }
                    }
                }
            )

            OutlinedTextField(
                value = dietText,
                onValueChange = { dietText = it },
                label = { Text("Dietary Restrictions (e.g., vegan, gluten-free)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next
                ),
                 trailingIcon = {
                    if (dietText.isNotEmpty()) {
                        IconButton(onClick = { dietText = "" }) {
                            Icon(Icons.Filled.Clear, "Clear diet")
                        }
                    }
                }
            )

            OutlinedTextField(
                value = timeText,
                onValueChange = { timeText = it },
                label = { Text("Cooking Time (e.g., 30 mins, 1 hour)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                 trailingIcon = {
                    if (timeText.isNotEmpty()) {
                        IconButton(onClick = { timeText = "" }) {
                            Icon(Icons.Filled.Clear, "Clear time")
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    focusManager.clearFocus() // Hide keyboard
                    val ingredientsList = ingredientsText.split(",")
                        .map { it.trim() }
                        .filter { it.isNotBlank() }
                    if (ingredientsList.isNotEmpty()) {
                        viewModel.generateNewRecipe(
                            ingredients = ingredientsList,
                            cuisineType = cuisineText.takeIf { it.isNotBlank() },
                            dietaryRestrictions = dietText.split(",")
                                .map { it.trim() }
                                .filter { it.isNotBlank() }
                                .takeIf { it.isNotEmpty() },
                            cookingTime = timeText.takeIf { it.isNotBlank() }
                        )
                    } else {
                        // Show error: ingredients cannot be empty
                        android.widget.Toast.makeText(context, "Please enter at least one ingredient.", android.widget.Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading // Disable button when loading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Generating...")
                } else {
                    Text("Generate Recipe")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RecipeGenerationScreenPreview() {
    PantryPalTheme {
        RecipeGenerationScreen(onNavigateToRecipeDetail = {})
    }
}
