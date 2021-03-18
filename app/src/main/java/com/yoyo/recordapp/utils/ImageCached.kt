package com.yoyo.recordapp.utils

import android.util.TypedValue
import android.content.Context
import android.graphics.Color
import android.util.SparseIntArray
import com.yoyo.recordapp.WordApplication

object ImageCached {

    // 使用了缓存优化的url容器
    private val thumbnails = mutableMapOf<String, String>()

    // 获得全局appContext
    fun appContext(): Context = WordApplication.context!!

    fun dp2px(dpValue: Int): Int {
        return dp2px(dpValue, appContext())
    }

    // 使用了缓存优化的尺寸容器
    private val dps = SparseIntArray()
    // 使用了缓存优化的颜色容器
    private val colors = mutableMapOf<String, Int>()

    fun dp2px(dpValue: Int, context: Context): Int { // if use viewContext can load preview
        val result = dps[dpValue]
        if (result > 0) return result
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue.toFloat(), context.resources.displayMetrics).toInt().also {
            dps.put(dpValue, it)
        }
    }

    fun getColor(color: String): Int {
        val result = colors[color]
        if (result != null) return result
        return try {
            Color.parseColor(color).also { colors[color] = it }
        } catch (e: Exception) {
            e.printStackTrace()
            Color.TRANSPARENT
        }
    }

    // 是否进行url优化
    private fun opt(url: String?) : Boolean {
        if (url.isNullOrEmpty()) return false
//        return pictureCloudDomain.any { url.startsWith(it) }
        if (url.contains("google")) return false
        if (url.contains("facebook")) return false
        return true
    }

    fun thumbnail(url: String?, width: Int, height: Int = width, quality: Int = 75, type: Int = 8): String? {
        if (!opt(url)) return url // maybe user avatar url startWith http://graph.facebook.com/
        val cacheKey = "$url-$width-$height-$quality"
        val result = thumbnails[cacheKey]
        if (!result.isNullOrEmpty()) return result
        // https://docs.ucloud.cn/storage_cdn/ufile/service/pic
        val processedUrl = if (width == 0 && height == 0) {
            "$url?iopcmd=convert&dst=webp&Q=$quality"
        } else {
            "$url?iopcmd=convert&dst=webp&Q=$quality|iopcmd=thumbnail&type=$type&width=$width&height=$height"
        }
        return processedUrl.also {
            thumbnails[cacheKey] = it
        }
    }

    // 成比例只限制高度,自动拿宽度
    fun proportional(url: String?, height: Int): String? {
        // http://unicolive-sh.cn-sh2.ufileos.com/5c73d8668bdcf0042514b0ce483bb562?iopcmd=convert&dst=webp&Q=75|iopcmd=thumbnail&type=5&height=45
        if (!opt(url)) return url
        return "$url?iopcmd=convert&dst=webp&Q=75|iopcmd=thumbnail&type=5&height=$height"
    }

}