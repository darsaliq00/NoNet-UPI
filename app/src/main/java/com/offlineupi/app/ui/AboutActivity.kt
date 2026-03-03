package com.offlineupi.app.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.offlineupi.app.BuildConfig
import com.offlineupi.app.R
import com.offlineupi.app.databinding.ActivityAboutBinding

/**
 * Static informational screen — no ViewModel required.
 *
 * SECURITY: Opens URLs in the system browser via ACTION_VIEW.
 * No internet permission is added to the app; the system browser handles networking.
 * No sensitive data is created or stored here.
 */
class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Back navigation via toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Set version dynamically
        binding.tvVersion.text = getString(R.string.about_version_label) + " " + BuildConfig.VERSION_NAME

        // GitHub button — opens in system browser (no INTERNET permission needed in this app)
        binding.btnGithub.setOnClickListener {
            openUrl("https://github.com/darsaliq00/NoNet-UPI/tree/main")
        }

        // Telegram button — opens Telegram app or browser
        binding.btnTelegram.setOnClickListener {
            openUrl("https://t.me/CYPHER_222")
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}
