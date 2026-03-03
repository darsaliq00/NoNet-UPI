package com.offlineupi.app.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.offlineupi.app.databinding.ActivityMainBinding

/**
 * Home screen of the UPI Offline Assistant app.
 * Contains a single entry point: the "Scan QR (Offline UPI)" button,
 * and an "About" button for app information.
 *
 * SECURITY: No sensitive data is created or stored in this Activity.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnScanQr.setOnClickListener {
            startActivity(Intent(this, ScanQrActivity::class.java))
        }

        binding.btnAbout.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }
    }
}
