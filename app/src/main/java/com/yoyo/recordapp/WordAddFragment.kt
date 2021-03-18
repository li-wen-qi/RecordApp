package com.yoyo.recordapp

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.yoyo.recordapp.bean.Word
import com.yoyo.recordapp.db.AppDataBase
import kotlinx.android.synthetic.main.fragment_add_word.*
import kotlinx.android.synthetic.main.toolbar.view.*


class WordAddFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_word, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnAdd.setOnClickListener {
            hideSoftInputManager()
            addWord()
        }
        toolbar.iv_left.setOnClickListener {
            hideSoftInputManager()
            findNavController().navigate(R.id.action_WordAddFragment_to_MainFragment)
        }
    }

    private fun addWord() {
        val name = wordText.text.toString()
        val example = exampleText.text.toString()
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(requireContext(), "请输入单词", Toast.LENGTH_SHORT).show()
            return
        }
        var word = Word(0, name, example)
        try {
            AppDataBase.getInstance(requireContext()).wordDao().insertWord(word)
            findNavController().navigate(R.id.action_WordAddFragment_to_MainFragment)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun hideSoftInputManager(){
        val imm = activity?.applicationContext?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager?
        //imm?.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
        imm?.hideSoftInputFromWindow(wordText.windowToken, 0)
    }
}