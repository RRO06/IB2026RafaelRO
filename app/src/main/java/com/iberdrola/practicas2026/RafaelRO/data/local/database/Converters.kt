package com.iberdrola.practicas2026.RafaelRO.data.local.database

import androidx.room.TypeConverter
import com.iberdrola.practicas2026.RafaelRO.domain.model.Estado
import com.iberdrola.practicas2026.RafaelRO.domain.model.Tipo
import java.time.LocalDate

class Converters {
    // Para las fechas (LocalDate <-> String)
    @TypeConverter
    fun fromTimestamp(value: String?): LocalDate? = value?.let { LocalDate.parse(it) }

    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): String? = date?.toString()

    // Para el Enum (Tipo <-> String)
    @TypeConverter
    fun fromTipo(value: String): Tipo = Tipo.valueOf(value)

    @TypeConverter
    fun tipoToString(tipo: Tipo): String = tipo.name

    // Para el Enum (Estado <-> String)
    @TypeConverter
    fun fromEstado(value: String): Estado = Estado.valueOf(value)

    @TypeConverter
    fun estadoToString(estado: Estado): String = estado.name
}