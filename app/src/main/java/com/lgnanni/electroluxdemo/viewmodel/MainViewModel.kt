package com.lgnanni.electroluxdemo.viewmodel

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gcorp.retrofithelper.Response
import com.gcorp.retrofithelper.ResponseHandler
import com.gcorp.retrofithelper.RetrofitClient
import com.lgnanni.electroluxdemo.data.Photo
import com.lgnanni.electroluxdemo.data.PhotosWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.io.*
import java.net.URL
import java.util.*

class MainViewModel : ViewModel() {
    private var _photos = MutableLiveData(emptyList<Photo>())
    val photos: LiveData<List<Photo>> = _photos

    private var _selectedPhoto = MutableLiveData("")
    val selectedPhoto: LiveData<String> = _selectedPhoto

    companion object {
        lateinit var retrofitClient: RetrofitClient
    }

    private val API_BASE = "https://api.flickr.com/services/rest/"
    private val API_KEY = "171f377e4b52f2cd6740dc0ce789b8e0"

    fun loadPhotos(context: Context, tag: String = "electrolux") {
        retrofitClient = RetrofitClient.instance
            .setBaseUrl(API_BASE)
            .setConnectionTimeout(4)
            .setReadingTimeout(15)
            //add Headers
            .addHeader("Content-Type", "application/json")
            .addHeader("client", "android")
            .addHeader("language", Locale.getDefault().language)
            .addHeader("os", android.os.Build.VERSION.RELEASE)

        retrofitClient.Get<PhotosWrapper>()
            .setPath("?method=flickr.photos.search" +
                    "&api_key=$API_KEY" +
                    "&tags=$tag" +
                    "&media=photo" +
                    "&per_page=21" +
                    "&page=1" +
                    "&format=json" +
                    "&nojsoncallback=1")
            .setResponseHandler(PhotosWrapper::class.java,
                object : ResponseHandler<PhotosWrapper>() {
                    override fun onSuccess(response: Response<PhotosWrapper>) {
                        super.onSuccess(response)
                        _photos.value = response.body.photos.photo
                    }

                    override fun onError(response: Response<PhotosWrapper>?) {
                        super.onError(response)
                        Toast.makeText(context, response.toString(), Toast.LENGTH_LONG).show()
                    }

                    override fun onFailed(e: Throwable?) {
                        super.onFailed(e)
                        Toast.makeText(context, e?.message, Toast.LENGTH_LONG).show()
                    }
                }).run(context)

    }

    fun setSelectedPhoto(photoUrl: String) {
        _selectedPhoto.value = photoUrl
    }
}