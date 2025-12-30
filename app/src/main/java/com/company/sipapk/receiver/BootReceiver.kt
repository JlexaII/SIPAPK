package com.company.sipapk.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.company.sipapk.service.AgentForegroundService

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.i("BootReceiver", "BOOT_COMPLETED -> starting agent service")
            val svc = Intent(context, AgentForegroundService::class.java)
            context.startForegroundService(svc)
        }
    }
}
