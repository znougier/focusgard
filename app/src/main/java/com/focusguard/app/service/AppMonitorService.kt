package com.focusguard.app.service

import android.app.*
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.focusguard.app.R
import com.focusguard.app.data.repository.AppRepository
import com.focusguard.app.receiver.ScreenUnlockReceiver
import com.focusguard.app.ui.blocked.BlockedActivity
import com.focusguard.app.util.BlockChecker
import com.focusguard.app.util.PinManager
import com.focusguard.app.util.WakeUpManager
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

class AppMonitorService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private lateinit var repository: AppRepository
    private var monitorJob: Job? = null
    private var lastForegroundApp = ""
    private var usageStatsManager: UsageStatsManager? = null

    companion object {
        const val CHANNEL_ID = "focusguard_monitor"
        const val NOTIFICATION_ID = 1001
        private const val TAG = "AppMonitorService"
        private const val CHECK_INTERVAL_MS = 1000L

        fun start(context: Context) {
            val intent = Intent(context, AppMonitorService::class.java)
            context.startForegroundService(intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, AppMonitorService::class.java)
            context.stopService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        repository = AppRepository(this)
        usageStatsManager = getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, buildNotification())
        startMonitoring()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    private fun startMonitoring() {
        monitorJob?.cancel()
        monitorJob = serviceScope.launch {
            while (isActive) {
                try {
                    checkCurrentApp()
                } catch (e: Exception) {
                    Log.e(TAG, "Monitor error: ${e.message}")
                }
                delay(CHECK_INTERVAL_MS)
            }
        }
    }

    private suspend fun checkCurrentApp() {
        val currentApp = getForegroundApp() ?: return
        if (currentApp == packageName) return // Don't block ourselves
        if (currentApp == lastForegroundApp) return // Same app, already checked

        lastForegroundApp = currentApp

        // Check if emergency mode is active
        if (PinManager.isInEmergencyMode(this)) return

        // Get active blocked apps and schedules
        val blockedApps = repository.getActiveBlocked()
        val blockedPackages = blockedApps.map { it.packageName }

        if (!blockedPackages.contains(currentApp)) return

        val blockedApp = blockedApps.first { it.packageName == currentApp }
        val schedules = repository.getActiveSchedules()

        var shouldBlock = false
        var blockReason = ""

        // Check schedule blocking
        if (BlockChecker.isCurrentlyScheduleBlocked(schedules, currentApp)) {
            shouldBlock = true
            blockReason = "schedule"
        }

        // Check daily limit
        if (!shouldBlock && blockedApp.dailyLimitMinutes > 0) {
            val today = BlockChecker.todayDateString()
            val usedMinutes = repository.getTodayMinutes(currentApp, today)
            if (BlockChecker.isLimitExceeded(usedMinutes, blockedApp.dailyLimitMinutes)) {
                shouldBlock = true
                blockReason = "limit_${blockedApp.dailyLimitMinutes}"
            }
        }

        if (shouldBlock) {
            launchBlockScreen(currentApp, blockedApp.appName, blockReason)
        }
    }

    private fun launchBlockScreen(packageName: String, appName: String, reason: String) {
        val intent = Intent(this, BlockedActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra("package_name", packageName)
            putExtra("app_name", appName)
            putExtra("block_reason", reason)
            putExtra("message", BlockChecker.getRandomMessage())
        }
        startActivity(intent)
    }

    private fun getForegroundApp(): String? {
        val usm = usageStatsManager ?: return null
        val now = System.currentTimeMillis()
        val stats = usm.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            now - TimeUnit.SECONDS.toMillis(10),
            now
        )
        return stats?.maxByOrNull { it.lastTimeUsed }?.packageName
    }

    // Track usage every minute via a separate coroutine
    fun trackUsage(packageName: String, appName: String) {
        serviceScope.launch {
            val today = BlockChecker.todayDateString()
            val existing = repository.getUsageByDate(today)
            // Usage tracking is handled by querying UsageStatsManager directly
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "FocusGuard Monitor",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Surveillance active des applications"
            setShowBadge(false)
        }
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    private fun buildNotification(): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            packageManager.getLaunchIntentForPackage(packageName),
            PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("FocusGuard actif")
            .setContentText("Protection en cours...")
            .setSmallIcon(R.drawable.ic_shield)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
}
