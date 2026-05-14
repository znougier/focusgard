package com.focusguard.app.ui.home

import android.app.usage.UsageStatsManager
import android.content.Context
import androidx.lifecycle.*
import com.focusguard.app.data.repository.AppRepository
import com.focusguard.app.util.BlockChecker
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class HomeViewModel(private val context: Context) : ViewModel() {
    private val repository = AppRepository(context)

    val activeBlockedCount: LiveData<Int> = repository.activeBlockedApps.asLiveData().map { it.size }
    val activeScheduleCount: LiveData<Int> = repository.allSchedules.asLiveData().map { list -> list.count { it.isEnabled } }

    private val _todayScreenTime = MutableLiveData<Int>()
    val todayScreenTime: LiveData<Int> = _todayScreenTime

    init {
        loadTodayScreenTime()
    }

    private fun loadTodayScreenTime() {
        viewModelScope.launch {
            val usm = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val now = System.currentTimeMillis()
            val startOfDay = now - TimeUnit.HOURS.toMillis(24)
            val stats = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startOfDay, now)
            val totalMs = stats?.filter { it.totalTimeInForeground > 0 }
                ?.sumOf { it.totalTimeInForeground } ?: 0L
            _todayScreenTime.postValue((totalMs / 60000).toInt())
        }
    }
}

class HomeViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return HomeViewModel(context) as T
    }
}
