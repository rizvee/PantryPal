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
import com.example.pantrypal.view.screen.CameraScanScreen // Import CameraScanScreen
import com.example.pantrypal.view.screen.PantryScreen
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
            PantryScreen(
                onAddItemClick = { navController.navigate(Screen.CameraScanScreen.route) } // Navigate to CameraScanScreen
            )
        }
        composable(Screen.RecipeScreen.route) {
            RecipeScreenPlaceholder(onNavigateToPantry = { navController.navigate(Screen.PantryScreen.route) })
        }
        composable(Screen.CameraScanScreen.route) { // Add route for CameraScanScreen
            CameraScanScreen(
                onNavigateBack = { navController.popBackStack() } // Navigate back
            )
        }
    }
}

sealed class Screen(val route: String) {
    object PantryScreen : Screen("pantryScreen")
    object RecipeScreen : Screen("recipeScreen")
    object CameraScanScreen : Screen("cameraScanScreen") // Add CameraScanScreen
}

@Composable
fun RecipeScreenPlaceholder(onNavigateToPantry: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "Recipe Screen (Placeholder)",
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
