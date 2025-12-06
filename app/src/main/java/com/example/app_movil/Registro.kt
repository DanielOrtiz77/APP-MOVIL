package com.example.app_movil

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase

class Registro : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        val txtNombre = findViewById<EditText>(R.id.txtNombre)
        val txtEmail = findViewById<EditText>(R.id.txtEmail)
        val txtPassword = findViewById<EditText>(R.id.txtPassword)
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)

        btnRegistrar.setOnClickListener {
            val nombre = txtNombre.text.toString().trim()
            val email = txtEmail.text.toString().trim()
            val password = txtPassword.text.toString().trim()

            if (nombre.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isPasswordValid(password)) {
                Toast.makeText(this, "La contraseña debe tener al menos 8 caracteres, letras, un número y un punto.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        val userId = user?.uid

                        // 1. Guardar el nombre en el perfil de Firebase Auth
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(nombre)
                            .build()
                        user?.updateProfile(profileUpdates)

                        // 2. Guardar datos adicionales en Realtime Database
                        if (userId != null) {
                            val userRef = database.getReference("usuarios").child(userId)
                            val userData = mapOf("nombre" to nombre, "tipo" to "Normal")
                            userRef.setValue(userData)
                                .addOnFailureListener { e ->
                                    Log.e("Registro", "Error al guardar datos adicionales: ${e.message}")
                                }
                        }

                        // --- CAMBIO CLAVE: CERRAR SESIÓN DESPUÉS DE REGISTRAR ---
                        auth.signOut()

                        // 3. Redirigir al usuario a la pantalla principal para que inicie sesión
                        Toast.makeText(this, "Registro exitoso. Ahora inicia sesión.", Toast.LENGTH_LONG).show()
                        val intent = Intent(this, Principal::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish() // Cierra la actividad de registro.

                    } else {
                        // El registro de autenticación falló. Mostramos el error de Firebase.
                        Toast.makeText(baseContext, "Fallo en la autenticación: ${task.exception?.message}",
                            Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        val passwordRegex = Regex("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*\\.).{8,}$")
        return passwordRegex.matches(password)
    }
}
