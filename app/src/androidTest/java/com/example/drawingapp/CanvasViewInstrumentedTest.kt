package com.example.drawingapp

import android.graphics.Color
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CanvasViewInstrumentedTest {

    private lateinit var canvasView: CanvasView

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        canvasView = CanvasView(context, null)
    }

    @Test
    fun testToolSize() {
        val expectedToolSize = 10f
        canvasView.toolSize = expectedToolSize
        assertEquals(expectedToolSize, canvasView.toolSize)
    }

    @Test
    fun testToolColor() {
        val expectedToolColor = Color.RED
        canvasView.toolColor = expectedToolColor
        assertEquals(expectedToolColor, canvasView.toolColor)
    }

    @Test
    fun testToolShape() {
        val expectedToolShape = "PEN"
        canvasView.toolShape = expectedToolShape
        assertEquals(expectedToolShape, canvasView.toolShape)
    }

    @Test
    fun testDrawOnCanvas() {
        val x = 100f
        val y = 200f
        canvasView.drawOnCanvas(x, y)
        // Add assertions to verify the drawing on the canvas
    }

    @Test
    fun testEndDrawing() {
        canvasView.endDrawing()
        // Add assertions to verify the end of drawing
    }

    // Add more tests as needed

}