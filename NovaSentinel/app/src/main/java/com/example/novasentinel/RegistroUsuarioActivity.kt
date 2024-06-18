package com.example.novasentinel

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class RegistroUsuarioActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var txtNombreU: EditText
    private lateinit var txtIdentificacionU: EditText
    private lateinit var txtCorreoU: EditText
    private lateinit var txtFechaU: EditText
    private lateinit var txtGenero: EditText
    private lateinit var txtContraseñaU: EditText
    private lateinit var txtContraseñaUR: EditText
    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.US)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_usuario)

        // Inicializar Firebase Auth y Firestore
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Inicializar EditTexts
        txtNombreU = findViewById(R.id.txtNombreU)
        txtIdentificacionU = findViewById(R.id.txtIdentificacionU)
        txtCorreoU = findViewById(R.id.txtCorreoU)
        txtFechaU = findViewById(R.id.txtFechaU)
        txtGenero = findViewById(R.id.TextGenero)
        txtContraseñaU = findViewById(R.id.txtContraseñaU)
        txtContraseñaUR = findViewById(R.id.txtContraseñaUR)

        // Configurar el selector de género
        txtGenero.setOnClickListener {
            showGenderPickerDialog()
        }

        // Configurar el selector de fecha
        txtFechaU.setOnClickListener {
            showDatePickerDialog()
        }

        // Asegurarse de que el teclado no aparezca
        txtFechaU.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                hideKeyboard(v)
                showDatePickerDialog()
            }
        }
        txtFechaU.setOnClickListener {
            hideKeyboard(it)
            showDatePickerDialog()
        }

        // Botón para registrar al usuario
        val btnRegistrarU = findViewById<Button>(R.id.btnregistrarU)
        btnRegistrarU.setOnClickListener {
            registrarUsuario()
        }

        // Botón para retornar a la actividad de inicio
        val btnYaCuentaU = findViewById<Button>(R.id.btnYaCuentaU)
        btnYaCuentaU.setOnClickListener {
            val intent = Intent(this, InicioUsuarioActivity::class.java)
            startActivity(intent)
        }
    }

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun showGenderPickerDialog() {
        val genres = arrayOf("Femenino", "Masculino", "Binario", "Otro")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Selecciona tu género")
        builder.setItems(genres) { _, which ->
            txtGenero.setText(genres[which])
        }
        builder.show()
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            R.style.CustomDatePickerDialogTheme, // Aplica el tema personalizado aquí
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDay)
                if (isAtLeast13YearsOld(selectedDate.time)) {
                    txtFechaU.setText(dateFormatter.format(selectedDate.time))
                } else {
                    Toast.makeText(this, "Debes tener al menos 13 años", Toast.LENGTH_SHORT).show()
                }
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun isAtLeast13YearsOld(date: Date): Boolean {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.YEAR, -13)
        return date.before(calendar.time)
    }

    private fun registrarUsuario() {
        // Obtener los datos del usuario
        val nombre = txtNombreU.text.toString().trim()
        val identificacion = txtIdentificacionU.text.toString().trim()
        val correo = txtCorreoU.text.toString().trim()
        val fechaNacimiento = txtFechaU.text.toString().trim()
        val genero = txtGenero.text.toString().trim()
        val contraseña = txtContraseñaU.text.toString().trim()
        val contraseñaRepetida = txtContraseñaUR.text.toString().trim()

        // Verificar que las contraseñas coincidan
        if (contraseña != contraseñaRepetida) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            return
        }

        // Registrar al usuario en Firebase Auth y Firestore
        auth.createUserWithEmailAndPassword(correo, contraseña)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Obtener el ID del usuario registrado
                    val userId = auth.currentUser?.uid

                    // Crear un mapa con los datos del usuario
                    val usuario = hashMapOf(
                        "nombre" to nombre,
                        "identificacion" to identificacion,
                        "correo" to correo,
                        "fechaNacimiento" to fechaNacimiento,
                        "genero" to genero
                        // Agrega más campos según sea necesario
                    )

                    // Agregar el usuario a Cloud Firestore
                    db.collection("usuarios")
                        .document(userId!!)
                        .set(usuario)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show()
                            // Aquí puedes redirigir al usuario a otra actividad si deseas
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error al registrar usuario: $e", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "Error al registrar usuario: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
