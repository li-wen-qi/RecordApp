package com.yoyo.recordapp

import android.app.Application
import android.content.Context
import com.yoyo.recordapp.db.AppDataBase

class WordApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        context = this
        AppDataBase.getInstance(this)
    }
    companion object {
        @JvmStatic
        var context: Context? = null
    }

}