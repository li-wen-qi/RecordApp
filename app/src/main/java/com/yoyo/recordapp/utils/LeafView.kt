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
    internal class Leaf {
        var x = 0
        var y = 0
        var angle = 0
        var direction /*旋转方向*/ = 0
    }

    private val mRandom = Random()
    private var mProgressPaint: Paint? = null
    private var mWidth = 0
    private var mHeight = 0

    /**
     * 进度条颜色
     */
    private var mProgressColor = -0x5800
    /**
     * 叶子
     */
    private var mLeafPath: Path? = null
    private var mLeafArray: MutableList<Leaf>? = null

    private var mTreePath: Path? = null

    /**
     * 叶子宽度
     */
    private var mLeafWidth = 66
    /**
     * 叶子数量
     */
    private var mLeafCount = 1

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
        mWidth = 200
        mHeight = 200

        mLeafWidth = 100
        initLeafArray()
        initLeafPath()
    }

    private fun drawLeaf(canvas: Canvas) {
        canvas.save()
        canvas.translate(500f, 500f)//移动画布原点
        for (leaf in mLeafArray!!) {
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
        if (mLeafArray == null) {
            mLeafArray = ArrayList()
        } else {
            mLeafArray!!.clear()
        }
        for (i in 0 until mLeafCount) {
            val leaf = Leaf()
            leaf.angle = 0//mRandom.nextInt(360)
            leaf.direction = mRandom.nextInt(2)
            mLeafArray!!.add(leaf)
        }
    }

    /**
     * 设置叶子的坐标
     */
    private fun setLeafLocation(leaf: Leaf) {

        leaf.x = mRandom.nextInt(60)
        leaf.y = mRandom.nextInt(60)
    }

}