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
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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


        tvusumanipular = findViewById(R.id.tvusumanipular)
        btnon = findViewById(R.id.btnon)
        btnoff = findViewById(R.id.btnoff)
        btnvolman = findViewById(R.id.btnvolman)

        usu = intent.getParcelableExtra("usu", Usuario::class.java)

        tvusumanipular.setText("Bienvenido Usuario " + usu?.nombre)

        encender()
        apagar()
        volver()
        desactivarFlecha()



    } // Cierra el onCreate.



    fun encender(){
        btnon.setOnClickListener {

            var estado = "Encendido"
            var fecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            var hora  = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())



            // variable llamada "json" que contiene los datos formateados en JSON
            // Debe estar así, sin espacios adicionales, de lo contrario,
            // firebase no reconocerá el formato cuando ESP32 intente enviarlos.
            val json = """
            {
              "estado" : "$estado",
              "fecha"  : "$fecha",
              "hora"   : "$hora",
              "numero" : 1,
              "usuario" : "${usu?.nombre?.uppercase()}"
            }
            """.trimIndent()



            // La IP es la que muestra ESP32 a través del monitor serial.
            val url = "http://10.26.175.103/guardar"



            //----------------------------------------------------------------------------------------------------------------------
            //--- "Thread" siempre queda igual, ya que, se le entrega la variable "json"                                         ---
            //--- y el código se lo entrega al ESP32                                                                             ---
            //----------------------------------------------------------------------------------------------------------------------
            Thread {
                try {
                    val conn = URL(url).openConnection() as HttpURLConnection
                    conn.requestMethod = "POST"
                    conn.setRequestProperty("Content-Type", "application/json")
                    conn.doOutput = true
                    conn.outputStream.write(json.toByteArray())

                    val response = conn.inputStream.bufferedReader().readText()
                    runOnUiThread {
                        Toast.makeText(this, "Enviado correctamente", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }.start()
            //----------------------------------------------------------------------------------------------------------------------


        }
    } // Cierra la función "encender".




    fun apagar(){
        btnoff.setOnClickListener {

            var estado = "Apagado"
            var fecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            var hora  = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())



            // variable llamada "json" que contiene los datos formateados en JSON
            // Debe estar así, sin espacios adicionales, de lo contrario,
            // firebase no reconocerá el formato cuando ESP32 intente enviarlos.
            val json = """
                {
                  "estado" : "$estado",
                  "fecha"  : "$fecha",
                  "hora"   : "$hora",
                  "numero" : 0,
                  "usuario" : "${usu?.nombre?.uppercase()}"
                }
                """.trimIndent()



            // La IP es la que muestra ESP32 a través del monitor serial.
            val url = "http://10.26.175.103/guardar"

            //----------------------------------------------------------------------------------------------------------------------
            //--- "Thread" siempre queda igual, ya que, se le entrega la variable "json"                                         ---
            //--- y el código se lo entrega al ESP32                                                                             ---
            //----------------------------------------------------------------------------------------------------------------------
            Thread {
                try {
                    val conn = URL(url).openConnection() as HttpURLConnection
                    conn.requestMethod = "POST"
                    conn.setRequestProperty("Content-Type", "application/json")
                    conn.doOutput = true
                    conn.outputStream.write(json.toByteArray())

                    val response = conn.inputStream.bufferedReader().readText()
                    runOnUiThread {
                        Toast.makeText(this, "Enviado correctamente", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }.start()
            //----------------------------------------------------------------------------------------------------------------------


        }
    } // Cierra la función "apagar".



    fun volver(){

        btnvolman.setOnClickListener {
            var intent = Intent(this@ManipularArduino, Menu::class.java)
            intent.putExtra("usu", usu)
            startActivity(intent)
        }

    } // Cierra la funcion volver().


    fun desactivarFlecha(){
        onBackPressedDispatcher.addCallback(this){

        }

    } // Cierra la funcion desactivarFlecha.



} // Cierra la clase Manipular ESP32.