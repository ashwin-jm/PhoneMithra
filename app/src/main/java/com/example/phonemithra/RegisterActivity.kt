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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        auth = Firebase.auth

        val registerbutton: Button = findViewById(R.id.registerButton)
        registerbutton.setOnClickListener {
            performRegistration()
        }

        val registerlogin: TextView = findViewById(R.id.registerLogin)
        registerlogin.setOnClickListener {
            val i = Intent(this, LoginActivity::class.java)
            startActivity(i)
        }

    }

    private fun performRegistration() {
        val email: EditText = findViewById(R.id.registerEmail)
        val name = findViewById<EditText>(R.id.registerName)
        val phone: EditText = findViewById(R.id.registerPhone)
        val pass: EditText = findViewById(R.id.registerPassword)
        val passc: EditText = findViewById(R.id.registerPasswordc)

        val username = name.text.toString()
        val useremail = email.text.toString()
        val userphone = phone.text.toString()
        val userpass = pass.text.toString()
        val userpassc = passc.text.toString()

        val userData = HashMap<String, Any>()
        userData["user_name"] = username
        userData["user_phone"] = userphone
        userData["user_email"] = useremail

        if(username.isEmpty() || useremail.isEmpty() || userphone.isEmpty() || userpass.isEmpty() || userpassc.isEmpty()){
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        else if(userpass != userpassc){
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        else{
            auth.createUserWithEmailAndPassword(useremail, userpass)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                            val userid = auth.currentUser!!.uid
                            db.collection("users").document(userid).set(userData).addOnSuccessListener {
                                val i = Intent(this, MainActivity::class.java)
                                i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(i)
                                Toast.makeText(this, "Account successfully created for "+ username, Toast.LENGTH_SHORT).show()
                            }
                                .addOnFailureListener{
                                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                                }



                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(this, "Some error occurred",
                            Toast.LENGTH_SHORT).show()

                    }
                }
        }

    }
}