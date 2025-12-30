package com.company.sipapk.store

import android.content.Context

class AppPrefs(context: Context) {
    private val sp = context.getSharedPreferences("sipapk_prefs", Context.MODE_PRIVATE)

    var deviceId: String
        get() = sp.getString("device_id", "") ?: ""
        set(value) = sp.edit().putString("device_id", value).apply()

    var activationCode: String
        get() = sp.getString("activation_code", "") ?: ""
        set(value) = sp.edit().putString("activation_code", value).apply()

    var lastHeartbeatTs: Long
        get() = sp.getLong("last_heartbeat_ts", 0L)
        set(value) = sp.edit().putLong("last_heartbeat_ts", value).apply()
}
