package com.yoyo.recordapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.yoyo.recordapp.bean.Word
import com.yoyo.recordapp.dao.WordDao


@Database(entities = [Word::class], version = 1)
abstract class AppDataBase : RoomDatabase() {

    abstract fun wordDao(): WordDao

    companion object {
        private var instance: AppDataBase? = null

        fun getInstance(context: Context): AppDataBase {
            if (instance == null) {
                synchronized(AppDataBase::class.java) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDataBase::class.java,
                        "word_db"
                    )
                        .allowMainThreadQueries()
                        .build()
                }
            }
            return instance!!
        }
    }




}
