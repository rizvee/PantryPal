package com.example.pantrypal.di

import android.content.Context
import androidx.room.Room
import com.example.pantrypal.model.dao.PantryItemDao
import com.example.pantrypal.model.db.PantryPalDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // Ensures instances are singletons and live as long as the app
object DatabaseModule {

    @Provides
    @Singleton
    fun providePantryPalDatabase(@ApplicationContext appContext: Context): PantryPalDatabase {
        return Room.databaseBuilder(
            appContext,
            PantryPalDatabase::class.java,
            "pantrypal_database" // Name of the database file
        )
        // Add fallbackToDestructiveMigration() if you want to allow Room to destroy and
        // recreate the database during schema changes (useful during development, but use migrations for production).
        // .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    @Singleton // Dao instance should also be a singleton if the DB is a singleton
    fun providePantryItemDao(database: PantryPalDatabase): PantryItemDao {
        return database.pantryItemDao()
    }
}
