package com.blackweather.android.customView;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.BounceInterpolator;

import com.blackweather.android.R;

public class BarView extends View {

    private Paint mPaint;
    private ValueAnimator mValueAnimator;
    private float mNumerator = 1;
    private float mDenominator = 1;
    private float mStopX;
    private int mColor;
    private float mStrokeWidth;

    public BarView(Context context) {
        super(context);
        init();
    }

    public BarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BarView);
        mColor = typedArray.getColor(R.styleable.BarView_color, 0);
        mStrokeWidth = typedArray.getDimension(R.styleable.BarView_width, 0);
        typedArray.recycle();
        init();
    }

    private void init() {
        mPaint = new Paint();
        mStopX = (mNumerator / mDenominator) * getWidth();
        mValueAnimator = ValueAnimator.ofFloat(0,1);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float curX = (float) animation.getAnimatedValue()
                        * getStop();
                setStop(curX);
                invalidate();
            }
        });
        mValueAnimator.setInterpolator(new BounceInterpolator());
        mValueAnimator.setDuration(2000);
        mValueAnimator.setRepeatCount(0);
    }

    private float getStop() {
        return (mNumerator / mDenominator) * getWidth();
    }

    private void setStop(float stopX) {
        mStopX = stopX;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(mColor);
        mPaint.setStrokeWidth(getHeight());
        mPaint.setStyle(Paint.Style.STROKE); // 描边
        canvas.drawLine(0,getHeight() / 2,mStopX,getHeight() / 2, mPaint);
    }

    public void setData(float numerator, float denominator) {
        mNumerator = numerator;
        mDenominator = denominator;
        mValueAnimator.start();
    }
}
