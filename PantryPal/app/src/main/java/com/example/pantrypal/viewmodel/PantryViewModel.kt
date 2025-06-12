package com.example.pantrypal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pantrypal.model.PantryItem
import com.example.pantrypal.model.dao.PantryItemDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PantryScreenState(
    val items: List<PantryItem> = emptyList(),
    val isLoading: Boolean = false
    // Add other UI related states like error messages or sorting preferences here
)

@HiltViewModel
class PantryViewModel @Inject constructor(
    private val pantryItemDao: PantryItemDao
) : ViewModel() {

    val pantryScreenState: StateFlow<PantryScreenState> = pantryItemDao.getAllItems()
        .map { items -> PantryScreenState(items = items, isLoading = false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = PantryScreenState(isLoading = true)
        )

    // Example functions for adding/deleting items (can be expanded later)
    fun addItem(name: String, quantity: String, purchaseDate: Long, expiryDate: Long) {
        viewModelScope.launch {
            pantryItemDao.insert(PantryItem(name = name, quantity = quantity, purchaseDate = purchaseDate, predictedExpiryDate = expiryDate))
        }
    }

    fun deleteItem(item: PantryItem) {
        viewModelScope.launch {
            pantryItemDao.delete(item)
        }
    }

    // Example function to update an item
    // fun updateItem(item: PantryItem) {
    //     viewModelScope.launch {
    //         pantryItemDao.update(item)
    //     }
    // }
}
