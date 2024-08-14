package com.example.novasentinel

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")
        sendRegistrationToServer(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")

        remoteMessage.notification?.let {
            val title = it.title
            val body = it.body
            Log.d(TAG, "Message Notification Title: $title")
            Log.d(TAG, "Message Notification Body: $body")

            val latitude = remoteMessage.data["latitude"]
            val longitude = remoteMessage.data["longitude"]
            Log.d(TAG, "Message Data Payload: Latitude: $latitude, Longitude: $longitude")

            sendNotification(title, body, latitude, longitude)
        }
    }

    private fun sendNotification(title: String?, body: String?, latitude: String?, longitude: String?) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("latitude", latitude)
            putExtra("longitude", longitude)
            putExtra("navigateToFragment", "NotificacionFragment") // Esto indica que debe ir al fragmento específico
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        val channelId = "default_channel_id"
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.iconc)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(notificationManager, channelId)
        notificationManager.notify(0, notificationBuilder.build())

        Log.d(TAG, "Notification sent with title: $title and body: $body")
    }


    private fun createNotificationChannel(notificationManager: NotificationManager, channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Default Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Channel for default notifications"
            }
            notificationManager.createNotificationChannel(channel)
            Log.d(TAG, "Notification channel created: $channelId")
        }
    }

    private fun sendRegistrationToServer(token: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userEmail = currentUser?.email

        if (userEmail != null) {
            val db = FirebaseFirestore.getInstance()

            db.collection("usuarios").whereEqualTo("correo", userEmail).get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val document = documents.documents[0]
                        val entidadAsociada = document.getString("entidadAsociada")
                        if (entidadAsociada != null) {
                            // Aquí buscamos la entidad usando el campo nombreEmpresa
                            db.collection("entidades")
                                .whereEqualTo("nombreEmpresa", entidadAsociada)
                                .get()
                                .addOnSuccessListener { querySnapshot ->
                                    if (!querySnapshot.isEmpty) {
                                        val entityDocument = querySnapshot.documents[0]
                                        val entityId = entityDocument.id

                                        // Actualizamos el token en el documento de la entidad encontrada
                                        db.collection("entidades").document(entityId)
                                            .update("fcmToken", token)
                                            .addOnSuccessListener {
                                                Log.d(TAG, "Token updated successfully in Firestore.")
                                            }
                                            .addOnFailureListener { e ->
                                                Log.e(TAG, "Error updating token in Firestore.", e)
                                            }
                                    } else {
                                        Log.e(TAG, "No entity found with nombreEmpresa: $entidadAsociada")
                                    }
                                }
                                .addOnFailureListener { e ->
                                    Log.e(TAG, "Error fetching entidad with nombreEmpresa", e)
                                }
                        } else {
                            Log.e(TAG, "Entidad asociada not found for user.")
                        }
                    } else {
                        Log.e(TAG, "No user found with the given email.")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error getting user document.", e)
                }
        }
    }


    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}
