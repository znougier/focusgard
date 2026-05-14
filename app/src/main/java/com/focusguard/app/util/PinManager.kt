package com.focusguard.app.util

import android.content.Context
import androidx.core.content.edit

object PinManager {
    private const val PREF_NAME = "focusguard_prefs"
    private const val KEY_PIN = "user_pin"
    private const val KEY_PIN_SET = "pin_set"
    private const val KEY_SERVICE_RUNNING = "service_running"
    private const val KEY_EMERGENCY_TIMESTAMP = "emergency_timestamp"

    fun isPinSet(context: Context): Boolean {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_PIN_SET, false)
    }

    fun setPin(context: Context, pin: String) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit {
            putString(KEY_PIN, pin.hashCode().toString())
            putBoolean(KEY_PIN_SET, true)
        }
    }

    fun verifyPin(context: Context, pin: String): Boolean {
        val stored = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(KEY_PIN, null) ?: return false
        return stored == pin.hashCode().toString()
    }

    fun isServiceEnabled(context: Context): Boolean {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_SERVICE_RUNNING, true)
    }

    fun setServiceEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit {
            putBoolean(KEY_SERVICE_RUNNING, enabled)
        }
    }

    // Emergency mode: stores timestamp when emergency was granted
    fun grantEmergency(context: Context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit {
            putLong(KEY_EMERGENCY_TIMESTAMP, System.currentTimeMillis())
        }
    }

    fun isInEmergencyMode(context: Context): Boolean {
        val ts = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getLong(KEY_EMERGENCY_TIMESTAMP, 0L)
        if (ts == 0L) return false
        val elapsed = System.currentTimeMillis() - ts
        return elapsed < 5 * 60 * 1000L // 5 minutes
    }

    fun getRemainingEmergencySeconds(context: Context): Int {
        val ts = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getLong(KEY_EMERGENCY_TIMESTAMP, 0L)
        if (ts == 0L) return 0
        val elapsed = System.currentTimeMillis() - ts
        val remaining = (5 * 60 * 1000L - elapsed) / 1000
        return if (remaining > 0) remaining.toInt() else 0
    }
}
