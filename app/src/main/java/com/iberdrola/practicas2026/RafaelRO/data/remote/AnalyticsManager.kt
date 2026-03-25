package com.iberdrola.practicas2026.RafaelRO.data.remote

import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsManager @Inject constructor() {

    private val analytics = Firebase.analytics

    fun logClick(
        buttonName: String,
        screenName: String,
        extraParams: Map<String, String> = emptyMap()
    ) {
        analytics.logEvent("button_click") {
            param("button_name", buttonName)
            param("screen_name", screenName)
            extraParams.forEach { (key, value) -> param(key, value) }
        }
    }
}