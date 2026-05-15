plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.google.services)
    alias(libs.plugins.crashlytics)
}

android {
    namespace = "com.iberdrola.practicas2026.RafaelRO"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.iberdrola.practicas2026.RafaelRO"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.androidx.junit.ktx)
    ksp(libs.room.compiler)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // Navigation
    implementation(libs.navigation.compose)

    // Lottie
    implementation(libs.lottie.compose)

    // Retrofit
    implementation(libs.retrofit.main)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp.logging)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.config)

    // UI & Storage
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.coil.compose)

    // AndroidX & Compose Base
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // Unit Testing (test)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.androidx.arch.core.testing)
    testImplementation(libs.mockk)

    // Instrumented Testing (androidTest)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    // Debug
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

// Tarea para automatizar adb reverse tcp:3000 tcp:3000 para Mockoon
tasks.register("adbReverse") {
    group = "custom"
    description = "Configura adb reverse para Mockoon en todos los dispositivos conectados"
    doLast {
        val android = project.extensions.getByName("android") as com.android.build.gradle.BaseExtension
        val adb = android.adbExecutable.absolutePath

        try {
            val process = ProcessBuilder(adb, "devices").start()
            val devices = process.inputStream.bufferedReader().readText()
                .lines()
                .filter { it.endsWith("\tdevice") }
                .map { it.split("\t")[0] }

            if (devices.isEmpty()) {
                println("--- [Mockoon] No se detectaron dispositivos conectados. ---")
            } else {
                devices.forEach { device ->
                    println("--- [Mockoon] Configurando adb reverse en dispositivo: $device ---")
                    ProcessBuilder(adb, "-s", device, "reverse", "tcp:3000", "tcp:3000").start().waitFor()
                }
                println("--- [Mockoon] ADB Reverse completado con éxito. ---")
            }
        } catch (e: Exception) {
            println("--- [Mockoon] Error ejecutando adb reverse: ${e.message} ---")
        }
    }
}

// Tarea para habilitar el modo DebugView de Firebase Analytics automáticamente
tasks.register("enableFirebaseDebug") {
    group = "custom"
    description = "Habilita el modo DebugView de Firebase Analytics en todos los dispositivos conectados"
    doLast {
        val android = project.extensions.getByName("android") as com.android.build.gradle.BaseExtension
        val adb = android.adbExecutable.absolutePath
        val appId = "com.iberdrola.practicas2026.RafaelRO"

        try {
            val process = ProcessBuilder(adb, "devices").start()
            val devices = process.inputStream.bufferedReader().readText()
                .lines()
                .filter { it.endsWith("\tdevice") }
                .map { it.split("\t")[0] }

            if (devices.isEmpty()) {
                println("--- [Firebase] No se detectaron dispositivos para habilitar DebugView. ---")
            } else {
                devices.forEach { device ->
                    println("--- [Firebase] Habilitando DebugView en: $device para $appId ---")
                    ProcessBuilder(adb, "-s", device, "shell", "setprop", "debug.firebase.analytics.app", appId).start().waitFor()
                }
                println("--- [Firebase] DebugView habilitado con éxito. ---")
            }
        } catch (e: Exception) {
            println("--- [Firebase] Error al habilitar DebugView: ${e.message} ---")
        }
    }
}

// Hook global para que se ejecuten las tareas antes de construir o instalar
tasks.configureEach {
    if (this.name == "preBuild" || this.name.startsWith("install")) {
        dependsOn("adbReverse")
        dependsOn("enableFirebaseDebug")
    }
}
