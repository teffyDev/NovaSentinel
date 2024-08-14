package com.example.novasentinel.ui.historial

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.novasentinel.Alert
import com.google.firebase.firestore.FirebaseFirestore

class HistorialViewModel : ViewModel() {

    private val _alerts = MutableLiveData<List<Alert>>()
    val alerts: LiveData<List<Alert>> get() = _alerts

    init {
        fetchAlerts()
    }

    private fun fetchAlerts() {
        val db = FirebaseFirestore.getInstance()

        db.collection("alerts")
            .get()
            .addOnSuccessListener { result ->
                val alertList = mutableListOf<Alert>()
                for (document in result) {
                    val alert = document.toObject(Alert::class.java)
                    alertList.add(alert)
                }
                _alerts.value = alertList
                Log.d("HistorialViewModel", "Alerts retrieved: ${alertList.size}")

            }
            .addOnFailureListener { exception ->
                // Manejar errores
                Log.e("HistorialViewModel", "Error retrieving alerts", exception)

                _alerts.value = emptyList()
            }
    }
}