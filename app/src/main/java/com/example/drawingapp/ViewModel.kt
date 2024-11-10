package com.example.drawingapp

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.graphics.Color
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.drawingapp.db.CanvasImage
import com.example.drawingapp.db.CanvasImageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class DrawViewModel(val repository: CanvasImageRepository) : ViewModel() {
    var toolSize : MutableLiveData<Float> = MutableLiveData(5f)
    var toolColor : MutableLiveData<Int> = MutableLiveData(Color.BLACK)
    var toolShape : MutableLiveData<String> = MutableLiveData("PEN")
    private var currName : MutableLiveData<String> = repository.fileName
    private var saveNum : Int = 0;

    var bitmap : MutableLiveData<Bitmap> = repository.bitmap
    //var bitmap: Bitmap = Bitmap.createBitmap(1024, 1024, Bitmap.Config.ARGB_8888)
    //var repo = DrawingApplication().repository

    var canvasList: Flow<List<CanvasImage>> = repository.canvasImages
   // var canvasCount: Flow<Int> = repository.canvasCount
    var currCount: Int = 0

    fun saveCanvas() {
        repository.addCanvasImage(bitmap, currName.value!!)
    }

    fun setToolColor(color: Int) {
        toolColor.value = color
    }

    fun setToolSize(size: Float) {
        toolSize.value = size
    }

    fun setToolShape(shape: String) {
        toolShape.value = shape
    }
    fun setName(name: String){
        currName.value = name
    }
    fun displayCanvas(i: Int) {

    }
    var canvasView: CanvasView? = null
    fun selectCanvas(canvasImage: CanvasImage) {
        bitmap.value = canvasImage.getBitmap()
    }

    fun shareDrawing() {
        repository.shareCanvasImage(bitmap, currName.value!!)
    }

    fun signIn(s: String, s1: String) {
        repository.cloudManager.signIn(s, s1)
    }

    fun signUp(s: String, s1: String) {
        repository.cloudManager.createUser(s, s1)
    }

    // This factory class allows us to define custom constructors for the view model
    class Factory(private val repository: CanvasImageRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DrawViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return DrawViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}