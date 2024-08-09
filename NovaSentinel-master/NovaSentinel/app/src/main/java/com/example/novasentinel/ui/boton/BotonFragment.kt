package com.example.novasentinel.ui.boton

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.novasentinel.R
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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
                    // Obtener ubicación y enviar notificación
                    getCurrentLocation()
                }
            }
            true // Indica que se ha manejado el evento
        }

        return view
    }

    private fun getCurrentLocation() {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permissions if not granted
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                101
            )
            return
        }
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude
                saveAlertToFirestore(latitude, longitude)
            } else {
                Log.e("Location", "Failed to get location")
            }
        }
    }

    private fun saveAlertToFirestore(latitude: Double, longitude: Double) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userEmail = currentUser?.email

        // Fetch the entity ID for the logged in user
        FirebaseFirestore.getInstance().collection("usuarios").document(userEmail!!).get() // Cambiado de "users" a "usuarios"
            .addOnSuccessListener { document ->
                val entityId = document.getString("entityId")

                if (entityId != null) {
                    // Save alert to Firestore
                    val alertData = HashMap<String, Any>()
                    alertData["title"] = "Emergencia"
                    alertData["body"] = "El usuario ha enviado una emergencia."
                    alertData["latitude"] = latitude.toString()
                    alertData["longitude"] = longitude.toString()
                    alertData["entityId"] = entityId

                    FirebaseFirestore.getInstance().collection("alerts").add(alertData)
                        .addOnSuccessListener {
                            Log.d("Firestore", "Alert saved successfully")
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firestore", "Error saving alert", e)
                        }
                } else {
                    Log.e("Firestore", "Entity ID not found for user.")
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error fetching entity ID", e)
            }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso de ubicación concedido, obtener la ubicación nuevamente
                getCurrentLocation()
            } else {
                Log.e("Permissions", "Location permission denied")
            }
        }
    }
}
