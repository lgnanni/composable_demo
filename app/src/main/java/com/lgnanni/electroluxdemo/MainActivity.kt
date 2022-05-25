package com.lgnanni.electroluxdemo

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer
import com.lgnanni.electroluxdemo.data.Photo
import com.lgnanni.electroluxdemo.ui.theme.ElectroluxDemoTheme
import com.lgnanni.electroluxdemo.viewmodel.MainViewModel
import com.skydoves.landscapist.glide.GlideImage

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ElectroluxDemoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    PhotoGrid()
                }
            }
        }

        viewModel.loadPhotos(this)
    }


    @Composable
    fun Greeting(name: String) {
        Text(text = "Hello $name!")
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun PhotoGrid() {
        val photos = viewModel.photos.observeAsState().value
        LazyVerticalGrid(cells = GridCells.Adaptive(minSize = 128.dp)) {
            items(photos!!) { photo ->
                PhotoElement(photoUrl = photo.getUrl())
            }
        }

    }

    @Composable
    fun PhotoElement(photoUrl: String) {
        ElectroluxDemoTheme() {
            GlideImage(
                imageModel = photoUrl,
                contentScale = ContentScale.Crop,
                placeHolder = Icons.Default.Place
            )
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        ElectroluxDemoTheme {
            Greeting("Android")
        }
    }
}