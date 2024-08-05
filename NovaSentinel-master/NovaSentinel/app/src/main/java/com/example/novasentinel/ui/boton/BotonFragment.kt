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
import com.google.auth.oauth2.GoogleCredentials
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

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
                        enviarNotificacion("d1eU9sjtTg-8OtIr7AhNNd:APA91bEtthcX4Dxw96SIq0c6WD1c5dau-YHcCMBYrnodm2ckYQLao4AGlnn46RR2bOviGwzJY61hCkmKQe5IJ4fVI9t882HyF3JvhT0BYsP6cALAsw2SuWJYekWGk3UvtINmFdicll7D")
                    }
                }
            }
            true // Indica que se ha manejado el evento
        }

        return view
    }

    private fun enviarNotificacion(token: String) {
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            try {
                val accessToken = getAccessToken()
                val message = JSONObject().apply {
                    put("message", JSONObject().apply {
                        put("token", token)
                        put("notification", JSONObject().apply {
                            put("title", "Breaking News")
                            put("body", "New news story available.")
                        })
                        put("data", JSONObject().apply {
                            put("story_id", "story_12345")
                        })
                    })
                }

                val url = URL("https://fcm.googleapis.com/v1/projects/novasentinel-30414/messages:send")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Authorization", "Bearer $accessToken")
                conn.setRequestProperty("Content-Type", "application/json")
                conn.doOutput = true

                val outputStream = DataOutputStream(conn.outputStream)
                outputStream.writeBytes(message.toString())
                outputStream.flush()
                outputStream.close()

                val responseCode = conn.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Log.d("FCM", "Notification sent successfully.")
                } else {
                    Log.e("FCM", "Failed to send notification. Response code: $responseCode")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getAccessToken(): String {
        val credentials = GoogleCredentials
            .fromStream(requireContext().assets.open("service_account.json"))
            .createScoped(listOf("https://www.googleapis.com/auth/firebase.messaging"))
        credentials.refreshIfExpired()
        return credentials.accessToken.tokenValue
    }
}

