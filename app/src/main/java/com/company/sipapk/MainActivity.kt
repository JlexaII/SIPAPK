package com.company.sipapk

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.company.sipapk.service.AgentForegroundService
import com.company.sipapk.store.AppPrefs
import com.company.sipapk.ui.theme.SIPAPKTheme
import java.util.UUID

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = AppPrefs(this)

        if (prefs.deviceId.isBlank()) {
            prefs.deviceId = UUID.randomUUID().toString()
        }

        setContent {
            SIPAPKTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainScreen(
                        deviceId = prefs.deviceId,
                        initialActivation = prefs.activationCode,
                        onSaveActivation = { code -> prefs.activationCode = code },
                        onStartAgent = {
                            startForegroundService(Intent(this, AgentForegroundService::class.java))
                        },
                        onStopAgent = {
                            stopService(Intent(this, AgentForegroundService::class.java))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun MainScreen(
    deviceId: String,
    initialActivation: String,
    onSaveActivation: (String) -> Unit,
    onStartAgent: () -> Unit,
    onStopAgent: () -> Unit
) {
    var activation by remember { mutableStateOf(initialActivation) }
    var status by remember { mutableStateOf(if (activation.isBlank()) "waiting for activation code" else "activation code saved (not verified)") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Device ID:")
        Spacer(Modifier.height(6.dp))
        Text(text = deviceId)

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = activation,
            onValueChange = { activation = it },
            label = { Text("Activation code") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                val code = activation.trim()
                onSaveActivation(code)
                status = if (code.isBlank()) "waiting for activation code" else "activation code saved (not verified)"
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save activation code")
        }

        Spacer(Modifier.height(16.dp))
        Text(text = "Status: $status")

        Spacer(Modifier.height(16.dp))

        Button(onClick = {
            onStartAgent()
            status = "starting…"
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Start agent (foreground)")
        }

        Spacer(Modifier.height(8.dp))

        OutlinedButton(onClick = {
            onStopAgent()
            status = "stopped"
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Stop agent")
        }
    }
}
