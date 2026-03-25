package com.iberdrola.practicas2026.RafaelRO.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.iberdrola.practicas2026.RafaelRO.data.local.database.AppDatabase
import com.iberdrola.practicas2026.RafaelRO.domain.model.Contrato
import com.iberdrola.practicas2026.RafaelRO.domain.model.Tipo
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class) // Indica que es un test de Android
class ContratoDAOTest {

    private lateinit var database: AppDatabase
    private lateinit var contratoDao: ContratoDAO

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // Creamos la base de datos en memoria (volátil)
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries() // Solo para tests
            .build()
        contratoDao = database.contratoDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndGetAllContratos() = runBlocking {
        // GIVEN: Una lista de contratos
        val contratos = listOf(
            Contrato(id = 1, tipo = Tipo.Luz, telefono = "600", direccion = "Dir 1", estado = true, email = "a@a.com"),
            Contrato(id = 2, tipo = Tipo.Gas, telefono = "700", direccion = "Dir 2", estado = false, email = "b@b.com")
        )

        // WHEN: Insertamos y recuperamos
        contratoDao.insertContratos(contratos)
        val result = contratoDao.getAllContratos().first() // .first() para obtener la primera emisión del Flow

        // THEN: Los datos coinciden
        assertEquals(2, result.size)
        assertEquals(Tipo.Luz, result[0].tipo)
    }

    @Test
    fun updateContratoFieldsCorrectamente() = runBlocking {
        // GIVEN: Un contrato insertado
        val contratoInicial = Contrato(id = 10, tipo = Tipo.Luz, telefono = "123", direccion = "Calle A", estado = false, email = "viejo@test.com")
        contratoDao.insertContratos(listOf(contratoInicial))

        // WHEN: Actualizamos campos específicos con tu @Query personalizada
        val nuevoEmail = "nuevo@test.com"
        val nuevoEstado = true
        contratoDao.updateContratoFields(10, nuevoEmail, nuevoEstado)

        // THEN: Verificamos que los cambios se aplicaron
        val contratoActualizado = contratoDao.getContratoById(10)
        assertNotNull(contratoActualizado)
        assertEquals(nuevoEmail, contratoActualizado?.email)
        assertEquals(nuevoEstado, contratoActualizado?.estado)
    }

    @Test
    fun getContratosCountRetornaValorCorrecto() = runBlocking {
        // GIVEN
        val contratos = listOf(
            Contrato(id = 1, tipo = Tipo.Luz, telefono = "1", direccion = "D1", estado = true, email = "e1"),
            Contrato(id = 2, tipo = Tipo.Luz, telefono = "2", direccion = "D2", estado = true, email = "e2")
        )
        contratoDao.insertContratos(contratos)

        // WHEN
        val count = contratoDao.getContratosCount()

        // THEN
        assertEquals(2, count)
    }
}