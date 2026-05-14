package com.focusguard.app.ui.schedule

import android.content.Context
import android.view.*
import android.widget.*
import androidx.lifecycle.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.focusguard.app.R
import com.focusguard.app.data.model.Schedule
import com.focusguard.app.data.repository.AppRepository
import kotlinx.coroutines.launch

class ScheduleViewModel(private val context: Context) : ViewModel() {
    private val repository = AppRepository(context)
    val schedules: LiveData<List<Schedule>> = repository.allSchedules.asLiveData()

    fun add(schedule: Schedule) = viewModelScope.launch { repository.addSchedule(schedule) }
    fun delete(schedule: Schedule) = viewModelScope.launch { repository.deleteSchedule(schedule) }
    fun setEnabled(id: Int, enabled: Boolean) = viewModelScope.launch { repository.setScheduleEnabled(id, enabled) }
}

class ScheduleViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ScheduleViewModel(context) as T
    }
}

class ScheduleAdapter(
    private val onToggle: (Schedule, Boolean) -> Unit,
    private val onDelete: (Schedule) -> Unit
) : ListAdapter<Schedule, ScheduleAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tv_schedule_name)
        val tvTime: TextView = itemView.findViewById(R.id.tv_schedule_time)
        val tvDays: TextView = itemView.findViewById(R.id.tv_schedule_days)
        val switchEnabled: Switch = itemView.findViewById(R.id.switch_schedule)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btn_delete_schedule)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_schedule, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val s = getItem(position)
        holder.tvName.text = s.name
        holder.tvTime.text = String.format("%02d:%02d → %02d:%02d", s.startHour, s.startMinute, s.endHour, s.endMinute)

        val dayNames = mapOf("1" to "Dim", "2" to "Lun", "3" to "Mar", "4" to "Mer", "5" to "Jeu", "6" to "Ven", "7" to "Sam")
        val days = s.daysOfWeek.split(",").mapNotNull { dayNames[it] }.joinToString(", ")
        holder.tvDays.text = if (s.daysOfWeek == "1,2,3,4,5,6,7") "Tous les jours" else days

        holder.switchEnabled.setOnCheckedChangeListener(null)
        holder.switchEnabled.isChecked = s.isEnabled
        holder.switchEnabled.setOnCheckedChangeListener { _, checked -> onToggle(s, checked) }
        holder.btnDelete.setOnClickListener { onDelete(s) }
    }

    class DiffCallback : DiffUtil.ItemCallback<Schedule>() {
        override fun areItemsTheSame(a: Schedule, b: Schedule) = a.id == b.id
        override fun areContentsTheSame(a: Schedule, b: Schedule) = a == b
    }
}
