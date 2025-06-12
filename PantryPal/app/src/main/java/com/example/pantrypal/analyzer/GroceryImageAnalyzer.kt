package com.example.pantrypal.analyzer

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy

class GroceryImageAnalyzer(
    private val onItemDetected: (String) -> Unit // Callback to return detected item info
) : ImageAnalysis.Analyzer {

    private var frameCounter = 0
    private val analysisThrottle = 30 // Analyze roughly every 30 frames (e.g., once per second at 30fps)

    @SuppressLint("UnsafeOptInUsageError") // Needed for image.planes
    override fun analyze(image: ImageProxy) {
        // Simple throttling: process 1 frame out of every `analysisThrottle` frames.
        if (frameCounter % analysisThrottle == 0) {
            val mediaImage = image.image // Get the underlying android.media.Image
            if (mediaImage != null) {
                // Placeholder for actual image processing logic (e.g., TensorFlow Lite model)
                // For now, just log image information.
                val imageWidth = mediaImage.width
                val imageHeight = mediaImage.height
                val format = mediaImage.format

                Log.d(
                    "GroceryImageAnalyzer",
                    "Analyzing frame: $frameCounter, Timestamp: ${image.imageInfo.timestamp}, " +
                            "Size: ${imageWidth}x$imageHeight, Format: $format"
                )

                // Simulate item detection after some processing
                // In a real app, this would come from your ML model
                val detectedItemName = "Placeholder Item (Frame: $frameCounter)"

                // Invoke the callback with the detected item information
                onItemDetected(detectedItemName)
            }
        }
        frameCounter++

        // Crucial: Always close the ImageProxy to free up the buffer for the next frame.
        image.close()
    }
}
