package com.focusguard.app.ui.schedule

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.focusguard.app.data.model.Schedule
import com.focusguard.app.databinding.FragmentScheduleBinding
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat

class ScheduleFragment : Fragment() {

    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ScheduleViewModel by viewModels { ScheduleViewModelFactory(requireContext()) }
    private lateinit var adapter: ScheduleAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ScheduleAdapter(
            onToggle = { schedule, enabled -> viewModel.setEnabled(schedule.id, enabled) },
            onDelete = { schedule -> viewModel.delete(schedule) }
        )

        binding.rvSchedules.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSchedules.adapter = adapter

        viewModel.schedules.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            binding.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }

        // Pre-populate with morning routine
        binding.btnAddMorning.setOnClickListener {
            val morningSchedule = Schedule(
                name = "Matin sans distractions",
                startHour = 6, startMinute = 0,
                endHour = 8, endMinute = 0,
                daysOfWeek = "2,3,4,5,6", // Mon-Fri
                applyToAll = true,
                isEnabled = true
            )
            viewModel.add(morningSchedule)
        }

        binding.btnAddNight.setOnClickListener {
            val nightSchedule = Schedule(
                name = "Nuit numérique",
                startHour = 22, startMinute = 0,
                endHour = 7, endMinute = 0,
                daysOfWeek = "1,2,3,4,5,6,7",
                applyToAll = true,
                isEnabled = true
            )
            viewModel.add(nightSchedule)
        }

        binding.fabAddSchedule.setOnClickListener {
            showAddScheduleDialog()
        }
    }

    private fun showAddScheduleDialog() {
        val dialogView = layoutInflater.inflate(com.focusguard.app.R.layout.dialog_add_schedule, null)
        val etName = dialogView.findViewById<android.widget.EditText>(com.focusguard.app.R.id.et_schedule_name)
        val tvStartTime = dialogView.findViewById<android.widget.TextView>(com.focusguard.app.R.id.tv_start_time)
        val tvEndTime = dialogView.findViewById<android.widget.TextView>(com.focusguard.app.R.id.tv_end_time)
        val cbApplyToAll = dialogView.findViewById<android.widget.CheckBox>(com.focusguard.app.R.id.cb_apply_all)

        val dayCheckboxIds = listOf(
            com.focusguard.app.R.id.cb_mon, com.focusguard.app.R.id.cb_tue,
            com.focusguard.app.R.id.cb_wed, com.focusguard.app.R.id.cb_thu,
            com.focusguard.app.R.id.cb_fri, com.focusguard.app.R.id.cb_sat,
            com.focusguard.app.R.id.cb_sun
        )
        val dayValues = listOf("2", "3", "4", "5", "6", "7", "1") // Mon=2 in Calendar

        var startHour = 6; var startMin = 0
        var endHour = 8; var endMin = 0
        tvStartTime.text = "06:00"
        tvEndTime.text = "08:00"

        tvStartTime.setOnClickListener {
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(startHour).setMinute(startMin)
                .build().apply {
                    addOnPositiveButtonClickListener {
                        startHour = this.hour; startMin = this.minute
                        tvStartTime.text = String.format("%02d:%02d", startHour, startMin)
                    }
                    show(childFragmentManager, "startPicker")
                }
        }

        tvEndTime.setOnClickListener {
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(endHour).setMinute(endMin)
                .build().apply {
                    addOnPositiveButtonClickListener {
                        endHour = this.hour; endMin = this.minute
                        tvEndTime.text = String.format("%02d:%02d", endHour, endMin)
                    }
                    show(childFragmentManager, "endPicker")
                }
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Nouveau planning")
            .setView(dialogView)
            .setPositiveButton("Créer") { _, _ ->
                val name = etName.text.toString().ifEmpty { "Mon planning" }
                val selectedDays = dayValues.filterIndexed { i, _ ->
                    dialogView.findViewById<android.widget.CheckBox>(dayCheckboxIds[i]).isChecked
                }.joinToString(",").ifEmpty { "1,2,3,4,5,6,7" }

                val schedule = Schedule(
                    name = name,
                    startHour = startHour, startMinute = startMin,
                    endHour = endHour, endMinute = endMin,
                    daysOfWeek = selectedDays,
                    applyToAll = cbApplyToAll.isChecked,
                    isEnabled = true
                )
                viewModel.add(schedule)
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
