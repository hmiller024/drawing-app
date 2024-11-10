package com.example.drawingapp
import android.graphics.Color.rgb
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.drawingapp.databinding.ToolbarColorBinding

class ToolbarColorFragment: Fragment() {
    //private val binding: ToolbarColorBinding by lazy{ToolbarColorBinding.inflate((layoutInflater))}
    private val myViewModel: DrawViewModel by activityViewModels()
//    {
//        DrawViewModel.Factory((requireActivity().application as DrawingApplication).repository)
//    }
    private lateinit var binding: ToolbarColorBinding
    private var clickCallback : () -> Unit = {}

    private var defaultColor = 0

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //https://github.com/Dhaval2404/ColorPicker
        binding = ToolbarColorBinding.inflate(layoutInflater)

        binding.rSlider?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                setColor()
                binding.rVal?.text = binding.rSlider!!.progress.toString()
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {        }
            override fun onStopTrackingTouch(p0: SeekBar?) {       }
        })
        binding.gSlider?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                setColor()
                binding.gVal?.text = binding.gSlider!!.progress.toString()
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {        }
            override fun onStopTrackingTouch(p0: SeekBar?) {       }
        })
        binding.bSlider?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                setColor()
                binding.bVal?.text = binding.bSlider!!.progress.toString()
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {        }
            override fun onStopTrackingTouch(p0: SeekBar?) {       }
        })

        return binding.root
    }
    fun setColor(){
//        val rValue = 255 // Example red value
//        val gValue = 128 // Example green value
//        val bValue = 0   // Example blue value

// Create the color integer by shifting the RGB values and combining them.
//        val color = (255 shl 24) or (rValue shl 16) or (gValue shl 8) or bValue
        val rValue = binding.rSlider!!.progress
        val gValue = binding.gSlider!!.progress
        val bValue = binding.bSlider!!.progress
        val color = rgb(rValue, gValue, bValue)
        myViewModel.setToolColor(color)
    }

}