package com.example.novasentinel

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class InicioEntidadActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio_entidad)

        auth = FirebaseAuth.getInstance()

        // Botón para iniciar sesión
        val btnEntrarE = findViewById<Button>(R.id.btnEntrarE)
        btnEntrarE.setOnClickListener {
            iniciarSesion()
        }

        // Botón para ir a la pantalla de registro
        val btnRegistrateE = findViewById<Button>(R.id.btnRegistrateE)
        btnRegistrateE.setOnClickListener {
            val intent = Intent(this, RegistroEntidadActivity::class.java)
            startActivity(intent)
        }

        // Botón para ir a la pantalla de restablecer contraseña
        val btnRestablacerE = findViewById<Button>(R.id.btnRestablacerE)
        btnRestablacerE.setOnClickListener {
            val intent = Intent(this, RestablecerEntidadActivity::class.java)
            startActivity(intent)
        }
    }

    private fun iniciarSesion() {
        val correo = findViewById<EditText>(R.id.txtCorreoE).text.toString().trim()
        val contraseña = findViewById<EditText>(R.id.txtConreseñaE).text.toString().trim()

        if (correo.isEmpty() || contraseña.isEmpty()) {
            Toast.makeText(this, "Por favor ingresa el correo y la contraseña", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(correo, contraseña)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val usuarioActual = auth.currentUser
                    if (usuarioActual != null) {
                        val entidadesRef = FirebaseFirestore.getInstance().collection("entidades")
                        entidadesRef.document(usuarioActual.uid).get()
                            .addOnSuccessListener { document ->
                                if (document.exists()) {
                                    // El usuario actual es una entidad
                                    val intent = Intent(this, MenuEntidadActivity::class.java)
                                    startActivity(intent)
                                    finish() // Esto cierra la actividad actual, por lo que al volver atrás desde MenuEntidadActivity, no volverá aquí.
                                } else {
                                    // El usuario actual no es una entidad
                                    Toast.makeText(this, "Usuario no autorizado", Toast.LENGTH_SHORT).show()
                                    auth.signOut()
                                }
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error al verificar tipo de usuario: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        // Usuario actual es nulo
                        Toast.makeText(this, "No se pudo obtener información del usuario", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Si el inicio de sesión falla, muestra un mensaje al usuario.
                    Toast.makeText(this, "Error al iniciar sesión: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

}
