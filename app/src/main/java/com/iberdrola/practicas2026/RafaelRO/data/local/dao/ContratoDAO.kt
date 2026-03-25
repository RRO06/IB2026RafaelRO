package com.iberdrola.practicas2026.RafaelRO.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.iberdrola.practicas2026.RafaelRO.domain.model.Contrato
import kotlinx.coroutines.flow.Flow

@Dao
interface ContratoDAO {
    @Query("SELECT * FROM Contrato")
    fun getAllContratos(): Flow<List<Contrato>>

    @Query("SELECT * FROM Contrato WHERE id = :id")
    suspend fun getContratoById(id: Int): Contrato?

    @Query("UPDATE Contrato SET email = :nuevoEmail, estado = :terminosAceptados WHERE id = :id")
    suspend fun updateContratoFields(id: Int, nuevoEmail: String, terminosAceptados: Boolean)
    @Update
    suspend fun updateContrato(contrato: Contrato)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContratos(contratos: List<Contrato>)

    @Query("SELECT COUNT(*) FROM Contrato")
    suspend fun getContratosCount(): Int
}