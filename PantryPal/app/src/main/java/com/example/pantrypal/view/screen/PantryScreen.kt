package com.example.pantrypal.view.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit // For Recipe Generation
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pantrypal.model.PantryItem
import com.example.pantrypal.view.composables.PantryItemCard
import com.example.pantrypal.viewmodel.PantryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantryScreen(
    viewModel: PantryViewModel = hiltViewModel(),
    onNavigateToCameraScan: () -> Unit, // Renamed from onAddItemClick for clarity
    onNavigateToRecipeGeneration: () -> Unit // New navigation callback
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
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                FloatingActionButton(onClick = onNavigateToCameraScan) {
                    Icon(Icons.Filled.PhotoCamera, "Scan new item with camera")
                }
                ExtendedFloatingActionButton(
                    icon = { Icon(Icons.Filled.Edit, contentDescription = "Generate Recipe") },
                    text = { Text("Generate Recipe") },
                    onClick = onNavigateToRecipeGeneration
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End // Default, but explicit
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            if (isLoading && items.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (!isLoading && items.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Your pantry is empty. Tap the camera button to scan items or generate a recipe!")
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
