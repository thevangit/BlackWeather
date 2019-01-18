package com.blackweather.android.customView;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.BounceInterpolator;

import com.blackweather.android.BlackApplication;
import com.blackweather.android.R;
import com.blackweather.android.utilities.DensityUtil;

public class CircleView extends View {

    private float mNumerator = 1;
    private float mDenominator = 1;
    private Paint mPaint;
    private int mColor;
    private float mStrokeWidth;
    private float mSweepAngle;
    private ValueAnimator mValueAnimator;

    public CircleView(Context context) {
        super(context);
        init();
    }

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 从xml文件中获取控件的自定义属性,记得回收TypedArray对象
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleView);
        mColor = typedArray.getColor(R.styleable.CircleView_paintColor, 0);
        mStrokeWidth = typedArray.getDimension(R.styleable.CircleView_strokeWidth, 0);
        typedArray.recycle();
        init();
    }

    private void init() {
        mPaint = new Paint();
        mSweepAngle = (mNumerator / mDenominator) * 360;
        mValueAnimator = ValueAnimator.ofFloat(0,1);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float curAngle = (float) animation.getAnimatedValue()
                        * getSweepAngle();
                setSweepAngle(curAngle);
                invalidate();
            }
        });
        mValueAnimator.setInterpolator(new BounceInterpolator());
        mValueAnimator.setDuration(2000);
        mValueAnimator.setRepeatCount(0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(mColor);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setStyle(Paint.Style.STROKE); //描边
        canvas.drawArc(getPaddingTop(), getPaddingTop(), getWidth() - getPaddingRight(),
                getHeight() - getPaddingBottom(), 90, mSweepAngle,
                false, mPaint);
    }

    /**
     * 刷新数据并且会触发动画
     *
     * @param numerator 分子
     * @param denominator 分母不能为0，若为0则默认为1
     */
    public void setData(float numerator, float denominator) {
        mNumerator = numerator;
        if (denominator != 0) mDenominator = denominator;
        mValueAnimator.start();
    }

    private void setSweepAngle(float sweepAngle){
        mSweepAngle = sweepAngle;
    }

    private void setStrokeWidth(int dp) {
        mStrokeWidth = DensityUtil.dip2px(BlackApplication.getContext(),dp);
    }

    private void setPaintColor(int colorRes) {
        mColor = colorRes;
    }

    private float getSweepAngle() {
        return (mNumerator / mDenominator) * 360;
    }

}
