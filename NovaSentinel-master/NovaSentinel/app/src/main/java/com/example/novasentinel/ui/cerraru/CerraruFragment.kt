package com.example.novasentinel.ui.cerraru

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import com.example.novasentinel.MainActivity
import com.example.novasentinel.R
import com.google.firebase.auth.FirebaseAuth

class CerraruFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cerraru, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        sharedPreferences = requireActivity().getSharedPreferences("MyAppPreferences", AppCompatActivity.MODE_PRIVATE)

        val btnCerrarSesion = view.findViewById<ImageButton>(R.id.btnVolverEntrada)
        btnCerrarSesion.setOnClickListener {
            cerrarSesion()
        }
    }

    private fun cerrarSesion() {
        // Cerrar sesión en Firebase Auth
        auth.signOut()

        // Limpiar SharedPreferences
        val editor = sharedPreferences.edit()
        editor.putBoolean("is_logged_in", false)
        editor.apply()

        // Redirigir al usuario a la pantalla de inicio de sesión
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }
}
