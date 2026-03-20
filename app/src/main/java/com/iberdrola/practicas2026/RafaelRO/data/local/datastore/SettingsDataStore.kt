package com.iberdrola.practicas2026.RafaelRO.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.iberdrola.practicas2026.RafaelRO.domain.model.UserProfile
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
        private object PreferencesKeys {
            val FOTO_PERFIL = stringPreferencesKey("foto_perfil")
            val NOMBRE_USUARIO = stringPreferencesKey("nombre_usuario")
            val EMAIL_USUARIO = stringPreferencesKey("email_usuario")
            val TELEFONO_USUARIO = stringPreferencesKey("telefono_usuario")
            val ID_USUARIO = stringPreferencesKey("id_usuario")
            val ULTIMA_CONEXION = longPreferencesKey("ultima_conexion")
        }
        private val MODO_NUBE = booleanPreferencesKey("modo_nube")
        private val CONTADOR_BACK = intPreferencesKey("contador_back")
        private val OBJETIVO_MOSTRAR = intPreferencesKey("objetivo_mostrar")
        private val YA_OPINO = booleanPreferencesKey("ya_opino")
    }
    val userPerfilFlow: Flow<UserProfile> = context.dataStore.data.map { prefs ->
        UserProfile(
            foto = prefs[PreferencesKeys.FOTO_PERFIL] ?: "",
            nombre = prefs[PreferencesKeys.NOMBRE_USUARIO] ?: "Usuario",
            id = prefs[PreferencesKeys.ID_USUARIO] ?: "ID-000",
            email = prefs[PreferencesKeys.EMAIL_USUARIO] ?: "", // Default vacío
            telefono = prefs[PreferencesKeys.TELEFONO_USUARIO] ?: "", // Default vacío
            ultimaConexion = prefs[PreferencesKeys.ULTIMA_CONEXION] ?: System.currentTimeMillis()
        )
    }
    suspend fun guardarDatosPerfil(nombre: String, id: String, email: String, telefono: String, foto : String) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.NOMBRE_USUARIO] = nombre
            prefs[PreferencesKeys.ID_USUARIO] = id
            prefs[PreferencesKeys.EMAIL_USUARIO] = email
            prefs[PreferencesKeys.TELEFONO_USUARIO] = telefono
            prefs[PreferencesKeys.FOTO_PERFIL] = foto
        }
    }
    // Flow para observar todos los datos de opinión
    val opinionStateFlow: Flow<OpinionPreferences> = context.dataStore.data.map { pref ->
        OpinionPreferences(
            contador = pref[CONTADOR_BACK] ?: 0,
            objetivo = pref[OBJETIVO_MOSTRAR] ?: 1, // Empezamos en 1 para que salga la primera vez
            yaOpino = pref[YA_OPINO] ?: false
        )
    }
    suspend fun incrementarContador() {
        context.dataStore.edit { pref ->
            val actual = pref[CONTADOR_BACK] ?: 0
            pref[CONTADOR_BACK] = actual + 1
        }
    }
    suspend fun resetEstadoOpinion(nuevoObjetivo: Int, yaOpino: Boolean = false) {
        context.dataStore.edit { pref ->
            pref[CONTADOR_BACK] = 0
            pref[OBJETIVO_MOSTRAR] = nuevoObjetivo
            pref[YA_OPINO] = yaOpino
        }
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
data class OpinionPreferences(val contador: Int, val objetivo: Int, val yaOpino: Boolean)