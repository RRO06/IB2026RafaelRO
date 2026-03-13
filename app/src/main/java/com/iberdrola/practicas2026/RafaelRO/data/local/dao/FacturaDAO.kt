package com.iberdrola.practicas2026.RafaelRO.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.iberdrola.practicas2026.RafaelRO.domain.model.Factura

@Dao
interface FacturaDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFactura(factura: Factura)

    // Inserta una lista completa de facturas
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(facturas: List<Factura>)

    @Query("SELECT * FROM Factura")
    suspend fun getAllFacturas(): List<Factura>

    // Elimina una factura específica
    @Delete
    suspend fun deleteFactura(factura: Factura)

    // Elimina todo el contenido de la tabla Factura
    @Query("DELETE FROM Factura")
    suspend fun deleteAll()
}