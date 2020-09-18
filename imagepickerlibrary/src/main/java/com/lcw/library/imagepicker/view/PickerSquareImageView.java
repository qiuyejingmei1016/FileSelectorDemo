package com.lcw.library.imagepicker.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * 正方形ImageView
 * Create by: chenWei.li
 * Date: 2018/9/1
 * Time: 下午10:12
 * Email: lichenwei.me@foxmail.com
 */
public class PickerSquareImageView extends android.support.v7.widget.AppCompatImageView {

    public PickerSquareImageView(Context context) {
        this(context, null);
    }

    public PickerSquareImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PickerSquareImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}