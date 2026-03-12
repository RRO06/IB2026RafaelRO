package com.iberdrola.practicas2026.RafaelRO.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

enum class Tipo{
    Luz,
    Gas
}

@Entity
data class Factura(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val fechaInicio : LocalDate,
    val fechaFinal : LocalDate,
    val tipo : Tipo,
    val estado : Boolean,
    val valor : Double
)
