package com.example.app_movil

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class Menu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val usu = intent.getParcelableExtra<Usuario>("usuario")
        val tvusumenu = findViewById<TextView>(R.id.tvusumenu)

        val btnmanipular = findViewById<Button>(R.id.btn1)
        val btnacciones = findViewById<Button>(R.id.btn2)
        val btnperfil = findViewById<Button>(R.id.btn3)
        val btncerrar = findViewById<Button>(R.id.btn4)

        if (usu != null) {
            tvusumenu.text = "Bienvenido Usuario: ${usu.nombre}"
        }

        btnmanipular.setOnClickListener {
            val intent = Intent(this, ManipularArduino::class.java)
            intent.putExtra("usuario", usu)
            startActivity(intent)
        }

        btnacciones.setOnClickListener {
            val intent = Intent(this, Acciones::class.java)
            intent.putExtra("usuario", usu)
            startActivity(intent)
        }

        btnperfil.setOnClickListener {
            val intent = Intent(this, Perfil::class.java)
            intent.putExtra("usuario", usu)
            startActivity(intent)
        }

        btncerrar.setOnClickListener {
            val intent = Intent(this, Principal::class.java)
            startActivity(intent)
        }
    }
}