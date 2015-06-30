package com.guoyonghui.musicplayer.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

/**
 * Created by 永辉 on 2015/6/29.
 */
public class ElasticListView extends ListView {

    private static final float SCALE_X = 0.98f;

    private static final float SCALE_Y = 0.9f;

    private static final long ANIMATION_DURATION = 100;

    private int mDownPosition;

    private View mDownView;

    private boolean mIsDown;

    public ElasticListView(Context context) {
        super(context);

        initialize();
    }

    public ElasticListView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initialize();
    }

    public ElasticListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initialize();
    }

    private void initialize() {
        setDividerHeight(0);

        setSelector(android.R.color.transparent);
    }

    @TargetApi(12)
    private void showDownAnimation() {
        mDownView = getChildAt(mDownPosition - getFirstVisiblePosition());
        if(mDownView != null && !mIsDown) {
            mDownView.animate().scaleX(SCALE_X).scaleY(SCALE_Y).setDuration(ANIMATION_DURATION);
            mIsDown = true;
        }
    }

    @TargetApi(12)
    private void recoverDownAnimation() {
        if(mDownView != null && mIsDown) {
            mDownView.animate().scaleX(1f).scaleY(1f).setDuration(ANIMATION_DURATION);
            mIsDown = false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int downX = (int) ev.getX();
                int downY = (int) ev.getY();

                mDownPosition = pointToPosition(downX, downY);

                showDownAnimation();
                break;
            case MotionEvent.ACTION_MOVE:
                recoverDownAnimation();
                break;
            case MotionEvent.ACTION_UP:
                recoverDownAnimation();
                break;

            default:
                break;
        }

        return super.onTouchEvent(ev);
    }
}
