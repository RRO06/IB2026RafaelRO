package com.iberdrola.practicas2026.RafaelRO.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Contrato(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val tipo: Tipo,
    val telefono : String,
    val direccion: String,
    val estado: Boolean,
    val email : String
)

