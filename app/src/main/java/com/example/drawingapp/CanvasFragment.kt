package com.example.drawingapp

import android.graphics.Canvas
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels

class CanvasFragment : Fragment(R.layout.canvas) {
    //Add the canvasView class
    private lateinit var canvasView: CanvasView
    private var clickCallback: () -> Unit = {}
    private val myViewModel: DrawViewModel by activityViewModels()
    {
        DrawViewModel.Factory((requireActivity().application as DrawingApplication).repository)
    }



    //Initialize the canvasView
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        canvasView = view.findViewById(R.id.canvasView)

        //Observe the brush data
        myViewModel.toolSize.observe(viewLifecycleOwner) {
            canvasView.toolSize = it
            canvasView.updateBrush()
        }

        myViewModel.toolColor.observe(viewLifecycleOwner) {
            canvasView.toolColor = it
            canvasView.updateBrush()
        }

        myViewModel.toolShape.observe(viewLifecycleOwner) {
            canvasView.toolShape = it
            canvasView.updateBrush()

        }

        //Observe bitmap
        myViewModel.bitmap.observe(viewLifecycleOwner) {
            canvasView.bitmap = it
            canvasView.paintCanvas = Canvas(it)
            canvasView.invalidate()
        }
    }

    //Trigger when user is drawing to view
    fun drawOnCanvas(x: Float, y: Float) {
        canvasView.drawOnCanvas(x, y)
        myViewModel.bitmap.value = canvasView.bitmap
    }

    //Trigger when user is done drawing
    fun endDrawing() {
        canvasView.endDrawing()
        myViewModel.bitmap.value = canvasView.bitmap
    }

    public fun setListener(listener: () -> Unit) {
        clickCallback = listener
    }
}