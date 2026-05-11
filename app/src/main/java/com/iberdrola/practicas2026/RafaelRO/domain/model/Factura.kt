package com.iberdrola.practicas2026.RafaelRO.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
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
    @SerializedName("id")
    val id: Int = 0,
    
    @SerializedName("fechaExpedicion")
    val fechaExpedicion: LocalDate,
    
    @SerializedName("fechaInicio")
    val fechaInicio : LocalDate,
    
    @SerializedName("fechaFinal")
    val fechaFinal : LocalDate,
    
    @SerializedName("tipo")
    val tipo : Tipo,
    
    @SerializedName("estado")
    val estado : Estado,
    
    @SerializedName("valor")
    val valor : Double
)
