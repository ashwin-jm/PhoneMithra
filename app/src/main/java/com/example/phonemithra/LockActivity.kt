package com.example.phonemithra

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Switch

class LockActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock)

        val lockSwitch: Switch = findViewById(R.id.lockPermission)

        lockSwitch.setOnCheckedChangeListener{_, isChecked ->
            if(isChecked){
                val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, ComponentName(this, DeviceAdminReceiver::class.java))
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "This app needs device admin privileges to lock the screen.")

                // Start the device admin activation activity
                startActivityForResult(intent, 0)

            }
        }
    }
}