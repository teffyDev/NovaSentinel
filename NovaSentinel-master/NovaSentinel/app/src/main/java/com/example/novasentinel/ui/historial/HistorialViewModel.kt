package com.example.novasentinel.ui.historial

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.novasentinel.Alert
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

        // Obtén el ID de la entidad logueada (deberías tenerlo almacenado en alguna parte)
        val entityId = "ID_DE_LA_ENTIDAD_LOGUEADA"  // Reemplaza con la forma correcta de obtener el ID

        db.collection("alerts")
            .whereEqualTo("entityID", entityId)
            .get()
            .addOnSuccessListener { result ->
                handleAlertResults(result, db)
            }
            .addOnFailureListener { exception ->
                Log.e("HistorialViewModel", "Error retrieving alerts", exception)
                _alerts.value = emptyList()
            }
    }

    private fun handleAlertResults(result: QuerySnapshot, db: FirebaseFirestore) {
        val alertList = mutableListOf<Alert>()
        val pendingAlerts = mutableListOf<Alert>() // Lista de alertas pendientes de completar con el nombre de usuario

        for (document in result) {
            val alert = document.toObject(Alert::class.java)
            val userId = document.getString("userID")

            if (!userId.isNullOrEmpty()) {
                pendingAlerts.add(alert)
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
                        updateAlertsIfNeeded(alertList, pendingAlerts)
                    }
                    .addOnFailureListener {
                        alert.userID = "Usuario desconocido"
                        alertList.add(alert)
                        updateAlertsIfNeeded(alertList, pendingAlerts)
                    }
            } else {
                alert.userID = "Usuario desconocido"
                alertList.add(alert)
            }
        }

        if (pendingAlerts.isEmpty()) {
            _alerts.value = alertList
        }
    }

    private fun updateAlertsIfNeeded(alertList: MutableList<Alert>, pendingAlerts: MutableList<Alert>) {
        if (alertList.size == pendingAlerts.size) {
            _alerts.value = alertList
        }
    }
}
