package com.hongyongfeng.neteasecloudmusic.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Wingfung Hung
 * 流式布局
 */
public class FlowLayout extends ViewGroup {

    /**
     * 用来记录描述有多少行View
     */
    private final List<Line> mLines = new ArrayList<>();
    private int mHorizontalSpace = 10;
    private int mVerticalSpace = 6;

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlowLayout(Context context) {
        super(context);
    }

    public void setSpace(int horizontalSpace, int verticalSpace) {
        this.mHorizontalSpace = horizontalSpace;
        this.mVerticalSpace = verticalSpace;
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //清空
        mLines.clear();
        //用来记录当前已经添加到了哪一行
        Line mCurrentLine = null;//获取父布局最大宽度
        int layoutWidth = MeasureSpec.getSize(widthMeasureSpec);
        // 获取行最大的宽度
        int maxLineWidth = layoutWidth - getPaddingLeft() - getPaddingRight();

        //测量孩子
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View v = getChildAt(i);
            //如果孩子不可见
            if (v.getVisibility() == GONE) {
                continue;
            }
            measureChild(v, widthMeasureSpec, heightMeasureSpec);
            // 往lines添加孩子
            if (mCurrentLine == null) {
                // 说明还没有开始添加孩子
                mCurrentLine = new Line(maxLineWidth, mHorizontalSpace);

                // 添加到 Lines中
                mLines.add(mCurrentLine);

                // 行中一个孩子都没有
                mCurrentLine.addView(v);
            } else {
                // 行中有孩子了
                boolean canAdd = mCurrentLine.canAdd(v);
                if (canAdd) {
                    mCurrentLine.addView(v);
                } else {
                    //装不下，换行
                    mCurrentLine = new Line(maxLineWidth, mHorizontalSpace);
                    mLines.add(mCurrentLine);
                    // 将view添加到line
                    mCurrentLine.addView(v);
                }

            }
        }
        // 设置自己的宽度和高度
        float allHeight = 0;
        for (int i = 0; i < mLines.size(); i++) {
            float mHeight = mLines.get(i).mHeight;
            // 加行高
            allHeight += mHeight;
            // 加间距
            if (i != 0) {
                allHeight += mVerticalSpace;
            }
        }
        int measuredHeight = (int) (allHeight + getPaddingTop() + getPaddingBottom() + 0.5f);
        setMeasuredDimension(layoutWidth, measuredHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // 给Child 布局---> 给Line布局

        int paddingLeft = getPaddingLeft();
        int offsetTop = getPaddingTop();
        for (int i = 0; i < mLines.size(); i++) {
            Line line = mLines.get(i);

            // 给行布局
            line.layout(paddingLeft, offsetTop);

            offsetTop += line.mHeight + mVerticalSpace;
        }
    }


    static class Line {
        // 属性
        private final List<View> mViews = new ArrayList<>();// 用来记录每一行有几个View
        private final float mMaxWidth;  // 行最大的宽度
        private float mUsedWidth;  // 已经使用了多少宽度
        private float mHeight; // 行的高度
        private final float mHorizontalSpace; // View和view之间的水平间距

        // 构造
        public Line(int maxWidth, int horizontalSpace) {
            this.mMaxWidth = maxWidth;
            this.mHorizontalSpace = horizontalSpace;
        }
        // 方法

        /**
         * 添加view，记录属性的变化
         *
         * @param view 每个TextView视图
         */
        public void addView(View view) {
            // 加载View的方法

            int size = mViews.size();
            int viewWidth = view.getMeasuredWidth();
            int viewHeight = view.getMeasuredHeight();
            // 计算宽和高
            if (size == 0) {
                // 说还没有添加View
                if (viewWidth > mMaxWidth) {
                    mUsedWidth = mMaxWidth;
                } else {
                    mUsedWidth = viewWidth;
                }
                mHeight = viewHeight;
            } else {
                // 多个view的情况
                mUsedWidth += viewWidth + mHorizontalSpace;
                mHeight = mHeight < viewHeight ? viewHeight : mHeight;
            }

            // 将View记录到集合中
            mViews.add(view);
        }

        /**
         * 用来判断是否可以将View添加到line中
         *
         * @param view 每个TextView视图
         * @return 加得进去则返回true，加不进去则返回false
         */
        public boolean canAdd(View view) {
            // 判断是否能添加View

            int size = mViews.size();

            if (size == 0) {
                return true;
            }

            int viewWidth = view.getMeasuredWidth();

            // 预计使用的宽度
            float planWidth = mUsedWidth + mHorizontalSpace + viewWidth;

            // 加不进去
            return planWidth < mMaxWidth;
        }

        /**
         * 给孩子布局
         *
         * @param offsetLeft 控件左边界偏离父布局左边界的距离
         * @param offsetTop 控件上边界偏离父布局上边界的距离
         */
        public void layout(int offsetLeft, int offsetTop) {
            // 给孩子布局

            int currentLeft = offsetLeft;

            int size = mViews.size();
            // 判断已经使用的宽度是否小于最大的宽度
            float extra ;
            float widthAvg = 0;
            if (mMaxWidth > mUsedWidth) {
                extra = mMaxWidth - mUsedWidth;
                widthAvg = extra / size;
            }

            for (int i = 0; i < size; i++) {
                View view = mViews.get(i);
                int viewWidth = view.getMeasuredWidth();
                int viewHeight = view.getMeasuredHeight();

                // 判断是否有富余
                if (widthAvg != 0) {
                    // 改变宽度
                    int newWidth = (int) (viewWidth + widthAvg + 0.5f);
                    int widthMeasureSpec = MeasureSpec.makeMeasureSpec(newWidth, MeasureSpec.EXACTLY);
                    int heightMeasureSpec = MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY);
                    view.measure(widthMeasureSpec, heightMeasureSpec);

                    viewWidth = view.getMeasuredWidth();
                    viewHeight = view.getMeasuredHeight();
                }

                // 布局
                //当前子控件在该行中的相对位置
                int left = currentLeft;
                int top = (int) (offsetTop + (mHeight - viewHeight) / 2 + 0.5f);
                int right = left + viewWidth;
                int bottom = top + viewHeight;
                //设置按钮的相对位置
                view.layout(left, top, right, bottom);

                //循环相加设置每个按钮的位置
                //设置下一个按钮的左边界位置
                currentLeft += viewWidth + mHorizontalSpace;
            }
        }

    }
}

