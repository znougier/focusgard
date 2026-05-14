package com.focusguard.app.util

import android.content.Context
import com.focusguard.app.data.model.Schedule
import java.text.SimpleDateFormat
import java.util.*

object BlockChecker {

    fun isCurrentlyScheduleBlocked(schedules: List<Schedule>, packageName: String): Boolean {
        val now = Calendar.getInstance()
        val currentHour = now.get(Calendar.HOUR_OF_DAY)
        val currentMinute = now.get(Calendar.MINUTE)
        val currentDay = now.get(Calendar.DAY_OF_WEEK) // 1=Sun, 2=Mon...
        val androidDay = currentDay.toString()

        val currentTotalMinutes = currentHour * 60 + currentMinute

        for (schedule in schedules) {
            if (!schedule.isEnabled) continue

            // Check day
            val days = schedule.daysOfWeek.split(",")
            if (!days.contains(androidDay)) continue

            // Check if this schedule applies to this package
            if (!schedule.applyToAll) {
                val pkgs = schedule.packageNames.split(",")
                if (!pkgs.contains(packageName)) continue
            }

            // Check time range (handles overnight ranges like 22:00 - 07:00)
            val startTotal = schedule.startHour * 60 + schedule.startMinute
            val endTotal = schedule.endHour * 60 + schedule.endMinute

            val inRange = if (startTotal <= endTotal) {
                currentTotalMinutes in startTotal..endTotal
            } else {
                // overnight: e.g. 22:00 to 07:00
                currentTotalMinutes >= startTotal || currentTotalMinutes <= endTotal
            }

            if (inRange) return true
        }
        return false
    }

    fun todayDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    fun isLimitExceeded(usedMinutes: Int, limitMinutes: Int): Boolean {
        if (limitMinutes <= 0) return false
        return usedMinutes >= limitMinutes
    }

    // Motivational messages shown on block screen
    val motivationalMessages = listOf(
        "🌅 Profite de ce matin sans distraction. Tu t'en remercieras.",
        "💪 Chaque minute récupérée est une minute investie en toi.",
        "🧠 Ton cerveau mérite un vrai repos. Pas un scroll infini.",
        "⏰ Ce blocage est un cadeau que tu t'es fait hier soir.",
        "🌿 Respire. Le monde numérique peut attendre.",
        "🚀 Les grandes choses commencent par de petites disciplines.",
        "✨ Tu contrôles ta vie, pas l'algorithme.",
        "📖 Ce temps libre est à toi. Qu'est-ce qui compte vraiment ?",
        "🎯 Focus. C'est ça qui change tout.",
        "🏆 Les champions choisissent la discipline avant le plaisir."
    )

    fun getRandomMessage(): String = motivationalMessages.random()
}
