package com.yoyo.recordapp

import android.app.Application
import com.yoyo.recordapp.db.AppDataBase

class WordApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AppDataBase.getInstance(this)
    }
}