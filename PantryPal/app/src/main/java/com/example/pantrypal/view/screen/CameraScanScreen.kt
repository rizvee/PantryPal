package com.example.pantrypal.view.screen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.pantrypal.analyzer.GroceryImageAnalyzer // Will be created later
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScanScreen(
    // viewModel: CameraViewModel = hiltViewModel() // If a ViewModel is used
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
        }
    )

    // Used to build and bind camera use cases
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val previewView = remember { PreviewView(context) }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    // Request permission if not already granted
    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Scan Groceries") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (hasCameraPermission) {
                Text("Camera Permission Granted. Setting up camera...")
                AndroidView(
                    factory = { previewView },
                    modifier = Modifier.fillMaxSize().weight(1f),
                    update = {
                        // This block is called when the view is updated,
                        // which is a good place to bind the camera use cases.
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                        val imageAnalyzer = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                            .also {
                                it.setAnalyzer(cameraExecutor, GroceryImageAnalyzer { item ->
                                    // This lambda will be called by the analyzer with the detected item
                                    Log.d("CameraScanScreen", "Detected item: $item")
                                    // Here you would typically update ViewModel or UI state
                                })
                            }

                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                        try {
                            cameraProvider.unbindAll() // Unbind use cases before rebinding
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                imageAnalyzer // Add imageAnalyzer here
                            )
                        } catch (exc: Exception) {
                            Log.e("CameraScanScreen", "Use case binding failed", exc)
                            // Handle exceptions, e.g., show an error message
                        }
                    }
                )
                // Add any overlay UI here if needed (e.g., a button to trigger scan)
                Button(onClick = { /* TODO: Manual scan trigger if needed */ }) {
                    Text("Scan")
                }

            } else {
                Text("Camera permission is required to scan items.")
                Button(onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) }) {
                    Text("Request Camera Permission")
                }
            }
            Button(onClick = onNavigateBack, modifier = Modifier.padding(16.dp)) {
                Text("Back to Pantry")
            }
        }
    }
    // Ensure to release the executor when the composable is disposed
    // DisposedEffect or similar might be needed if not using viewModelScope for executor
    // However, since it's a single thread executor for analysis, it might be managed by CameraX lifecycle.
    // For now, we'll rely on CameraX to manage its resources with bindToLifecycle.
}

// Helper function to start camera (might be part of CameraViewModel or a utility class)
private fun startCamera(
    context: Context,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner,
    previewView: PreviewView,
    onItemDetected: (String) -> Unit // Callback for detected items
) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    cameraProviderFuture.addListener({
        val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        val imageAnalyzer = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also {
                it.setAnalyzer(Executors.newSingleThreadExecutor(), GroceryImageAnalyzer { item ->
                    onItemDetected(item) // Pass the detected item to the callback
                })
            }

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageAnalyzer // Bind the analyzer
            )
        } catch (exc: Exception) {
            Log.e("CameraScanScreen", "Use case binding failed", exc)
        }
    }, ContextCompat.getMainExecutor(context))
}
