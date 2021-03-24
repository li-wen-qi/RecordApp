package com.yoyo.recordapp.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import java.util.*
import kotlin.collections.ArrayList

/**
 * liwenqi Date on 2021/3/24.
 * 描述：
 */
class LeafView: View {
    private val mRandom = Random()
    private var mProgressPaint: Paint? = null
    private val mMiddleAmplitude = 18 /*中等振幅大小*/
    private val mAmplitudeDisparity = 8 /*振幅差*/
    private var mWidth = 0
    private var mHeight = 0
    /**
     * 叶子风行周期
     */
    private var mLeafOnceCycleTime = 1500
    /**
     * 进度条颜色
     */
    private var mProgressColor = -0x5800
    /**
     * 叶子
     */
    private var mLeafPath: Path? = null
    private var mLeafPathArray: MutableList<LeafLoadingView.Leaf>? = null

    /**
     * 叶子宽度
     */
    private var mLeafWidth = 66
    private val mFanCenterRadius = 4 /*风扇中心圆点半径*/
    private val fanLeafInMargin = 10 /*风扇叶距离内部*/
    private val fanLeafOutMargin = 20 /*风扇叶距离外部*/
    /**
     * 叶子数量
     */
    private var mLeafCount = 7

    /**
     * 半圆半径
     */
    private var mSemicircleRadius = 0

    /**
     * 进度条距离背景边缘
     */
    private var mProgressPadding = 18
    /**
     * 进度条总宽度
     */
    private var mProgressBarWidth = 0

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    private fun init() {
        mProgressPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mProgressPaint!!.style = Paint.Style.FILL
        mProgressPaint!!.color = mProgressColor
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawLeaf(canvas)

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w
        mHeight = h
        val leftMargin = Math.sqrt(
                Math.pow(
                        (mHeight / 2).toDouble(),
                        2.0
                ) - Math.pow((mHeight / 2 - mProgressPadding).toDouble(), 2.0)
        )
        mLeafWidth = (mHeight * 3f / 8 + 0.5f).toInt()
        mSemicircleRadius = ((mHeight - mProgressPadding * 2f) / 2).toInt()
        mProgressBarWidth = (mWidth - mProgressPadding - mHeight / 2 - leftMargin).toInt()
        initLeafArray()
        initLeafPath()
    }

    private fun drawLeaf(canvas: Canvas) {
        canvas.save()
        canvas.translate((mWidth - mSemicircleRadius).toFloat(), (mHeight / 2).toFloat())
        for (leaf in mLeafPathArray!!) {
            canvas.save()
            setLeafLocation(leaf)
            canvas.translate(leaf.x.toFloat(), leaf.y.toFloat())
            /*旋转叶子的角度*/canvas.rotate(leaf.angle.toFloat())
            canvas.drawPath(mLeafPath!!, mProgressPaint!!)
            canvas.restore()
        }
        canvas.restore()
    }


    /**
     * 初始化叶子的路径
     */
    private fun initLeafPath() {
        mLeafPath = Path()
        mLeafPath!!.moveTo(-1 / 20f * mLeafWidth, 4 / 10f * mLeafWidth)
        mLeafPath!!.lineTo(1 / 40f * mLeafWidth, 4 / 10f * mLeafWidth)
        mLeafPath!!.lineTo(1 / 20f * mLeafWidth, 2 / 10f * mLeafWidth)
        mLeafPath!!.cubicTo(
                1 / 3f * mLeafWidth, 0f,
                1 / 4f * mLeafWidth, -2 / 5f * mLeafWidth, 0f, -1 / 2f * mLeafWidth
        )
        mLeafPath!!.cubicTo(
                -1 / 4f * mLeafWidth, -2 / 5f * mLeafWidth,
                -1 / 3f * mLeafWidth, 0f,
                -1 / 20f * mLeafWidth, 2 / 10f * mLeafWidth
        )
        mLeafPath!!.close()
    }

    private fun initLeafArray() {
        if (mLeafPathArray == null) {
            mLeafPathArray = ArrayList()
        } else {
            mLeafPathArray!!.clear()
        }
        for (i in 0 until mLeafCount) {
            val leaf = LeafLoadingView.Leaf()
            leaf.angle = mRandom.nextInt(360)
            leaf.direction = mRandom.nextInt(2)
            val randomType = mRandom.nextInt(3)

            /*随时类型－ 随机振幅*/
            var type = LeafLoadingView.StartType.MIDDLE
            when (randomType) {
                0 -> {
                }
                1 -> type = LeafLoadingView.StartType.LITTLE
                2 -> type = LeafLoadingView.StartType.BIG
                else -> {
                }
            }
            leaf.type = type
            leaf.startTime = System.currentTimeMillis() + mRandom.nextInt(mLeafOnceCycleTime)
            mLeafPathArray!!.add(leaf)
        }
    }

    /**
     * 设置叶子的坐标
     */
    private fun setLeafLocation(leaf: LeafLoadingView.Leaf) {
        /*根据叶子的旋转方向，修改旋转度数*/
        leaf.angle += if (leaf.direction == 0) 5 else -5
        val currentTimeMillis = System.currentTimeMillis()
        /*计算当前时间和叶子出场时间的差值*/
        val timeDiff = currentTimeMillis - leaf.startTime

        /*1. 未到出场时间*/if (timeDiff < 0) {
            return
        }

        /*2. 到达终点*/if (timeDiff > mLeafOnceCycleTime) {
            leaf.x = 0
            leaf.y = 0
            /*重置坐标到原点，并且把开始时间加上一个周期，再加一个随机值避免每个周期出场时间都一样*/leaf.startTime += (mLeafOnceCycleTime + mRandom.nextInt(
                    1000
            )).toLong()
            return
        }

        /*3. 在飞行途中*/
        /*按照时间比例，计算x*/leaf.x = (-(((mWidth - mProgressPadding - mLeafWidth / 2 - mSemicircleRadius)
                * timeDiff * 1f) / mLeafOnceCycleTime)).toInt()
        leaf.y = getLocationY(leaf) - mHeight / 4
    }

    /*通过叶子信息获取当前叶子的Y值*/
    private fun getLocationY(leaf: LeafLoadingView.Leaf): Int {
        // y = A(wx+Q)+h
        val w = (2.toFloat() * Math.PI / mProgressBarWidth).toFloat()
        var a = mMiddleAmplitude.toFloat()
        when (leaf.type) {
            LeafLoadingView.StartType.LITTLE ->                 /*小振幅 ＝ 中等振幅 － 振幅差*/a =
                    (mMiddleAmplitude - mAmplitudeDisparity).toFloat()
            LeafLoadingView.StartType.MIDDLE -> a = mMiddleAmplitude.toFloat()
            LeafLoadingView.StartType.BIG ->                 /*小振幅 ＝ 中等振幅 + 振幅差*/a =
                    (mMiddleAmplitude + mAmplitudeDisparity).toFloat()
            else -> {
            }
        }
        return (a * Math.sin((w * leaf.x).toDouble())).toInt() + mSemicircleRadius * 3 / 4
    }
}