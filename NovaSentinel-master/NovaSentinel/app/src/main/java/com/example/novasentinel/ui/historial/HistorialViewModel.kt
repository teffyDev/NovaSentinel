package com.example.novasentinel.ui.historial

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.novasentinel.Alert
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

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
                processAlertDocuments(result)
            }
            .addOnFailureListener { exception ->
                Log.e("HistorialViewModel", "Error retrieving alerts", exception)
                _alerts.value = emptyList()
            }
    }

    private fun processAlertDocuments(result: QuerySnapshot) {
        val db = FirebaseFirestore.getInstance()
        val alertList = mutableListOf<Alert>()
        val pendingTasks = mutableListOf<() -> Unit>()

        for (document in result) {
            val alert = document.toObject(Alert::class.java)
            val userId = document.getString("userID")

            if (!userId.isNullOrEmpty()) {
                pendingTasks.add {
                    db.collection("usuarios").document(userId)
                        .get()
                        .addOnSuccessListener { userDocument ->
                            if (userDocument.exists()) {
                                val userName = userDocument.getString("nombre")
                                alert.userID = userName ?: "Usuario desconocido"
                            } else {
                                alert.userID = "Usuario desconocido"
                            }
                            alertList.add(alert)
                            checkCompletion(pendingTasks.size, alertList)
                        }
                        .addOnFailureListener {
                            alert.userID = "Usuario desconocido"
                            alertList.add(alert)
                            checkCompletion(pendingTasks.size, alertList)
                        }
                }
            } else {
                alert.userID = "Usuario desconocido"
                alertList.add(alert)
            }
        }

        pendingTasks.forEach { it.invoke() }

        // Si no hay tareas pendientes, actualizar directamente
        if (pendingTasks.isEmpty()) {
            _alerts.value = alertList
        }
    }

    private fun checkCompletion(pendingSize: Int, alertList: List<Alert>) {
        if (alertList.size == pendingSize) {
            _alerts.value = alertList
        }
    }
}