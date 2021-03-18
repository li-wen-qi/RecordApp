package com.yoyo.recordapp.utils

import android.view.animation.AccelerateDecelerateInterpolator

object Injection {
    val accelerateDecelerateInterpolator by lazy { AccelerateDecelerateInterpolator() }

    val transformCropCircle by lazy { com.bumptech.glide.load.resource.bitmap.CircleCrop() }

}