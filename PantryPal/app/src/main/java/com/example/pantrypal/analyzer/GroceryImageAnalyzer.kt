package com.example.pantrypal.analyzer

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.RectF
import android.util.Log
import androidx.camera.core.ImageProxy
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.Rot90Op
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.vision.detector.Detection
import org.tensorflow.lite.task.vision.detector.ObjectDetector

// Data class to hold detection results (already defined, ensure it's accessible)
// data class DetectionResult(
//     val boundingBox: RectF,
//     val label: String,
//     val confidence: Float
// )

class GroceryImageAnalyzer(
    private val context: Context,
    private val modelName: String = "model.tflite",
    private var detectionThreshold: Float = 0.5f,
    private var numThreads: Int = 2,
    // Updated callback signature
    private val onResults: (results: List<DetectionResult>, analyzedImageWidth: Int, analyzedImageHeight: Int) -> Unit
) : androidx.camera.core.ImageAnalysis.Analyzer {

    companion object {
        private const val TAG = "GroceryImageAnalyzer"
    }

    private var objectDetector: ObjectDetector? = null
    // imageProcessor for rotation is created on-the-fly if needed

    init {
        setupObjectDetector()
    }

    private fun setupObjectDetector() {
        try {
            val baseOptionsBuilder = BaseOptions.builder().setNumThreads(numThreads)
            val optionsBuilder = ObjectDetector.ObjectDetectorOptions.builder()
                .setBaseOptions(baseOptionsBuilder.build())
                .setScoreThreshold(detectionThreshold)
            objectDetector = ObjectDetector.createFromFileAndOptions(context, modelName, optionsBuilder.build())
            Log.i(TAG, "ObjectDetector initialized successfully.")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing ObjectDetector: ${e.message}", e)
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        if (objectDetector == null) {
            Log.w(TAG, "ObjectDetector not initialized, skipping analysis.")
            imageProxy.close()
            return
        }

        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            imageProxy.close()
            return
        }

        val rotationDegrees = imageProxy.imageInfo.rotationDegrees
        var tensorImage = TensorImage.fromBitmap(mediaImage.toBitmap()) // Convert to Bitmap first

        // Apply rotation to the TensorImage
        if (rotationDegrees != 0) {
            val imageProcessorForRotation = ImageProcessor.Builder()
                .add(Rot90Op(rotationDegrees / 90)) // Apply clockwise rotation
                .build()
            tensorImage = imageProcessorForRotation.process(tensorImage)
        }

        // Crucial: Get dimensions from the TensorImage that will be fed to the model
        val analyzedWidth = tensorImage.width
        val analyzedHeight = tensorImage.height

        try {
            val detections = objectDetector?.detect(tensorImage)

            if (detections != null) {
                val results = detections.map { detection ->
                    DetectionResult(
                        boundingBox = detection.boundingBox, // These are relative to the (possibly rotated) tensorImage
                        label = detection.categories.firstOrNull()?.label ?: "Unknown",
                        confidence = detection.categories.firstOrNull()?.score ?: 0f
                    )
                }
                // Pass the results AND the dimensions of the analyzed image
                onResults(results, analyzedWidth, analyzedHeight)
            } else {
                onResults(emptyList(), analyzedWidth, analyzedHeight)
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error during object detection: ${e.message}", e)
            onResults(emptyList(), analyzedWidth, analyzedHeight) // Notify with empty results and dimensions
        } finally {
            imageProxy.close()
        }
    }
}

// Make sure the toBitmap() extension function is still present in this file or accessible
// (It was part of the previous subtask for this file)
@SuppressLint("UnsafeOptInUsageError")
fun android.media.Image.toBitmap(): android.graphics.Bitmap {
    if (format != android.graphics.ImageFormat.YUV_420_888) {
        Log.w("ImageToBitmap", "Image format is not YUV_420_888. Bitmap conversion might be incorrect.")
    }
    val yBuffer = planes[0].buffer
    val uBuffer = planes[1].buffer
    val vBuffer = planes[2].buffer
    val ySize = yBuffer.remaining()
    val uSize = uBuffer.remaining()
    val vSize = vBuffer.remaining()
    val nv21 = ByteArray(ySize + uSize + vSize)
    yBuffer.get(nv21, 0, ySize)
    vBuffer.get(nv21, ySize, vSize)
    uBuffer.get(nv21, ySize + vSize, uSize)
    val yuvImage = android.graphics.YuvImage(nv21, android.graphics.ImageFormat.NV21, this.width, this.height, null)
    val out = java.io.ByteArrayOutputStream()
    yuvImage.compressToJpeg(android.graphics.Rect(0, 0, this.width, this.height), 80, out)
    val imageBytes = out.toByteArray()
    return android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
}
