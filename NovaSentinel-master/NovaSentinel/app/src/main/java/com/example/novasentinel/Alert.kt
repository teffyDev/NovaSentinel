package com.example.novasentinel

data class Alert(
    val titulo: String = "",
    val body: String = "",
    val entityID: String = "",
    val latitude: String? = null,
    val longitude: String? = null,
    var userID: String? = ""
) {
    // Métodos adicionales si es necesario
    fun getLatitudeAsDouble(): Double? {
        return latitude?.toDoubleOrNull()
    }

    fun getLongitudeAsDouble(): Double? {
        return longitude?.toDoubleOrNull()
    }
}
