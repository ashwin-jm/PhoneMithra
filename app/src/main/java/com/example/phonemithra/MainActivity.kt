package com.example.phonemithra

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.tasks.Continuation
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.concurrent.locks.Lock


class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = Firebase.firestore
        val user = FirebaseAuth.getInstance().currentUser
        val userRef = db.collection("users").document(user!!.uid)

        userRef.get()
            .addOnSuccessListener { documentSnapshot ->
                val name = documentSnapshot.getString("user_name")

                val nameTextView = findViewById<TextView>(R.id.homeWelcome)
                nameTextView.text = name
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            }

        val bottomnav: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomnav.selectedItemId = R.id.home
        bottomnav.setOnNavigationItemSelectedListener { item ->
            when(item.itemId){
                R.id.home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.profile -> {
                    startActivity(Intent(this, Profile::class.java))
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.logout -> {
                    val auth = FirebaseAuth.getInstance()
                    val user = auth.currentUser
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage("Do you want to log out?").setCancelable(false)
                        .setPositiveButton("Yes"){
                                dialog, which -> auth.signOut()

                            val intent = Intent(this, LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            Toast.makeText(this, "Logged Out Successfully", Toast.LENGTH_SHORT).show()
                        }
                        .setNegativeButton("No"){
                                dialog, which -> dialog.cancel()
                        }
                        .create().show()


                    return@setOnNavigationItemSelectedListener true
                }
                else -> {
                    return@setOnNavigationItemSelectedListener true
                }
            }
        }

        val buttonLocation: Button = findViewById(R.id.buttonLocation)
        buttonLocation.setOnClickListener {
            startActivity(Intent(this, LocationActivity::class.java))
        }
        val buttonContact: Button = findViewById(R.id.buttonContact)
        buttonContact.setOnClickListener {
            startActivity(Intent(this, ContactActivity::class.java))
        }
        val buttonSound: Button = findViewById(R.id.buttonSound)
        buttonSound.setOnClickListener {
            startActivity(Intent(this, SoundActivity::class.java))
        }
        val buttonLock: Button = findViewById(R.id.buttonLock)
        buttonLock.setOnClickListener {
            startActivity(Intent(this, LockActivity::class.java))
        }
    }
    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Do you want to exit?").setCancelable(false)
            .setPositiveButton("Yes"){
                dialog, which -> moveTaskToBack(true)
            }
            .setNegativeButton("No"){
                dialog, which -> dialog.cancel()
            }
            .create().show()

    }
}
