package com.example.app_movil

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class Acciones : AppCompatActivity() {

    // --- Variables para los componentes de la UI ---
    private lateinit var tvDistanciaSensor1: TextView
    private lateinit var tvDistanciaSensor2: TextView
    private lateinit var tvStatusConexion: TextView
    private lateinit var btnVolver: Button

    // --- Variables para la conexión a Firebase ---
    private lateinit var db: FirebaseFirestore
    private var firestoreListener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acciones)

        // --- Enlazar componentes de la UI ---
        tvDistanciaSensor1 = findViewById(R.id.tvDistanciaSensor1)
        tvDistanciaSensor2 = findViewById(R.id.tvDistanciaSensor2)
        tvStatusConexion = findViewById(R.id.tvStatusConexionAcciones)
        btnVolver = findViewById(R.id.btnvolveracc)

        // --- Inicializar Firebase ---
        db = FirebaseFirestore.getInstance()

        // --- Configurar botón de volver ---
        btnVolver.setOnClickListener {
            finish() // Cierra esta pantalla y vuelve a la anterior
        }

        // --- Empezar a escuchar los datos de Firebase ---
        iniciarListenerFirestore()
    }

    private fun iniciarListenerFirestore() {
        tvStatusConexion.text = "Estado: Conectando a la nube..."

        // Escuchamos el documento 'estado_actual' en la colección 'sensores'
        // que tu script de Python está actualizando.
        val docRef = db.collection("sensores").document("estado_actual")

        firestoreListener = docRef.addSnapshotListener { snapshot, e ->
            // Si hay un error de conexión, lo mostramos
            if (e != null) {
                tvStatusConexion.text = "Estado: Error de conexión"
                tvDistanciaSensor1.text = "--- cm"
                tvDistanciaSensor2.text = "--- cm"
                return@addSnapshotListener
            }

            // Si el documento existe y tiene datos
            if (snapshot != null && snapshot.exists()) {
                tvStatusConexion.text = "Estado: Conectado (Nube)"

                // Obtenemos los valores de distancia del documento
                val distancia1 = snapshot.getDouble("distancia1") ?: 0.0
                val distancia2 = snapshot.getDouble("distancia2") ?: 0.0

                // Actualizamos los TextViews en la pantalla
                tvDistanciaSensor1.text = "%.2f cm".format(distancia1)
                tvDistanciaSensor2.text = "%.2f cm".format(distancia2)
            } else {
                // Si el documento no existe o no tiene datos
                tvStatusConexion.text = "Estado: Esperando datos del script..."
                tvDistanciaSensor1.text = "--- cm"
                tvDistanciaSensor2.text = "--- cm"
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Es muy importante detener el listener al salir de la pantalla
        // para evitar consumo de datos y errores.
        firestoreListener?.remove()
    }
}
