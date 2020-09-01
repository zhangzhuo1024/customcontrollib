package com.example.customcontrollib;

import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.PathInterpolator;

/**
 * Created by zhangzhuo.
 * Blog: https://blog.csdn.net/zhangzhuo1024
 * Date: 2020/3/19
 */
public class IconView extends View {

    private static final int ICON_MIN_WIDTH = 20;
    private float mCycleWidth;
    private float mCycleTroke;
    private float mHalfCycleTroke;
    private float mRedPointWidth;
    private float mCycleCentreWidth;
    private float mRedSquareWidth;
    private float mRedSquareRadius;
    private float mMoveDistance;
    private float mRedLayoutRadius;
    private float mRedLayoutOffset;
    private float mRedLayoutWidth;
    private int mLayoutWidth;
    private int mLayoutHeight;
    private int mCycleColor;
    private int mRedPointColor;
    private Paint mCyclePaint;
    private Paint mRedPointPaint;
    private RectF mCycleRect;
    private RectF mRedPointRect;

    //代码中直接实例化自定义控件，会调用此构造函数，如 new IconView(this)
    public IconView(Context context) {
        this(context, null);
    }

    //在xml中布局使用时会调用此构造函数，attrs含有xml中添加的属性值
    public IconView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    //系统默认会调用前两种构造函数，我们统一将上两种调用到第三个，然后在第三个中初始化
    public IconView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.IconView);
        mCycleTroke = typedArray.getDimension(R.styleable.IconView_cycle_troke, getContext().getResources().getDimensionPixelSize(R.dimen.cycle_troke));
        mCycleWidth = typedArray.getDimension(R.styleable.IconView_cycle_width, getContext().getResources().getDimensionPixelSize(R.dimen.cycle_width));
        mCycleCentreWidth = mCycleWidth - mCycleTroke;
        mCycleColor = typedArray.getColor(R.styleable.IconView_cycle_color, getContext().getResources().getColor(R.color.cycle_color));
        mHalfCycleTroke = mCycleTroke / 2;

        mRedPointWidth = typedArray.getDimension(R.styleable.IconView_red_point_width, getContext().getResources().getDimensionPixelSize(R.dimen.red_point_width));
        mRedPointColor = typedArray.getColor(R.styleable.IconView_red_point_color, getContext().getResources().getColor(R.color.red_point_color));

        mRedSquareWidth = typedArray.getDimension(R.styleable.IconView_square_width, getContext().getResources().getDimensionPixelSize(R.dimen.square_width));
        mRedSquareRadius = typedArray.getDimension(R.styleable.IconView_square_radius, getContext().getResources().getDimensionPixelSize(R.dimen.square_radius));

        mMoveDistance = typedArray.getDimension(R.styleable.IconView_move_distance, getContext().getResources().getDimensionPixelSize(R.dimen.move_distance));

        mCyclePaint = new Paint();
        mCyclePaint.setAntiAlias(true);
        mCyclePaint.setStyle(Paint.Style.STROKE);
        mCyclePaint.setStrokeWidth(mCycleTroke);
        mCyclePaint.setColor(mCycleColor);

        mRedPointPaint = new Paint();
        mRedPointPaint.setAntiAlias(true);
        mRedPointPaint.setStyle(Paint.Style.FILL);
        mRedPointPaint.setColor(mRedPointColor);

        mCycleRect = new RectF(0, 0, 0, 0);
        mRedPointRect = new RectF(0, 0, 0, 0);

        mRedLayoutOffset = 0f;
        mRedLayoutWidth = mRedPointWidth;
        mRedLayoutRadius = mRedLayoutWidth / 2;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mLayoutWidth = w;
        this.mLayoutHeight = h;
        mCycleRect.left = (mLayoutWidth - mCycleCentreWidth) / 2;
        mCycleRect.right = mLayoutWidth - (mLayoutWidth - mCycleCentreWidth) / 2;

        setRectVerticalValue();
        mRedLayoutRadius = mRedLayoutWidth / 2;
    }

    private void setRectVerticalValue() {
        mCycleRect.top = mLayoutHeight - mCycleCentreWidth - mHalfCycleTroke - mRedLayoutOffset;
        mCycleRect.bottom = mLayoutHeight - mHalfCycleTroke - mRedLayoutOffset;

        mRedPointRect.top = mLayoutHeight - (mCycleWidth / 2 - mRedLayoutWidth / 2) - mRedLayoutWidth - mRedLayoutOffset;
        mRedPointRect.bottom = mLayoutHeight - (mCycleWidth / 2 - mRedLayoutWidth / 2) - mRedLayoutOffset;
        mRedPointRect.left = (mLayoutWidth - mRedLayoutWidth) / 2;
        mRedPointRect.right = mLayoutWidth - (mLayoutWidth - mRedLayoutWidth) / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRoundRect(mCycleRect, (mLayoutWidth - mCycleTroke) / 2, (mLayoutWidth - mCycleTroke) / 2, mCyclePaint);
        canvas.drawRoundRect(mRedPointRect, mRedLayoutRadius, mRedLayoutRadius, mRedPointPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int layoutWidth = measureWidthAndHeight(widthMeasureSpec, 0);
        int layoutHeight = measureWidthAndHeight(heightMeasureSpec, 1);
        this.mLayoutWidth = layoutWidth;
        this.mLayoutHeight = layoutHeight;
        setMeasuredDimension(layoutWidth, layoutHeight);
    }

    private int measureWidthAndHeight(int measureSpec, int type) {
        int model = MeasureSpec.getMode(measureSpec);//获得当前空间值的一个模式
        int size = MeasureSpec.getSize(measureSpec);//获得当前空间值的推荐值

        switch (model) {
            case MeasureSpec.EXACTLY://当你的控件设置了一个精确的值或者为match_parent时, 为这种模式
                //size = (int) paint.measureText(labels[0]);
                return size;
            case MeasureSpec.AT_MOST://当你的控件设置为wrap_content时，为这种模式
                if (type == 0) {
                    //测量宽度
                    size = ICON_MIN_WIDTH;
                    return size;
                } else {
                    //测量高度
                    size = ICON_MIN_WIDTH;
                    return size;
                }
            case MeasureSpec.UNSPECIFIED:
                Log.e("IconView", "未指定自定义控件高度");
                break;
        }
        Log.e("IconView", "onMeasure error");
        return 0;
    }

    public void startToMove() {
        TransAnimator redPointValue = new TransAnimator(mRedPointWidth, mRedPointWidth / 2, 0f);
        TransAnimator redSquareValue = new TransAnimator(mRedSquareWidth, mRedSquareRadius, mMoveDistance);
        ValueAnimator valueAnimator = ObjectAnimator.ofObject(new TransTypeEvaluator(), redPointValue, redSquareValue);
        valueAnimator.setDuration(5000);
        valueAnimator.setInterpolator(new PathInterpolator(0.3f, 0f, 0, 1));
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                TransAnimator animatedValue = (TransAnimator) animation.getAnimatedValue();
                mRedLayoutWidth = animatedValue.width;
                mRedLayoutRadius = animatedValue.radius;
                mRedLayoutOffset = animatedValue.offset;
                setRectVerticalValue();
                invalidate();
            }
        });
        mCyclePaint.setColor(mCycleColor);
        ValueAnimator cycleAlphaAnimator = ObjectAnimator.ofFloat(1.0f, 0);
        cycleAlphaAnimator.setDuration(1800);
        cycleAlphaAnimator.setInterpolator(new PathInterpolator(0.33f, 0, 0.67f, 1));
        final int cycleAlpha = mCyclePaint.getAlpha();
        cycleAlphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float v = (float) animation.getAnimatedValue() * cycleAlpha;
                mCyclePaint.setAlpha((int) v);
            }
        });
        valueAnimator.start();
        cycleAlphaAnimator.start();
    }

    public void ringChangeToRect(boolean isRingToRect) {
        TransAnimator redPointValue = new TransAnimator(mRedPointWidth, mRedPointWidth / 2, 0f);
        TransAnimator redSquareValue = new TransAnimator(mRedSquareWidth, mRedSquareRadius, 0f);
        ValueAnimator valueAnimator;
        if (isRingToRect) {
            valueAnimator = ObjectAnimator.ofObject(new TransTypeEvaluator(), redPointValue, redSquareValue);
        } else {
            valueAnimator = ObjectAnimator.ofObject(new TransTypeEvaluator(), redSquareValue, redPointValue);
        }
        valueAnimator.setDuration(5000);
        valueAnimator.setInterpolator(new PathInterpolator(0.3f, 0f, 0, 1));
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                TransAnimator animatedValue = (TransAnimator) animation.getAnimatedValue();
                mRedLayoutWidth = animatedValue.width;
                mRedLayoutRadius = animatedValue.radius;
                setRectVerticalValue();
                invalidate();
            }
        });
        valueAnimator.start();
    }

    private class TransAnimator {
        float width;
        float radius;
        float offset;

        TransAnimator(float width, float radius, float offset) {
            this.width = width;
            this.radius = radius;
            this.offset = offset;
        }
    }

    private class TransTypeEvaluator implements TypeEvaluator<TransAnimator> {
        TransAnimator transAnimator = new TransAnimator(0f, 0f, 0f);

        @Override
        public TransAnimator evaluate(float fraction, TransAnimator startValue, TransAnimator endValue) {
            transAnimator.width = startValue.width + (endValue.width - startValue.width) * fraction;
            transAnimator.radius = startValue.radius + (endValue.radius - startValue.radius) * fraction;
            transAnimator.offset = startValue.offset + (endValue.offset - startValue.offset) * fraction;
            return transAnimator;
        }
    }

}
