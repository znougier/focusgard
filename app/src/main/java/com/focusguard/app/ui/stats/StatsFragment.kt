package com.focusguard.app.ui.stats

import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.focusguard.app.databinding.FragmentStatsBinding
import java.util.concurrent.TimeUnit

class StatsFragment : Fragment() {

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadStats()
    }

    private fun loadStats() {
        val usm = requireContext().getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val pm = requireContext().packageManager
        val now = System.currentTimeMillis()
        val startOfDay = now - TimeUnit.HOURS.toMillis(24)

        val stats = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startOfDay, now)
            ?.filter { it.totalTimeInForeground > 60_000 } // > 1 minute
            ?.filter { it.packageName != requireContext().packageName }
            ?.sortedByDescending { it.totalTimeInForeground }
            ?.take(10) ?: emptyList()

        val totalMs = stats.sumOf { it.totalTimeInForeground }
        val totalMin = totalMs / 60000
        val h = totalMin / 60
        val m = totalMin % 60
        binding.tvTotalTime.text = if (h > 0) "${h}h ${m}min aujourd'hui" else "${m}min aujourd'hui"

        val items = stats.map { stat ->
            val appName = try {
                pm.getApplicationLabel(pm.getApplicationInfo(stat.packageName, 0)).toString()
            } catch (e: PackageManager.NameNotFoundException) {
                stat.packageName
            }
            val minutes = (stat.totalTimeInForeground / 60000).toInt()
            val maxMinutes = (stats.first().totalTimeInForeground / 60000).toInt()
            StatsItem(appName, minutes, maxMinutes)
        }

        val adapter = StatsAdapter(items)
        binding.rvStats.layoutManager = LinearLayoutManager(requireContext())
        binding.rvStats.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

data class StatsItem(val appName: String, val minutes: Int, val maxMinutes: Int)
