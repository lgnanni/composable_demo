package com.lgnanni.electroluxdemo

import android.content.ContentValues
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.selection.selectable
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.lgnanni.electroluxdemo.ui.theme.ElectroluxDemoTheme
import com.lgnanni.electroluxdemo.viewmodel.MainViewModel
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    //Initialization of job
    private var job = Job()

    // Initialization of scope for the coroutine to run in
    private var scopeForSaving = CoroutineScope(job + Dispatchers.Main)

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ElectroluxDemoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val selectedPhoto = viewModel.selectedPhoto.observeAsState().value

                    AnimatedVisibility(
                        visible = selectedPhoto.isNullOrBlank(),
                        enter = fadeIn(),
                        exit = fadeOut()) {
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
                    AnimatedVisibility(
                        visible = selectedPhoto!!.isNotEmpty(),
                        enter = scaleIn() + expandVertically(expandFrom = Alignment.CenterVertically),
                        // By Default, `scaleOut` uses the center as its pivot point. When used with an
                        // ExitTransition that shrinks towards the center, the content will be shrinking both
                        // in terms of scale and layout size towards the center.
                        exit = scaleOut() + shrinkVertically(shrinkTowards = Alignment.CenterVertically)) {

                        Column(modifier = Modifier
                            .fillMaxSize(1.0f)
                            .background(Color.DarkGray),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center) {

                            var imageBitmap: Bitmap? = null
                            Box(modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .fillMaxHeight(0.5f)
                                .background(Color.LightGray)
                                .border(2.dp, Color.Blue)
                                .clickable { viewModel.setSelectedPhoto("") }) {
                                GlideImage(
                                    modifier = Modifier
                                        .align(Alignment.Center),
                                    imageModel = selectedPhoto,
                                    success = {
                                        imageBitmap = it.drawable!!.toBitmap()
                                        Image(
                                            bitmap = imageBitmap!!.asImageBitmap(),
                                            contentDescription = null,
                                        )
                                    }
                                )
                            }

                            Button(modifier = Modifier
                                .fillMaxWidth(0.9f),
                                onClick = {
                                    scopeForSaving.launch {
                                        imageBitmap?.let { saveToStorage(it) }
                                    }
                                }
                            ) {
                                Text("Save Image")
                            }
                        }
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
            cells = GridCells.Fixed(3)) {
            items(photos!!) { photo ->
                PhotoElement(photoUrl = photo.getUrl())
            }
        }

    }

    @Composable
    fun PhotoElement(photoUrl: String) {
        ElectroluxDemoTheme() {
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .padding(8.dp), contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
                GlideImage(
                    imageModel = photoUrl,
                    modifier = Modifier
                        .border(1.dp, Color.White)
                        .clickable { viewModel.setSelectedPhoto(photoUrl) },
                    requestOptions = {
                        RequestOptions()
                            .override(90, 90)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .centerCrop()
                    },
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center
                )
            }
        }
    }

    @Composable
    fun TextSearchBar(modifier: Modifier = Modifier,
                      label: String,
                      onDoneActionClick: (String) -> Unit = {},
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
                IconButton(onClick = { text = "" }) {
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

    private suspend fun saveToStorage(bitmap: Bitmap) {
        withContext(Dispatchers.IO){
            val filename = "Electrolux_Demo_${System.currentTimeMillis()}.jpg"
            var fos: OutputStream? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentResolver?.also { resolver ->
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                    }
                    val imageUri: Uri? =
                        resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                    fos = imageUri?.let { resolver.openOutputStream(it) }
                }
            } else {
                val imagesDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val image = File(imagesDir, filename)
                fos = FileOutputStream(image)
            }
            fos?.use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                runOnUiThread {
                    viewModel.setSelectedPhoto("")
                    Toast.makeText(this@MainActivity, "$filename saved to Photos", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}