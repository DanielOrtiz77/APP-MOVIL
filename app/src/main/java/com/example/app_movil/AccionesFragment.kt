package com.example.app_movil

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class AccionesFragment : Fragment() {

    private lateinit var tvDistanciaSensor1: TextView
    private lateinit var tvDistanciaSensor2: TextView
    private lateinit var tvStatusConexion: TextView
    private lateinit var btnVolver: Button

    private lateinit var db: FirebaseFirestore
    private var firestoreListener: ListenerRegistration? = null

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
        btnVolver = view.findViewById(R.id.btnvolveracc)

        // Ocultar botón innecesario
        btnVolver.visibility = View.GONE

        db = FirebaseFirestore.getInstance()
        iniciarListenerFirestore()
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
    }
}
