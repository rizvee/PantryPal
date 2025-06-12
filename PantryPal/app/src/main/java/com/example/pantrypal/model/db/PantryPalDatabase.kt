package com.example.pantrypal.model.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.pantrypal.model.PantryItem
import com.example.pantrypal.model.dao.PantryItemDao

@Database(entities = [PantryItem::class], version = 1, exportSchema = false)
abstract class PantryPalDatabase : RoomDatabase() {

    abstract fun pantryItemDao(): PantryItemDao

    // You can add a companion object for a singleton instance if not using Hilt
    // or if you need manual instantiation elsewhere, though Hilt will manage this.
    // companion object {
    //     @Volatile
    //     private var INSTANCE: PantryPalDatabase? = null
    //
    //     fun getDatabase(context: android.content.Context): PantryPalDatabase {
    //         return INSTANCE ?: synchronized(this) {
    //             val instance = Room.databaseBuilder(
    //                 context.applicationContext,
    //                 PantryPalDatabase::class.java,
    //                 "pantrypal_database"
    //             )
    //             // Add migrations here if needed in the future
    //             .build()
    //             INSTANCE = instance
    //             instance
    //         }
    //     }
    // }
}
