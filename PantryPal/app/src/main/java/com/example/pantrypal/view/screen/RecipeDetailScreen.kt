package com.example.pantrypal.view.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack // Ensure this import is present
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pantrypal.view.ui.theme.PantryPalTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    recipeText: String?,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Generated Recipe") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
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
                .verticalScroll(rememberScrollState())
        ) {
            if (recipeText.isNullOrBlank()) {
                Text(
                    text = "No recipe content found or an error occurred.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)
                )
            } else {
                Text(
                    text = recipeText,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Recipe Detail With Content")
@Composable
fun RecipeDetailScreenPreview_WithContent() {
    PantryPalTheme {
        RecipeDetailScreen(
            recipeText = "Recipe Title\n\nIngredients:\n- Item 1\n- Item 2\n\nInstructions:\n1. Step one.\n2. Step two.",
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Recipe Detail Empty")
@Composable
fun RecipeDetailScreenPreview_Empty() {
    PantryPalTheme {
        RecipeDetailScreen(
            recipeText = null,
            onNavigateBack = {}
        )
    }
}
