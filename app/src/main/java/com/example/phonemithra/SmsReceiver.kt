package com.example.phonemithra

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.media.AudioManager
import android.provider.ContactsContract
import android.provider.Telephony.Sms
import android.telephony.SmsManager
import android.telephony.SmsMessage
import androidx.constraintlayout.motion.widget.Debug.getLocation

class SmsReceiver : BroadcastReceiver() {
    @SuppressLint("Range")
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
                    val location = getLocation(context)
                    if(location!=null){
                        val smsManager = SmsManager.getDefault()
                        val latitude = location.latitude
                        val longitude = location.longitude
                        val mapsLink = "https://www.google.com/maps/search/?api=1&query=$latitude,$longitude"
                        smsManager.sendTextMessage(sender, null, mapsLink, null, null)
                    }
                }
            }
        }

    }

    @SuppressLint("MissingPermission")
    private fun getLocation(context: Context?): Location? {
        val locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

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