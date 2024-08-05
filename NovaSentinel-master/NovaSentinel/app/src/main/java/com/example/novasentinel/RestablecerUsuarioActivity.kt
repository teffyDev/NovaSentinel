package com.example.novasentinel

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class RestablecerUsuarioActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var txtCorreoU: EditText
    private lateinit var btnEnviarRU: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restablecer_usuario)

        auth = FirebaseAuth.getInstance()

        txtCorreoU = findViewById(R.id.txtCorreoU)
        btnEnviarRU = findViewById(R.id.btnEnviarRU)

        btnEnviarRU.setOnClickListener {
            val email = txtCorreoU.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "Por favor, ingrese su correo electrónico.", Toast.LENGTH_SHORT).show()
            } else {
                restablecerContraseña(email)
            }
        }
    }

    private fun restablecerContraseña(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Correo de restablecimiento enviado.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error al enviar el correo de restablecimiento.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
