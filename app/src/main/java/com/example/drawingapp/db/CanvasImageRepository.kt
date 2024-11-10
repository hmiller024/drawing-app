package com.example.drawingapp.db

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import com.example.drawingapp.DrawingApplication
import com.example.drawingapp.FirebaseManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

@Dao
class CanvasImageRepository(
    private val scope: CoroutineScope,
    private val canvasImageDao: CanvasImageDao,
    private val applicationContext: Context
) {
    //Create list of canvas images
    val canvasImages = canvasImageDao.getAllCanvasImages()
   // val canvasCount = canvasImageDao.getCount()
    val cloudManager = FirebaseManager()


    var bitmap: MutableLiveData<Bitmap> = MutableLiveData(Bitmap.createBitmap(1024, 1024, Bitmap.Config.ARGB_8888))
    var fileName: MutableLiveData<String> = MutableLiveData("fileName")

    private val filesDir = "app/src/main/res/raw"


    //Add data to database
    fun addCanvasImage(image: MutableLiveData<Bitmap>, name: String) {
        scope.launch {
            val image = image.value ?: return@launch // Return if the value is null

            //Store image
            val file = storeImage(image, name)
            //Create canvas image and insert into database
            val canvasImage = CanvasImage(file.absolutePath)
            canvasImageDao.insertCanvasImage(canvasImage)
        }
    }



    //Method to store an image
    private fun storeImage(image: Bitmap, name: String): File {
        val directory = File(applicationContext.filesDir, "DrawingApp")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val file = File(directory, "$name.png")
        try {
            FileOutputStream(file).use { fileOutputStream ->
                image.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return file
    }

    fun shareCanvasImage(image: MutableLiveData<Bitmap>, name: String) {
        scope.launch {
            val image = image.value ?: return@launch // Return if the value is null

            //Store image
            val file = storeImage(image, name)
            //Create canvas image and insert into database
            val canvasImage = CanvasImage(file.absolutePath)


            cloudManager.saveDrawing(canvasImage, true)
        }
    }
}