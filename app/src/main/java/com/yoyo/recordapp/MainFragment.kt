package com.yoyo.recordapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.yoyo.recordapp.bean.Word
import com.yoyo.recordapp.db.AppDataBase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.toolbar.view.*

class MainFragment : Fragment() {

    private var wordList: MutableList<Word> = mutableListOf()
    private var mDisposables: CompositeDisposable? = null
    private val listAdapter: ListAdapter by lazy {
        ListAdapter(requireContext(), wordList, object : ClickListener {
            override fun deleteClick() {
                //删除
                Toast.makeText(requireContext(), "删除", Toast.LENGTH_LONG).show()
            }

            override fun updateClick(word: Word) {
                //更新
                Toast.makeText(requireContext(), "更新", Toast.LENGTH_LONG).show()
            }

        })
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.iv_left.visibility = View.INVISIBLE
        fab.setOnClickListener {
            findNavController().navigate(R.id.action_MainFragment_to_WordAddFragment)
        }
        initRecycler()
        getUserList()
    }

    private fun initRecycler() {
        rvList?.run {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = listAdapter
        }
    }

    private fun getUserList() {
        var disposable =
            AppDataBase.getInstance(requireContext()).wordDao().getWords().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    wordList.clear()
                    wordList.addAll(it)
                    listAdapter.notifyDataSetChanged()
                }, {

                })
        mDisposables?.add(disposable)

    }

    override fun onDestroy() {
        super.onDestroy()
        mDisposables?.clear()
    }

    override fun onResume() {
        super.onResume()
        try {
            getUserList()
        } catch (e: Exception) {
        }
    }
}