package com.example.novasentinel

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

class InicioEntidadActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var passwordEditText: EditText
    private lateinit var togglePasswordVisibility: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio_entidad)

        auth = FirebaseAuth.getInstance()

        // Referencias a los componentes del diseño
        passwordEditText = findViewById(R.id.txtConreseñaE)
        togglePasswordVisibility = findViewById(R.id.togglePasswordVisibility)

        // Verifica si ya hay una sesión activa
        val sharedPreferences = getSharedPreferences("EntidadPrefs", Context.MODE_PRIVATE)
        val sesionActiva = sharedPreferences.getBoolean("sesionActiva", false)

        if (sesionActiva) {
            // Redirige automáticamente a MenuEntidadActivity
            val intent = Intent(this, MenuEntidadActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Configuración del botón de visibilidad de la contraseña
        togglePasswordVisibility.setOnClickListener {
            togglePasswordVisibility()
        }

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

    private fun togglePasswordVisibility() {
        if (passwordEditText.inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
            // La contraseña está visible, oculta la contraseña
            passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            togglePasswordVisibility.setImageResource(R.drawable.ojocc)
        } else {
            // La contraseña está oculta, muéstrala
            passwordEditText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            togglePasswordVisibility.setImageResource(R.drawable.ojoac)
        }
        // Mueve el cursor al final del texto
        passwordEditText.setSelection(passwordEditText.text.length)
    }

    private fun iniciarSesion() {
        val correo = findViewById<EditText>(R.id.txtCorreoE).text.toString().trim()
        val contraseña = passwordEditText.text.toString().trim()

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
                                    FirebaseMessaging.getInstance().token.addOnCompleteListener { tokenTask ->
                                        if (tokenTask.isSuccessful) {
                                            val token = tokenTask.result
                                            entidadesRef.document(usuarioActual.uid)
                                                .update("fcmToken", token)
                                                .addOnSuccessListener {
                                                    // Guardar la sesión activa
                                                    val sharedPreferences = getSharedPreferences("EntidadPrefs", Context.MODE_PRIVATE)
                                                    with(sharedPreferences.edit()) {
                                                        putBoolean("sesionActiva", true)
                                                        apply()
                                                    }

                                                    val intent = Intent(this, MenuEntidadActivity::class.java)
                                                    startActivity(intent)
                                                    finish()
                                                }
                                                .addOnFailureListener { e ->
                                                    Toast.makeText(this, "Error al guardar el token FCM: ${e.message}", Toast.LENGTH_SHORT).show()
                                                }
                                        } else {
                                            Toast.makeText(this, "Error al obtener el token FCM: ${tokenTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    }
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
                        Toast.makeText(this, "No se pudo obtener información del usuario", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Error al iniciar sesión: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
