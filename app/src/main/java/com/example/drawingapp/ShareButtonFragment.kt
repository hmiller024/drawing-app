package com.example.drawingapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.drawingapp.databinding.ShareButtonBinding
import com.example.drawingapp.databinding.ToolbarColorBinding

class ShareButtonFragment: Fragment() {
    //private val binding: ToolbarColorBinding by lazy{ToolbarColorBinding.inflate((layoutInflater))}
    private val myViewModel: DrawViewModel by activityViewModels()

    private lateinit var binding: ShareButtonBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = ShareButtonBinding.inflate(layoutInflater)

        binding.shareButton.setOnClickListener {
            myViewModel.shareDrawing();
        }

        return binding.root

    }

    }