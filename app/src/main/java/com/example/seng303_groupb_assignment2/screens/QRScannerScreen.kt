package com.example.seng303_groupb_assignment2.screens

import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

@OptIn(ExperimentalGetImage::class)
@Composable
fun QRScannerScreen(onQRCodeScanned: (String) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = {
                previewView.apply {
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(this.surfaceProvider)
                    }
                    val barcodeScanner = BarcodeScanning.getClient()

                    val imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build().also {
                            it.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
                                val mediaImage = imageProxy.image
                                if (mediaImage != null) {
                                    val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                                    barcodeScanner.process(image)
                                        .addOnSuccessListener { barcodes ->
                                            for (barcode in barcodes) {
                                                val qrCodeValue = barcode.rawValue
                                                val boundingBox = barcode.boundingBox
                                                if (qrCodeValue != null && boundingBox != null) {
                                                    // Calculate box position in pixels
                                                    val boxSizeDp = 200
                                                    val density = context.resources.displayMetrics.density
                                                    val boxSizePx = (boxSizeDp * density).toInt()
                                                    val centerX = previewView.width / 2
                                                    val centerY = previewView.height / 2 + 180f

                                                    // Define the overlay box area in pixels
                                                    val boxLeft = centerX - boxSizePx / 2
                                                    val boxTop = centerY - boxSizePx / 2
                                                    val boxRight = centerX + boxSizePx / 2
                                                    val boxBottom = centerY + boxSizePx / 2

                                                    // Scale bounding box relative to the PreviewView
                                                    val scaleX = previewView.width.toFloat() / mediaImage.width
                                                    val scaleY = previewView.height.toFloat() / mediaImage.height

                                                    val scaledLeft = (boundingBox.left * scaleX).toInt()
                                                    val scaledTop = (boundingBox.top * scaleY).toInt()
                                                    val scaledRight = (boundingBox.right * scaleX).toInt()
                                                    val scaledBottom = (boundingBox.bottom * scaleY).toInt()

                                                    // Check if barcode bounding box is within the overlay box
                                                    if (scaledLeft >= boxLeft && scaledRight <= boxRight &&
                                                        scaledTop >= boxTop && scaledBottom <= boxBottom) {
                                                        onQRCodeScanned(qrCodeValue)
                                                    }
                                                }
                                            }
                                        }
                                        .addOnFailureListener {
                                            // Handle failure
                                        }
                                        .addOnCompleteListener {
                                            imageProxy.close()
                                        }
                                }
                            }
                        }

                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageAnalysis)
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // QR code scanning box overlay
        Box(
            modifier = Modifier
                .size(200.dp) // Size of the overlay
                .align(Alignment.Center)
                .border(2.dp, Color.White, RoundedCornerShape(8.dp))
        )
    }
}
