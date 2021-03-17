package com.yoyo.recordapp.utils

import android.os.Environment
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.*

/**
 * renamed from BuildProperties
 */
class PropertyGlobalUtils private constructor() {
    private val properties: Properties
    val isEmpty: Boolean
        get() = properties.isEmpty

    fun size(): Int {
        return properties.size
    }

    fun values(): Collection<Any> {
        return properties.values
    }

    fun getProperty(name: String?, defaultValue: String?): String {
        return properties.getProperty(name, defaultValue)
    }

    companion object {
        @JvmStatic
        @Throws(IOException::class)
        fun newInstance(): PropertyGlobalUtils {
            return PropertyGlobalUtils()
        }
    }

    init {
        properties = Properties()
        properties.load(FileInputStream(File(Environment.getRootDirectory(), "build.prop")))
    }
}