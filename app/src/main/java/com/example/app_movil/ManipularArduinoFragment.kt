package com.example.app_movil

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ManipularArduinoFragment : Fragment() {

    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_manipular_arduino, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvusumanipular: TextView = view.findViewById(R.id.tvusumanipular)
        val btnon: Button = view.findViewById(R.id.btnon)
        val btnoff: Button = view.findViewById(R.id.btnoff)
        val btnvolman: Button = view.findViewById(R.id.btnvolman)

        btnvolman.visibility = View.GONE

        userViewModel.usuario.observe(viewLifecycleOwner) { usu ->
            tvusumanipular.text = "Bienvenido Usuario: ${usu.nombre}"
        }

        // El script de Python espera 'ABRIR' para encender el LED (comando '1')
        btnon.setOnClickListener { enviarComandoPorFirebase("ABRIR") }

        // El script de Python espera 'CERRAR' para apagar el LED (comando '0')
        btnoff.setOnClickListener { enviarComandoPorFirebase("CERRAR") }
    }

    private fun enviarComandoPorFirebase(comando: String) {
        val db = Firebase.firestore
        val comandoRef = db.collection("comandos").document("servo_control")
        val data = hashMapOf("comando" to comando)

        comandoRef.set(data)
            .addOnSuccessListener {
                val accion = if (comando == "ABRIR") "encendido" else "apagado"
                Toast.makeText(context, "Comando de $accion enviado.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error al enviar comando: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}