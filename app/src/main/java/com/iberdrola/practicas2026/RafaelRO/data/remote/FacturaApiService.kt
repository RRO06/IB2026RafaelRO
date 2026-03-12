package com.iberdrola.practicas2026.RafaelRO.data.remote

import com.iberdrola.practicas2026.RafaelRO.domain.model.Factura
import retrofit2.http.GET

interface FacturasApiService {
    @GET("facturas")
    suspend fun getFacturas(): List<Factura>
}