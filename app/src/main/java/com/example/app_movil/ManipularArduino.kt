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
    private lateinit var tvEstadoLed: TextView

    private var usu: Usuario? = null
    private val db = Firebase.firestore

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
        tvEstadoLed = findViewById(R.id.tvEstadoLed)

        usu = intent.getParcelableExtra("usu", Usuario::class.java)
        tvusumanipular.text = "Bienvenido Usuario ${usu?.nombre}"

        setupListeners()
        desactivarFlecha()
    }

    private fun setupListeners() {
        // Botón ON: Envía comando para ABRIR y actualiza estados de LED y Portón
        btnon.setOnClickListener {
            tvEstadoLed.text = "Estado del LED: Encendido"
            enviarComandoYActualizarEstados("ABRIR", "Encendido", "Abierto")
        }

        // Botón OFF: Envía comando para CERRAR y actualiza estados de LED y Portón
        btnoff.setOnClickListener {
            tvEstadoLed.text = "Estado del LED: Apagado"
            enviarComandoYActualizarEstados("CERRAR", "Apagado", "Cerrado")
        }

        btnvolman.setOnClickListener {
            val intent = Intent(this@ManipularArduino, Menu::class.java)
            intent.putExtra("usu", usu)
            startActivity(intent)
        }
    }

    private fun enviarComandoYActualizarEstados(comando: String, estadoLed: String, estadoPorton: String) {
        val comandoRef = db.collection("comandos").document("servo_control")
        val data = hashMapOf("comando" to comando)

        comandoRef.set(data)
            .addOnSuccessListener {
                Toast.makeText(this, "Comando '$comando' enviado.", Toast.LENGTH_SHORT).show()

                // Actualizar estado del LED
                val ledEstadoRef = db.collection("estados").document("led_estado")
                ledEstadoRef.set(hashMapOf("estado" to estadoLed))

                // Actualizar estado del Portón
                val portonEstadoRef = db.collection("estados").document("porton_estado")
                portonEstadoRef.set(hashMapOf("estado" to estadoPorton))

            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al enviar comando: ${e.message}", Toast.LENGTH_LONG).show()
                tvEstadoLed.text = "Estado del LED: Error"
            }
    }

    private fun desactivarFlecha() {
        onBackPressedDispatcher.addCallback(this) {}
    }
}