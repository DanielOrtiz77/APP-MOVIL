package com.example.app_movil

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class Menu : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView
    private var usu: Usuario? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        // Simulación: Obtener datos del usuario después del login.
        // En tu caso real, lo recibirías del Intent de la Activity Principal.
        // Aquí lo creo para el ejemplo, pero tú debes usar la línea comentada.
        // usu = intent.getParcelableExtra("usuario")
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        usu = Usuario(
            nombre = firebaseUser?.displayName ?: firebaseUser?.email ?: "Anónimo",
            email = firebaseUser?.email ?: "N/A",
            tipo = "Normal", // Deberías obtener esto de tu base de datos
            contraseña = 0 // No almacenar contraseñas en texto plano
        )


        bottomNav = findViewById(R.id.bottom_navigation)

        bottomNav.setOnItemSelectedListener { item ->
            var selectedFragment: Fragment? = null

            when (item.itemId) {
                R.id.nav_manipular -> {
                    selectedFragment = ManipularArduinoFragment()
                }
                R.id.nav_acciones -> {
                    selectedFragment = AccionesFragment()
                }
                R.id.nav_perfil -> {
                    selectedFragment = PerfilFragment()
                }
                R.id.nav_cerrar_sesion -> {
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this, Principal::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    return@setOnItemSelectedListener true // Finaliza el listener aquí
                }
            }

            if (selectedFragment != null) {
                // Pasar el objeto Usuario al fragment
                val bundle = Bundle()
                bundle.putParcelable("usuario", usu)
                selectedFragment.arguments = bundle

                // Reemplazar el contenido del FrameLayout con el nuevo fragment
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit()
            }
            true
        }

        // Cargar el fragmento inicial por defecto al abrir la actividad
        if (savedInstanceState == null) {
            bottomNav.selectedItemId = R.id.nav_manipular // Esto activará el listener y cargará el fragmento
        }
    }
}
