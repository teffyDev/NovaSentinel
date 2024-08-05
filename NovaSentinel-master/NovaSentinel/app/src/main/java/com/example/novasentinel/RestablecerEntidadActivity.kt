package com.example.novasentinel

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class RestablecerEntidadActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var txtCorreoE: EditText
    private lateinit var btnEnviarRE: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restablecer_entidad)

        auth = FirebaseAuth.getInstance()

        txtCorreoE = findViewById(R.id.txtCorreoE)
        btnEnviarRE = findViewById(R.id.btnEnviarRE)

        btnEnviarRE.setOnClickListener {
            val email = txtCorreoE.text.toString().trim()
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
