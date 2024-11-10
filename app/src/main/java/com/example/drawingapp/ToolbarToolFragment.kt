package com.example.drawingapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.drawingapp.databinding.ToolbarToolBinding


class ToolbarToolFragment:Fragment() {
    private lateinit var binding: ToolbarToolBinding
    private val myViewModel: DrawViewModel by activityViewModels()
//    {
//        DrawViewModel.Factory((requireActivity().application as DrawingApplication).repository)
//    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ToolbarToolBinding.inflate(layoutInflater)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnEraser.setOnClickListener()
        {
            myViewModel.setToolShape("ERASER")
        }
        binding.btnPaintbrush.setOnClickListener()
        {
            myViewModel.setToolShape("PAINTBRUSH")
        }
        binding.btnPen.setOnClickListener()
        {
            myViewModel.setToolShape("PEN")
        }
        binding.btnInvert.setOnClickListener(){
            myViewModel.setToolShape("INVERT")
        }
        binding.btnBlur.setOnClickListener(){
            myViewModel.setToolShape("BLUR")
        }
        binding.btnDither.setOnClickListener(){
            myViewModel.setToolShape("DITHER")
        }

    }


    }
