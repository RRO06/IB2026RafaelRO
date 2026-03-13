package com.iberdrola.practicas2026.RafaelRO.data.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializer
import com.iberdrola.practicas2026.RafaelRO.data.remote.FacturasApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.time.LocalDate.parse
import javax.inject.Singleton
import kotlin.jvm.java

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(LocalDate::class.java, JsonDeserializer { json, _, _ ->
                LocalDate.parse(json.asJsonPrimitive.asString)
            })
            .registerTypeAdapter(LocalDate::class.java, JsonSerializer<LocalDate> { src, _, _ ->
                JsonPrimitive(src.toString())
            })
            .create()
    }

    @Provides
    @Singleton
    // 1. Recibe el gson que creaste arriba como parámetro
    fun provideRetrofit(gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://localhost:3000/")
            // 2. Pásalo a la factoría de conversión
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideFacturasApiService(retrofit: Retrofit): FacturasApiService {
        return retrofit.create(FacturasApiService::class.java)
    }
}