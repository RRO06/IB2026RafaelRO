package com.iberdrola.practicas2026.RafaelRO.data.local.dao.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.iberdrola.practicas2026.RafaelRO.data.local.dao.FacturaDAO
import com.iberdrola.practicas2026.RafaelRO.data.local.database.AppDatabase
import com.iberdrola.practicas2026.RafaelRO.domain.model.Estado
import com.iberdrola.practicas2026.RafaelRO.domain.model.Factura
import com.iberdrola.practicas2026.RafaelRO.domain.model.Tipo
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class AppDatabaseTests {

    private lateinit var db: AppDatabase
    private lateinit var facturaDao: FacturaDAO

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // Importante: Usamos la clase real AppDatabase que ya tiene la anotación @TypeConverters
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .build()
        facturaDao = db.facturaDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun verificarQueLosConvertersFuncionanCorrectamente() = runBlocking {
        // GIVEN: Una fecha y enums específicos
        val fechaEsperada = LocalDate.of(2026, 3, 25)
        val factura = Factura(
            id = 1,
            fechaInicio = fechaEsperada,
            fechaFinal = fechaEsperada.plusDays(30),
            tipo = Tipo.Gas,
            estado = Estado.CuotaFija,
            valor = 100.0
        )

        // WHEN: Insertamos en la DB (Room usará Converters.dateToTimestamp y estadoToString)
        facturaDao.insertFactura(factura)

        // THEN: Recuperamos de la DB (Room usará Converters.fromTimestamp y fromEstado)
        val facturaRecuperada = facturaDao.getAllFacturas().first()

        assertEquals("La fecha debe ser la misma tras la conversión", fechaEsperada, facturaRecuperada.fechaInicio)
        assertEquals("El Enum Tipo debe persistirse correctamente", Tipo.Gas, facturaRecuperada.tipo)
        assertEquals("El Enum Estado debe persistirse correctamente", Estado.CuotaFija, facturaRecuperada.estado)
    }
}