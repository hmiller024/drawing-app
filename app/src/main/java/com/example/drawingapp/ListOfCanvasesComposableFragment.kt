package com.example.drawingapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewDebug.IntToString
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import com.example.drawingapp.databinding.ListOfCanvasesBinding
import com.example.drawingapp.db.CanvasImage


class ListOfCanvasesComposableFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val binding = ListOfCanvasesBinding.inflate(layoutInflater)

        //ComposeView gives us a `Composable` context to run functions in
        binding.composeView1.setContent {
            Log.e("Test", "onCreateView: ",)
            ListOfCanvases(modifier = Modifier.padding(0.dp))

        }

        return binding.root
    }

    @Composable
    fun CanvasImageCard(image: CanvasImage, modifier: Modifier) {
        val vm: DrawViewModel by viewModels()
        {
            DrawViewModel.Factory((requireActivity().application as DrawingApplication).repository)
        }
        Card(modifier = Modifier.padding(5.dp))
        {
            Column {
                Button(
                    modifier = modifier.padding(16.dp),
                    contentPadding = PaddingValues(
                        top = 0.dp,
                        bottom = 0.dp,
                        start = 16.dp,
                        end = 16.dp
                    ),
                    onClick = {
                        vm.selectCanvas(image)
                    }) { Text(image.getFileName()) }


            }

        }
    }

    @Composable
    fun ListOfCanvases(modifier: Modifier = Modifier) {
        Log.e("listofCanvases", "inside list of canvases: ",)
//        val vm: DrawViewModel by viewModels()
//        {
//            DrawViewModel.Factory((requireActivity().application as DrawingApplication).repository)
//        }
        var repo = (requireActivity().application as DrawingApplication).repository
        //too expensive to be recreated each recompose?
        //val canvasList by vm.canvasList.collectAsState(listOf())
        val canvasList by repo.canvasImages.collectAsState(listOf())



            LazyRow {

                for (image in canvasList!!.asReversed()) {
                    item {
                        Log.d("CanvasImage", "Path: $image")
                        CanvasImageCard(image = image, modifier = Modifier.padding(0.dp))
                    }
                }

        }


    }
}
