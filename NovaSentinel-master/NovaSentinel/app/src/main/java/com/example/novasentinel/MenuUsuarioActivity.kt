package com.example.novasentinel

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.novasentinel.databinding.ActivityMenuUsuarioBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MenuUsuarioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuUsuarioBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMenuUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_menu_usuario)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.botonFragment, R.id.perfilFragment, R.id.acosoFragment, R.id.cerraruFragment
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}
