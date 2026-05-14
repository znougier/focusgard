package com.focusguard.app.data.db

import android.content.Context
import androidx.room.*
import com.focusguard.app.data.model.BlockedApp
import com.focusguard.app.data.model.Schedule
import com.focusguard.app.data.model.UsageRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockedAppDao {
    @Query("SELECT * FROM blocked_apps ORDER BY appName ASC")
    fun getAllFlow(): Flow<List<BlockedApp>>

    @Query("SELECT * FROM blocked_apps WHERE isEnabled = 1")
    suspend fun getActiveBlocked(): List<BlockedApp>

    @Query("SELECT * FROM blocked_apps WHERE isEnabled = 1")
    fun getActiveBlockedFlow(): Flow<List<BlockedApp>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(app: BlockedApp)

    @Delete
    suspend fun delete(app: BlockedApp)

    @Query("UPDATE blocked_apps SET isEnabled = :enabled WHERE packageName = :packageName")
    suspend fun setEnabled(packageName: String, enabled: Boolean)

    @Query("UPDATE blocked_apps SET dailyLimitMinutes = :minutes WHERE packageName = :packageName")
    suspend fun setDailyLimit(packageName: String, minutes: Int)

    @Query("SELECT * FROM blocked_apps WHERE packageName = :packageName")
    suspend fun getByPackage(packageName: String): BlockedApp?
}

@Dao
interface ScheduleDao {
    @Query("SELECT * FROM schedules ORDER BY startHour ASC")
    fun getAllFlow(): Flow<List<Schedule>>

    @Query("SELECT * FROM schedules WHERE isEnabled = 1")
    suspend fun getActiveSchedules(): List<Schedule>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(schedule: Schedule): Long

    @Delete
    suspend fun delete(schedule: Schedule)

    @Query("UPDATE schedules SET isEnabled = :enabled WHERE id = :id")
    suspend fun setEnabled(id: Int, enabled: Boolean)
}

@Dao
interface UsageRecordDao {
    @Query("SELECT * FROM usage_records WHERE date = :date ORDER BY totalMinutes DESC")
    fun getByDateFlow(date: String): Flow<List<UsageRecord>>

    @Query("SELECT * FROM usage_records WHERE date >= :startDate ORDER BY date DESC, totalMinutes DESC")
    fun getRecentFlow(startDate: String): Flow<List<UsageRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: UsageRecord)

    @Query("SELECT * FROM usage_records WHERE packageName = :pkg AND date = :date")
    suspend fun getRecord(pkg: String, date: String): UsageRecord?

    @Query("SELECT SUM(totalMinutes) FROM usage_records WHERE packageName = :pkg AND date = :date")
    suspend fun getTodayMinutes(pkg: String, date: String): Int?
}

@Database(
    entities = [BlockedApp::class, Schedule::class, UsageRecord::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun blockedAppDao(): BlockedAppDao
    abstract fun scheduleDao(): ScheduleDao
    abstract fun usageRecordDao(): UsageRecordDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "focusguard_db")
                    .build().also { INSTANCE = it }
            }
        }
    }
}
