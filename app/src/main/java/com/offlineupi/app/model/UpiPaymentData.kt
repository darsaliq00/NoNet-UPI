package com.offlineupi.app.model

/**
 * Holds parsed UPI payment data extracted from a scanned QR code.
 *
 * SECURITY: Do not log, persist, or transmit this data.
 * Fields are read-only after construction to prevent accidental mutation.
 *
 * @param payeeAddress  UPI VPA / ID (pa parameter)
 * @param payeeName     Human-readable payee name (pn parameter)
 * @param amount        Payment amount in INR, nullable if not present in QR (am parameter)
 */
data class UpiPaymentData(
    val payeeAddress: String,
    val payeeName: String,
    val amount: String?
) {
    /**
     * Returns true if the amount was provided by the QR code (not null/blank).
     */
    val hasAmount: Boolean get() = !amount.isNullOrBlank()
}
