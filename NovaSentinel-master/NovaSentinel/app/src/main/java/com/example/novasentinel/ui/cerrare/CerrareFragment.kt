package com.example.novasentinel.ui.cerrare

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.novasentinel.MainActivity
import com.example.novasentinel.R
import com.google.firebase.auth.FirebaseAuth

class CerrareFragment : Fragment() {

    companion object {
        fun newInstance() = CerrareFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cerrare, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnVolverInicio = view.findViewById<View>(R.id.btnVolverInicio)
        btnVolverInicio.setOnClickListener {
            // Limpiar la sesión activa
            val sharedPreferences = requireActivity().getSharedPreferences("EntidadPrefs", Context.MODE_PRIVATE)
            with(sharedPreferences.edit()) {
                clear()
                apply()
            }

            // Cerrar sesión de FirebaseAuth
            FirebaseAuth.getInstance().signOut()

            // Redirigir a MainActivity
            val intent = Intent(requireActivity(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            requireActivity().finish()
        }
    }
}
