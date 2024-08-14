package com.example.novasentinel

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.novasentinel.ui.notificacion.NotificacionFragment
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {

    private val PERMISSION_REQUEST_CODE = 101

    private val requestLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allGranted = permissions.entries.all { it.value }
            if (!allGranted) {
                // If any permission is denied, close the application
                finish()
            } else {
                checkAndRedirectIfLoggedIn()
            }
        }

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                // Permission denied, close the application
                finish()
            } else {
                checkAndRequestLocationPermission()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        if (intent.hasExtra("latitude") && intent.hasExtra("longitude")) {
            val latitude = intent.getStringExtra("latitude")
            val longitude = intent.getStringExtra("longitude")

            val fragment = NotificacionFragment().apply {
                arguments = Bundle().apply {
                    putString("latitude", latitude)
                    putString("longitude", longitude)
                }
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.main, fragment)
                .commit()
            return
        }

        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Solicitar permisos de notificación y ubicación
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            checkAndRequestLocationPermission()
        }
    }

    private fun checkAndRequestLocationPermission() {
        if (!hasLocationPermission()) {
            requestLocationPermissions()
        } else {
            checkAndRedirectIfLoggedIn()
        }
    }

    private fun checkAndRedirectIfLoggedIn() {
        val sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)

        // Verificar si el usuario ha iniciado sesión
        if (isLoggedIn) {
            // Redirigir a la actividad principal del usuario
            val intent = Intent(this, MenuUsuarioActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            // Si no ha iniciado sesión, muestra los botones
            setupButtons()
        }
    }

    private fun setupButtons() {
        val btnUsuario = findViewById<Button>(R.id.btnUsuario)
        val btnEntidad = findViewById<Button>(R.id.btnEntidad)

        btnUsuario.setOnClickListener {
            // Ir a la pantalla de inicio de sesión del usuario
            val intent = Intent(this, InicioUsuarioActivity::class.java)
            startActivity(intent)
        }

        btnEntidad.setOnClickListener {
            // Ir a la pantalla de inicio de sesión de la entidad
            val intent = Intent(this, InicioEntidadActivity::class.java)
            startActivity(intent)
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermissions() {
        requestLocationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent?.let {
            val latitude = it.getStringExtra("latitude")
            val longitude = it.getStringExtra("longitude")
            if (latitude != null && longitude != null) {
                // Crea una instancia de NotificacionFragment con los argumentos de ubicación
                val fragment = NotificacionFragment().apply {
                    arguments = Bundle().apply {
                        putString("latitude", latitude)
                        putString("longitude", longitude)
                    }
                }

                // Reemplaza el contenido del contenedor con el NotificacionFragment
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, fragment) // Usa el ID correcto del contenedor en tu layout
                    .commit()
            }
        }
    }

}

