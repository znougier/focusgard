package com.focusguard.app.ui.blocked

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.focusguard.app.databinding.ActivityBlockedBinding
import com.focusguard.app.util.PinManager
import android.text.InputType
import android.widget.EditText

class BlockedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBlockedBinding
    private var packageName: String = ""
    private var appName: String = ""
    private var blockReason: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBlockedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        packageName = intent.getStringExtra("package_name") ?: ""
        appName = intent.getStringExtra("app_name") ?: "cette application"
        blockReason = intent.getStringExtra("block_reason") ?: ""
        val message = intent.getStringExtra("message") ?: ""

        setupUI(message)
    }

    private fun setupUI(message: String) {
        binding.tvAppName.text = appName
        binding.tvMotivation.text = message

        when {
            blockReason.startsWith("limit_") -> {
                val mins = blockReason.removePrefix("limit_")
                binding.tvBlockReason.text = "⏱️ Limite quotidienne de $mins min atteinte"
            }
            blockReason == "schedule" -> {
                binding.tvBlockReason.text = "📅 Bloquée selon ton planning"
            }
            else -> {
                binding.tvBlockReason.text = "🛡️ Application bloquée par FocusGuard"
            }
        }

        // Check if already in emergency mode
        if (PinManager.isInEmergencyMode(this)) {
            val remaining = PinManager.getRemainingEmergencySeconds(this)
            showEmergencyActive(remaining)
        }

        binding.btnGoBack.setOnClickListener {
            goToHome()
        }

        binding.btnEmergency.setOnClickListener {
            showEmergencyDialog()
        }
    }

    private fun showEmergencyDialog() {
        val pinInput = EditText(this).apply {
            hint = "Code PIN"
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        }

        AlertDialog.Builder(this)
            .setTitle("🚨 Mode urgence")
            .setMessage("Entrez votre PIN pour débloquer pendant 5 minutes.")
            .setView(pinInput)
            .setPositiveButton("Débloquer") { _, _ ->
                val pin = pinInput.text.toString()
                if (PinManager.verifyPin(this, pin)) {
                    PinManager.grantEmergency(this)
                    Toast.makeText(this, "✅ Débloqué pour 5 minutes", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "❌ PIN incorrect", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    private fun showEmergencyActive(remainingSeconds: Int) {
        binding.btnEmergency.visibility = View.GONE
        binding.tvEmergencyTimer.visibility = View.VISIBLE

        object : CountDownTimer(remainingSeconds * 1000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secs = millisUntilFinished / 1000
                val mins = secs / 60
                val s = secs % 60
                binding.tvEmergencyTimer.text = "Mode urgence: ${mins}m ${s}s restantes"
            }
            override fun onFinish() {
                binding.tvEmergencyTimer.text = "Mode urgence expiré"
                binding.btnEmergency.visibility = View.VISIBLE
            }
        }.start()
    }

    private fun goToHome() {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        goToHome()
    }
}
