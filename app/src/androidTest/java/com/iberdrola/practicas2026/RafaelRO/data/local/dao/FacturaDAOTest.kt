package com.iberdrola.practicas2026.RafaelRO.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.iberdrola.practicas2026.RafaelRO.data.local.database.AppDatabase
import com.iberdrola.practicas2026.RafaelRO.domain.model.Estado
import com.iberdrola.practicas2026.RafaelRO.domain.model.Factura
import com.iberdrola.practicas2026.RafaelRO.domain.model.Tipo
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class FacturaDAOTest {

    private lateinit var database: AppDatabase
    private lateinit var facturaDao: FacturaDAO

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // Base de datos volátil en RAM
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        facturaDao = database.facturaDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAll_y_getAllFacturas_retorna_datos_correctos() = runBlocking {
        // GIVEN: Una lista de facturas con diferentes fechas y tipos
        val facturas = listOf(
            Factura(id = 1, fechaInicio = LocalDate.of(2024,1,1), fechaFinal = LocalDate.of(2024,1,31), tipo = Tipo.Luz, estado = Estado.Pagado, valor = 45.0),
            Factura(id = 2, fechaInicio = LocalDate.of(2024,2,1), fechaFinal = LocalDate.of(2024,2,28), tipo = Tipo.Gas, estado = Estado.PendientePago, valor = 80.0)
        )

        // WHEN: Insertamos todas
        facturaDao.insertAll(facturas)
        val result = facturaDao.getAllFacturas()

        // THEN: Se han guardado 2 y los tipos (Enums) se han mapeado bien
        assertEquals(2, result.size)
        assertEquals(Tipo.Luz, result.find { it.id == 1 }?.tipo)
        assertEquals(Estado.PendientePago, result.find { it.id == 2 }?.estado)
    }

    @Test
    fun deleteAll_vacia_la_tabla_por_completo() = runBlocking {
        // GIVEN: Tabla con datos
        val factura = Factura(id = 5, fechaInicio = LocalDate.now(), fechaFinal = LocalDate.now(), tipo = Tipo.Luz, estado = Estado.Pagado, valor = 10.0)
        facturaDao.insertFactura(factura)

        // Verificamos que se insertó
        assertEquals(1, facturaDao.getAllFacturas().size)

        // WHEN: Borramos todo
        facturaDao.deleteAll()

        // THEN: La lista debe estar vacía
        val result = facturaDao.getAllFacturas()
        assertTrue("La base de datos debería estar vacía tras deleteAll", result.isEmpty())
    }

    @Test
    fun onConflict_REPLACE_actualiza_factura_existente() = runBlocking {
        // GIVEN: Una factura ya guardada
        val facturaOriginal = Factura(id = 10, fechaInicio = LocalDate.now(), fechaFinal = LocalDate.now(), tipo = Tipo.Luz, estado = Estado.PendientePago, valor = 50.0)
        facturaDao.insertFactura(facturaOriginal)

        // WHEN: Insertamos otra factura con el MISMO ID pero distinto valor
        val facturaNueva = facturaOriginal.copy(valor = 100.0, estado = Estado.Pagado)
        facturaDao.insertFactura(facturaNueva)

        // THEN: No se crea una nueva, se sobreescribe la existente
        val result = facturaDao.getAllFacturas()
        assertEquals(1, result.size)
        assertEquals(100.0, result[0].valor, 0.1)
        assertEquals(Estado.Pagado, result[0].estado)
    }
}