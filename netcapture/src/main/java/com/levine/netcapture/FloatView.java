package com.levine.netcapture;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.PathInterpolator;
import android.widget.FrameLayout;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ScreenUtils;

public class FloatView extends FrameLayout {

    private View mView;
    private RectF mRectF;
    private long mDownTime;

    public FloatView(Context context) {
        super(context);
        init();
    }

    public FloatView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public FloatView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    private void init() {
//        setBackgroundColor(Color.parseColor("#80000000"));
        mView = View.inflate(getContext(), R.layout.view_float_btn, null);
        addView(mView);
        mView.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
        mView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        mView.setX(0);
        mView.setY(ScreenUtils.getScreenHeight() / 2 / 2);
        mRectF = new RectF();
    }

    public void setCoordinate(float x, float y) {
        mView.setX(x);
        mView.setY(y);
        mRectF.set(mView.getX(), mView.getY(), mView.getX() + mView.getMeasuredWidth(), mView.getY() + mView.getMeasuredHeight());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mRectF.set(mView.getX(), mView.getY(), mView.getX() + mView.getMeasuredWidth(), mView.getY() + mView.getMeasuredHeight());
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mRectF.contains(event.getX(), event.getY())) {
                    mDownTime = System.currentTimeMillis();
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (System.currentTimeMillis() - mDownTime > 100) {
                    mView.setX(event.getX() - mView.getMeasuredWidth() / 2);
                    mView.setY(event.getY() - mView.getMeasuredHeight() / 2);
                    NCP.coordinateX = mView.getX();
                    NCP.coordinateY = mView.getY();
                    mRectF.set(mView.getX(), mView.getY(), mView.getX() + mView.getMeasuredWidth(), mView.getY() + mView.getMeasuredHeight());
                }
                return true;
            case MotionEvent.ACTION_UP:
                if (System.currentTimeMillis() - mDownTime < 100) {
                    NetCaptureRecordActivity.launch(getContext());
                    return true;
                }
                fix();
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    /**
     * 位置校正
     */
    private void fix() {
        ValueAnimator animator = ValueAnimator.ofFloat(mView.getX(), 0);
        animator.addUpdateListener(animation -> {
            mView.setX((Float) animation.getAnimatedValue());
            NCP.coordinateX = mView.getX();
            NCP.coordinateY = mView.getY();
            mRectF.set(mView.getX(), mView.getY(), mView.getX() + mView.getMeasuredWidth(), mView.getY() + mView.getMeasuredHeight());
        });
        animator.setDuration(300);
        animator.start();
    }
}
