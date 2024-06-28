package com.example.novasentinel.ui.acoso

import android.content.Intent
import android.net.Uri
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.example.novasentinel.R

class AcosoFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el diseño del fragmento
        val view = inflater.inflate(R.layout.fragment_acoso, container, false)

        // Obtener la referencia al ImageButton
        val btnDenuncia: ImageButton = view.findViewById(R.id.btndenuncia)

        // Configurar OnClickListener para el ImageButton
        btnDenuncia.setOnClickListener {
            val url = "https://www.fiscalia.gov.co/colombia/wp-content/uploads/Salas-de-Recepción-de-Denuncias.pdf"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
        // Obtener la referencia al botón
        val btnPurpura: ImageButton = view.findViewById(R.id.btnpurpura)

// Configurar el listener de clic para el botón
        btnPurpura.setOnClickListener {
            // Abrir el enlace al hacer clic en el botón
            val url = "https://sdmujer.gov.co/nuestros-servicios/servicios-para-las-mujeres/linea-purpura"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }



        // Retornar la vista inflada
        return view
    }
}