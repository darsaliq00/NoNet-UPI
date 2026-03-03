package com.offlineupi.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.offlineupi.app.model.UpiPaymentData

/**
 * ViewModel for [ConfirmationActivity].
 * Holds the parsed [UpiPaymentData] and validates user-entered amount
 * when the QR did not include one.
 */
class ConfirmationViewModel : ViewModel() {

    private val _paymentData = MutableLiveData<UpiPaymentData>()
    val paymentData: LiveData<UpiPaymentData> = _paymentData

    private val _amountError = MutableLiveData<String?>()
    val amountError: LiveData<String?> = _amountError

    fun setPaymentData(data: UpiPaymentData) {
        _paymentData.value = data
    }

    /**
     * Validates the user-provided amount when QR did not include one.
     * Returns the validated amount string, or null if invalid.
     * SECURITY: Does not log the amount.
     */
    fun validateAndGetAmount(userInput: String): String? {
        val trimmed = userInput.trim()
        if (trimmed.isBlank()) {
            _amountError.value = "Please enter an amount."
            return null
        }
        val amount = trimmed.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            _amountError.value = "Enter a valid amount greater than 0."
            return null
        }
        _amountError.value = null
        return trimmed
    }

    fun clearAmountError() {
        _amountError.value = null
    }
}
