package com.example.novasentinel.ui.boton

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.novasentinel.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

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
                    // Enviar notificación
                    CoroutineScope(Dispatchers.IO).launch {
                        enviarNotificacion()
                    }
                }
            }
            true // Indica que se ha manejado el evento
        }

        return view
    }

    private suspend fun enviarNotificacion() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val db = FirebaseFirestore.getInstance()
            db.collection("entidades")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val token = document.getString("fcmToken")
                        if (token != null) {
                            CoroutineScope(Dispatchers.IO).launch {
                                enviarNotificacionConToken(token)
                            }
                        } else {
                            Log.w("BotonFragment", "Entidad sin token FCM")
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("BotonFragment", "Error al obtener tokens FCM: ${e.message}")
                }
        } else {
            Log.e("BotonFragment", "Usuario no autenticado")
        }
    }

    private suspend fun enviarNotificacionConToken(token: String) {
        try {
            val client = OkHttpClient()
            val json = JSONObject()
            json.put("to", token)
            val notification = JSONObject()
            notification.put("title", "Alerta de emergencia")
            notification.put("body", "Un usuario ha presionado el botón de emergencia")
            json.put("notification", notification)
            val body = json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
            val request = Request.Builder()
                .header("Authorization", "key=AIzaSyAGUnJnO8fs_vtjDhmMJI_UwzsGt4Evu80")  // Reemplaza "TU_CLAVE_DEL_SERVIDOR" con tu clave real
                .url("https://fcm.googleapis.com/fcm/send")
                .post(body)
                .build()
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    Log.d("BotonFragment", "Notificación enviada exitosamente")
                } else {
                    Log.e("BotonFragment", "Error en la respuesta del servidor FCM: ${response.message}, Cuerpo de respuesta: ${response.body?.string()}")
                }
            }
        } catch (e: Exception) {
            Log.e("BotonFragment", "Error al enviar la notificación: ${e.message}")
        }
    }
}

