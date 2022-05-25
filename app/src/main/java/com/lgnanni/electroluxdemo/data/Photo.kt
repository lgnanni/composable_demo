package com.lgnanni.electroluxdemo.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.parcelize.IgnoredOnParcel

@Parcelize
data class Photo (
    val id: Integer,
    val owner: String,
    val secret: String,
    val server: Integer,
    val title: String,
    val isPublic: Boolean): Parcelable {

    @IgnoredOnParcel
    private val URL_BASE = "https://live.staticflickr.com/"

    fun getUrl() : String {
        return StringBuilder()
            .append(URL_BASE)
            .append(server)
            .append('/')
            .append(id)
            .append('_')
            .append(secret)
            .append('_')
            .append('w')
            .append(".jpg").toString()
    }
}

