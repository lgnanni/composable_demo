package com.lgnanni.electroluxdemo.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.parcelize.IgnoredOnParcel

@Parcelize
data class Photo (
    val id: String,
    val owner: String,
    val secret: String,
    val server: Integer,
    val title: String,
    val isPublic: Boolean,
    val isFriend: Boolean,
    val isFamily: Boolean): Parcelable {
    
    fun getUrl() : String {
        return StringBuilder()
            .append("https://live.staticflickr.com/")
            .append(server)
            .append('/')
            .append(id)
            .append('_')
            .append(secret)
            .append('_')
            .append('b')
            .append(".jpg").toString()
    }
}

