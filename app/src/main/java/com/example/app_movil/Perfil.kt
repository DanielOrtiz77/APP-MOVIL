package com.example.app_movil

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Perfil : AppCompatActivity() {


    private lateinit var tvusuperfil: TextView
    private lateinit var tvnomper: TextView
    private lateinit var tvemaper: TextView
    private lateinit var tvtipper: TextView
    private lateinit var tvconper: TextView
    private lateinit var btnvolper: Button

    private var usu: Usuario? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_perfil)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        tvusuperfil = findViewById(R.id.tvusuperfil)
        tvnomper = findViewById(R.id.tvnomper)
        tvemaper = findViewById(R.id.tvemaper)
        tvtipper = findViewById(R.id.tvtipper)
        tvconper = findViewById(R.id.tvconper)
        btnvolper = findViewById(R.id.btnvolper)

        usu = intent.getParcelableExtra("usu", Usuario::class.java)

        tvusuperfil.setText("Bienvenido Usuario : " + usu?.nombre)
        tvnomper.setText(usu?.nombre?.lowercase())
        tvemaper.setText(usu?.email?.lowercase())
        tvtipper.setText(usu?.tipo?.lowercase())
        tvconper.setText(usu?.contraseña?.toString())


        volver()
        desactivarFlecha()



    } // Cierra el onCreate


    fun volver(){

        btnvolper.setOnClickListener {
            var intent = Intent(this@Perfil, Menu::class.java)
            intent.putExtra("usu", usu)
            startActivity(intent)
        }

    } // Cierra la funcion volver().


    fun desactivarFlecha(){
        onBackPressedDispatcher.addCallback(this){

        }

    } // Cierra la funcion desactivarFlecha.



} // Cierra la clase Perfil.