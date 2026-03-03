package com.offlineupi.app.repository

import android.net.Uri
import com.offlineupi.app.model.UpiPaymentData

/**
 * Parses a raw QR code string and validates it as a UPI payment URI.
 *
 * SECURITY:
 * - Does NOT log payee address, amount, or any payment parameters.
 * - Does NOT make network calls.
 * - Does NOT store any parsed data beyond what is returned as Result.
 *
 * Expected UPI URI format:
 *   upi://pay?pa=<vpa>&pn=<name>&am=<amount>&...
 */
object UpiQrParser {

    private const val UPI_SCHEME = "upi"
    private const val UPI_HOST = "pay"

    /**
     * Parses the given QR string.
     *
     * @param rawQr The raw string decoded from the barcode scanner.
     * @return [Result.success] with [UpiPaymentData] on valid UPI QR,
     *         [Result.failure] with a descriptive [Exception] on invalid input.
     */
    fun parse(rawQr: String): Result<UpiPaymentData> {
        if (rawQr.isBlank()) {
            return Result.failure(Exception("Empty QR code data."))
        }

        // Validate UPI URI structure
        val uri: Uri = try {
            Uri.parse(rawQr)
        } catch (e: Exception) {
            return Result.failure(Exception("Invalid QR: Could not parse URI."))
        }

        if (uri.scheme?.lowercase() != UPI_SCHEME) {
            return Result.failure(Exception("Invalid QR: Not a UPI payment code."))
        }

        if (uri.host?.lowercase() != UPI_HOST) {
            return Result.failure(Exception("Invalid QR: Not a UPI payment code."))
        }

        val payeeAddress = uri.getQueryParameter("pa")
        if (payeeAddress.isNullOrBlank()) {
            return Result.failure(Exception("Invalid QR: Missing UPI ID (pa)."))
        }

        // Minimal VPA validation: must contain '@'
        if (!payeeAddress.contains('@')) {
            return Result.failure(Exception("Invalid QR: UPI ID format is incorrect."))
        }

        val payeeName = uri.getQueryParameter("pn")?.trim()?.ifBlank { null }

        // amount is optional — null is acceptable
        val amount = uri.getQueryParameter("am")?.trim()?.ifBlank { null }

        // SECURITY: Do NOT log payeeAddress or amount
        return Result.success(
            UpiPaymentData(
                payeeAddress = payeeAddress.trim(),
                payeeName = payeeName ?: "Unknown Payee",
                amount = amount
            )
        )
    }
}
