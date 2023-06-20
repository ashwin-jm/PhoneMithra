package com.example.phonemithra

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Profile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val db = Firebase.firestore
        val user = FirebaseAuth.getInstance().currentUser
        val userRef = db.collection("users").document(user!!.uid)

        userRef.get()
            .addOnSuccessListener { documentSnapshot ->
                val name = documentSnapshot.getString("user_name")
                val phoneNumber = documentSnapshot.getString("user_phone")
                val userEmail = documentSnapshot.getString("user_email")

                val nameTextView = findViewById<TextView>(R.id.userName)
                nameTextView.text = name
                val numberTextView = findViewById<TextView>(R.id.userNumber)
                numberTextView.text = phoneNumber
                val emailTextView = findViewById<TextView>(R.id.userEmail)
                emailTextView.text = userEmail
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            }

        val bottomnav: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomnav.selectedItemId = R.id.profile
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
                    auth.signOut()

                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    Toast.makeText(this, "Logged Out Successfully", Toast.LENGTH_SHORT).show()

                    return@setOnNavigationItemSelectedListener true
                }
                else -> {
                    Toast.makeText(this, "Some error occured", Toast.LENGTH_SHORT).show()
                    return@setOnNavigationItemSelectedListener true
                }
            }
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        val bottomnav: BottomNavigationView = findViewById(R.id.bottom_navigation)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        bottomnav.selectedItemId = R.id.home
    }
}