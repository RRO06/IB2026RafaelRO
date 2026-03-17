package com.iberdrola.practicas2026.RafaelRO.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.iberdrola.practicas2026.RafaelRO.data.local.dao.FacturaDAO
import com.iberdrola.practicas2026.RafaelRO.domain.model.Factura
import java.util.concurrent.Executors

@TypeConverters(Converters::class)
@Database(
    entities = [Factura::class], version = 2, exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun facturaDao(): FacturaDAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, AppDatabase::class.java, "app_database.db"
                )
                    // 2. CAMBIO IMPORTANTE: Permitir migración destructiva
                    // Si la versión del dispositivo es menor que la versión del código (2),
                    // y no hay una migración manual definida, Room borrará la base de datos
                    // y la creará de nuevo.
                    .fallbackToDestructiveMigration()
                    // Callback para pre-poblar la base de datos
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Se utiliza un executor para realizar la inserción en un hilo de fondo
                            //Las tareas se ejecutan de forma secuencial en un hilo/s
                            Executors.newSingleThreadExecutor().execute {
                                INSTANCE?.let { database ->
                                    prepopulateDatabase(database)
                                }
                            }
                        }
                    }).build()
                INSTANCE = instance
                instance
            }
        }

        fun prepopulateDatabase(database: AppDatabase) {
            val facturaDao = database.facturaDao()
        }
    }
}