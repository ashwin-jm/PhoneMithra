package com.example.phonemithra

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = Firebase.auth

        val loginbutton: Button = findViewById(R.id.loginButton)
        loginbutton.setOnClickListener {
            performLogIn()
        }

        val loginregister: TextView = findViewById(R.id.loginRegister)
        loginregister.setOnClickListener {
            val i = Intent(this, RegisterActivity::class.java)
            startActivity(i)
        }
    }

    private fun performLogIn() {
        val email: EditText = findViewById(R.id.loginEmail)
        val pass: EditText = findViewById(R.id.loginPassword)

        val useremail = email.text.toString()
        val userpass = pass.text.toString()

        if(useremail.isEmpty() || userpass.isEmpty()){
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }
        else{
            auth.signInWithEmailAndPassword(useremail, userpass)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                       val i = Intent(this, MainActivity::class.java)
                        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(i)
                        Toast.makeText(this, "LogIn Successful", Toast.LENGTH_SHORT).show()


                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(this, "LogIn Unsuccessful", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}