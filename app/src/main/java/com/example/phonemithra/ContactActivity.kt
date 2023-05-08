package com.example.phonemithra

import android.Manifest.permission.READ_CONTACTS
import android.app.appsearch.SetSchemaRequest.READ_CONTACTS
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Switch
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class ContactActivity : AppCompatActivity() {

    private val REQUEST_CONTACT_PERMISSION = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)

        val contactSwitch: Switch = findViewById(R.id.contactPermission)

        val permission = android.Manifest.permission.READ_CONTACTS
        val packageManager = packageManager

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            contactSwitch.isChecked = true
        }

        contactSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Permission is granted
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, arrayOf(permission), REQUEST_CONTACT_PERMISSION)
                }
            }
            else{
                if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, arrayOf(permission), REQUEST_CONTACT_PERMISSION)
                }
            }
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CONTACT_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show()
            }
        }
    }
}