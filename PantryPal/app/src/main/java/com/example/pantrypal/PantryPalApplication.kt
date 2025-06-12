package com.example.pantrypal

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PantryPalApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialization code here if needed
    }
}
