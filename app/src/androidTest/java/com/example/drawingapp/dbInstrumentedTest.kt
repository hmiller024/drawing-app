package com.example.drawingapp

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.drawingapp.db.CanvasImage
import com.example.drawingapp.db.CanvasImageDao
import com.example.drawingapp.db.CanvasImageDatabase
import com.example.drawingapp.db.CanvasImageRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class dbInstrumentedTest {
    private lateinit var canvasImageDao: CanvasImageDao
    private lateinit var db: CanvasImageDatabase
    private lateinit var repository: CanvasImageRepository
    private var mockBitmap: MutableLiveData<Bitmap> = MutableLiveData(Bitmap.createBitmap(1024, 1024, Bitmap.Config.ARGB_8888))


    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, CanvasImageDatabase::class.java).build()
        canvasImageDao = db.canvasImageDao()
      // repository = CanvasImageRepository(CoroutineScope(this),  canvasImageDao, context)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun testDeleteAll() {
        runBlocking {

            // Delete all canvas images
            canvasImageDao.deleteAllCanvasImages()

            // Assert that the count of canvas images is initially 0
            assertEquals(0, canvasImageDao.getCount())


        }
    }
}
