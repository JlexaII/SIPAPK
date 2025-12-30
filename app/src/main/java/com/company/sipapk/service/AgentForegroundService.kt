package com.company.sipapk.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.company.sipapk.R
import com.company.sipapk.store.AppPrefs

class AgentForegroundService : Service() {

    private val tag = "AgentService"
    private val channelId = "sipapk_agent_channel"
    private val notifId = 1001

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var prefs: AppPrefs

    private val heartbeatRunnable = object : Runnable {
        override fun run() {
            try {
                val now = System.currentTimeMillis()
                prefs.lastHeartbeatTs = now
                Log.i(tag, "heartbeat deviceId=${prefs.deviceId} ts=$now")
            } catch (t: Throwable) {
                Log.e(tag, "heartbeat error", t)
            } finally {
                handler.postDelayed(this, 30_000L)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        prefs = AppPrefs(this)

        createNotificationChannelIfNeeded()
        startForeground(notifId, buildNotification("Agent running"))

        handler.post(heartbeatRunnable)
        Log.i(tag, "service started")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        Log.i(tag, "service stopped")
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannelIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(NotificationManager::class.java)
            val channel = NotificationChannel(
                channelId,
                "SIPAPK Agent",
                NotificationManager.IMPORTANCE_LOW
            )
            nm.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(text: String): Notification {
        return NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("SIPAPK")
            .setContentText(text)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()
    }
}
