package com.example.phonemithra

import android.content.Context
import android.content.Intent
import android.widget.Toast

class DeviceAdminReceiver: android.app.admin.DeviceAdminReceiver() {
    override fun onEnabled(context: Context, intent: Intent) {
        Toast.makeText(context, "Device admin enabled", Toast.LENGTH_SHORT).show()
    }

    override fun onDisabled(context: Context, intent: Intent) {
        Toast.makeText(context, "Device admin not enabled", Toast.LENGTH_SHORT).show()
    }
}