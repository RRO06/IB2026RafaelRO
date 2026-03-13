package com.iberdrola.practicas2026.RafaelRO.data.repository

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.iberdrola.practicas2026.RafaelRO.data.local.dao.FacturaDAO
import com.iberdrola.practicas2026.RafaelRO.data.local.datastore.SettingsDataStore
import com.iberdrola.practicas2026.RafaelRO.data.remote.FacturasApiService
import com.iberdrola.practicas2026.RafaelRO.domain.model.Factura
import com.iberdrola.practicas2026.RafaelRO.domain.network.BaseResult
import com.iberdrola.practicas2026.RafaelRO.domain.network.InvokeException
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class FacturaRepository @Inject constructor(
    private val facturasApiService: FacturasApiService,
    private val facturaDao: FacturaDAO,
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
        } catch (_: retrofit2.HttpException) {
            BaseResult.Error(InvokeException.ServerError)
        } catch (e: Exception) {
            Log.e("REPO_DEBUG", "Error capturado: ${e.message}", e)
            BaseResult.Error(InvokeException.UnknownError(e.message))
        }
    }
    private suspend fun getFacturasLocal(): BaseResult<List<Factura>> {
        return try {
            var lista = facturaDao.getAllFacturas()

            if (lista.isEmpty()) {
                val jsonString = context.assets.open("facturas.json").bufferedReader().use { it.readText() }
                val type = object : TypeToken<List<Factura>>() {}.type
                val facturasDesdeJson = gson.fromJson<List<Factura>>(jsonString, type)
                facturaDao.insertAll(facturasDesdeJson)
                lista = facturasDesdeJson
            }
            BaseResult.Sucess(lista)
        } catch (e: Exception) {
            BaseResult.Error(InvokeException.FileError)
        }
    }
}