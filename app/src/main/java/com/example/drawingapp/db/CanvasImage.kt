package com.example.drawingapp.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Transient
import android.graphics.Bitmap
import android.graphics.BitmapFactory

@Entity(tableName = "canvas_image")
data class CanvasImage (
    @PrimaryKey
    var url: String,

    @Transient
    var timestamp: Long = System.currentTimeMillis()
) {
    //Get the bitmap from the url
    fun getBitmap(): Bitmap {
        var bitmap = BitmapFactory.decodeFile(url)
        var mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        return mutableBitmap
    }

    fun getFileName(): String {
        return url.substringAfterLast("/")
    }
}