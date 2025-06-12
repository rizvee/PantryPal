package com.example.pantrypal.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.pantrypal.model.PantryItem
import kotlinx.coroutines.flow.Flow

@Dao
interface PantryItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: PantryItem)

    @Update
    suspend fun update(item: PantryItem)

    @Delete
    suspend fun delete(item: PantryItem)

    @Query("SELECT * FROM pantry_items ORDER BY purchaseDate DESC")
    fun getAllItems(): Flow<List<PantryItem>>

    // Optional: Add other specific queries if needed later, e.g.,
    // @Query("SELECT * FROM pantry_items WHERE id = :itemId")
    // fun getItemById(itemId: Int): Flow<PantryItem>
}
