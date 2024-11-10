package com.example.drawingapp

import android.app.Application
import androidx.lifecycle.ViewModel
import com.example.drawingapp.db.CanvasImageDatabase
import com.example.drawingapp.db.CanvasImageRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

//Store singletons here
class DrawingApplication : Application() {
    //Initialize scope, db, and repo
    val scope = CoroutineScope(SupervisorJob())
    val db by lazy { CanvasImageDatabase.getDatabase(applicationContext) }
    val repository by lazy { CanvasImageRepository(scope, db.canvasImageDao(), applicationContext) }
    //val vm by lazy { DrawViewModel(repository)}
}