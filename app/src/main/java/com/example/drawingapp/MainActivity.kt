package com.example.drawingapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

import com.example.drawingapp.databinding.ActivityMainBinding
import com.github.dhaval2404.colorpicker.ColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape



class MainActivity : AppCompatActivity() {



    private val binding : ActivityMainBinding by lazy{ActivityMainBinding.inflate(layoutInflater)}
    val myViewModel : DrawViewModel by viewModels() {
        DrawViewModel.Factory((application as DrawingApplication).repository)
    }

    //null error
    //var repo = (application as DrawingApplication).repository


    private var defaultColor = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

      //  myViewModel.signIn()


        setContentView(R.layout.activity_main)

        val toolbarSizeFragment = ToolbarSizeFragment()
        val toolbarColorFragment = ToolbarColorFragment()
        val toolbarToolFragment = ToolbarToolFragment()
        val toolbarFileFragment = ToolbarFileFragment()
        val shareButtonFragment = ShareButtonFragment()

        val listOfCanvasesComposableFragment = ListOfCanvasesComposableFragment()


        //Create canvas
        val canvasFragment = CanvasFragment()
        val fTrans = supportFragmentManager.beginTransaction()
        fTrans.add(R.id.canvasFragmentView, canvasFragment)
        fTrans.commit()

        binding.colorButton?.setOnClickListener{

            ColorPickerDialog
                .Builder(this)
                .setTitle("Pick Theme")
                .setColorShape(ColorShape.CIRCLE)
                .setDefaultColor(defaultColor)
                .setColorListener { color, colorHex ->
                    //Set tool color
                    myViewModel.setToolColor(color)
                }
                .show()
        }
        binding.landscapeColorButton?.setOnClickListener{
            val fTrans = supportFragmentManager.beginTransaction()
            fTrans.remove(listOfCanvasesComposableFragment)
            fTrans.replace(R.id.expandedToolbarFragmentView, toolbarColorFragment)
            fTrans.addToBackStack(null)
            fTrans.commit()
        }
        binding.toolButton.setOnClickListener{
            val fTrans = supportFragmentManager.beginTransaction()
            fTrans.remove(listOfCanvasesComposableFragment)
            fTrans.replace(R.id.expandedToolbarFragmentView, toolbarToolFragment)
            fTrans.addToBackStack(null)
            fTrans.commit()
        }
        binding.sizeButton.setOnClickListener{
            val fTrans = supportFragmentManager.beginTransaction()
            fTrans.remove(listOfCanvasesComposableFragment)
            fTrans.replace(R.id.expandedToolbarFragmentView, toolbarSizeFragment)
            fTrans.addToBackStack(null)
            fTrans.commit()
        }

        binding.canvasListButton?.setOnClickListener{
            val fTrans = supportFragmentManager.beginTransaction()
            fTrans.remove(toolbarColorFragment)
            fTrans.remove(toolbarFileFragment)
            fTrans.remove(toolbarSizeFragment)
            fTrans.remove(toolbarSizeFragment)
            fTrans.remove(shareButtonFragment)

            fTrans.replace(R.id.listOfCanvasesComposableFragmentView, listOfCanvasesComposableFragment)
            fTrans.addToBackStack(null)
            fTrans.commit()
            //myViewModel.displayCanvas(1)
        }

        binding.saveButton?.setOnClickListener{

            myViewModel.setName(binding.fileName!!.text.toString())
            myViewModel.saveCanvas()

            showSavedFileSuccessToast(this , "saved file '${binding.fileName!!.text}'")

            val fTrans = supportFragmentManager.beginTransaction()
            fTrans.remove(toolbarColorFragment)
            fTrans.remove(toolbarFileFragment)
            fTrans.remove(toolbarSizeFragment)
            fTrans.remove(listOfCanvasesComposableFragment)
            fTrans.replace(R.id.expandedToolbarFragmentView, shareButtonFragment)
            fTrans.addToBackStack(null)
            fTrans.commit()


        }


        setContentView(binding.root)

    }

    fun showSavedFileSuccessToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }


}