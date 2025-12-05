package com.example.app_movil

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class ManipularArduinoFragment : Fragment() {

    private var usu: Usuario? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Recoger el usuario de los argumentos del fragment
        arguments?.let {
            usu = it.getParcelable("usuario")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout para este fragment
        return inflater.inflate(R.layout.activity_manipular_arduino, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Enlazar vistas
        val tvusumanipular: TextView = view.findViewById(R.id.tvusumanipular)
        val btnon: Button = view.findViewById(R.id.btnon)
        val btnoff: Button = view.findViewById(R.id.btnoff)
        val btnvolman: Button = view.findViewById(R.id.btnvolman)

        // Ocultar el botón "Volver" que ya no es necesario
        btnvolman.visibility = View.GONE

        tvusumanipular.text = "Bienvenido Usuario ${usu?.nombre}"

        // Configurar listeners
        setupEncender(btnon)
        setupApagar(btnoff)
    }

    private fun setupEncender(btn: Button) {
        btn.setOnClickListener {
            val json = """
            {
              "estado" : "Encendido",
              "fecha"  : "${SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())}",
              "hora"   : "${SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())}",
              "numero" : 1,
              "usuario" : "${usu?.nombre?.uppercase()}"
            }
            """.trimIndent()
            enviarComando(json)
        }
    }

    private fun setupApagar(btn: Button) {
        btn.setOnClickListener {
            val json = """
            {
              "estado" : "Apagado",
              "fecha"  : "${SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())}",
              "hora"   : "${SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())}",
              "numero" : 0,
              "usuario" : "${usu?.nombre?.uppercase()}"
            }
            """.trimIndent()
            enviarComando(json)
        }
    }

    private fun enviarComando(json: String) {
        val url = "http://10.26.175.103/guardar" // Asegúrate que esta IP es accesible
        Thread {
            try {
                val conn = URL(url).openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json")
                conn.doOutput = true
                conn.outputStream.write(json.toByteArray())
                conn.inputStream.bufferedReader().readText() // Esperar respuesta

                activity?.runOnUiThread {
                    Toast.makeText(context, "Enviado correctamente", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                activity?.runOnUiThread {
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }
}
