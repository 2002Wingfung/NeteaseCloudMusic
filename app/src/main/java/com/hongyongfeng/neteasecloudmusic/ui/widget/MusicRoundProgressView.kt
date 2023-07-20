package com.hongyongfeng.neteasecloudmusic.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.hongyongfeng.neteasecloudmusic.R


/**
 * 圆形进度条
 *
 * @author llw
 */
class MusicRoundProgressView(context: Context, attrs: AttributeSet?) :
    View(context, attrs) {
    /**
     * 画笔
     */
    private val mPaint: Paint

    /**
     * 画笔颜色
     */
    private val mPaintColor: Int

    /**
     * 半径
     */
    private val mRadius: Float

    /**
     * 圆环半径
     */
    private val mRingRadius: Float

    /**
     * 圆环宽度
     */
    private val mStrokeWidth: Float

    /**
     * 圆心 X 轴坐标
     */
    private var mCenterX = 0

    /**
     * 圆心 Y 轴坐标
     */
    private var mCenterY = 0

    /**
     * 总进度
     */
    private var mTotalProgress = 0

    /**
     * 当前进度
     */
    private var mProgress = 0

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundProgressView)
        //半径
        mRadius = typedArray.getDimension(R.styleable.RoundProgressView_radius, 40f)
        //宽度
        mStrokeWidth = typedArray.getDimension(R.styleable.RoundProgressView_strokeWidth, 5f)
        //颜色
        mPaintColor = typedArray.getColor(R.styleable.RoundProgressView_strokeColor, -0x1)
        //圆环半径 = 半径 + 圆环宽度的1/2
        mRingRadius = mRadius + mStrokeWidth / 2

        //画笔
        mPaint = Paint()
        mPaint.isAntiAlias = true
        mPaint.color = mPaintColor
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = mStrokeWidth
    }

    override fun onDraw(canvas: Canvas) {
        mCenterX = width / 2
        mCenterY = height / 2
        if (mProgress > 0) {
            val rectF = RectF()
            rectF.left = mCenterX - mRingRadius
            rectF.top = mCenterY - mRingRadius
            rectF.right = mRingRadius * 2 + (mCenterX - mRingRadius)
            rectF.bottom = mRingRadius * 2 + (mCenterY - mRingRadius)
            canvas.drawArc(rectF, -90f, mProgress.toFloat() / mTotalProgress * 360, false, mPaint)
        }
    }

    /**
     * 设置进度
     */
    fun setProgress(progress: Int, totalProgress: Int) {
        mProgress = progress
        mTotalProgress = totalProgress
        //重绘
        postInvalidate()
    }
}