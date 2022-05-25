package com.lgnanni.electroluxdemo.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gcorp.retrofithelper.Response
import com.gcorp.retrofithelper.ResponseHandler
import com.gcorp.retrofithelper.RetrofitClient
import com.lgnanni.electroluxdemo.data.Photo
import com.lgnanni.electroluxdemo.data.Photos
import java.util.*

class MainViewModel : ViewModel() {
    private var _photos = MutableLiveData(emptyList<Photo>())
    val photos: LiveData<List<Photo>> = _photos
    companion object {
        lateinit var retrofitClient: RetrofitClient

    }

    private val API_BASE = "http://www.flickr.com/services/rest/?"
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

        retrofitClient.Get<Photos>()
            .setPath("method=flickr.photos.search" +
                    "&api_key=$API_KEY" +
                    "&tags=$tag" +
                    "&media=photo" +
                    "&per_page=21" +
                    "&page=1" +
                    "&format=json" +
                    "&nojsoncallback=1")
            .setResponseHandler(Photos::class.java,
                object : ResponseHandler<Photos>() {
                    override fun onSuccess(response: Response<Photos>) {
                        super.onSuccess(response)
                        _photos.value = response.body.photo
                    }

                    override fun onError(response: Response<Photos>?) {
                        super.onError(response)
                        Toast.makeText(context, response.toString(), Toast.LENGTH_LONG).show()
                    }

                    override fun onFailed(e: Throwable?) {
                        super.onFailed(e)
                        Toast.makeText(context, e?.message, Toast.LENGTH_LONG).show()
                    }
                }).run(context)

    }
}