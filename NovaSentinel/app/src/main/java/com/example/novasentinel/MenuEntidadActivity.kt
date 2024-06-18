package com.example.novasentinel

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.novasentinel.databinding.ActivityMenuEntidadBinding

class MenuEntidadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuEntidadBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMenuEntidadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_menu_entidad)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.notificacionFragment, R.id.historialFragment, R.id.cerrareFragment
            )
        )

        // Configuración de la ActionBar con la Toolbar
        setSupportActionBar(binding.toolbar)

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}
