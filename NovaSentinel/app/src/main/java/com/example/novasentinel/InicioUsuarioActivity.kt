package com.example.novasentinel

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class InicioUsuarioActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var txtCorreoU: EditText
    private lateinit var txtContraseñaU: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio_usuario)

        // Inicializar Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Inicializar EditTexts
        txtCorreoU = findViewById(R.id.txtCorreoU)
        txtContraseñaU = findViewById(R.id.txtContraseñaU)

        // Botón para iniciar sesión
        val btnEntrarU = findViewById<Button>(R.id.btnEntrarU)
        btnEntrarU.setOnClickListener {
            iniciarSesion()
        }

        // Botón lleva a registro usuario
        val btnRegistrarU = findViewById<Button>(R.id.btnRegistrateU)
        btnRegistrarU.setOnClickListener {
            val intent = Intent(this, RegistroUsuarioActivity::class.java)
            startActivity(intent)
        }

        // Botón lleva a restablecimiento usuario
        val btnRestablacerU = findViewById<Button>(R.id.btnRestablacerU)
        btnRestablacerU.setOnClickListener {
            val intent = Intent(this, RestablecerUsuarioActivity::class.java)
            startActivity(intent)
        }
    }

    private fun iniciarSesion() {
        val correo = txtCorreoU.text.toString().trim()
        val contraseña = txtContraseñaU.text.toString().trim()

        if (correo.isEmpty() || contraseña.isEmpty()) {
            Toast.makeText(this, "Por favor ingresa el correo y la contraseña", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(correo, contraseña)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val usuarioActual = auth.currentUser
                    if (usuarioActual != null) {
                        val usuariosRef = FirebaseFirestore.getInstance().collection("usuarios")
                        usuariosRef.document(usuarioActual.uid).get()
                            .addOnSuccessListener { document ->
                                if (document.exists()) {
                                    // El usuario actual es un usuario
                                    val intent = Intent(this, MenuUsuarioActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    // El usuario actual no es un usuario
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
