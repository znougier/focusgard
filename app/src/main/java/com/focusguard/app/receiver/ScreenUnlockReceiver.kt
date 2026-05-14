package com.focusguard.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.focusguard.app.util.WakeUpManager

/**
 * Se déclenche à chaque déverrouillage de l'écran (ACTION_USER_PRESENT).
 * Délègue à WakeUpManager pour savoir si c'est le réveil du jour.
 *
 * Note : ce receiver est enregistré dynamiquement dans AppMonitorService
 * (ACTION_USER_PRESENT ne peut pas être déclaré dans le Manifest depuis Android 8+).
 */
class ScreenUnlockReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_USER_PRESENT) return

        val isNewWakeUp = WakeUpManager.onScreenUnlocked(context)
        if (isNewWakeUp) {
            val wakeTs = WakeUpManager.getTodayWakeTime(context)
            val duration = WakeUpManager.getWakeBlockDuration(context)
            Log.i(TAG, "🌅 Réveil détecté ! Blocage matinal actif pendant $duration minutes. (ts=$wakeTs)")
        }
    }

    companion object {
        private const val TAG = "ScreenUnlockReceiver"
    }
}
