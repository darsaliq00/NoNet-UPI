package com.offlineupi.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.offlineupi.app.model.UpiPaymentData
import com.offlineupi.app.repository.UpiQrParser

/**
 * ViewModel for [ScanQrActivity].
 * Processes raw barcode scan results through [UpiQrParser]
 * and exposes scan state to the UI via LiveData.
 */
class ScanQrViewModel : ViewModel() {

    sealed class ScanState {
        object Idle : ScanState()
        data class Success(val data: UpiPaymentData) : ScanState()
        data class Error(val message: String) : ScanState()
    }

    private val _scanState = MutableLiveData<ScanState>(ScanState.Idle)
    val scanState: LiveData<ScanState> = _scanState

    // Flag to prevent processing multiple barcodes rapidly
    private var isProcessing = false

    /**
     * Process a raw QR string from the barcode scanner.
     * Validates and parses the UPI URI; updates [scanState].
     *
     * SECURITY: This function does not log raw QR content.
     */
    fun processQrResult(rawQr: String) {
        if (isProcessing) return
        isProcessing = true

        val result = UpiQrParser.parse(rawQr)
        result.fold(
            onSuccess = { data ->
                _scanState.value = ScanState.Success(data)
            },
            onFailure = { exception ->
                _scanState.value = ScanState.Error(exception.message ?: "Unknown error.")
                // Allow retrying after an error
                isProcessing = false
            }
        )
    }

    /** Reset to idle state to allow a new scan attempt. */
    fun reset() {
        isProcessing = false
        _scanState.value = ScanState.Idle
    }
}
