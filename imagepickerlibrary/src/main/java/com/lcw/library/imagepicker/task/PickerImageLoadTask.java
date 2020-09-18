package com.lcw.library.imagepicker.task;

import android.content.Context;

import com.lcw.library.imagepicker.data.PickerMediaFile;
import com.lcw.library.imagepicker.listener.PickerMediaLoadCallback;
import com.lcw.library.imagepicker.loader.PickerImageScanner;
import com.lcw.library.imagepicker.loader.PickerMediaHandler;

import java.util.ArrayList;

/**
 * 媒体库扫描任务（图片）
 * Create by: chenWei.li
 * Date: 2018/8/25
 * Time: 下午12:31
 * Email: lichenwei.me@foxmail.com
 */
public class PickerImageLoadTask implements Runnable {

    private Context mContext;
    private PickerImageScanner mImageScanner;
    private PickerMediaLoadCallback mMediaLoadCallback;

    public PickerImageLoadTask(Context context, PickerMediaLoadCallback mediaLoadCallback) {
        this.mContext = context;
        this.mMediaLoadCallback = mediaLoadCallback;
        mImageScanner = new PickerImageScanner(context);
    }

    @Override
    public void run() {
        //存放所有照片
        ArrayList<PickerMediaFile> imageFileList = new ArrayList<>();

        if (mImageScanner != null) {
            imageFileList = mImageScanner.queryMedia();
        }

        if (mMediaLoadCallback != null) {
            mMediaLoadCallback.loadMediaSuccess(PickerMediaHandler.getImageFolder(mContext, imageFileList));
        }
    }
}