package com.example.novasentinel

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
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
    private lateinit var txtTipoIdentificacionU: EditText
    private lateinit var txtEntidadAsoc: EditText
    private lateinit var btnShowHidePasswordU: ImageButton
    private lateinit var btnShowHidePasswordUR: ImageButton
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
        txtTipoIdentificacionU = findViewById(R.id.txtTipoIdentificacionU)
        txtEntidadAsoc = findViewById(R.id.TexEntidadAsoc)

        // Inicializar botones de mostrar/ocultar contraseña
        btnShowHidePasswordU = findViewById(R.id.btnShowHidePasswordU)
        btnShowHidePasswordUR = findViewById(R.id.btnShowHidePasswordUR)

        var isPasswordVisibleU = false
        var isPasswordVisibleUR = false

        btnShowHidePasswordU.setOnClickListener {
            isPasswordVisibleU = !isPasswordVisibleU
            txtContraseñaU.inputType = if (isPasswordVisibleU) {
                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            txtContraseñaU.setSelection(txtContraseñaU.text.length)
            btnShowHidePasswordU.setImageResource(if (isPasswordVisibleU) R.drawable.ojoan else R.drawable.ojocn)
        }

        btnShowHidePasswordUR.setOnClickListener {
            isPasswordVisibleUR = !isPasswordVisibleUR
            txtContraseñaUR.inputType = if (isPasswordVisibleUR) {
                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            txtContraseñaUR.setSelection(txtContraseñaUR.text.length)
            btnShowHidePasswordUR.setImageResource(if (isPasswordVisibleUR) R.drawable.ojoan else R.drawable.ojocn)
        }

        // Desactivar teclado para los campos de tipo de documento y entidad
        txtTipoIdentificacionU.inputType = InputType.TYPE_NULL
        txtEntidadAsoc.inputType = InputType.TYPE_NULL

        // Configurar el selector de género
        txtGenero.setOnClickListener {
            showGenderPickerDialog()
        }

        // Configurar el selector de fecha
        txtFechaU.setOnClickListener {
            showDatePickerDialog()
        }

        // Configurar el selector de tipo de identificación
        txtTipoIdentificacionU.setOnClickListener {
            showDocumentTypePickerDialog()
        }
        txtTipoIdentificacionU.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                hideKeyboard(v)
                showDocumentTypePickerDialog()
            }
        }

        // Configurar el selector de entidad asociada
        txtEntidadAsoc.setOnClickListener {
            showEntityPickerDialog()
        }
        txtEntidadAsoc.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                hideKeyboard(v)
                showEntityPickerDialog()
            }
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

    private fun showDocumentTypePickerDialog() {
        val documentTypes = arrayOf("Tarjeta de Identidad", "Cédula de Ciudadanía", "Cédula de Extranjería", "Pasaporte")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Selecciona tu tipo de identificación")
        builder.setItems(documentTypes) { _, which ->
            txtTipoIdentificacionU.setText(documentTypes[which])
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

    private fun showEntityPickerDialog() {
        db.collection("entidades")
            .get()
            .addOnSuccessListener { result ->
                val entities = result.map { it.getString("nombre") ?: "" }
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Selecciona una entidad")
                builder.setItems(entities.toTypedArray()) { _, which ->
                    txtEntidadAsoc.setText(entities[which])
                }
                builder.show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar las entidades: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun registrarUsuario() {
        val nombre = txtNombreU.text.toString()
        val identificacion = txtIdentificacionU.text.toString()
        val correo = txtCorreoU.text.toString()
        val fechaNacimiento = txtFechaU.text.toString()
        val genero = txtGenero.text.toString()
        val tipoIdentificacion = txtTipoIdentificacionU.text.toString()
        val entidadAsociada = txtEntidadAsoc.text.toString()
        val contraseña = txtContraseñaU.text.toString()
        val contraseñaR = txtContraseñaUR.text.toString()

        if (nombre.isEmpty() || identificacion.isEmpty() || correo.isEmpty() || fechaNacimiento.isEmpty() ||
            genero.isEmpty() || tipoIdentificacion.isEmpty() || entidadAsociada.isEmpty() ||
            contraseña.isEmpty() || contraseñaR.isEmpty()) {
            Toast.makeText(this, "Por favor, llena todos los campos.", Toast.LENGTH_SHORT).show()
            return
        }

        if (contraseña != contraseñaR) {
            Toast.makeText(this, "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(correo, contraseña)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val userData = hashMapOf(
                        "nombre" to nombre,
                        "identificacion" to identificacion,
                        "correo" to correo,
                        "fechaNacimiento" to fechaNacimiento,
                        "genero" to genero,
                        "tipoIdentificacion" to tipoIdentificacion,
                        "entidadAsociada" to entidadAsociada
                    )

                    user?.let {
                        db.collection("usuarios").document(it.uid)
                            .set(userData)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, InicioUsuarioActivity::class.java)
                                startActivity(intent)
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error al guardar los datos: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    Toast.makeText(this, "Error en el registro: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}

