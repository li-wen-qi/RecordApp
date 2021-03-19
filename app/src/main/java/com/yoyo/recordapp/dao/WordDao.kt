package com.yoyo.recordapp.dao

import androidx.room.*
import com.yoyo.recordapp.bean.Word
import io.reactivex.Single

@Dao
interface WordDao {

    @Query("SELECT * FROM word ORDER BY id DESC")
    fun getWords(): Single<MutableList<Word>>

    @Query("SELECT * FROM word where id=:id")
    fun getWordById(id: Int): Word

    @Insert
    fun insertWord(word: Word):Long

    @Update
    fun updateWord(word: Word):Int

    @Delete
    fun deleteWord(word:Word):Int

}