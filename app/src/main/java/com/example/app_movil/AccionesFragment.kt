package com.example.app_movil

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class AccionesFragment : Fragment() {

    private lateinit var tvDistanciaSensor1: TextView
    private lateinit var tvDistanciaSensor2: TextView
    private lateinit var tvStatusConexion: TextView
    private lateinit var tvEstadoPorton: TextView
    private lateinit var btnVolver: Button

    private lateinit var db: FirebaseFirestore
    private var firestoreListener: ListenerRegistration? = null
    private var portonStateListener: ListenerRegistration? = null
    private val TAG = "AccionesFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_acciones, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvDistanciaSensor1 = view.findViewById(R.id.tvDistanciaSensor1)
        tvDistanciaSensor2 = view.findViewById(R.id.tvDistanciaSensor2)
        tvStatusConexion = view.findViewById(R.id.tvStatusConexionAcciones)
        tvEstadoPorton = view.findViewById(R.id.tvEstadoPorton)
        btnVolver = view.findViewById(R.id.btnvolveracc)

        // Ocultar botón innecesario
        btnVolver.visibility = View.GONE

        db = FirebaseFirestore.getInstance()
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
                tvEstadoPorton.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                // LEEMOS EL CAMPO 'estado', NO 'comando'
                val estado = snapshot.getString("estado")
                Log.d(TAG, "Datos recibidos de Firestore. 'estado': $estado")

                when (estado) {
                    "ABIERTO" -> {
                        tvEstadoPorton.text = "Portón Abierto"
                        tvEstadoPorton.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark))
                    }
                    "CERRADO" -> {
                        tvEstadoPorton.text = "Portón Cerrado"
                        tvEstadoPorton.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_blue_dark))
                    }
                    else -> {
                        Log.w(TAG, "Campo 'estado' desconocido o nulo: $estado")
                        tvEstadoPorton.text = "Desconocido"
                        tvEstadoPorton.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
                    }
                }
            } else {
                Log.w(TAG, "El documento 'servo_control' no existe o está vacío.")
                tvEstadoPorton.text = "Desconocido"
                tvEstadoPorton.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
            }
        }
    }

    private fun iniciarListenerFirestore() {
        tvStatusConexion.text = "Estado: Conectando a la nube..."
        val docRef = db.collection("sensores").document("estado_actual")
        firestoreListener = docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                tvStatusConexion.text = "Estado: Error de conexión"
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                tvStatusConexion.text = "Estado: Conectado (Nube)"
                val distancia1 = snapshot.getDouble("distancia1") ?: 0.0
                val distancia2 = snapshot.getDouble("distancia2") ?: 0.0
                tvDistanciaSensor1.text = "%.2f cm".format(distancia1)
                tvDistanciaSensor2.text = "%.2f cm".format(distancia2)
            } else {
                tvStatusConexion.text = "Estado: Esperando datos..."
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Detener el listener para evitar consumo innecesario
        firestoreListener?.remove()
        portonStateListener?.remove()
    }
}
