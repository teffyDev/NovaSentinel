package com.example.novasentinel.ui.cerrare

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.novasentinel.MainActivity
import com.example.novasentinel.R


class CerrareFragment : Fragment() {

    companion object {
        fun newInstance() = CerrareFragment()
    }

    private val viewModel: CerrareViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cerrare, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Crear una variable local para el botón btnVolverInicio
        val btnVolverInicio = view.findViewById<View>(R.id.btnVolverInicio)

        // Configurar el click listener para el botón btnVolverInicio
        btnVolverInicio.setOnClickListener {
            // Crear un Intent para iniciar MainActivity
            val intent = Intent(requireActivity(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            requireActivity().finish() // Opcional: Finalizar la actividad actual después de navegar a MainActivity
        }
    }
}
