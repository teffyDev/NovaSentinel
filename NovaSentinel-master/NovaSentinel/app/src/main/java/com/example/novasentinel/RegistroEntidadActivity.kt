package com.example.novasentinel

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegistroEntidadActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_entidad)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val btnRegistrarEntidad = findViewById<Button>(R.id.btnregistrarE)
        val btnYaCuentaE = findViewById<Button>(R.id.btnYaCuentaE)
        val txtNombreE = findViewById<EditText>(R.id.txtNombreE)
        val etNitEmpresa = findViewById<EditText>(R.id.txtNitE)
        val etCorreo = findViewById<EditText>(R.id.txtCorreoE)
        val etContraseña = findViewById<EditText>(R.id.txtConreseñaE)
        val etRepetirContraseña = findViewById<EditText>(R.id.txtConreseñaER)

        val btnShowPassword = findViewById<ImageButton>(R.id.btnShowPasswordE)
        val btnShowPasswordR = findViewById<ImageButton>(R.id.btnShowPasswordER)

        var isPasswordVisible = false
        var isPasswordVisibleR = false

        btnShowPassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                etContraseña.transformationMethod = HideReturnsTransformationMethod.getInstance()
                btnShowPassword.setImageResource(R.drawable.ojoac) // Cambia el ícono a "ocultar"
            } else {
                etContraseña.transformationMethod = PasswordTransformationMethod.getInstance()
                btnShowPassword.setImageResource(R.drawable.ojocc) // Cambia el ícono a "mostrar"
            }
            etContraseña.setSelection(etContraseña.text.length) // Mantener el cursor al final
        }

        btnShowPasswordR.setOnClickListener {
            isPasswordVisibleR = !isPasswordVisibleR
            if (isPasswordVisibleR) {
                etRepetirContraseña.transformationMethod = HideReturnsTransformationMethod.getInstance()
                btnShowPasswordR.setImageResource(R.drawable.ojoac) // Cambia el ícono a "ocultar"
            } else {
                etRepetirContraseña.transformationMethod = PasswordTransformationMethod.getInstance()
                btnShowPasswordR.setImageResource(R.drawable.ojocc) // Cambia el ícono a "mostrar"
            }
            etRepetirContraseña.setSelection(etRepetirContraseña.text.length) // Mantener el cursor al final
        }

        btnRegistrarEntidad.setOnClickListener {
            val nombreEmpresa = txtNombreE.text.toString().trim()
            val nitEmpresa = etNitEmpresa.text.toString().trim()
            val correo = etCorreo.text.toString().trim()
            val contraseña = etContraseña.text.toString().trim()
            val repetirContraseña = etRepetirContraseña.text.toString().trim()

            if (contraseña == repetirContraseña) {
                auth.createUserWithEmailAndPassword(correo, contraseña).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid
                        val entidad = hashMapOf(
                            "nombreEmpresa" to nombreEmpresa,
                            "nitEmpresa" to nitEmpresa,
                            "correo" to correo
                        )

                        userId?.let {
                            db.collection("entidades").document(it).set(entidad)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Entidad registrada exitosamente", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Error al registrar entidad", Toast.LENGTH_SHORT).show()
                                }
                        }
                    } else {
                        Toast.makeText(this, "Error de autenticación", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            }
        }

        btnYaCuentaE.setOnClickListener {
            // Abrir la actividad InicioEntidadActivity
            val intent = Intent(this, InicioEntidadActivity::class.java)
            startActivity(intent)
        }
    }
}
