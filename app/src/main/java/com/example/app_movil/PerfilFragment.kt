package com.example.app_movil

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

class PerfilFragment : Fragment() {

    // Obtener el ViewModel compartido de la actividad Menu
    private val userViewModel: UserViewModel by activityViewModels()

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
        val btnvolper: Button = view.findViewById(R.id.btnvolper)

        // El botón para volver no es necesario en una navegación con fragmentos
        btnvolper.visibility = View.GONE

        // Observar los cambios en el usuario y actualizar la UI
        userViewModel.usuario.observe(viewLifecycleOwner) { usu ->
            if (usu != null) {
                tvusuperfil.text = "Bienvenido Usuario: ${usu.nombre}"
                tvnomper.text = usu.nombre.lowercase()
                tvemaper.text = usu.email.lowercase()
                tvtipper.text = usu.tipo.lowercase()
            }
        }
    }
}
