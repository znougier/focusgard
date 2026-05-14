package com.focusguard.app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.focusguard.app.databinding.FragmentHomeBinding
import com.focusguard.app.service.AppMonitorService
import com.focusguard.app.util.PinManager
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dateFormat = SimpleDateFormat("EEEE d MMMM", Locale.FRENCH)
        binding.tvDate.text = dateFormat.format(Date())

        viewModel.activeBlockedCount.observe(viewLifecycleOwner) { count ->
            binding.tvBlockedCount.text = "$count app${if (count > 1) "s" else ""} bloquée${if (count > 1) "s" else ""}"
        }

        viewModel.activeScheduleCount.observe(viewLifecycleOwner) { count ->
            binding.tvScheduleCount.text = "$count planning${if (count > 1) "s" else ""} actif${if (count > 1) "s" else ""}"
        }

        viewModel.todayScreenTime.observe(viewLifecycleOwner) { minutes ->
            val h = minutes / 60
            val m = minutes % 60
            binding.tvScreenTime.text = if (h > 0) "${h}h ${m}min" else "${m} min"
        }

        val isEnabled = PinManager.isServiceEnabled(requireContext())
        binding.switchProtection.isChecked = isEnabled
        updateShieldState(isEnabled)

        binding.switchProtection.setOnCheckedChangeListener { _, checked ->
            PinManager.setServiceEnabled(requireContext(), checked)
            if (checked) AppMonitorService.start(requireContext())
            else AppMonitorService.stop(requireContext())
            updateShieldState(checked)
        }
    }

    private fun updateShieldState(enabled: Boolean) {
        if (enabled) {
            binding.tvProtectionStatus.text = "Protection ACTIVE"
            binding.ivShield.alpha = 1f
        } else {
            binding.tvProtectionStatus.text = "Protection INACTIVE"
            binding.ivShield.alpha = 0.3f
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
