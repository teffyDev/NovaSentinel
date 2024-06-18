package com.example.novasentinel.ui.perfil

import android.content.Intent
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.novasentinel.R
import com.example.novasentinel.HistorialUsuarioActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class PerfilFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var txtNombreU: EditText
    private lateinit var txtIdentificacionU: EditText
    private lateinit var txtCorreoU: EditText
    private lateinit var txtFechaU: EditText
    private lateinit var txtGenero: EditText
    private lateinit var btnActualizar: Button
    private lateinit var btnHistorial: Button
    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.US)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_perfil, container, false)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        txtNombreU = view.findViewById(R.id.txtNombreU)
        txtIdentificacionU = view.findViewById(R.id.txtIdentificacionU)
        txtCorreoU = view.findViewById(R.id.txtCorreoU)
        txtFechaU = view.findViewById(R.id.txtFechaU)
        txtGenero = view.findViewById(R.id.TextGenero)
        btnActualizar = view.findViewById(R.id.btnactualizar)
        btnHistorial = view.findViewById(R.id.btnhistrorial)

        cargarDatosUsuario()

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

        btnActualizar.setOnClickListener {
            actualizarDatosUsuario()
        }

        btnHistorial.setOnClickListener {
            val intent = Intent(requireContext(), HistorialUsuarioActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    private fun hideKeyboard(view: View) {
        val imm = requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun showGenderPickerDialog() {
        val genres = arrayOf("Femenino", "Masculino", "Binario", "Otro")
        val builder = AlertDialog.Builder(requireContext())
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
            requireContext(),
            R.style.CustomDatePickerDialogTheme,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDay)
                if (isAtLeast13YearsOld(selectedDate.time)) {
                    txtFechaU.setText(dateFormatter.format(selectedDate.time))
                } else {
                    Toast.makeText(requireContext(), "Debes tener al menos 13 años", Toast.LENGTH_SHORT).show()
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

    private fun cargarDatosUsuario() {
        val usuarioActual = auth.currentUser
        if (usuarioActual != null) {
            val userId = usuarioActual.uid
            val usuarioRef = db.collection("usuarios").document(userId)
            usuarioRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        txtNombreU.setText(document.getString("nombre"))
                        txtIdentificacionU.setText(document.getString("identificacion"))
                        txtCorreoU.setText(document.getString("correo"))
                        txtFechaU.setText(document.getString("fechaNacimiento"))
                        txtGenero.setText(document.getString("genero"))
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Error al cargar datos: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun actualizarDatosUsuario() {
        val nombre = txtNombreU.text.toString().trim()
        val identificacion = txtIdentificacionU.text.toString().trim()
        val correo = txtCorreoU.text.toString().trim()
        val fechaNacimiento = txtFechaU.text.toString().trim()
        val genero = txtGenero.text.toString().trim()

        val usuarioActual = auth.currentUser
        if (usuarioActual != null) {
            val userId = usuarioActual.uid
            val usuario = hashMapOf(
                "nombre" to nombre,
                "identificacion" to identificacion,
                "correo" to correo,
                "fechaNacimiento" to fechaNacimiento,
                "genero" to genero
            )
            db.collection("usuarios").document(userId)
                .set(usuario)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Datos actualizados correctamente", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Error al actualizar datos: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
