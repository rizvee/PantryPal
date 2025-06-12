package com.example.pantrypal.view.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pantrypal.analyzer.DetectionResult // Your DetectionResult data class
import kotlin.math.max
import kotlin.math.min

@Composable
fun BoundingBoxOverlay(
    results: List<DetectionResult>,
    imageWidth: Int, // Width of the image the results are based on
    imageHeight: Int, // Height of the image the results are based on
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current

    Canvas(modifier = modifier.fillMaxSize()) {
        // The size of the Canvas (View that draws this)
        val canvasWidth = size.width
        val canvasHeight = size.height

        if (imageWidth <= 0 || imageHeight <= 0) {
            // Avoid division by zero if image dimensions are invalid
            return@Canvas
        }

        // Calculate scaling factors to map image coordinates to canvas coordinates
        // This needs to handle aspect ratio differences.
        // Option 1: Fit inside (letterbox/pillarbox) - maintains aspect ratio of boxes
        val scaleX = canvasWidth / imageWidth.toFloat()
        val scaleY = canvasHeight / imageHeight.toFloat()
        val scale = min(scaleX, scaleY) // Use the smaller scale to fit the image within the canvas

        // Calculate offsets to center the scaled image within the canvas
        val offsetX = (canvasWidth - imageWidth * scale) / 2f
        val offsetY = (canvasHeight - imageHeight * scale) / 2f

        results.forEach { result ->
            val boundingBox = result.boundingBox

            // Scale and offset the bounding box coordinates
            val scaledLeft = boundingBox.left * scale + offsetX
            val scaledTop = boundingBox.top * scale + offsetY
            val scaledRight = boundingBox.right * scale + offsetX
            val scaledBottom = boundingBox.bottom * scale + offsetY

            // Draw the bounding box rectangle
            drawRect(
                color = Color.Yellow, // Or choose color based on class/confidence
                topLeft = Offset(scaledLeft, scaledTop),
                size = Size(scaledRight - scaledLeft, scaledBottom - scaledTop),
                style = Stroke(width = 2.dp.toPx())
            )

            // Prepare text for label and confidence
            val labelText = "${result.label} (${"%.2f".format(result.confidence)})"
            val textStyle = TextStyle(
                color = Color.White,
                fontSize = 14.sp,
                background = Color.Black.copy(alpha = 0.5f) // Semi-transparent background for readability
            )
            val textLayoutResult = textMeasurer.measure(text = labelText, style = textStyle)

            // Draw text label above the bounding box
            drawText(
                textLayoutResult = textLayoutResult,
                topLeft = Offset(scaledLeft, max(0f, scaledTop - textLayoutResult.size.height - 2.dp.toPx()))
                // Ensure text doesn't go off-screen at the top
            )
        }
    }
}
