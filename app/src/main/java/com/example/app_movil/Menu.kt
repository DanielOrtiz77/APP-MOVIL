package com.example.app_movil

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

// --- ViewModel para compartir datos del usuario ---
class UserViewModel : ViewModel() {
    val usuario = MutableLiveData<Usuario>()
}

class Menu : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView
    // Inicializar el ViewModel
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        // --- OBTENER EL USUARIO DEL INTENT Y GUARDARLO EN EL VIEWMODEL ---
        val usu: Usuario? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("usuario", Usuario::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("usuario")
        }

        if (usu != null) {
            userViewModel.usuario.value = usu
        } else {
            cerrarSesion()
            return // Salir para evitar errores
        }
        // --- FIN DE LA LÓGICA DEL USUARIO ---

        bottomNav = findViewById(R.id.bottom_navigation)

        bottomNav.setOnItemSelectedListener { item ->
            val selectedFragment: Fragment? = when (item.itemId) {
                R.id.nav_manipular -> ManipularArduinoFragment()
                R.id.nav_acciones -> AccionesFragment()
                R.id.nav_perfil -> PerfilFragment()
                R.id.nav_cerrar_sesion -> {
                    cerrarSesion()
                    return@setOnItemSelectedListener true
                }
                else -> null
            }

            if (selectedFragment != null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit()
            }
            true
        }

        // Cargar el fragmento inicial por defecto
        if (savedInstanceState == null) {
            bottomNav.selectedItemId = R.id.nav_manipular
        }
    }

    private fun cerrarSesion() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, Principal::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}
