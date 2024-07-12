package com.example.novasentinel.ui.boton

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import com.example.novasentinel.R
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONObject
import java.io.IOException

class BotonFragment : Fragment() {

    private lateinit var imageButton: ImageButton
    private lateinit var imageButtonNormal: Drawable
    private lateinit var imageButtonPressed: Drawable
    private val client = OkHttpClient()

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
                    sendNotification()
                }
            }
            true // Indica que se ha manejado el evento
        }

        return view
    }

    private fun sendNotification() {
        val url = "https://us-central1-novasentinel-30414.cloudfunctions.net/sendPushNotification"
        val json = JSONObject()
        json.put("title", "Emergencia")
        json.put("body", "¡Alguien presionó el botón de emergencia!")

        val body = RequestBody.create("application/json; charset=utf-8".toMediaType(), json.toString())
        val request = Request.Builder().url(url).post(body).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Maneja la falla de la solicitud
                e.printStackTrace()
                Log.e("FCM", "Request failed: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                // Maneja la respuesta de la solicitud
                if (response.isSuccessful) {
                    Log.d("FCM", "Notificación enviada exitosamente")
                } else {
                    Log.e("FCM", "Fallo al enviar la notificación: ${response.message}")
                }
            }
        })
    }
}

