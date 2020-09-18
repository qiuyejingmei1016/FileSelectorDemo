package com.lcw.library.imagepicker.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * 正方形RelativeLayout
 * Create by: chenWei.li
 * Date: 2018/9/1
 * Time: 下午10:12
 * Email: lichenwei.me@foxmail.com
 */
public class PickerSquareRelativeLayout extends RelativeLayout {

    public PickerSquareRelativeLayout(Context context) {
        this(context, null);
    }

    public PickerSquareRelativeLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PickerSquareRelativeLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}