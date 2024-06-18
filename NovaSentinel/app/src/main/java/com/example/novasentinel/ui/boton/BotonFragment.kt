package com.example.novasentinel.ui.boton

import android.graphics.drawable.Drawable
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import com.example.novasentinel.R

class BotonFragment : Fragment() {

    private lateinit var imageButton: ImageButton
    private lateinit var imageButtonNormal: Drawable
    private lateinit var imageButtonPressed: Drawable

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_boton, container, false)

        imageButton = view.findViewById(R.id.imgboton)
        imageButtonNormal = ContextCompat.getDrawable(requireContext(), R.drawable.botonn)!!
        imageButtonPressed = ContextCompat.getDrawable(requireContext(), R.drawable.botono)!!

        // Set the initial image
        imageButton.setImageDrawable(imageButtonNormal)

        // Set the touch listener
        imageButton.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Cambiar imagen al presionar
                    imageButton.setImageDrawable(imageButtonPressed)
                }
                MotionEvent.ACTION_UP -> {
                    // Cambiar imagen al soltar
                    imageButton.setImageDrawable(imageButtonNormal)
                }
            }
            true // Indica que se ha manejado el evento
        }

        return view
    }
}
