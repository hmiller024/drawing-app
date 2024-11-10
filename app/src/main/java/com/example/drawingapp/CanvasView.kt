package com.example.drawingapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
external fun invert(bitmap: Bitmap, highlightBitmap: Bitmap, tool: Int)
class CanvasView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    //Define local variables
    var bitmap: Bitmap? = null
    var mainCanvas: Canvas? = null
    var paintCanvas: Canvas? = null
    var brush: Paint = Paint()

    //Tool properties
    var toolSize: Float = 5f
    var toolColor: Int = 0
    var toolShape: String = "PEN"

    //Position properties
    var lastX: Float = 0f
    var lastY: Float = 0f

    private var highlightBitmap: Bitmap? = null

    companion object {
        init {
            System.loadLibrary("drawingapp")
        }
    }

    //Initialize the canvas
    init {
        //Draw border around the canvas
        //Maybe remove this later
        val border = Paint()
        border.color = -0x1000000
        border.style = Paint.Style.STROKE
        border.strokeWidth = 5f
        paintCanvas?.drawRect(1f, 1f, 1023f, 1023f, border)

        //Set the brush properties
        brush.isAntiAlias = true
        brush.isDither = true
        brush.color = toolColor
        brush.style = Paint.Style.STROKE
        brush.strokeJoin = Paint.Join.ROUND
        brush.strokeCap = Paint.Cap.ROUND
        brush.strokeWidth = toolSize
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //Draw the bitmap
        bitmap?.let { canvas.drawBitmap(it, 0f, 0f, null) }
    }


    fun updateBrush() {
        brush.color = toolColor
        brush.strokeWidth = toolSize

        //Change the brush shape
        when (toolShape) {
            "PEN" -> {
                brush.strokeJoin = Paint.Join.ROUND
                brush.strokeCap = Paint.Cap.ROUND
            }
        }

        when(toolShape)
        {
            "PAINTBRUSH" -> {
                brush.strokeCap = Paint.Cap.SQUARE
            }
        }

        //Eraser to use on a white canvas
        when(toolShape)
        {
            "ERASER" -> {
                brush.color = Color.WHITE
                brush.strokeJoin = Paint.Join.ROUND
                brush.strokeCap = Paint.Cap.ROUND

            }
        }

    }

    //Function to draw on the canvas
    fun drawOnCanvas(x: Float, y: Float) {
        if (toolShape == "INVERT" || toolShape =="BLUR" || toolShape == "DITHER") {
            // Create a highlighted bitmap if it doesn't exist
            if (highlightBitmap == null) {
                highlightBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            }
            val highlightCanvas = Canvas(highlightBitmap!!)

            // Draw onto the highlighted bitmap instead of directly onto the main canvas
            highlightCanvas.drawLine(lastX, lastY, x, y, brush)
        } else {
            paintCanvas?.drawLine(lastX, lastY, x, y, brush)
        }

        //Set the last position
        lastX = x
        lastY = y

        //Reset the canvas
        invalidate()
    }

    //Function to end the drawing
    fun endDrawing() {

        if(toolShape == "INVERT"){
            bitmap?.let { highlightBitmap?.let { it1 -> invert(it, it1, 1) } }
        }
        if(toolShape == "BLUR"){
            bitmap?.let { highlightBitmap?.let { it1 -> invert(it, it1, 2) } }
        }
        if(toolShape == "DITHER"){
            bitmap?.let { highlightBitmap?.let { it1 -> invert(it, it1, 3) } }
        }
        highlightBitmap = null
        lastX = 0f
        lastY = 0f
    }

    //Get user's touch position
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = x
                lastY = y
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                drawOnCanvas(x, y)
            }
            MotionEvent.ACTION_UP -> {
                endDrawing()
            }
        }
        return super.onTouchEvent(event)
    }
}