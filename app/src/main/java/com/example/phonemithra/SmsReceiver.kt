package com.example.phonemithra

import android.annotation.SuppressLint
import android.app.admin.DeviceAdminReceiver
import android.app.admin.DevicePolicyManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.media.AudioManager
import android.provider.ContactsContract
import android.provider.Settings
import android.telephony.SmsManager
import android.telephony.SmsMessage
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


private lateinit var fusedLocationClient: FusedLocationProviderClient

class SmsReceiver : BroadcastReceiver() {

    val auth = FirebaseAuth.getInstance()
    val database = Firebase.firestore

    @SuppressLint("Range", "MissingPermission")
    override fun onReceive(context: Context?, intent: Intent?) {
        val bundle = intent?.extras
        if(bundle!=null){
            val pdus = bundle.get("pdus") as Array<Any>
            for (i in pdus.indices){
                val smsMessage = SmsMessage.createFromPdu(pdus[i] as ByteArray)
                val message = smsMessage.messageBody
                val sender = smsMessage.originatingAddress

                val currentUser = auth.currentUser
                val userRef = database.collection("users").document(currentUser!!.uid)

                val messageParts = message.split(" ")
                val functionMessage = messageParts.subList(2,messageParts.size).joinToString(" ")
                if(messageParts[0] == "phonemithra"){
                    userRef.get()
                        .addOnSuccessListener { documentSnapshot ->
                            val fpasscode = documentSnapshot.getString("user_passcode")
                            if(fpasscode == messageParts[1]){

                                if(functionMessage == "change sound profile"){
                                    val audioManager = context?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL)
                                    sendSuccessText(sender, context)
                                }

                                else if(functionMessage.startsWith("contact")){
                                    val contactName = functionMessage.substring(8)
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

                                else if(functionMessage == "location"){
                                    fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                                    fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                                        val smsManager = SmsManager.getDefault()
                                        val latitude = location?.latitude
                                        val longitude = location?.longitude
                                        val mapsLink = "https://www.google.com/maps/search/?api=1&query=$latitude,$longitude"
                                        smsManager.sendTextMessage(sender, null, mapsLink, null, null)
                                    }
                                }

                                else if(functionMessage == "lock") {
                                    val password = "1234"
                                    lockScreen(password, context, sender)

                                }
                            }
                        }

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