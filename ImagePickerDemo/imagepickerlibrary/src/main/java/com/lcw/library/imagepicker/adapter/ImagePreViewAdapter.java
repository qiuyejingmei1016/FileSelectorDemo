package com.lcw.library.imagepicker.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.lcw.library.imagepicker.data.PickerMediaFile;
import com.lcw.library.imagepicker.manager.PickerConfigManager;
import com.lcw.library.imagepicker.view.PickerPinchImageView;

import java.util.LinkedList;
import java.util.List;

/**
 * 大图浏览适配器（并不是比较好的方案，后期会用RecyclerView来实现）
 * Create by: chenWei.li
 * Date: 2018/8/30
 * Time: 上午12:57
 * Email: lichenwei.me@foxmail.com
 */
public class ImagePreViewAdapter extends PagerAdapter {

    private Context mContext;
    private List<PickerMediaFile> mMediaFileList;

    LinkedList<PickerPinchImageView> viewCache = new LinkedList<PickerPinchImageView>();

    public ImagePreViewAdapter(Context context, List<PickerMediaFile> mediaFileList) {
        this.mContext = context;
        this.mMediaFileList = mediaFileList;
    }

    @Override
    public int getCount() {
        return mMediaFileList == null ? 0 : mMediaFileList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        PickerPinchImageView imageView;
        if (viewCache.size() > 0) {
            imageView = viewCache.remove();
            imageView.reset();
        } else {
            imageView = new PickerPinchImageView(mContext);
        }
        try {
            PickerConfigManager.getInstance().getImageLoader().loadPreImage(imageView, mMediaFileList.get(position).getPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        PickerPinchImageView imageView = (PickerPinchImageView) object;
        container.removeView(imageView);
        viewCache.add(imageView);
    }
}
