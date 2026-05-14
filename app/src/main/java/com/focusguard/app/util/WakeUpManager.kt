package com.focusguard.app.util

import android.content.Context
import androidx.core.content.edit

/**
 * Gère la détection du réveil et le blocage matinal.
 *
 * Logique :
 * - Le "réveil" = premier déverrouillage du téléphone entre MORNING_START_HOUR et MORNING_END_HOUR
 * - Une fois détecté, les apps sont bloquées pendant wakeBlockDurationMinutes
 * - Un seul réveil est enregistré par jour (réinitialisation à minuit)
 */
object WakeUpManager {

    private const val PREF_NAME = "focusguard_wakeup"

    // Heure min/max pour considérer un déverrouillage comme un "réveil"
    const val MORNING_START_HOUR = 4   // 4h du matin
    const val MORNING_END_HOUR   = 11  // 11h du matin

    // Clés SharedPreferences
    private const val KEY_WAKE_ENABLED       = "wake_block_enabled"
    private const val KEY_WAKE_DURATION_MINS = "wake_block_duration_minutes"
    private const val KEY_WAKE_TIMESTAMP     = "wake_detected_timestamp"   // epoch ms
    private const val KEY_WAKE_DATE          = "wake_detected_date"        // "yyyy-MM-dd"

    // ── Paramètres ────────────────────────────────────────────────────────────

    fun isWakeBlockEnabled(context: Context): Boolean =
        prefs(context).getBoolean(KEY_WAKE_ENABLED, true)

    fun setWakeBlockEnabled(context: Context, enabled: Boolean) =
        prefs(context).edit { putBoolean(KEY_WAKE_ENABLED, enabled) }

    /** Durée du blocage matinal en minutes (défaut : 60 min) */
    fun getWakeBlockDuration(context: Context): Int =
        prefs(context).getInt(KEY_WAKE_DURATION_MINS, 60)

    fun setWakeBlockDuration(context: Context, minutes: Int) =
        prefs(context).edit { putInt(KEY_WAKE_DURATION_MINS, minutes) }

    // ── Détection du réveil ───────────────────────────────────────────────────

    /**
     * À appeler à chaque déverrouillage de l'écran.
     * Enregistre le réveil si :
     *  1. Le blocage matinal est activé
     *  2. On est dans la fenêtre horaire matin
     *  3. Aucun réveil n'a déjà été enregistré aujourd'hui
     * @return true si c'est le réveil du jour (nouveau)
     */
    fun onScreenUnlocked(context: Context): Boolean {
        if (!isWakeBlockEnabled(context)) return false

        val now = java.util.Calendar.getInstance()
        val hour = now.get(java.util.Calendar.HOUR_OF_DAY)
        if (hour < MORNING_START_HOUR || hour >= MORNING_END_HOUR) return false

        val today = BlockChecker.todayDateString()
        val alreadyDetected = prefs(context).getString(KEY_WAKE_DATE, null) == today
        if (alreadyDetected) return false

        // Nouveau réveil !
        prefs(context).edit {
            putLong(KEY_WAKE_TIMESTAMP, System.currentTimeMillis())
            putString(KEY_WAKE_DATE, today)
        }
        return true
    }

    // ── État du blocage matinal ───────────────────────────────────────────────

    /**
     * Renvoie true si le blocage matinal est actuellement actif.
     * C'est le cas si : réveil détecté aujourd'hui ET durée pas encore écoulée.
     */
    fun isMorningBlockActive(context: Context): Boolean {
        if (!isWakeBlockEnabled(context)) return false

        val today = BlockChecker.todayDateString()
        val wakeDate = prefs(context).getString(KEY_WAKE_DATE, null) ?: return false
        if (wakeDate != today) return false

        val wakeTs = prefs(context).getLong(KEY_WAKE_TIMESTAMP, 0L)
        if (wakeTs == 0L) return false

        val durationMs = getWakeBlockDuration(context) * 60_000L
        return System.currentTimeMillis() < wakeTs + durationMs
    }

    /** Secondes restantes avant la fin du blocage matinal (0 si inactif) */
    fun getRemainingMorningSeconds(context: Context): Long {
        if (!isMorningBlockActive(context)) return 0L
        val wakeTs = prefs(context).getLong(KEY_WAKE_TIMESTAMP, 0L)
        val endTs   = wakeTs + getWakeBlockDuration(context) * 60_000L
        return ((endTs - System.currentTimeMillis()) / 1000L).coerceAtLeast(0L)
    }

    /** Heure de réveil détectée aujourd'hui, ou null */
    fun getTodayWakeTime(context: Context): Long? {
        val today = BlockChecker.todayDateString()
        if (prefs(context).getString(KEY_WAKE_DATE, null) != today) return null
        val ts = prefs(context).getLong(KEY_WAKE_TIMESTAMP, 0L)
        return if (ts > 0L) ts else null
    }

    /** Réinitialise manuellement (pour les tests ou si l'utilisateur veut relancer) */
    fun resetToday(context: Context) =
        prefs(context).edit {
            remove(KEY_WAKE_TIMESTAMP)
            remove(KEY_WAKE_DATE)
        }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
}
