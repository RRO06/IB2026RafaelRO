package com.iberdrola.practicas2026.RafaelRO.data.remote

import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsManager @Inject constructor() {

    private val analytics = Firebase.analytics

    fun registrarClic(
        nombreBoton: String,
        nombrePantalla: String,
        parametrosExtra: Map<String, String> = emptyMap()
    ) {
        analytics.logEvent("clic_boton") {
            param("nombre_boton", nombreBoton)
            param("nombre_pantalla", nombrePantalla)
            parametrosExtra.forEach { (llave, valor) -> param(llave, valor) }
        }
    }
}