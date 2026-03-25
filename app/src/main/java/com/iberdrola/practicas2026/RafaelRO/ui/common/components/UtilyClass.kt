package com.iberdrola.practicas2026.RafaelRO.ui.common.components

import android.content.Context
import android.net.Uri
import android.util.Log
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class UtilyClass {
    companion object {
        // --- Formateadores existentes ---
        fun toSpanishMediumDate(fecha: LocalDate): String {
            val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale("es", "ES"))
            return fecha.format(formatter).lowercase()
        }

        fun toLongSpanishDate(fecha: LocalDate): String {
            val formatter = DateTimeFormatter.ofPattern("d 'de' MMMM", Locale("es", "ES"))
            return fecha.format(formatter).lowercase()
        }

        fun toCurrencyFormat(cantidad: Double): String {
            return "%.2f €".format(cantidad)
        }

        // --- Validaciones Reutilizables ---

        /**
         * Valida correos electrónicos con el estándar de Android.
         */
        fun isValidEmail(email: String): Boolean {
            val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()
            return email.isNotBlank() && email.matches(emailRegex)
        }

        /**
         * Valida números de teléfono españoles:
         * 9 dígitos, empezando por 6, 7 (móviles) o 9 (fijos).
         */
        fun isValidSpanishPhone(phone: String): Boolean {
            val phoneRegex = "^[679]\\d{8}$".toRegex()
            return phone.matches(phoneRegex)
        }

        /**
         * Valida que el nombre no contenga números o símbolos raros (opcional).
         */
        fun isValidName(name: String): Boolean {
            return name.isNotBlank() && name.length >= 3
        }
        fun saveImageToCache(context: Context, uri: Uri): String? {
            return try {
                val inputStream = context.contentResolver.openInputStream(uri)
                // Usamos un nombre fijo para que la nueva foto siempre sobreescriba a la anterior
                val file = File(context.cacheDir, "user_profile_cache.jpg")

                inputStream?.use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                file.absolutePath
            } catch (e: Exception) {
                Log.e("UtilyClass", "Error en caché: ${e.message}")
                null
            }
        }
    }

}