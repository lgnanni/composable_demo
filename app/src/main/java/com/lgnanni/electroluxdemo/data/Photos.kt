package com.lgnanni.electroluxdemo.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Photos (
    val page: Integer,
    val pages: Integer,
    val perpage: Integer,
    val total: Integer,
    val photo: List<Photo>) :Parcelable