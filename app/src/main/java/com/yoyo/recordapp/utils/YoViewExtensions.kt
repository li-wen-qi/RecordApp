package com.yoyo.recordapp.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.request.RequestOptions
import com.yoyo.recordapp.R
import com.yoyo.recordapp.WordApplication
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

// 获得全局appContext

fun LifecycleOwner.onDestroy(callback: () -> Unit) {
    lifecycle.addObserver(object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onDestroy() = callback()
    })
}

fun View.setOnClickCallback(interval: Long = 1000L, callback: (View) -> Unit) {
    Observable.create<View> {
        setOnClickListener(it::onNext)
    }.throttleFirst(interval, TimeUnit.MILLISECONDS)
            .doOnNext(callback)
            .subscribe()
}

fun ImageView.loadThumbnails(url: String?, width: Int, height: Int = width, quality: Int = 75, type: Int = 8, placeHolder: Int? = null, errorHolder: Int? = null,
                             transformation: Transformation<Bitmap>? = null) {
    val thumbnailsUrl = if (url.isNullOrEmpty()) "" else ImageCached.thumbnail(url, width, height, quality, type)
    load(thumbnailsUrl, placeHolder, errorHolder, transformation)
}

fun RecyclerView?.trySmoothScrollToBottom() {
    val self = this ?: return
    if (self.scrollState == RecyclerView.SCROLL_STATE_IDLE) {
        val itemCount = self.adapter?.itemCount ?: -1
        if (itemCount > 0) {
            smoothScrollToPosition(itemCount - 1)
        }
    }
}

fun ImageView.load(url: String?, placeHolder: Int? = null, errorHolder: Int? = null,
                   transformation: Transformation<Bitmap>? = null) {
    if (url.isNullOrEmpty()) {
        setImageResource(placeHolder ?: R.color.transparent)
        return
    }
    Glide.with(this).load(url)
            .apply {
                if (placeHolder != null || errorHolder != null || transformation != null) {
                    apply(RequestOptions().apply {
                        if (placeHolder != null) placeholder(placeHolder)
                        if (errorHolder != null) error(errorHolder)
                        if (transformation != null) transform(transformation)
                    })
                }
            }
            .into(this)

}

/**
 * 支持本地资源
 */
fun ImageView.load(@DrawableRes resId: Int, placeHolder: Int? = null, errorHolder: Int? = null,
                   transformation: Transformation<Bitmap>? = null) {
    Glide.with(this).load(resId)
            .apply {
                if (placeHolder != null || errorHolder != null || transformation != null) {
                    apply(RequestOptions().apply {
                        if (placeHolder != null) placeholder(placeHolder)
                        if (errorHolder != null) error(errorHolder)
                        if (transformation != null) transform(transformation)
                    })
                }
            }
            .into(this)
}

fun Context.resolveThemeAttribute(resId: Int): Int {
    return TypedValue()
            .apply { theme.resolveAttribute(resId, this, true) }
            .resourceId
}

fun Activity.closeKeyBoard(): Boolean {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    return imm?.hideSoftInputFromWindow(window.decorView.applicationWindowToken, 0) ?: false
}

fun ViewGroup.inflate(resId: Int): View {
    return LayoutInflater.from(context).inflate(resId, this, false)
}

// https://stackoverflow.com/questions/9273218/is-it-always-safe-to-cast-context-to-activity-within-view/45364110
private fun getActivity(context: Context?): Activity? {
    if (context == null) return null
    if (context is Activity) return context
    if (context is ContextWrapper) return getActivity(context.baseContext)
    return null
}


