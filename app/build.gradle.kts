plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
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

    implementation("androidx.compose.material:material-icons-extended:1.7.0")
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

// Tarea para automatizar adb reverse tcp:3000 tcp:3000
// Se ejecuta en cada compilación para asegurar que Mockoon sea accesible desde el dispositivo
tasks.register("adbReverse") {
    group = "custom"
    description = "Configura adb reverse para Mockoon en todos los dispositivos conectados"
    doLast {
        val android = project.extensions.getByName("android") as com.android.build.gradle.BaseExtension
        val adb = android.adbExecutable.absolutePath
        
        try {
            // Obtenemos todos los dispositivos conectados que están listos
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

// Hook global para que se ejecute siempre antes de cualquier tarea de preBuild o instalación
tasks.configureEach {
    if (this.name == "preBuild" || this.name.startsWith("install")) {
        dependsOn("adbReverse")
    }
}
