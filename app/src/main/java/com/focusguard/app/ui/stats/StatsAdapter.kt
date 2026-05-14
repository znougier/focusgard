package com.focusguard.app.ui.stats

import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.focusguard.app.R

class StatsAdapter(private val items: List<StatsItem>) : RecyclerView.Adapter<StatsAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tv_stat_app_name)
        val tvTime: TextView = itemView.findViewById(R.id.tv_stat_time)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progress_usage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_stat, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvName.text = item.appName
        val h = item.minutes / 60
        val m = item.minutes % 60
        holder.tvTime.text = if (h > 0) "${h}h ${m}min" else "${m}min"
        holder.progressBar.max = item.maxMinutes
        holder.progressBar.progress = item.minutes
    }

    override fun getItemCount() = items.size
}
