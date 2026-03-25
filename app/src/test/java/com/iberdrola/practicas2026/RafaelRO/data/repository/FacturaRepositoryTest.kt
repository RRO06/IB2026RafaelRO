package com.iberdrola.practicas2026.RafaelRO.data.repository

import android.content.Context
import android.content.res.AssetManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.iberdrola.practicas2026.RafaelRO.data.local.dao.ContratoDAO
import com.iberdrola.practicas2026.RafaelRO.data.local.dao.FacturaDAO
import com.iberdrola.practicas2026.RafaelRO.data.local.datastore.SettingsDataStore
import com.iberdrola.practicas2026.RafaelRO.data.remote.FacturasApiService
import com.iberdrola.practicas2026.RafaelRO.domain.model.Estado
import com.iberdrola.practicas2026.RafaelRO.domain.model.Factura
import com.iberdrola.practicas2026.RafaelRO.domain.model.Tipo
import com.iberdrola.practicas2026.RafaelRO.domain.network.BaseResult
import com.iberdrola.practicas2026.RafaelRO.domain.network.InvokeException
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayInputStream
import java.time.LocalDate

class FacturaRepositoryTest {

    // --- MOCKS ---
    private val apiService: FacturasApiService = mockk()
    private val facturaDao: FacturaDAO = mockk(relaxed = true)
    private val contratoDao: ContratoDAO = mockk(relaxed = true)
    private val settingsDataStore: SettingsDataStore = mockk()
    private val context: Context = mockk()
    private val assetManager: AssetManager = mockk()

    // --- GSON CONFIGURADO (Igual que en tu NetworkModule) ---
    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(LocalDate::class.java, JsonDeserializer { json, _, _ ->
            LocalDate.parse(json.asJsonPrimitive.asString)
        })
        .create()

    private lateinit var repository: FacturaRepository

    @Before
    fun setUp() {
        // Vinculamos el assetManager al context mockeado
        every { context.assets } returns assetManager

        repository = FacturaRepository(
            apiService, facturaDao, contratoDao, gson, settingsDataStore, context
        )
    }

    @Test
    fun `cuando modoNube es true debe llamar a la API y actualizar la base de datos`() = runTest {
        // GIVEN: Modo nube activado y respuesta de API
        coEvery { settingsDataStore.modoNubeFlow } returns flowOf(true)
        val facturaFake = Factura(
            id = 1,
            fechaInicio = LocalDate.of(2024, 1, 1),
            fechaFinal = LocalDate.of(2024, 1, 31),
            tipo = Tipo.Luz,
            estado = Estado.Pagado,
            valor = 50.0
        )
        val responseFake = listOf(facturaFake)
        coEvery { apiService.getFacturas() } returns responseFake

        // WHEN
        val result = repository.getFacturas()

        // THEN
        assertTrue("El resultado debería ser Success", result is BaseResult.Sucess)
        assertEquals(responseFake, (result as BaseResult.Sucess).data)

        // Verificamos que se limpie y se inserte en Room
        coVerify(exactly = 1) { facturaDao.deleteAll() }
        coVerify(exactly = 1) { facturaDao.insertAll(responseFake) }
    }

    @Test
    fun `cuando modoNube es false debe leer y parsear correctamente el JSON de assets`() = runTest {
        // GIVEN: Modo nube desactivado
        coEvery { settingsDataStore.modoNubeFlow } returns flowOf(false)

        // JSON que simula el archivo "facturas.json" con formato ISO para las fechas
        val jsonFake = """
            {
              "facturas": [
                {
                  "id": 99,
                  "fechaInicio": "2024-01-01",
                  "fechaFinal": "2024-01-15",
                  "tipo": "Luz",
                  "estado": "Pagado",
                  "valor": 120.5
                }
              ]
            }
        """.trimIndent()

        val inputStream = ByteArrayInputStream(jsonFake.toByteArray())
        every { assetManager.open("facturas.json") } returns inputStream

        // WHEN
        val result = repository.getFacturas()

        // THEN
        assertTrue("Debería ser Success. Si falla aquí, revisa el log de excepciones de Gson", result is BaseResult.Sucess)
        val facturas = (result as BaseResult.Sucess).data
        assertEquals(1, facturas.size)
        assertEquals(99, facturas[0].id)
        assertEquals(Tipo.Luz, facturas[0].tipo)
        assertEquals(LocalDate.of(2024, 1, 1), facturas[0].fechaInicio)

        // Verificamos que NO se llamó a la API
        coVerify(exactly = 0) { apiService.getFacturas() }
    }

    @Test
    fun `cuando la API falla y hay internet debe devolver error de servidor`() = runTest {
        // GIVEN: Modo nube activo pero la API da error 500
        coEvery { settingsDataStore.modoNubeFlow } returns flowOf(true)
        coEvery { apiService.getFacturas() } throws retrofit2.HttpException(
            mockk(relaxed = true) {
                every { code() } returns 500
            }
        )

        // WHEN
        val result = repository.getFacturas()

        // THEN
        assertTrue(result is BaseResult.Error)
        assertEquals(InvokeException.ServerError, (result as BaseResult.Error).exception)
    }
}