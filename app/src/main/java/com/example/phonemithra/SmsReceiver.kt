package com.example.phonemithra

import android.annotation.SuppressLint
import android.app.admin.DeviceAdminReceiver
import android.app.admin.DevicePolicyManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.location.Location
import android.media.AudioManager
import android.provider.ContactsContract
import android.telephony.SmsManager
import android.telephony.SmsMessage
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

private lateinit var fusedLocationClient: FusedLocationProviderClient

class SmsReceiver : BroadcastReceiver() {

    @SuppressLint("Range", "MissingPermission")
    override fun onReceive(context: Context?, intent: Intent?) {
        val bundle = intent?.extras
        if(bundle!=null){
            val pdus = bundle.get("pdus") as Array<Any>
            for (i in pdus.indices){
                val smsMessage = SmsMessage.createFromPdu(pdus[i] as ByteArray)
                val message = smsMessage.messageBody
                val sender = smsMessage.originatingAddress

                if(message == "change sound profile"){
                    val audioManager = context?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL)
                    sendSuccessText(sender, context)
                }

                else if(message.startsWith("contact")){
                    val contactName = message.substring(8)
                    val contacts = context?.contentResolver?.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,null,null,null)
                    if((contacts != null) && (contacts.count > 0)){
                        while(contacts.moveToNext()){
                            val name = contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                            if(name.toLowerCase().contains(contactName.toLowerCase())){
                                val phoneNumber = contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                                sendContact(sender,context,phoneNumber)
                                break
                            }

                        }
                    }
                    contacts?.close()
                }
                else if(message == "location"){
                    fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                    fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                        val smsManager = SmsManager.getDefault()
                        val latitude = location?.latitude
                        val longitude = location?.longitude
                        val mapsLink = "https://www.google.com/maps/search/?api=1&query=$latitude,$longitude"
                        smsManager.sendTextMessage(sender, null, mapsLink, null, null)
                    }
                }

                else if(message == "lock"){
                    val password = "1234"
                    lockScreen(password,context, sender)

                }
            }
        }

    }

    private fun lockScreen(password: String, context: Context?, sender: String?) {
        val devicePolicyManager = context?.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val componentName = ComponentName(context, DeviceAdminReceiver::class.java)
        devicePolicyManager.lockNow()
        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(sender, null, "Device successfully locked", null, null)
    }

    private fun sendContact(sender: String?, context: Context, message: String?) {
        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(sender,null, message, null, null)

    }

    private fun sendSuccessText(sender: String?, context: Context) {
        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(sender, null, "Success", null, null)

    }

}