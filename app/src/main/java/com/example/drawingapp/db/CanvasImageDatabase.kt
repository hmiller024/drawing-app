package com.example.drawingapp.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CanvasImage::class], version = 2, exportSchema = false)
abstract class CanvasImageDatabase : RoomDatabase() {
    abstract fun canvasImageDao(): CanvasImageDao

    companion object {
        @Volatile
        private var INSTANCE: CanvasImageDatabase? = null

        fun getDatabase(context: android.content.Context): CanvasImageDatabase {
            return INSTANCE ?: synchronized(this) {
                //Check if already exists
                if (INSTANCE != null) {
                    return INSTANCE as CanvasImageDatabase
                }

                //Create database
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CanvasImageDatabase::class.java,
                    "canvas_image_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}