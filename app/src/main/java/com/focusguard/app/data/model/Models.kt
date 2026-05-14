package com.focusguard.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blocked_apps")
data class BlockedApp(
    @PrimaryKey val packageName: String,
    val appName: String,
    val isEnabled: Boolean = true,
    val dailyLimitMinutes: Int = 0, // 0 = no limit
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "schedules")
data class Schedule(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val startHour: Int,
    val startMinute: Int,
    val endHour: Int,
    val endMinute: Int,
    val daysOfWeek: String, // "1,2,3,4,5" = Mon-Fri
    val isEnabled: Boolean = true,
    val applyToAll: Boolean = false, // if true, applies to all blocked apps
    val packageNames: String = "" // comma-separated if applyToAll=false
)

@Entity(tableName = "usage_records")
data class UsageRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val packageName: String,
    val appName: String,
    val date: String, // "yyyy-MM-dd"
    val totalMinutes: Int,
    val openCount: Int
)
