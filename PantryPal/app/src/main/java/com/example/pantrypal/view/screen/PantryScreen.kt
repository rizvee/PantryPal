package com.example.pantrypal.view.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pantrypal.model.PantryItem
import com.example.pantrypal.view.composables.PantryItemCard // Will be created in the next step
import com.example.pantrypal.viewmodel.PantryViewModel
// import com.example.pantrypal.viewmodel.PantryScreenState // Not directly used if collecting state like this

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantryScreen(
    viewModel: PantryViewModel = hiltViewModel(),
    // onNavigateToRecipe: () -> Unit, // Keep if top-level navigation is needed from here
    onAddItemClick: () -> Unit // Example: For navigating to an "Add Item" screen
) {
    val pantryScreenState by viewModel.pantryScreenState.collectAsState()
    val items = pantryScreenState.items
    val isLoading = pantryScreenState.isLoading

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Pantry") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                // For now, let's use the ViewModel's addItem as a placeholder
                // In a real app, this would navigate to a new screen/dialog to input item details
                viewModel.addItem(
                    name = "Sample Item ${items.size + 1}",
                    quantity = "1 pc",
                    purchaseDate = System.currentTimeMillis(),
                    // Expire in 7 days for sample
                    expiryDate = System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000)
                )
                // onAddItemClick() // Use this if you have a dedicated add item screen/flow
            }) {
                Icon(Icons.Filled.Add, "Add new pantry item")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            if (isLoading && items.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (!isLoading && items.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Your pantry is empty. Tap the '+' button to add items!")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items, key = { item -> item.id }) { pantryItem ->
                        PantryItemCard(pantryItem = pantryItem)
                    }
                }
            }
        }
    }
}

// Preview for PantryScreen (Optional, but helpful)
// @Preview(showBackground = true)
// @Composable
// fun PantryScreenPreview() {
//     // You'd need a way to provide a mock PantryViewModel or PantryScreenState for previews
//     // For simplicity, this is omitted for now but is important for complex screens.
//     PantryPalTheme { // Assuming PantryPalTheme is defined
//          PantryScreen(onAddItemClick = {})
//     }
// }
