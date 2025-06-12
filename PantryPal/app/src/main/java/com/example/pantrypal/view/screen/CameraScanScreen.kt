package com.example.pantrypal.view.screen

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.RectF // Import for RectF
import android.util.Log
import android.util.Size
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.gestures.detectTapGestures // Import for tap detection
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput // Import for pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity // Import LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.pantrypal.analyzer.DetectionResult
import com.example.pantrypal.analyzer.GroceryImageAnalyzer
import com.example.pantrypal.view.composables.BoundingBoxOverlay
// Import for AddItemAlertDialog will be needed in a later step
// import com.example.pantrypal.view.composables.AddItemAlertDialog
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScanScreen(
    onNavigateBack: () -> Unit
    // pantryViewModel: PantryViewModel = hiltViewModel() // Will be needed for adding item
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val density = LocalDensity.current // Get density for px to dp conversion if needed, or for scaling logic

    var hasCameraPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasCameraPermission = granted }
    )

    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    var detectionResults by remember { mutableStateOf<List<DetectionResult>>(emptyList()) }
    var analyzedImageSize by remember { mutableStateOf(Size(0, 0)) }

    // State to hold the detection result that the user tapped on
    var selectedDetectionForDialog by remember { mutableStateOf<DetectionResult?>(null) }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
            Log.d("CameraScanScreen", "CameraExecutor shut down")
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Scan Groceries") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (hasCameraPermission) {
                var canvasSize by remember { mutableStateOf(Size(0,0)) } // To store canvas size for tap calculation

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .pointerInput(detectionResults, analyzedImageSize, canvasSize) { // Depend on these states
                            detectTapGestures { offset ->
                                if (analyzedImageSize.width == 0 || analyzedImageSize.height == 0 || canvasSize.width == 0 || canvasSize.height == 0) return@detectTapGestures

                                val scaleX = canvasSize.width.toFloat() / analyzedImageSize.width
                                val scaleY = canvasSize.height.toFloat() / analyzedImageSize.height
                                val scale = min(scaleX, scaleY)

                                val offsetX = (canvasSize.width - analyzedImageSize.width * scale) / 2f
                                val offsetY = (canvasSize.height - analyzedImageSize.height * scale) / 2f

                                for (detection in detectionResults.reversed()) { // Iterate reversed so top-most box is preferred
                                    val scaledBox = RectF(
                                        detection.boundingBox.left * scale + offsetX,
                                        detection.boundingBox.top * scale + offsetY,
                                        detection.boundingBox.right * scale + offsetX,
                                        detection.boundingBox.bottom * scale + offsetY
                                    )
                                    if (scaledBox.contains(offset.x, offset.y)) {
                                        selectedDetectionForDialog = detection
                                        Log.d("CameraScanScreen", "Tapped on: ${detection.label}")
                                        break // Found a tapped box
                                    }
                                }
                            }
                        }
                ) {
                    AndroidView(
                        factory = { PreviewView(context) },
                        modifier = Modifier.fillMaxSize(),
                        update = { previewView ->
                            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                            cameraProviderFuture.addListener({
                                val cameraProvider = cameraProviderFuture.get()
                                val previewUseCase = Preview.Builder().build().also {
                                    it.setSurfaceProvider(previewView.surfaceProvider)
                                }
                                val imageAnalysisUseCase = ImageAnalysis.Builder()
                                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                    .build()
                                    .also { analysisUseCase ->
                                        analysisUseCase.setAnalyzer(cameraExecutor, GroceryImageAnalyzer(
                                            context = context,
                                            onResults = { results, width, height ->
                                                detectionResults = results
                                                analyzedImageSize = Size(width, height)
                                            }
                                        ))
                                    }
                                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                                try {
                                    cameraProvider.unbindAll()
                                    cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, previewUseCase, imageAnalysisUseCase)
                                } catch (exc: Exception) {
                                    Log.e("CameraScanScreen", "Use case binding failed", exc)
                                }
                            }, ContextCompat.getMainExecutor(context))
                        }
                    )

                    if (analyzedImageSize.width > 0 && analyzedImageSize.height > 0) {
                        BoundingBoxOverlay(
                            results = detectionResults,
                            imageWidth = analyzedImageSize.width,
                            imageHeight = analyzedImageSize.height,
                            modifier = Modifier.fillMaxSize()
                                .onSizeChanged {
                                   canvasSize = Size(it.width, it.height)
                                }
                        )
                    }
                }
            } else {
                Text("Camera permission is required to scan items.")
                Button(onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) }) {
                    Text("Request Camera Permission")
                }
            }

            if (selectedDetectionForDialog != null) {
                Text("Selected: ${selectedDetectionForDialog?.label} - (Dialog will show here)") // Placeholder
                 Button(onClick = { selectedDetectionForDialog = null }) { // Temp way to clear selection
                    Text("Clear Selection (temp)")
                }
            }

            Button(onClick = onNavigateBack, modifier = Modifier.padding(16.dp)) {
                Text("Back to Pantry")
            }
        }
    }
}
