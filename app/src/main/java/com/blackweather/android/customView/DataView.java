package com.blackweather.android.customView;

import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatTextView;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.support.v7.widget.AppCompatTextView;
import android.view.animation.DecelerateInterpolator;

import com.blackweather.android.R;


public class DataView extends AppCompatTextView {

    private ValueAnimator mAnimator;
    private float mValue;
    private String mUnit;
    private boolean isRound;

    public DataView(Context context) {
        super(context);
        init();
    }

    public DataView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DataView);
        isRound = typedArray.getBoolean(R.styleable.DataView_isRound, false);
        typedArray.recycle();
    }

    private void init() {
        mAnimator = ValueAnimator.ofFloat(0, 1);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float curValue = (float) animation.getAnimatedValue() * mValue;
                if (isRound) {
                    setText(String.format("%.0f", curValue) + getUnit());
                } else {
                    setText(String.format("%.1f", curValue) + getUnit());
                }invalidate();
            }
        });
        mAnimator.setDuration(1000);
        mAnimator.setRepeatCount(0);
        mAnimator.setInterpolator(new DecelerateInterpolator());
    }

    public void setData(float data, String unit) {
        mValue = data;
        if (unit != null) {
            mUnit = unit;
        }
        mAnimator.start();
    }

    /**
     * 如果unit没有设置，则不显示单位
     */
    private String getUnit() {
        if (mUnit == null || mUnit.equals("")) {
            return "";
        } else {
            return mUnit;
        }
    }


}