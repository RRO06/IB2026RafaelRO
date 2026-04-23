package com.iberdrola.practicas2026.RafaelRO.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

enum class Tipo{
    Luz,
    Gas
}
enum class Estado{
    Pagado,
    PendientePago,
    Tramite,
    Anulado,
    CuotaFija
}
@Entity
data class Factura(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val fechaExpedicion: LocalDate,
    val fechaInicio : LocalDate,
    val fechaFinal : LocalDate,
    val tipo : Tipo,
    val estado : Estado,
    val valor : Double
)
