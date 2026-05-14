package com.focusguard.app.ui

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.focusguard.app.R
import com.focusguard.app.databinding.ActivityMainBinding
import com.focusguard.app.service.AppMonitorService
import com.focusguard.app.ui.setup.PinSetupActivity
import com.focusguard.app.util.PinManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()

        if (!PinManager.isPinSet(this)) {
            startActivity(Intent(this, PinSetupActivity::class.java))
        }

        if (!hasUsagePermission()) {
            showUsagePermissionDialog()
        } else {
            AppMonitorService.start(this)
        }
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNav.setupWithNavController(navController)
    }

    private fun hasUsagePermission(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun showUsagePermissionDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission requise")
            .setMessage("FocusGuard a besoin d'accéder aux statistiques d'utilisation pour surveiller et bloquer les applications.\n\nAppuyez sur OK pour accorder cette permission.")
            .setPositiveButton("Accorder la permission") { _, _ ->
                startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
            }
            .setCancelable(false)
            .show()
    }

    override fun onResume() {
        super.onResume()
        if (hasUsagePermission()) {
            AppMonitorService.start(this)
        }
    }
}
