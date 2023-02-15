package com.example.phonemithra

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class Profile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

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