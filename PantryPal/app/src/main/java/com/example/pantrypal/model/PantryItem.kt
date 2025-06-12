package com.example.pantrypal.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pantry_items")
data class PantryItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val quantity: String, // e.g., "1 kg", "2 packs", "500 ml"
    val purchaseDate: Long, // Timestamp, e.g., System.currentTimeMillis()
    val predictedExpiryDate: Long // Timestamp
)
