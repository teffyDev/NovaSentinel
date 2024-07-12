package com.example.novasentinel

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {

    private val PERMISSION_REQUEST_CODE = 101

    private val requestLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                if (!it.value) {
                    // If any permission is denied, close the application
                    finish()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        val sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)

        // Verificar si el usuario ha iniciado sesión
        if (isLoggedIn) {
            // Redirigir a la actividad principal del usuario
            val intent = Intent(this, MenuUsuarioActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Solicitar permisos de notificación
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), PERMISSION_REQUEST_CODE)
        }

        // Solicitar permisos de ubicación
        if (!hasLocationPermission()) {
            requestLocationPermissions()
        }

        // Encuentra los botones y establece los listeners
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

    
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permiso concedido, puedes mostrar notificaciones
            } else {
                // Permiso denegado, cierra la aplicación
                finish()
            }
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
}





