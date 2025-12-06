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
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class ManipularArduinoFragment : Fragment() {

    // Obtener el ViewModel compartido
    private val userViewModel: UserViewModel by activityViewModels()
    private var currentUser: Usuario? = null

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

        // Observar al usuario y actualizar la UI y la variable local
        userViewModel.usuario.observe(viewLifecycleOwner) { usu ->
            currentUser = usu
            tvusumanipular.text = "Bienvenido Usuario: ${usu.nombre}"
        }

        setupEncender(btnon)
        setupApagar(btnoff)
    }

    private fun setupEncender(btn: Button) {
        btn.setOnClickListener {
            val nombreUsuario = currentUser?.nombre?.uppercase() ?: "DESCONOCIDO"
            val json = """
            {
              "estado" : "Encendido",
              "fecha"  : "${SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())}",
              "hora"   : "${SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())}",
              "numero" : 1,
              "usuario" : "$nombreUsuario"
            }
            """.trimIndent()
            enviarComando(json)
        }
    }

    private fun setupApagar(btn: Button) {
        btn.setOnClickListener {
            val nombreUsuario = currentUser?.nombre?.uppercase() ?: "DESCONOCIDO"
            val json = """
            {
              "estado" : "Apagado",
              "fecha"  : "${SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())}",
              "hora"   : "${SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())}",
              "numero" : 0,
              "usuario" : "$nombreUsuario"
            }
            """.trimIndent()
            enviarComando(json)
        }
    }

    private fun enviarComando(json: String) {
        val url = "http://10.26.175.103/guardar" // Asegúrate de que esta IP es correcta y accesible
        Thread {
            try {
                val conn = URL(url).openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json")
                conn.doOutput = true
                conn.outputStream.write(json.toByteArray())
                conn.inputStream.bufferedReader().readText()

                activity?.runOnUiThread {
                    Toast.makeText(context, "Comando enviado", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                activity?.runOnUiThread {
                    Toast.makeText(context, "Error de conexión: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }
}
