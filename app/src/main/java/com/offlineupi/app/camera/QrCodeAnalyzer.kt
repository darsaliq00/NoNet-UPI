package com.offlineupi.app.camera

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

/**
 * CameraX ImageAnalysis.Analyzer that uses ML Kit barcode scanning
 * to detect QR codes from camera frames.
 *
 * ML Kit's [BarcodeScanning] SDK is bundled — works fully OFFLINE.
 * No network access is made.
 *
 * SECURITY: Only the decoded raw string is forwarded; no frame data is stored.
 *
 * @param onQrDetected Callback invoked on the calling thread when a QR code is found.
 */
class QrCodeAnalyzer(
    private val onQrDetected: (rawQr: String) -> Unit
) : ImageAnalysis.Analyzer {

    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .build()

    private val scanner = BarcodeScanning.getClient(options)

    @androidx.camera.core.ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image ?: run {
            imageProxy.close()
            return
        }

        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                // Take the first QR code found
                val qrCode = barcodes.firstOrNull()
                val rawValue = qrCode?.rawValue
                if (!rawValue.isNullOrBlank()) {
                    onQrDetected(rawValue)
                }
            }
            .addOnFailureListener {
                // Ignore transient scan failures; next frame will be analyzed
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }
}
