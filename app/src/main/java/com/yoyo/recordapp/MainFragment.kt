package com.yoyo.recordapp

import android.animation.ValueAnimator
import android.content.Intent
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
import com.yoyo.recordapp.utils.Injection
import com.yoyo.recordapp.utils.NumberAnimHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.toolbar.view.*

class MainFragment : Fragment() {
    private var animator: ValueAnimator? = null
    private var wordList: MutableList<Word> = mutableListOf()
    private var mDisposables: CompositeDisposable? = null
    private val listAdapter: ListAdapter by lazy {
        ListAdapter(requireContext(), wordList, object : ClickListener {
            override fun deleteClick(word :Word) {
                //删除
                AppDataBase.getInstance(requireContext()).wordDao().deleteWord(word)
                queryWordList()
            }

            override fun updateClick(word: Word) {
                //更新
                WordModifyActivity.start(requireContext(), word)
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
        toolBar.iv_left.visibility = View.INVISIBLE
        fab.setOnClickListener {
            findNavController().navigate(R.id.action_MainFragment_to_WordAddFragment)
        }
        initRecycler()
        queryWordList()
    }

    private fun initRecycler() {
        rvList?.run {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = listAdapter
        }
    }

    private fun queryWordList() {
        var disposable =
            AppDataBase.getInstance(requireContext()).wordDao().getWords().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    wordList.clear()
                    wordList.addAll(it)
                    listAdapter.notifyDataSetChanged()
                    tvCount.text = wordList.size.toString()
                    startNumberAnimation()
                }, {

                })
        mDisposables?.add(disposable)

    }

    override fun onDestroy() {
        super.onDestroy()
        mDisposables?.clear()

    }

    override fun onPause() {
        super.onPause()
        stopNumberAnimation()
    }
    override fun onResume() {
        super.onResume()
        try {
            queryWordList()
        } catch (e: Exception) {
        }
    }

    private fun stopNumberAnimation() {
        animator?.cancel()
    }

    private fun startNumberAnimation() {
        animator?.cancel()
        animator = NumberAnimHelper.provideAnimator(1f, 1.2f)
            .apply {
                duration = 800
                repeatMode = ValueAnimator.REVERSE
                repeatCount = ValueAnimator.INFINITE
                interpolator = Injection.accelerateDecelerateInterpolator
                addUpdateListener {
                    val value = it.animatedValue as Float
                    tvCount.scaleX = value
                    tvCount.scaleY = value
                }
            }
        animator?.start()
    }
}