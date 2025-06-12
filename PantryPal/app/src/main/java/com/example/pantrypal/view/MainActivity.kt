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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pantrypal.view.screen.CameraScanScreen
import com.example.pantrypal.view.screen.PantryScreen
import com.example.pantrypal.view.screen.RecipeDetailScreen // Import RecipeDetailScreen
import com.example.pantrypal.view.screen.RecipeGenerationScreen // Import RecipeGenerationScreen
import com.example.pantrypal.view.ui.theme.PantryPalTheme
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLDecoder // For decoding URL arguments
import java.net.URLEncoder // For encoding URL arguments
import java.nio.charset.StandardCharsets

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

// Helper for encoding URL path segments or query parameters
fun String.encodeUrl(): String = URLEncoder.encode(this, StandardCharsets.UTF_8.toString())
fun String.decodeUrl(): String = URLDecoder.decode(this, StandardCharsets.UTF_8.toString())


@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.PantryScreen.route) {
        composable(Screen.PantryScreen.route) {
            PantryScreen(
                onNavigateToCameraScan = { navController.navigate(Screen.CameraScanScreen.route) },
                onNavigateToRecipeGeneration = { navController.navigate(Screen.RecipeGenerationScreen.route) } // New navigation
            )
        }
        composable(Screen.RecipeScreen.route) { // This is the old placeholder, might be removed or kept
            RecipeScreenPlaceholder(onNavigateToPantry = { navController.navigate(Screen.PantryScreen.route) })
        }
        composable(Screen.CameraScanScreen.route) {
            CameraScanScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.RecipeGenerationScreen.route) { // New route for RecipeGenerationScreen
            RecipeGenerationScreen(
                onNavigateToRecipeDetail = { recipeText ->
                    // Pass recipeText as a URL encoded navigation argument
                    navController.navigate("${Screen.RecipeDetailScreen.route}/${recipeText.encodeUrl()}")
                },
                onNavigateBack = { navController.popBackStack() } // Optional: if RecipeGenScreen needs a back button in TopAppBar
            )
        }
        composable( // New route for RecipeDetailScreen
            route = "${Screen.RecipeDetailScreen.route}/{recipeText}",
            arguments = listOf(navArgument("recipeText") { type = NavType.StringType })
        ) { backStackEntry ->
            val recipeTextEncoded = backStackEntry.arguments?.getString("recipeText") ?: ""
            RecipeDetailScreen(
                recipeText = recipeTextEncoded.decodeUrl(), // Decode URL argument
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

sealed class Screen(val route: String) {
    object PantryScreen : Screen("pantryScreen")
    object RecipeScreen : Screen("recipeScreen") // Old placeholder
    object CameraScanScreen : Screen("cameraScanScreen")
    object RecipeGenerationScreen : Screen("recipeGenerationScreen") // New screen
    object RecipeDetailScreen : Screen("recipeDetailScreen")       // New screen
}

@Composable
fun RecipeScreenPlaceholder(onNavigateToPantry: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "Recipe Screen (Placeholder - to be replaced or removed)",
            style = MaterialTheme.typography.headlineMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PantryPalTheme {
        AppNavigation()
    }
}
