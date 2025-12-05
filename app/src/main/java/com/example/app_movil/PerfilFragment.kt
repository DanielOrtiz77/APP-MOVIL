package com.example.app_movil

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

class PerfilFragment : Fragment() {

    private var usu: Usuario? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            usu = it.getParcelable("usuario")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_perfil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvusuperfil: TextView = view.findViewById(R.id.tvusuperfil)
        val tvnomper: TextView = view.findViewById(R.id.tvnomper)
        val tvemaper: TextView = view.findViewById(R.id.tvemaper)
        val tvtipper: TextView = view.findViewById(R.id.tvtipper)
        val tvconper: TextView = view.findViewById(R.id.tvconper)
        val btnvolper: Button = view.findViewById(R.id.btnvolper)

        // Ocultar botón innecesario
        btnvolper.visibility = View.GONE

        tvusuperfil.text = "Bienvenido Usuario : ${usu?.nombre}"
        tvnomper.text = usu?.nombre?.lowercase()
        tvemaper.text = usu?.email?.lowercase()
        tvtipper.text = usu?.tipo?.lowercase()
        tvconper.text = usu?.contraseña.toString()
    }
}
