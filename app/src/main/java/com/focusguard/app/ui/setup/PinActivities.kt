package com.focusguard.app.ui.setup

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.focusguard.app.databinding.ActivityPinSetupBinding
import com.focusguard.app.util.PinManager

class PinSetupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPinSetupBinding
    private var firstPin = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPinSetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvTitle.text = "Créez votre code PIN"
        binding.tvSubtitle.text = "Ce PIN protège FocusGuard contre les modifications non autorisées"

        binding.btnConfirm.setOnClickListener {
            val pin = binding.etPin.text.toString()
            if (pin.length < 4) {
                Toast.makeText(this, "Le PIN doit contenir au moins 4 chiffres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (firstPin.isEmpty()) {
                firstPin = pin
                binding.tvTitle.text = "Confirmez votre PIN"
                binding.etPin.text?.clear()
            } else {
                if (firstPin == pin) {
                    PinManager.setPin(this, pin)
                    Toast.makeText(this, "✅ PIN configuré avec succès !", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "❌ Les PINs ne correspondent pas", Toast.LENGTH_SHORT).show()
                    firstPin = ""
                    binding.tvTitle.text = "Créez votre code PIN"
                    binding.etPin.text?.clear()
                }
            }
        }
    }
}

class PinVerifyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPinSetupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPinSetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvTitle.text = "Entrez votre code PIN"
        binding.tvSubtitle.text = "Pour accéder aux paramètres de FocusGuard"

        binding.btnConfirm.setOnClickListener {
            val pin = binding.etPin.text.toString()
            if (PinManager.verifyPin(this, pin)) {
                setResult(RESULT_OK)
                finish()
            } else {
                Toast.makeText(this, "❌ PIN incorrect", Toast.LENGTH_SHORT).show()
                binding.etPin.text?.clear()
            }
        }
    }
}
