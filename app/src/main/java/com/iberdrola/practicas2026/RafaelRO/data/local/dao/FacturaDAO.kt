package com.iberdrola.practicas2026.RafaelRO.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.iberdrola.practicas2026.RafaelRO.domain.model.Factura

@Dao
interface FacturaDAO{
    @Insert
    fun insertFactura(factura: Factura)

    @Query("SELECT * FROM Factura")
    fun getAllFacturas() : List<Factura>

}