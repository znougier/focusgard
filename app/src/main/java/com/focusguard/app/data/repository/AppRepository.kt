package com.focusguard.app.data.repository

import android.content.Context
import com.focusguard.app.data.db.AppDatabase
import com.focusguard.app.data.model.BlockedApp
import com.focusguard.app.data.model.Schedule
import com.focusguard.app.data.model.UsageRecord

class AppRepository(context: Context) {
    private val db = AppDatabase.getDatabase(context)
    private val blockedAppDao = db.blockedAppDao()
    private val scheduleDao = db.scheduleDao()
    private val usageDao = db.usageRecordDao()

    // Blocked apps
    val allBlockedApps = blockedAppDao.getAllFlow()
    val activeBlockedApps = blockedAppDao.getActiveBlockedFlow()

    suspend fun addBlockedApp(app: BlockedApp) = blockedAppDao.insert(app)
    suspend fun removeBlockedApp(app: BlockedApp) = blockedAppDao.delete(app)
    suspend fun setAppEnabled(packageName: String, enabled: Boolean) = blockedAppDao.setEnabled(packageName, enabled)
    suspend fun setDailyLimit(packageName: String, minutes: Int) = blockedAppDao.setDailyLimit(packageName, minutes)
    suspend fun getActiveBlocked() = blockedAppDao.getActiveBlocked()
    suspend fun getBlockedApp(packageName: String) = blockedAppDao.getByPackage(packageName)

    // Schedules
    val allSchedules = scheduleDao.getAllFlow()

    suspend fun addSchedule(schedule: Schedule) = scheduleDao.insert(schedule)
    suspend fun deleteSchedule(schedule: Schedule) = scheduleDao.delete(schedule)
    suspend fun setScheduleEnabled(id: Int, enabled: Boolean) = scheduleDao.setEnabled(id, enabled)
    suspend fun getActiveSchedules() = scheduleDao.getActiveSchedules()

    // Usage
    fun getUsageByDate(date: String) = usageDao.getByDateFlow(date)
    fun getRecentUsage(startDate: String) = usageDao.getRecentFlow(startDate)
    suspend fun saveUsageRecord(record: UsageRecord) = usageDao.insert(record)
    suspend fun getTodayMinutes(pkg: String, date: String) = usageDao.getTodayMinutes(pkg, date) ?: 0
}
