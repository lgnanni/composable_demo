package com.lgnanni.electroluxdemo.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PhotosWrapper (val photos: Photos) :Parcelable