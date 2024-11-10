package com.example.drawingapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.drawingapp.databinding.ToolbarSizeBinding


class ToolbarSizeFragment:Fragment() {
    private lateinit var binding: ToolbarSizeBinding
    private val myViewModel: DrawViewModel by activityViewModels()
//    {
//        DrawViewModel.Factory((requireActivity().application as DrawingApplication).repository)
//    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ToolbarSizeBinding.inflate(layoutInflater)

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //set tool size to slider value
        val seekBar = binding.slider
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                myViewModel.setToolSize(progress.toFloat())
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {  }
            override fun onStopTrackingTouch(p0: SeekBar?) {  }

        })
    }
}