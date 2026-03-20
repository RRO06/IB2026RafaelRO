package com.iberdrola.practicas2026.RafaelRO.data.di

import android.content.Context
import androidx.room.Room
import com.iberdrola.practicas2026.RafaelRO.data.local.dao.ContratoDAO
import com.iberdrola.practicas2026.RafaelRO.data.local.dao.FacturaDAO
import com.iberdrola.practicas2026.RafaelRO.data.local.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "facturas_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideFacturaDao(db: AppDatabase): FacturaDAO {
        return db.facturaDao()
    }
    @Provides
    fun provideContratoDao(db: AppDatabase): ContratoDAO {
        return db.contratoDao()
    }
}