package com.focusguard.app.ui.apps

import android.content.Context
import android.view.*
import android.widget.*
import androidx.lifecycle.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.focusguard.app.R
import com.focusguard.app.data.model.BlockedApp
import com.focusguard.app.data.repository.AppRepository
import kotlinx.coroutines.launch

// ViewModel
class AppsViewModel(private val context: Context) : ViewModel() {
    private val repository = AppRepository(context)
    val blockedApps: LiveData<List<BlockedApp>> = repository.allBlockedApps.asLiveData()

    fun addApp(app: BlockedApp) = viewModelScope.launch { repository.addBlockedApp(app) }
    fun removeApp(app: BlockedApp) = viewModelScope.launch { repository.removeBlockedApp(app) }
    fun setEnabled(packageName: String, enabled: Boolean) = viewModelScope.launch { repository.setAppEnabled(packageName, enabled) }
    fun setDailyLimit(packageName: String, minutes: Int) = viewModelScope.launch { repository.setDailyLimit(packageName, minutes) }
}

class AppsViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return AppsViewModel(context) as T
    }
}

// Adapter
class BlockedAppsAdapter(
    private val onToggle: (BlockedApp, Boolean) -> Unit,
    private val onSetLimit: (BlockedApp) -> Unit,
    private val onRemove: (BlockedApp) -> Unit
) : ListAdapter<BlockedApp, BlockedAppsAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tv_app_name)
        val tvLimit: TextView = itemView.findViewById(R.id.tv_limit)
        val switchEnabled: Switch = itemView.findViewById(R.id.switch_enabled)
        val btnLimit: ImageButton = itemView.findViewById(R.id.btn_limit)
        val btnRemove: ImageButton = itemView.findViewById(R.id.btn_remove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_blocked_app, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val app = getItem(position)
        holder.tvName.text = app.appName
        holder.tvLimit.text = if (app.dailyLimitMinutes > 0) {
            val h = app.dailyLimitMinutes / 60
            val m = app.dailyLimitMinutes % 60
            "Limite: ${if (h > 0) "${h}h " else ""}${if (m > 0) "${m}min" else ""}"
        } else {
            "Pas de limite"
        }
        holder.switchEnabled.isChecked = app.isEnabled
        holder.switchEnabled.setOnCheckedChangeListener { _, checked -> onToggle(app, checked) }
        holder.btnLimit.setOnClickListener { onSetLimit(app) }
        holder.btnRemove.setOnClickListener { onRemove(app) }
    }

    class DiffCallback : DiffUtil.ItemCallback<BlockedApp>() {
        override fun areItemsTheSame(a: BlockedApp, b: BlockedApp) = a.packageName == b.packageName
        override fun areContentsTheSame(a: BlockedApp, b: BlockedApp) = a == b
    }
}
