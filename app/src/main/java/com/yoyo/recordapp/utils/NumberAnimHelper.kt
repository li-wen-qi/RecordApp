package com.yoyo.recordapp.utils

import android.animation.Animator
import android.animation.ValueAnimator


object NumberAnimHelper {
    private val animators = mutableListOf<Animator>()

    fun provideAnimator(start: Float, end: Float): ValueAnimator {
        return ValueAnimator.ofFloat(start, end).also { animators.add(it) }
    }

    fun release() {
        animators.forEach { it.cancel() }
        animators.clear()
    }
}