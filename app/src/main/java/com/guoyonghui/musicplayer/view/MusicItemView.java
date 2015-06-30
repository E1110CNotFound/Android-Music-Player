package com.guoyonghui.musicplayer.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.RelativeLayout;

import com.guoyonghui.musicplayer.R;

/**
 * Created by 永辉 on 2015/6/29.
 */
public class MusicItemView extends RelativeLayout {

    /**
     * 分割线高度
     */
    private int mDividerHeight;

    /**
     * 分割线颜色
     */
    private int mDividerColor;

    /**
     * 白色Paint
     */
    private Paint mWhitePaint;

    /**
     * 分割线Paint
     */
    private Paint mDividerPaint;

    public MusicItemView(Context context) {
        super(context);

        initialize(null);
    }

    public MusicItemView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initialize(attrs);
    }

    public MusicItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initialize(attrs);
    }

    private void initialize(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MusicItemView);

        //设置分割线高度的默认值
        mDividerHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 1, getResources().getDisplayMetrics());

        //设置分割线颜色的默认值
        mDividerColor = getResources().getColor(R.color.divider_background);

        if (a != null) {
            try {
                mDividerHeight = (int) a.getDimension(R.styleable.MusicItemView_item_divider_height, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 1, getResources().getDisplayMetrics()));

                mDividerColor = a.getColor(R.styleable.MusicItemView_item_divider_background, getResources().getColor(R.color.divider_background));
            } finally {
                a.recycle();
            }
        }

        //初始化白色Paint
        mWhitePaint = new Paint();
        mWhitePaint.setColor(getResources().getColor(android.R.color.white));

        //根据分割线颜色初始化分割线Paint
        mDividerPaint = new Paint();
        mDividerPaint.setColor(mDividerColor);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        //绘制分割线
        canvas.drawRect(0, 0, width, mDividerHeight, mWhitePaint);
        canvas.drawRect(0, height - mDividerHeight, width, height, mDividerPaint);
    }
}
