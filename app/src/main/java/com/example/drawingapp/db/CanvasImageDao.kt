package com.example.drawingapp.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CanvasImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCanvasImage(canvasImage: CanvasImage)

    @Query ("SELECT * FROM canvas_image ORDER BY timestamp DESC")
    fun getAllCanvasImages(): Flow<List<CanvasImage>>
    
    @Query("SELECT count(*) FROM canvas_image")
    fun getCount(): Int

    @Query("DELETE FROM canvas_image")
    fun deleteAllCanvasImages()
}