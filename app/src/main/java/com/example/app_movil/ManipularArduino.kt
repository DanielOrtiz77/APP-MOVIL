package com.example.app_movil

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ManipularArduino : AppCompatActivity() {

    private lateinit var tvusumanipular: TextView
    private lateinit var btnon: Button
    private lateinit var btnoff: Button
    private lateinit var btnvolman: Button

    private var usu: Usuario? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_manipular_arduino)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializar vistas
        tvusumanipular = findViewById(R.id.tvusumanipular)
        btnon = findViewById(R.id.btnon)
        btnoff = findViewById(R.id.btnoff)
        btnvolman = findViewById(R.id.btnvolman)

        usu = intent.getParcelableExtra("usu", Usuario::class.java)
        tvusumanipular.text = "Bienvenido Usuario ${usu?.nombre}"

        setupListeners()
        desactivarFlecha()
    }

    private fun setupListeners() {
        // El script de Python espera 'ABRIR' para encender el LED (comando '1')
        btnon.setOnClickListener { enviarComandoPorFirebase("ABRIR") }

        // El script de Python espera 'CERRAR' para apagar el LED (comando '0')
        btnoff.setOnClickListener { enviarComandoPorFirebase("CERRAR") }

        btnvolman.setOnClickListener {
            val intent = Intent(this@ManipularArduino, Menu::class.java)
            intent.putExtra("usu", usu)
            startActivity(intent)
        }
    }

    private fun enviarComandoPorFirebase(comando: String) {
        val db = Firebase.firestore
        val comandoRef = db.collection("comandos").document("servo_control")
        val data = hashMapOf("comando" to comando)

        comandoRef.set(data)
            .addOnSuccessListener {
                val accion = if (comando == "ABRIR") "encendido" else "apagado"
                Toast.makeText(this, "Comando de $accion enviado.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al enviar comando: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun desactivarFlecha() {
        onBackPressedDispatcher.addCallback(this) {}
    }
}