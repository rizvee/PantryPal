package com.example.pantrypal.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pantrypal.view.screen.PantryScreen // Import the new PantryScreen
import com.example.pantrypal.view.ui.theme.PantryPalTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PantryPalTheme {
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
    NavHost(navController = navController, startDestination = Screen.PantryScreen.route) {
        composable(Screen.PantryScreen.route) {
            // Use the new PantryScreen from view.screen package
            PantryScreen(
                // viewModel is provided by hiltViewModel() within PantryScreen itself
                onAddItemClick = { /* TODO: Navigate to Add Item Screen */ }
                // onNavigateToRecipe = { navController.navigate(Screen.RecipeScreen.route) } // Example navigation
            )
        }
        composable(Screen.RecipeScreen.route) {
            // Basic placeholder - Replace with actual Recipe Screen UI
            RecipeScreenPlaceholder(onNavigateToPantry = { navController.navigate(Screen.PantryScreen.route) })
        }
        // Add other destinations here:
        // composable(Screen.AddItemScreen.route) { /* ... */ }
    }
}

// Define a sealed class for screen routes for better organization
sealed class Screen(val route: String) {
    object PantryScreen : Screen("pantryScreen")
    object RecipeScreen : Screen("recipeScreen")
    // object AddItemScreen : Screen("addItemScreen") // Example for future screen
}


@Composable
fun RecipeScreenPlaceholder(onNavigateToPantry: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "Recipe Screen (Placeholder)",
            style = MaterialTheme.typography.headlineMedium
        )
        // Button(onClick = onNavigateToPantry) { Text("Back to Pantry") } // Example
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PantryPalTheme {
        AppNavigation()
    }
}
