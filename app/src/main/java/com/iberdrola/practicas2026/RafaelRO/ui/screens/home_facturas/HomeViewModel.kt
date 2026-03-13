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
    }
    fun onModoNubeChanged(value : Boolean) {
        viewModelScope.launch {
            settingsDataStore.setModoNube(value)
        }
    }
}