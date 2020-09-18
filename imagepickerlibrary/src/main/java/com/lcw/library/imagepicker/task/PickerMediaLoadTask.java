package com.lcw.library.imagepicker.task;

import android.content.Context;

import com.lcw.library.imagepicker.data.PickerMediaFile;
import com.lcw.library.imagepicker.listener.PickerMediaLoadCallback;
import com.lcw.library.imagepicker.loader.PickerImageScanner;
import com.lcw.library.imagepicker.loader.PickerMediaHandler;
import com.lcw.library.imagepicker.loader.PickerVideoScanner;

import java.util.ArrayList;

/**
 * 媒体库扫描任务（图片、视频）
 * Create by: chenWei.li
 * Date: 2018/8/25
 * Time: 下午12:31
 * Email: lichenwei.me@foxmail.com
 */
public class PickerMediaLoadTask implements Runnable {

    private Context mContext;
    private PickerImageScanner mImageScanner;
    private PickerVideoScanner mVideoScanner;
    private PickerMediaLoadCallback mMediaLoadCallback;

    public PickerMediaLoadTask(Context context, PickerMediaLoadCallback mediaLoadCallback) {
        this.mContext = context;
        this.mMediaLoadCallback = mediaLoadCallback;
        mImageScanner = new PickerImageScanner(context);
        mVideoScanner = new PickerVideoScanner(context);
    }

    @Override
    public void run() {
        //存放所有照片
        ArrayList<PickerMediaFile> imageFileList = new ArrayList<>();
        //存放所有视频
        ArrayList<PickerMediaFile> videoFileList = new ArrayList<>();

        if (mImageScanner != null) {
            imageFileList = mImageScanner.queryMedia();
        }
        if (mVideoScanner != null) {
            videoFileList = mVideoScanner.queryMedia();
        }

        if (mMediaLoadCallback != null) {
            mMediaLoadCallback.loadMediaSuccess(PickerMediaHandler.getMediaFolder(mContext, imageFileList, videoFileList));
        }
    }
}