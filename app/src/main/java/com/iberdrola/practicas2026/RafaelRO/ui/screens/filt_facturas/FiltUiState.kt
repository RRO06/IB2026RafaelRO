package com.iberdrola.practicas2026.RafaelRO.ui.screens.filt_facturas

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.TypeParceler
import java.time.LocalDate

object LocalDateParceler : Parceler<LocalDate?> {
    override fun create(parcel: Parcel): LocalDate? {
        val s = parcel.readString()
        return if (s != null) LocalDate.parse(s) else null
    }

    override fun LocalDate?.write(parcel: Parcel, flags: Int) {
        parcel.writeString(this?.toString())
    }
}

@Parcelize
data class FiltUiState(
    @TypeParceler<LocalDate?, LocalDateParceler>
    val dateFrom: LocalDate? = null,
    @TypeParceler<LocalDate?, LocalDateParceler>
    val dateTo: LocalDate? = null,
    val showDatePickerFrom: Boolean = false,
    val showDatePickerTo: Boolean = false,
    val dateError: String? = null,
    val priceRangeStart: Float = 0f,
    val priceRangeEnd: Float = 500f,
    val selectedStates: Set<String> = setOf()
) : Parcelable
