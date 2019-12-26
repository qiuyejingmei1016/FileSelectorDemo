package com.lcw.library.imagepicker;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.lcw.library.imagepicker.utils.PickerImageLoader;

/**
 * 实现自定义图片加载
 * Create by: chenWei.li
 * Date: 2018/8/30
 * Time: 下午11:10
 * Email: lichenwei.me@foxmail.com
 */
public class PickerGlideLoader implements PickerImageLoader {

    private RequestOptions mOptions = new RequestOptions()
            .centerCrop()
            .format(DecodeFormat.PREFER_RGB_565)
            .placeholder(R.mipmap.sdk_ico_image_default)
            .error(R.mipmap.sdk_ico_image_default);

    private RequestOptions mPreOptions = new RequestOptions()
            .skipMemoryCache(true)
            .error(R.mipmap.sdk_ico_image_default);

    private Context mContext;

    public PickerGlideLoader(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void loadImage(ImageView imageView, String imagePath) {
        //小图加载
        Glide.with(imageView.getContext()).load(imagePath).apply(mOptions).into(imageView);
    }

    @Override
    public void loadPreImage(ImageView imageView, String imagePath) {
        //大图加载
        Glide.with(imageView.getContext()).load(imagePath).apply(mPreOptions).into(imageView);

    }

    @Override
    public void clearMemoryCache() {
        //清理缓存
        if (mContext != null) {
            Glide.get(mContext).clearMemory();
        }
    }
}
