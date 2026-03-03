package com.offlineupi.app.ui

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.offlineupi.app.R
import com.offlineupi.app.databinding.ActivityConfirmationBinding
import com.offlineupi.app.model.UpiPaymentData
import com.offlineupi.app.viewmodel.ConfirmationViewModel

/**
 * Confirmation screen showing parsed UPI payment details.
 * When the user taps "Pay via USSD (*99#)":
 *  A. Copies UPI ID to clipboard automatically.
 *  B. Shows a Toast instructing the user to paste when prompted.
 *  C. Requests CALL_PHONE permission if not already granted.
 *  D. Auto-dials *99# using Intent.ACTION_CALL (falls back to ACTION_DIAL if denied).
 *  E. Navigates to UssdInstructionActivity.
 *  F. Clears clipboard after 60 seconds (privacy).
 *
 * SECURITY:
 * - No UPI PIN is captured here.
 * - Payment data is NOT persisted to disk or sent over network.
 * - Clipboard is cleared after 60 seconds to minimise exposure window.
 * - UPI ID is NOT logged.
 */
class ConfirmationActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_PAYEE_ADDRESS = "extra_payee_address"
        const val EXTRA_PAYEE_NAME = "extra_payee_name"
        const val EXTRA_AMOUNT = "extra_amount"
        private const val CLIPBOARD_CLEAR_DELAY_MS = 60_000L
    }

    private lateinit var binding: ActivityConfirmationBinding
    private val viewModel: ConfirmationViewModel by viewModels()

    private val clipboardClearHandler = Handler(Looper.getMainLooper())
    private val clearClipboardRunnable = Runnable { clearClipboard() }

    // Holds the resolved payment data while waiting for permission result
    private var pendingPayeeAddress: String? = null
    private var pendingAmount: String? = null

    /** Launcher for CALL_PHONE runtime permission. */
    private val callPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            val address = pendingPayeeAddress ?: return@registerForActivityResult
            val amount = pendingAmount
            if (granted) {
                autoDialUssd()
            } else {
                // Permission denied — fall back to manual dialer
                Toast.makeText(this, getString(R.string.toast_dial_fallback), Toast.LENGTH_LONG).show()
                fallbackDialer()
            }
            navigateToUssdInstructions(address, amount)
            scheduleClearClipboard()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val payeeAddress = intent.getStringExtra(EXTRA_PAYEE_ADDRESS)
        val payeeName   = intent.getStringExtra(EXTRA_PAYEE_NAME)
        val amount      = intent.getStringExtra(EXTRA_AMOUNT)

        if (payeeAddress.isNullOrBlank()) { finish(); return }

        val data = UpiPaymentData(
            payeeAddress = payeeAddress,
            payeeName    = payeeName ?: "Unknown Payee",
            amount       = amount
        )
        viewModel.setPaymentData(data)
        setupUI(data)
        observeViewModel()
    }

    private fun setupUI(data: UpiPaymentData) {
        binding.tvPayeeName.text = data.payeeName
        binding.tvUpiId.text    = data.payeeAddress

        if (data.hasAmount) {
            binding.tvAmount.text      = getString(R.string.currency_format, data.amount)
            binding.tvAmount.isVisible = true
            binding.tilAmountInput.isVisible = false
        } else {
            binding.tvAmount.isVisible = false
            binding.tilAmountInput.isVisible = true
            binding.etAmount.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = viewModel.clearAmountError()
                override fun afterTextChanged(s: Editable?) = Unit
            })
        }

        binding.btnBack.setOnClickListener { finish() }
        binding.btnPayUssd.setOnClickListener { handlePayment(data) }
    }

    private fun observeViewModel() {
        viewModel.amountError.observe(this) { error ->
            binding.tilAmountInput.error = error
        }
    }

    private fun handlePayment(data: UpiPaymentData) {
        val finalAmount: String? = if (data.hasAmount) {
            data.amount
        } else {
            viewModel.validateAndGetAmount(binding.etAmount.text.toString()) ?: return
        }

        // Store pending values for the permission callback
        pendingPayeeAddress = data.payeeAddress
        pendingAmount       = finalAmount

        // A: Copy UPI ID to clipboard
        copyUpiIdToClipboard(data.payeeAddress)

        // B: Inform user
        Toast.makeText(this, getString(R.string.toast_upi_id_copied), Toast.LENGTH_LONG).show()

        // C+D: Check / request CALL_PHONE and dial
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
            == PackageManager.PERMISSION_GRANTED
        ) {
            // Already granted — auto-dial immediately
            autoDialUssd()
            navigateToUssdInstructions(data.payeeAddress, finalAmount)
            scheduleClearClipboard()
        } else {
            // Request permission; dialing + navigation happen in the launcher callback
            callPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
        }
    }

    /**
     * Automatically initiates the USSD call using ACTION_CALL.
     * Requires CALL_PHONE permission — granted before this is called.
     * *99# is encoded as tel:*99%23.
     */
    private fun autoDialUssd() {
        val callIntent = Intent(Intent.ACTION_CALL, Uri.parse("tel:*99%23"))
        startActivity(callIntent)
    }

    /**
     * Fallback: opens the dialer pre-filled with *99# when CALL_PHONE is denied.
     * User must tap the call button manually.
     */
    private fun fallbackDialer() {
        startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:*99%23")))
    }

    /** Copies UPI VPA to clipboard. VPA is NOT logged. */
    private fun copyUpiIdToClipboard(payeeAddress: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("UPI_ID", payeeAddress))
    }

    private fun scheduleClearClipboard() {
        clipboardClearHandler.removeCallbacks(clearClipboardRunnable)
        clipboardClearHandler.postDelayed(clearClipboardRunnable, CLIPBOARD_CLEAR_DELAY_MS)
    }

    private fun clearClipboard() {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("", ""))
    }

    private fun navigateToUssdInstructions(payeeAddress: String, amount: String?) {
        startActivity(Intent(this, UssdInstructionActivity::class.java).apply {
            putExtra(UssdInstructionActivity.EXTRA_PAYEE_ADDRESS, payeeAddress)
            putExtra(UssdInstructionActivity.EXTRA_AMOUNT, amount)
            putExtra(UssdInstructionActivity.EXTRA_CLIPBOARD_COPIED, true)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        clipboardClearHandler.removeCallbacks(clearClipboardRunnable)
    }
}
