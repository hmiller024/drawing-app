package com.example.drawingapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.drawingapp.db.CanvasImage
import com.example.drawingapp.db.CanvasImageDao
import com.example.drawingapp.db.CanvasImageDatabase
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.robolectric.Robolectric

@RunWith(AndroidJUnit4::class)
class CanvasViewTest {

    private lateinit var mockCanvas: Canvas

    private lateinit var mockBitmap: Bitmap

    private lateinit var canvasView: CanvasView

    private lateinit var mockContext: Context

    private lateinit var mockAttrs: AttributeSet

    private lateinit var userDao: CanvasImageDao
    private lateinit var db: CanvasImageDatabase


    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        //Set up vars
        mockCanvas = Canvas()
        mockBitmap = Bitmap.createBitmap(1024, 1024, Bitmap.Config.ARGB_8888)
        mockContext = InstrumentationRegistry.getInstrumentation().targetContext
        mockAttrs = Robolectric.buildAttributeSet().build()

        canvasView = CanvasView(mockContext, mockAttrs)
        canvasView.mainCanvas = mockCanvas
        canvasView.bitmap = mockBitmap

        db = Room.inMemoryDatabaseBuilder(
            mockContext, CanvasImageDatabase::class.java).build()
        userDao = db.canvasImageDao()

    }

    @Test
    fun testUpdateBrush() {
        val expectedColor = 123456
        val expectedSize = 10f
        val expectedShape = "PEN"

        canvasView.toolColor = expectedColor
        canvasView.toolSize = expectedSize
        canvasView.toolShape = expectedShape

        canvasView.updateBrush()

        val brush = canvasView.brush
        assertEquals(expectedColor, brush.color)
        assertEquals(expectedSize, brush.strokeWidth)

        when (expectedShape) {
            "PEN" -> {
                assertEquals(Paint.Join.ROUND, brush.strokeJoin)
                assertEquals(Paint.Cap.ROUND, brush.strokeCap)
            }
        }
    }

    @Test
    fun testDrawOnCanvas() {
        val expectedX = 100f
        val expectedY = 200f

        canvasView.lastX = 50f
        canvasView.lastY = 100f

        //Copy the bitmap
        val oldBitmap = canvasView.bitmap!!.copy(canvasView.bitmap!!.config, true)

        canvasView.drawOnCanvas(expectedX, expectedY)

        //Compare contents of the old and new bitmaps
        assertEquals(false, oldBitmap == canvasView.bitmap)
    }

    @Test
    fun testEndDrawing() {
        canvasView.lastX = 100f
        canvasView.lastY = 200f

        canvasView.endDrawing()

        assertEquals(0f, canvasView.lastX)
        assertEquals(0f, canvasView.lastY)
    }

}