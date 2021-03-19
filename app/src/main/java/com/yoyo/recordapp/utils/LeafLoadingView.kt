package com.yoyo.recordapp.utils

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import java.util.*
import kotlin.collections.ArrayList

class LeafLoadingView : View {
    private var number = 0
    private var mWidth = 0
    private var mHeight = 0
    private var mBgPaint: Paint? = null
    private var mProgressPaint: Paint? = null
    private var mFanPaint: Paint? = null
    private var mFanFillPaint: Paint? = null
    private var mTextPaint: Paint? = null
    private val mRandom = Random()

    /**
     * 进度条距离背景边缘
     */
    private var mProgressPadding = 18

    /**
     * 总进度
     */
    private var mTotalProgress = 100

    /**
     * 当前进度
     */
    private var mCurrentProgress = 5

    /**
     * 是否结束
     */
    private var mIsFinish = false

    /**
     * 背景条颜色
     */
    private var mBackgroundColor = -0x31b65

    /**
     * 进度条颜色
     */
    private var mProgressColor = -0x5800

    /**
     * 风扇叶颜色
     */
    private var mFanColor = -0x1

    /**
     * 风扇内部填充颜色
     */
    private var mFanInColor = -0x235b8

    /**
     * 背景矩形
     */
    private var mBgRect: RectF? = null

    /**
     * 进度条矩形
     */
    private var mProgressRectF: RectF? = null

    /**
     * 左边半圆矩形 用来画弧
     */
    private var mSemiCircleRectF: RectF? = null

    /**
     * 进度条总宽度
     */
    private var mProgressBarWidth = 0

    /**
     * 半圆半径
     */
    private var mSemicircleRadius = 0

    /**
     * 风扇叶
     */
    private var mFanLeafPath: Path? = null

    /**
     * 叶子
     */
    private var mLeafPath: Path? = null
    private var mLeafPathArray: MutableList<Leaf>? = null

    /**
     * 叶子宽度
     */
    private var mLeafWidth = 66
    private val mFanCenterRadius = 4 /*风扇中心圆点半径*/
    private val fanLeafInMargin = 10 /*风扇叶距离内部*/
    private val fanLeafOutMargin = 20 /*风扇叶距离外部*/

    /**
     * 风扇旋转方向
     */
    private val mFanRotateDirection = 1

    /**
     * 风扇叶外圆边宽度
     */
    private var mFanCircleWidth = 8

    /**
     * 叶子风行周期
     */
    private var mLeafOnceCycleTime = 1500

    /**
     * 叶子数量
     */
    private var mLeafCount = 7

    /**
     * 100% 字体大小
     */
    private var mTextMaxSize = 0
    private val mMiddleAmplitude = 18 /*中等振幅大小*/
    private val mAmplitudeDisparity = 8 /*振幅差*/
    private var fanRotateAngel = 30 /*风扇当前的旋转角度*/
    private val FanScaleTime = 150 /*风扇及其字体缩放动画时间*/
    private var mFanLeafScaleValue = 1f /*风扇缩放比例*/
    private var mTextBaseLineY = 0

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    private fun init() {
        initPaint()
    }

    private fun initPaint() {
        mBgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mBgPaint!!.style = Paint.Style.FILL
        mBgPaint!!.color = mBackgroundColor
        mFanPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mFanPaint!!.style = Paint.Style.FILL
        mFanPaint!!.color = mFanColor
        mFanFillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mFanFillPaint!!.style = Paint.Style.FILL
        mFanFillPaint!!.color = mFanInColor
        mProgressPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mProgressPaint!!.style = Paint.Style.FILL
        mProgressPaint!!.color = mProgressColor
        mTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mTextPaint!!.color = mFanColor
        mTextPaint!!.textAlign = Paint.Align.CENTER
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w
        mHeight = h

        /*根据view的高度来计算其他值*/mProgressPadding = (mHeight * 1f / 8 + 0.5f).toInt()
        mFanCircleWidth = (mHeight * 1f / 16 + 0.5f).toInt()
        mTextMaxSize = (mHeight * 21f / 64 + 0.5f).toInt()
        mLeafWidth = (mHeight * 3f / 8 + 0.5f).toInt()
        val leftMargin = Math.sqrt(
            Math.pow(
                (mHeight / 2).toDouble(),
                2.0
            ) - Math.pow((mHeight / 2 - mProgressPadding).toDouble(), 2.0)
        )
        mSemicircleRadius = ((mHeight - mProgressPadding * 2f) / 2).toInt()
        mProgressBarWidth = (mWidth - mProgressPadding - mHeight / 2 - leftMargin).toInt()
        initShape()
        initLeafArray()
    }

    private fun initShape() {
        /*我们将会把画布移动到view的中心去画背景矩形*/
        mBgRect = RectF(
            (-mWidth / 2).toFloat(),
            (-mHeight / 2).toFloat(),
            (mWidth / 2).toFloat(),
            (mHeight / 2).toFloat()
        )
        mProgressRectF = RectF(0F, (-mSemicircleRadius).toFloat(), 0F, mSemicircleRadius.toFloat())
        mSemiCircleRectF = RectF(
            (-mSemicircleRadius).toFloat(),
            (-mSemicircleRadius).toFloat(),
            mSemicircleRadius.toFloat(),
            mSemicircleRadius.toFloat()
        )
        initFanLeafPath()
        initLeafPath()
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

    /**
     * 初始化风扇叶的路径 只包含一个风扇叶的路径
     */
    private fun initFanLeafPath() {
        /*风扇叶距离中心的高度*/
        val fanLeafTop = mHeight / 2 - mFanCircleWidth - fanLeafOutMargin / 2
        val fanLeafRectWidth = mHeight / 2 - mFanCircleWidth
        mFanLeafPath = Path()
        mFanLeafPath!!.moveTo(0f, -fanLeafInMargin.toFloat())
        mFanLeafPath!!.cubicTo(
            fanLeafRectWidth / 4f,
            -fanLeafRectWidth / 3f,
            fanLeafRectWidth / 2f,
            (-fanLeafRectWidth + fanLeafOutMargin / 2).toFloat(),
            0f,
            -fanLeafTop.toFloat()
        )
        mFanLeafPath!!.cubicTo(
            -fanLeafRectWidth / 2f, (-fanLeafRectWidth + fanLeafOutMargin / 2).toFloat(),
            -fanLeafRectWidth / 4f, -fanLeafRectWidth / 3f, 0f, -fanLeafInMargin.toFloat()
        )
        mFanLeafPath!!.close()
    }

    private fun initLeafArray() {
        if (mLeafPathArray == null) {
            mLeafPathArray = ArrayList()
        } else {
            mLeafPathArray!!.clear()
        }
        for (i in 0 until mLeafCount) {
            val leaf = Leaf()
            leaf.angle = mRandom.nextInt(360)
            leaf.direction = mRandom.nextInt(2)
            val randomType = mRandom.nextInt(3)

            /*随时类型－ 随机振幅*/
            var type = StartType.MIDDLE
            when (randomType) {
                0 -> {
                }
                1 -> type = StartType.LITTLE
                2 -> type = StartType.BIG
                else -> {
                }
            }
            leaf.type = type
            leaf.startTime = System.currentTimeMillis() + mRandom.nextInt(mLeafOnceCycleTime)
            mLeafPathArray!!.add(leaf)
        }
    }

    internal class Leaf {
        var x = 0
        var y = 0
        var angle = 0
        var startTime: Long = 0
        var direction /*旋转方向*/ = 0
        var type: StartType? = null
    }

    enum class StartType {
        LITTLE, MIDDLE, BIG
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        println("绘制中    ${mLeafPathArray?.size} ${mIsFinish}")

        if(mIsFinish == false && mLeafPathArray?.size == 0){
            initLeafArray()
        }
        drawBackground(canvas)
        drawLeaf(canvas)
        drawProgress(canvas)
        if (mIsFinish) {
            drawFan(canvas, true)
            mLeafPathArray?.clear()
            invalidate()
        } else {
            drawFan(canvas, false)
            invalidate()
        }
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
     * 设置叶子的坐标
     */
    private fun setLeafLocation(leaf: Leaf) {
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
    private fun getLocationY(leaf: Leaf): Int {
        // y = A(wx+Q)+h
        val w = (2.toFloat() * Math.PI / mProgressBarWidth).toFloat()
        var a = mMiddleAmplitude.toFloat()
        when (leaf.type) {
            StartType.LITTLE ->                 /*小振幅 ＝ 中等振幅 － 振幅差*/a =
                (mMiddleAmplitude - mAmplitudeDisparity).toFloat()
            StartType.MIDDLE -> a = mMiddleAmplitude.toFloat()
            StartType.BIG ->                 /*小振幅 ＝ 中等振幅 + 振幅差*/a =
                (mMiddleAmplitude + mAmplitudeDisparity).toFloat()
            else -> {
            }
        }
        return (a * Math.sin((w * leaf.x).toDouble())).toInt() + mSemicircleRadius * 3 / 4
    }

    private fun drawFan(canvas: Canvas, scale: Boolean) {
        canvas.save()
        /*移动到风扇中心*/canvas.translate((mWidth - mHeight / 2).toFloat(), (mHeight / 2).toFloat())
        /*绘制外圆*/canvas.drawCircle(0f, 0f, (mHeight / 2).toFloat(), mFanPaint!!)
        /*绘制内圆*/canvas.drawCircle(
            0f,
            0f,
            (mHeight / 2 - mFanCircleWidth).toFloat(),
            mFanFillPaint!!
        )

        /*在执行缩放动画*/if (scale) {
            canvas.save() /*@1*/
            /*缩放画布*/canvas.scale(mFanLeafScaleValue, mFanLeafScaleValue)
        }

        /*风扇缩放比例小于30%的时候，不进行绘制*/if (mFanLeafScaleValue > 0.3) {
            canvas.drawCircle(0f, 0f, mFanCenterRadius.toFloat(), mFanPaint!!)
            canvas.rotate(-fanRotateAngel.toFloat())
            for (i in 0..3) {
                canvas.drawPath(mFanLeafPath!!, mFanPaint!!)
                canvas.rotate(90f)
            }
        }

        /*在执行缩放动画*/if (scale) {
            canvas.restore() /*还原@1处的画布状态*/

            /*缩放比例小于0.5的时候，开始绘制100%文字*/if (mFanLeafScaleValue < 0.5f) {
                draw100Text(canvas)
            }

            /*比例未达到0的时候，更新画布 继续动画*/if (mFanLeafScaleValue > 0) {
                postDelayed({ invalidate() }, (FanScaleTime / 10).toLong())
                mFanLeafScaleValue -= 0.05f
            }
        }

        /*更新风扇旋转值*/updateFanRotate(1)
        canvas.restore()
    }

    fun stopAnimator(){
        mFanLeafScaleValue = 0.4f
    }

    /**
     * 字体居中绘制处理
     */
    private fun initTextBaseLine() {
        val fontMetrics = mTextPaint!!.fontMetrics
        val top = fontMetrics.top /*为基线到字体上边框的距离*/
        val bottom = fontMetrics.bottom /*为基线到字体下边框的距离*/

        /*基线中间点的y轴计算公式*/mTextBaseLineY =
            (mSemiCircleRectF!!.centerY() - top / 2 - bottom / 2).toInt()
    }

    private fun draw100Text(canvas: Canvas) {
        /*设置字体的大小为： （1-风扇的缩放比例）*/
        mTextPaint!!.textSize = mTextMaxSize * (1 - mFanLeafScaleValue)
        mTextPaint!!.typeface = Typeface.DEFAULT_BOLD
        initTextBaseLine()
        canvas.drawText(
            number.toString(),
            mSemiCircleRectF!!.centerX(),
            mTextBaseLineY.toFloat(),
            mTextPaint!!
        )
    }

    /**
     * 绘制进度条
     */
    private fun drawProgress(canvas: Canvas) {
        /*获取当前进度条的宽度*/
        val currentProgressWidth = mProgressBarWidth * mCurrentProgress * 1.0f / mTotalProgress
        canvas.save()
        /*移动到左边半圆的圆心*/canvas.translate(
            (mSemicircleRadius + mProgressPadding).toFloat(),
            (mHeight / 2).toFloat()
        )

        /*进度还在半圆里面的时候，只花半圆*/if (currentProgressWidth > 0 && currentProgressWidth < mSemicircleRadius) {
            /*计算弧度夹角*/
            val degrees =
                Math.toDegrees(Math.acos(((mSemicircleRadius - currentProgressWidth) * 1f / mSemicircleRadius).toDouble()))
                    .toFloat()
            canvas.drawArc(
                mSemiCircleRectF!!, 180 - degrees, 2 * degrees,
                false, mProgressPaint!!
            )
        } else if (currentProgressWidth >= mSemicircleRadius) {
            /*进度条大于半圆的时候，需要绘制半圆加矩形*/
            canvas.drawArc(mSemiCircleRectF!!, 90f, 180f, false, mProgressPaint!!)
            mProgressRectF!!.right = currentProgressWidth - mSemicircleRadius
            canvas.drawRect(mProgressRectF!!, mProgressPaint!!)
        }
        canvas.restore()
    }

    /**
     * 绘制背景圆角矩形
     */
    private fun drawBackground(canvas: Canvas) {
        canvas.save()
        canvas.translate((mWidth / 2).toFloat(), (mHeight / 2).toFloat())
        canvas.drawRoundRect(
            mBgRect!!,
            (mHeight / 2).toFloat(),
            (mHeight / 2).toFloat(),
            mBgPaint!!
        )
        canvas.restore()
    }

    /**
     * 设置当前进度
     */
    fun setCurrentProgress(currentProgress: Int) {
        /*进度达到最大值的时候，标记完成，启动风扇缩小动画*/
        if (currentProgress >= mTotalProgress) {
            mIsFinish = true
        } else {
            mIsFinish = false
            mFanLeafScaleValue = 1f
        }
        if(currentProgress == number){
            mIsFinish = true
        }
        /*有进度的时候，加快风扇的旋转*/updateFanRotate(7)
        mCurrentProgress = currentProgress
        postInvalidate()
    }

    /**
     * 更新风扇旋转角度
     */
    private fun updateFanRotate(margin: Int) {
        fanRotateAngel += margin * mFanRotateDirection
        if (fanRotateAngel == 360) {
            fanRotateAngel = 0
        }
    }

    /*-------------set/get---------------*/
    var totalProgress: Int
        get() = mTotalProgress
        set(totalProgress) {
            mTotalProgress = totalProgress
            postInvalidate()
        }
    var bgRectColor: Int
        get() = mBackgroundColor
        set(bgColor) {
            mBackgroundColor = bgColor
            mBgPaint!!.color = mBackgroundColor
            postInvalidate()
        }
    var progressColor: Int
        get() = mProgressColor
        set(progressColor) {
            mProgressColor = progressColor
            mProgressPaint!!.color = mProgressColor
            postInvalidate()
        }
    var fanColor: Int
        get() = mFanColor
        set(fanColor) {
            mFanColor = fanColor
            mFanPaint!!.color = mFanColor
            postInvalidate()
        }
    var fanInColor: Int
        get() = mFanInColor
        set(fanInColor) {
            mFanInColor = fanInColor
            mFanFillPaint!!.color = mFanInColor
            postInvalidate()
        }
    var leafOnceCycleTime: Int
        get() = mLeafOnceCycleTime
        set(leafOnceCycleTime) {
            mLeafOnceCycleTime = leafOnceCycleTime
            postInvalidate()
        }
    var leafCount: Int
        get() = mLeafCount
        set(leafCount) {
            mLeafCount = leafCount
            initLeafArray()
            postInvalidate()
        }

    fun setNumber(num:Int){
        number = num
    }
}