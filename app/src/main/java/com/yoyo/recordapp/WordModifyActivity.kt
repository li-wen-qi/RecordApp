package com.yoyo.recordapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.yoyo.recordapp.bean.Word
import com.yoyo.recordapp.db.AppDataBase
import com.yoyo.recordapp.utils.SystemOperatorGlobalUtils
import kotlinx.android.synthetic.main.activity_update_word.*

/**
 * liwenqi Date on 2021/3/18.
 * 描述：
 */
class WordModifyActivity : AppCompatActivity() {

    val word by lazy { intent.getParcelableExtra<Word>(WORD_EXTRA) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_word)
        SystemOperatorGlobalUtils.setStatusBarColor(this, R.color.white)
        SystemOperatorGlobalUtils.setStatusBarDarkMode(true, this)
        initView()
    }

    private fun initView(){
        wordNumText.setText(word.id.toString())
        wordText.setText(word.name)
        meanText.setText(word.mean)
        exampleText.setText(word.example)
        btnUpdate.setOnClickListener {
            updateWord()
        }
    }

    private fun updateWord() {
        var name = wordText.text.toString()
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "请输入单词", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val word = Word(wordNumText.text.toString().toInt(), name, meanText.text.toString(), exampleText.text.toString())
            AppDataBase.getInstance(this).wordDao().updateWord(word)
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val WORD_EXTRA = "WORD_EXTRA"
        fun start(context: Context, word:Word) {
            context.startActivity(Intent(context, WordModifyActivity::class.java).apply {
                putExtra(WORD_EXTRA, word)
            })
        }
    }
}