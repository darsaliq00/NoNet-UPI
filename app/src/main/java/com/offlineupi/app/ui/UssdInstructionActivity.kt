package com.offlineupi.app.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.offlineupi.app.R
import com.offlineupi.app.databinding.ActivityUssdInstructionBinding

/**
 * Displays step-by-step USSD instructions for completing the UPI payment
 * after the system dialer has been launched.
 *
 * SECURITY:
 * - No UPI PIN is displayed, suggested, or captured.
 * - The UPI ID shown here is only for user reference; it is not stored.
 * - This screen makes NO network calls and has NO USSD automation.
 */
class UssdInstructionActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_PAYEE_ADDRESS = "extra_payee_address"
        const val EXTRA_AMOUNT = "extra_amount"
        /**
         * Boolean extra: true when UPI ID was automatically copied to clipboard
         * by [ConfirmationActivity]. Controls visibility of the clipboard badge.
         */
        const val EXTRA_CLIPBOARD_COPIED = "extra_clipboard_copied"
    }

    private lateinit var binding: ActivityUssdInstructionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUssdInstructionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val payeeAddress = intent.getStringExtra(EXTRA_PAYEE_ADDRESS) ?: ""
        val amount = intent.getStringExtra(EXTRA_AMOUNT)
        val clipboardCopied = intent.getBooleanExtra(EXTRA_CLIPBOARD_COPIED, true)

        setupInstructions(payeeAddress, amount, clipboardCopied)

        binding.btnDone.setOnClickListener {
            finishAffinity()
            startActivity(android.content.Intent(this, MainActivity::class.java))
        }
    }

    private fun setupInstructions(payeeAddress: String, amount: String?, clipboardCopied: Boolean) {
        // Step 1
        binding.tvStep1.text = getString(R.string.ussd_step1)

        // Step 2: Updated paste-from-clipboard instruction
        binding.tvStep2.text = getString(R.string.ussd_step2_clipboard, payeeAddress)

        // Clipboard copied badge
        binding.tvClipboardBadge.isVisible = clipboardCopied
        binding.tvClipboardBadge.text = getString(R.string.ussd_clipboard_copied_badge)

        // Step 3
        binding.tvStep3.text = if (!amount.isNullOrBlank()) {
            getString(R.string.ussd_step3_with_amount, amount)
        } else {
            getString(R.string.ussd_step3_no_amount)
        }

        // Step 4
        binding.tvStep4.text = getString(R.string.ussd_step4)

        // Disclaimer & security note
        binding.tvDisclaimer.text = getString(R.string.ussd_sms_disclaimer)
        binding.tvSecurityNote.text = getString(R.string.ussd_security_note)
    }
}
