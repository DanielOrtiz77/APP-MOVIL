package com.example.app_movil

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class Principal : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)

        auth = FirebaseAuth.getInstance()

        val txtEmailPrincipal = findViewById<EditText>(R.id.txtEmailPrincipal)
        val txtpass = findViewById<EditText>(R.id.txtcon)
        val btnini = findViewById<Button>(R.id.btnini)
        val btnreg = findViewById<Button>(R.id.btnreg)

        btnreg.setOnClickListener {
            val intent = Intent(this, Registro::class.java)
            startActivity(intent)
        }

        btnini.setOnClickListener {
            val email = txtEmailPrincipal.text.toString()
            val password = txtpass.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success
                            Log.d(TAG, "signInWithEmail:success")
                            Toast.makeText(
                                baseContext, "Autenticación exitosa.",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(this, Menu::class.java)
                            startActivity(intent)
                            finish() // Cierra la actividad actual para que el usuario no pueda volver atrás
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.exception)
                            Toast.makeText(
                                baseContext, "Fallo en la autenticación: ${task.exception?.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}