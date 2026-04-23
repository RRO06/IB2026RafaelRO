package com.iberdrola.practicas2026.RafaelRO.data.repository

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.iberdrola.practicas2026.RafaelRO.data.local.dao.ContratoDAO
import com.iberdrola.practicas2026.RafaelRO.data.local.dao.FacturaDAO
import com.iberdrola.practicas2026.RafaelRO.data.local.datastore.SettingsDataStore
import com.iberdrola.practicas2026.RafaelRO.data.remote.FacturasApiService
import com.iberdrola.practicas2026.RafaelRO.domain.model.Contrato
import com.iberdrola.practicas2026.RafaelRO.domain.model.Factura
import com.iberdrola.practicas2026.RafaelRO.domain.network.BaseResult
import com.iberdrola.practicas2026.RafaelRO.domain.network.InvokeException
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

data class FacturasJson(val facturas: List<Factura>)

class FacturaRepository @Inject constructor(
    private val facturasApiService: FacturasApiService,
    private val facturaDao: FacturaDAO,
    private val contratoDao: ContratoDAO,
    private val gson: Gson,
    private val settingsDataStore: SettingsDataStore,
    @ApplicationContext private val context: Context,
) {

    suspend fun getFacturas() : BaseResult<List<Factura>>{
        val modoNube = settingsDataStore.modoNubeFlow.firstOrNull() ?: false
        return if (modoNube){
            getFacturasAPI()
        }else{
            getFacturasLocal()
        }
    }

    fun getContratosFlow(): Flow<BaseResult<List<Contrato>>> = flow {
        contratoDao.getAllContratos().collect { contratos ->
            if (contratos.isEmpty()) {
                try {
                    cargarContratosDesdeJson()
                } catch (_: Exception) {
                    emit(BaseResult.Error(InvokeException.FileError))
                }
            } else {
                emit(BaseResult.Sucess(contratos))
            }
        }
    }.catch {
        emit(BaseResult.Error(InvokeException.FileError))
    }

    private suspend fun cargarContratosDesdeJson() {
        val jsonString = context.assets.open("contratos.json").bufferedReader().use { it.readText() }
        val type = object : TypeToken<List<Contrato>>() {}.type
        val contratosDesdeJson: List<Contrato> = gson.fromJson(jsonString, type)
        contratoDao.insertContratos(contratosDesdeJson)
    }

    suspend fun updateContratoFields(id: Int, nuevoEmail: String, terminosAceptados: Boolean): Boolean {
        return try {
            contratoDao.updateContratoFields(id, nuevoEmail, terminosAceptados)
            true
        } catch (_: Exception) {
            false
        }
    }

    private suspend fun getFacturasAPI(): BaseResult<List<Factura>> {
        return try {
            val response = facturasApiService.getFacturas()
            if (response.isNotEmpty()) {
                facturaDao.deleteAll()
                facturaDao.insertAll(response)
            }
            BaseResult.Sucess(response)
        } catch (_: java.net.UnknownHostException) {
            val local = facturaDao.getAllFacturas()
            if (local.isNotEmpty()) BaseResult.Sucess(local)
            else BaseResult.Error(InvokeException.NetworkError)
        } catch (_: java.net.SocketTimeoutException) {
            // Manejamos específicamente el error de tiempo de espera (timeout)
            val local = facturaDao.getAllFacturas()
            if (local.isNotEmpty()) BaseResult.Sucess(local)
            else BaseResult.Error(InvokeException.NetworkError)
        } catch (_: retrofit2.HttpException) {
            BaseResult.Error(InvokeException.ServerError)
        } catch (e: Exception) {
            Log.e("REPO_DEBUG", "Error capturado: ${e.message}", e)
            BaseResult.Error(InvokeException.UnknownError(e.message))
        }
    }

    private fun getFacturasLocal(): BaseResult<List<Factura>> {
        return try {
            val jsonString = context.assets.open("facturas.json").bufferedReader().use { it.readText() }
            val type = object : TypeToken<FacturasJson>() {}.type
            val facturasJson = gson.fromJson<FacturasJson>(jsonString, type)
            val facturas = facturasJson.facturas
            BaseResult.Sucess(facturas)
        } catch (_: Exception) {
            BaseResult.Error(InvokeException.FileError)
        }
    }
}