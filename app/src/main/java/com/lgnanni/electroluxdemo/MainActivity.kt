package com.lgnanni.electroluxdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
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
                    Column() {
                        TextSearchBar(
                            modifier = Modifier.padding(8.dp),
                            label = "Search",
                            onDoneActionClick = {
                                viewModel.loadPhotos(this@MainActivity, it)
                            }
                        )

                        PhotoGrid()
                    }

                }
            }
        }

        viewModel.loadPhotos(this)
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun PhotoGrid() {
        val photos = viewModel.photos.observeAsState().value
        LazyVerticalGrid(
            cells = GridCells.Adaptive(minSize = 100.dp)) {
            items(photos!!) { photo ->
                PhotoElement(photoUrl = photo.getUrl())
            }
        }

    }

    @Composable
    fun PhotoElement(photoUrl: String) {
        ElectroluxDemoTheme() {
            Box(modifier = Modifier.size(110.dp).padding(8.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
                GlideImage(
                    imageModel = photoUrl,
                    modifier = Modifier
                        .border(1.dp, Color.White),
                    requestOptions = {
                        RequestOptions()
                            .override(90, 90)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .centerCrop()
                    },
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center,
                    placeHolder = ImageBitmap.imageResource(id = R.drawable.placeholder),
                    error = ImageBitmap.imageResource(id = R.drawable.placeholder)

                )
            }
        }
    }

    @Composable
    fun TextSearchBar(modifier: Modifier = Modifier,
                      label: String,
                      onDoneActionClick: (String) -> Unit = {},
                      onClearClick: () -> Unit = {},
                      onFocusChanged: (FocusState) -> Unit = {},
    ) {

        var text by remember { mutableStateOf("") }

        OutlinedTextField(
            modifier = modifier
                .fillMaxWidth(1f)
                .onFocusChanged { onFocusChanged(it) },
            value = text,
            onValueChange = { text = it },
            label = { Text(text = label) },
            textStyle = MaterialTheme.typography.subtitle1,
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = { onClearClick() }) {
                    Icon(imageVector = Icons.Filled.Clear, contentDescription = "Clear")
                }
            },
            keyboardActions = KeyboardActions(onSearch = { onDoneActionClick(text) }),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search,
                keyboardType = KeyboardType.Text
            )
        )
    }
}