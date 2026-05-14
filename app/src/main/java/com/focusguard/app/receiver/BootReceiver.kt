package com.focusguard.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.focusguard.app.service.AppMonitorService
import com.focusguard.app.util.PinManager

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            if (PinManager.isServiceEnabled(context)) {
                AppMonitorService.start(context)
            }
        }
    }
}
