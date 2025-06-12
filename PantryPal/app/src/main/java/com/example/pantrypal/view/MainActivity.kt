package com.example.pantrypal.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pantrypal.view.ui.theme.PantryPalTheme // Assuming a Theme.kt will be created
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PantryPalTheme { // Apply the custom theme
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "pantryScreen") {
        composable("pantryScreen") {
            PantryScreen(onNavigateToRecipe = { navController.navigate("recipeScreen") })
        }
        composable("recipeScreen") {
            RecipeScreen(onNavigateToPantry = { navController.navigate("pantryScreen") })
        }
        // Add other destinations here
    }
}

@Composable
fun PantryScreen(onNavigateToRecipe: () -> Unit, modifier: Modifier = Modifier) {
    // Basic placeholder - Replace with actual Pantry UI
    Surface(modifier = modifier.fillMaxSize()) {
        Text(
            text = "Pantry Screen - Welcome to PantryPal!",
            // style = MaterialTheme.typography.h6 // Example style
        )
        // Button or interaction to navigate
        // Button(onClick = onNavigateToRecipe) { Text("Go to Recipes") }
    }
}

@Composable
fun RecipeScreen(onNavigateToPantry: () -> Unit, modifier: Modifier = Modifier) {
    // Basic placeholder - Replace with actual Recipe UI
    Surface(modifier = modifier.fillMaxSize()) {
        Text(
            text = "Recipe Screen",
            // style = MaterialTheme.typography.h6 // Example style
        )
        // Button(onClick = onNavigateToPantry) { Text("Back to Pantry") }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PantryPalTheme {
        AppNavigation()
    }
}
