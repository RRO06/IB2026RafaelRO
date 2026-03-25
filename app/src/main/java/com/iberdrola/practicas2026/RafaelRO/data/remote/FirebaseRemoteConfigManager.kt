package com.iberdrola.practicas2026.RafaelRO.data.remote

import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.firebase.remoteconfig.remoteConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteConfigManager @Inject constructor() {
    private val remoteConfig = Firebase.remoteConfig

    init {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 0
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(mapOf("feature_contratos_enabled" to true))
    }

    fun isContratosGasEnabled(): Boolean {
        return remoteConfig.getBoolean("gas_contratos_enabled")
    }
    fun isContratosLuzEnabled(): Boolean {
        return remoteConfig.getBoolean("luz_contratos_enabled")
    }

    fun fetchConfig(onComplete: () -> Unit) {
        remoteConfig.fetchAndActivate().addOnCompleteListener { onComplete() }
    }
}