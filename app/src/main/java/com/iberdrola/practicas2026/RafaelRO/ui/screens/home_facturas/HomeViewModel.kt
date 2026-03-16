package com.iberdrola.practicas2026.RafaelRO.ui.screens.home_facturas

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iberdrola.practicas2026.RafaelRO.data.local.datastore.SettingsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore
): ViewModel() {
    var stateUI by mutableStateOf(HomeUiState())
        private set
    init {
        viewModelScope.launch {
            settingsDataStore.modoNubeFlow.collect { modoGuardado ->
                stateUI = stateUI.copy(esModoNube = modoGuardado)
            }
        }
        viewModelScope.launch {
            settingsDataStore.opinionStateFlow.collect { prefs ->
                if (prefs.contador >= prefs.objetivo) {
                    stateUI = stateUI.copy(showBottomSheet = true)
                }
            }
        }
    }
    fun onBackFromFacturas() {
        viewModelScope.launch {
            settingsDataStore.incrementarContador()
        }
    }
    fun onOpinionDada() {
        stateUI = stateUI.copy(showBottomSheet = false)

        viewModelScope.launch {
            // No molestar hasta dentro de 10 veces
            settingsDataStore.resetEstadoOpinion(nuevoObjetivo = 10, yaOpino = true)
        }
    }
    fun onRecordarMasTarde() {
        stateUI = stateUI.copy(showBottomSheet = false)

        viewModelScope.launch {
            // Volver a mostrar en 3 veces
            settingsDataStore.resetEstadoOpinion(nuevoObjetivo = 3)
        }
    }
    fun onDismissSheet() {
        stateUI = stateUI.copy(showBottomSheet = false)

        viewModelScope.launch {
            // Se cerró sin elegir: volver a mostrar a la próxima (objetivo 1)
            settingsDataStore.resetEstadoOpinion(nuevoObjetivo = 1)
        }
    }
    fun onModoNubeChanged(value : Boolean) {
        viewModelScope.launch {
            settingsDataStore.setModoNube(value)
        }
    }
}