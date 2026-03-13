package com.iberdrola.practicas2026.RafaelRO.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(name = "settings")
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
){
    companion object{
        // Es la variable que se va a estar cambiando
        private val MODO_NUBE = booleanPreferencesKey("modo_nube")
    }
    // Se usa un Flow porque va a estar en cambio constante además de que se va a ir cambiando en la aplicación
    val modoNubeFlow : Flow<Boolean> =
        context.dataStore.data.map { preferences ->
            preferences[MODO_NUBE] ?: false
        }
    // Usaremos esta función para hacer los cambios en la HomeScreen que es en donde debemos establecer como queremos obtener los datos
    suspend fun setModoNube(value : Boolean){
        context.dataStore.edit { preferences ->
            preferences[MODO_NUBE] = value
        }
    }
}