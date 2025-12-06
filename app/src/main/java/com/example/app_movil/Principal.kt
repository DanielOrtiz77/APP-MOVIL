package com.example.app_movil

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class Principal : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)

        // Solo inicializamos Firebase Authentication
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
            val email = txtEmailPrincipal.text.toString().trim()
            val password = txtpass.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(this, "Iniciando sesión...", Toast.LENGTH_SHORT).show()

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "signInWithEmail:success - Autenticación correcta.")
                        val firebaseUser = auth.currentUser
                        if (firebaseUser != null) {
                            // --- ¡AQUÍ ESTÁ LA SOLUCIÓN DEFINITIVA! ---
                            // Ya no llamamos a una función que consulta la base de datos.
                            // Creamos el objeto Usuario directamente y navegamos.
                            crearUsuarioYNavegar(firebaseUser)
                        } else {
                            // Este caso es muy raro, pero es bueno tenerlo.
                            Toast.makeText(baseContext, "Error inesperado, no se encontró el usuario tras el login.", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Fallo en la autenticación: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    /**
     * Esta función crea el objeto Usuario usando solo la información de Firebase Authentication
     * y navega directamente al menú principal.
     */
    private fun crearUsuarioYNavegar(firebaseUser: FirebaseUser) {
        val userEmail = firebaseUser.email ?: "email.desconocido@error.com"

        // Obtenemos el nombre del perfil de Auth. Si está vacío o nulo (como en tus cuentas antiguas),
        // usamos la parte del email antes del '@' como nombre de respaldo.
        val nombreUsuario = firebaseUser.displayName?.takeIf { it.isNotBlank() } ?: userEmail.split('@')[0]

        // Creamos el objeto Usuario con los datos que tenemos.
        val usuario = Usuario(
            nombre = nombreUsuario,
            email = userEmail,
            tipo = "Normal", // Asumimos que todos son de tipo "Normal"
            contraseña = 0 // Este campo no se usa en el login, lo dejamos en 0.
        )

        Log.d(TAG, "Usuario creado para navegar: ${usuario.nombre}, ${usuario.email}")

        // Navegamos al menú con el objeto Usuario ya creado.
        val intent = Intent(this@Principal, Menu::class.java).apply {
            putExtra("usuario", usuario)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish() // Cierra la actividad de login
    }


    override fun onStart() {
        super.onStart()
        // Esta lógica es buena para limpiar sesiones residuales y evitar que la app
        // se abra logueada por error.
        if (auth.currentUser != null && this.javaClass == Principal::class.java) {
            auth.signOut()
        }
    }
}
