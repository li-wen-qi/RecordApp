package com.yoyo.recordapp

import android.animation.ValueAnimator
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.yoyo.recordapp.bean.Word
import com.yoyo.recordapp.db.AppDataBase
import com.yoyo.recordapp.utils.Injection
import com.yoyo.recordapp.utils.load
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.toolbar.view.*

class MainFragment : Fragment() {
    private val animator = ValueAnimator.ofInt(0, 100)
    private var wordList: MutableList<Word> = mutableListOf()
    private var mDisposables: CompositeDisposable? = null
    private val listAdapter: ListAdapter by lazy {
        ListAdapter(requireContext(), wordList, object : ClickListener {
            override fun deleteClick(word: Word) {
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
        toolBar.setRightText("添加")
        toolBar.setOnRightTvClickListener {
            findNavController().navigate(R.id.action_MainFragment_to_WordAddFragment)
        }
        refreshLayout.setOnRefreshListener {
            queryWordList()
        }
        imgvAvatar.load(Constants.AVATAR_URL, transformation = Injection.transformCropCircle)
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
                    startLeafLoadingView()
                    refreshLayout.isRefreshing = false
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
        stopLeafLoadingAnimation()
    }

    override fun onResume() {
        super.onResume()
        try {
            queryWordList()
        } catch (e: Exception) {
        }
    }

    private fun startLeafLoadingView(){
        animator?.duration = 3000
        animator.addUpdateListener { animation ->
            if (animation.animatedValue as Int == 70) {
                animator.cancel()
            }
            leafLoading.setCurrentProgress(animation.animatedValue as Int)
        }
        animator.start()
        leafLoading.setNumber(70)
    }

    private fun stopLeafLoadingAnimation() {
        animator?.cancel()
    }
}