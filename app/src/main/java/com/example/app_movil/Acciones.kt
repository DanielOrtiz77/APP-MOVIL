package com.example.app_movil

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class Acciones : AppCompatActivity() {

    // --- Variables para los componentes de la UI ---
    private lateinit var tvDistanciaSensor1: TextView
    private lateinit var tvDistanciaSensor2: TextView
    private lateinit var tvStatusConexion: TextView
    private lateinit var tvEstadoPorton: TextView
    private lateinit var btnVolver: Button

    // --- Variables para la conexión a Firebase ---
    private lateinit var db: FirebaseFirestore
    private var firestoreListener: ListenerRegistration? = null
    private var portonStateListener: ListenerRegistration? = null
    private val TAG = "AccionesActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acciones)

        // --- Enlazar componentes de la UI ---
        tvDistanciaSensor1 = findViewById(R.id.tvDistanciaSensor1)
        tvDistanciaSensor2 = findViewById(R.id.tvDistanciaSensor2)
        tvStatusConexion = findViewById(R.id.tvStatusConexionAcciones)
        tvEstadoPorton = findViewById(R.id.tvEstadoPorton)
        btnVolver = findViewById(R.id.btnvolveracc)

        // --- Inicializar Firebase ---
        db = FirebaseFirestore.getInstance()

        // --- Configurar botón de volver ---
        btnVolver.setOnClickListener {
            finish() // Cierra esta pantalla y vuelve a la anterior
        }

        // --- Empezar a escuchar los datos de Firebase ---
        iniciarListenerFirestore()
        iniciarListenerEstadoPorton()
    }

    private fun iniciarListenerEstadoPorton() {
        Log.d(TAG, "Iniciando listener para el estado del portón.")
        val docRef = db.collection("comandos").document("servo_control")
        portonStateListener = docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e(TAG, "Error al escuchar el estado del portón", e)
                tvEstadoPorton.text = "Error de conexión"
                tvEstadoPorton.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                // LEEMOS EL CAMPO 'estado', NO 'comando'
                val estado = snapshot.getString("estado")
                Log.d(TAG, "Datos recibidos de Firestore. 'estado': $estado")

                when (estado) {
                    "ABIERTO" -> {
                        tvEstadoPorton.text = "Portón Abierto"
                        tvEstadoPorton.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))
                    }
                    "CERRADO" -> {
                        tvEstadoPorton.text = "Portón Cerrado"
                        tvEstadoPorton.setTextColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark))
                    }
                    else -> {
                        // Si el campo 'estado' no existe o es nulo, se muestra 'Desconocido'
                        val comandoAnterior = snapshot.getString("comando")
                        Log.w(TAG, "Campo 'estado' desconocido o nulo: $estado. (Comando anterior: $comandoAnterior)")
                        tvEstadoPorton.text = "Desconocido"
                        tvEstadoPorton.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray))
                    }
                }
            } else {
                Log.w(TAG, "El documento 'servo_control' no existe o está vacío.")
                tvEstadoPorton.text = "Desconocido"
                tvEstadoPorton.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray))
            }
        }
    }

    private fun iniciarListenerFirestore() {
        tvStatusConexion.text = "Estado: Conectando a la nube..."
        val docRef = db.collection("sensores").document("estado_actual")

        firestoreListener = docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                tvStatusConexion.text = "Estado: Error de conexión"
                tvDistanciaSensor1.text = "--- cm"
                tvDistanciaSensor2.text = "--- cm"
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                tvStatusConexion.text = "Estado: Conectado (Nube)"
                val distancia1 = snapshot.getDouble("distancia1") ?: 0.0
                val distancia2 = snapshot.getDouble("distancia2") ?: 0.0

                tvDistanciaSensor1.text = "%.2f cm".format(distancia1)
                tvDistanciaSensor2.text = "%.2f cm".format(distancia2)
            } else {
                tvStatusConexion.text = "Estado: Esperando datos del script..."
                tvDistanciaSensor1.text = "--- cm"
                tvDistanciaSensor2.text = "--- cm"
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        firestoreListener?.remove()
        portonStateListener?.remove()
    }
}
