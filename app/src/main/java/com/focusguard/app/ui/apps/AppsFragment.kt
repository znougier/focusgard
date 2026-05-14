package com.focusguard.app.ui.apps

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.focusguard.app.data.model.BlockedApp
import com.focusguard.app.databinding.FragmentAppsBinding

class AppsFragment : Fragment() {

    private var _binding: FragmentAppsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AppsViewModel by viewModels { AppsViewModelFactory(requireContext()) }
    private lateinit var adapter: BlockedAppsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAppsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = BlockedAppsAdapter(
            onToggle = { app, enabled -> viewModel.setEnabled(app.packageName, enabled) },
            onSetLimit = { app -> showLimitDialog(app) },
            onRemove = { app -> viewModel.removeApp(app) }
        )

        binding.rvBlockedApps.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBlockedApps.adapter = adapter

        viewModel.blockedApps.observe(viewLifecycleOwner) { apps ->
            adapter.submitList(apps)
            binding.tvEmpty.visibility = if (apps.isEmpty()) View.VISIBLE else View.GONE
        }

        binding.fabAddApp.setOnClickListener {
            showAddAppDialog()
        }
    }

    private fun showAddAppDialog() {
        val pm = requireContext().packageManager
        val installedApps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter { it.flags and ApplicationInfo.FLAG_SYSTEM == 0 }
            .sortedBy { pm.getApplicationLabel(it).toString() }

        val appNames = installedApps.map { pm.getApplicationLabel(it).toString() }.toTypedArray()
        val checked = BooleanArray(installedApps.size) { false }

        AlertDialog.Builder(requireContext())
            .setTitle("Choisir des applications à bloquer")
            .setMultiChoiceItems(appNames, checked) { _, which, isChecked ->
                checked[which] = isChecked
            }
            .setPositiveButton("Ajouter") { _, _ ->
                installedApps.forEachIndexed { index, appInfo ->
                    if (checked[index]) {
                        val app = BlockedApp(
                            packageName = appInfo.packageName,
                            appName = pm.getApplicationLabel(appInfo).toString()
                        )
                        viewModel.addApp(app)
                    }
                }
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    private fun showLimitDialog(app: BlockedApp) {
        val options = arrayOf(
            "Pas de limite", "15 minutes", "30 minutes",
            "45 minutes", "1 heure", "2 heures", "3 heures"
        )
        val values = intArrayOf(0, 15, 30, 45, 60, 120, 180)
        val current = values.indexOf(app.dailyLimitMinutes).coerceAtLeast(0)

        AlertDialog.Builder(requireContext())
            .setTitle("Limite pour ${app.appName}")
            .setSingleChoiceItems(options, current) { dialog, which ->
                viewModel.setDailyLimit(app.packageName, values[which])
                dialog.dismiss()
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
