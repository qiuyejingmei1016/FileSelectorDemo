package com.lcw.library.imagepicker;

import android.app.Activity;
import android.content.Intent;

import com.lcw.library.imagepicker.activity.ImagePickerActivity;
import com.lcw.library.imagepicker.manager.PickerConfigManager;

import java.util.ArrayList;

/**
 * 统一调用入口
 * Create by: chenWei.li
 * Date: 2018/8/26
 * Time: 下午6:31
 * Email: lichenwei.me@foxmail.com
 */
public class ImagePicker {

    public static final String EXTRA_SELECT_IMAGES = "selectItems";

    private static volatile ImagePicker mImagePicker;

    private ImagePicker() {
    }

    /**
     * 创建对象
     *
     * @return
     */
    public static ImagePicker getInstance() {
        if (mImagePicker == null) {
            synchronized (ImagePicker.class) {
                if (mImagePicker == null) {
                    mImagePicker = new ImagePicker();
                }
            }
        }
        return mImagePicker;
    }

    /**
     * 设置标题
     *
     * @param title
     * @return
     */
    public ImagePicker setTitle(String title) {
        PickerConfigManager.getInstance().setTitle(title);
        return mImagePicker;
    }

    /**
     * 是否支持相机
     *
     * @param showCamera
     * @return
     */
    public ImagePicker showCamera(boolean showCamera) {
        PickerConfigManager.getInstance().setShowCamera(showCamera);
        return mImagePicker;
    }

    /**
     * 显示相机时，拍照方式（自定义拍证件照还是原生拍照）
     *
     * @param cameraTakeType IDCardCamera.TYPE_IDCARD_FRONT 2身份证反面   IDCardCamera.TYPE_IDCARD_FRONT = 1身份证正面  其他值表示原生拍照
     */
    public ImagePicker setCameraTakeType(int cameraTakeType) {
        PickerConfigManager.getInstance().setCameraTakeType(cameraTakeType);
        return mImagePicker;
    }

    /**
     * 是否展示图片
     *
     * @param showImage
     * @return
     */
    public ImagePicker showImage(boolean showImage) {
        PickerConfigManager.getInstance().setShowImage(showImage);
        return mImagePicker;
    }

    /**
     * 是否展示视频
     *
     * @param showVideo
     * @return
     */
    public ImagePicker showVideo(boolean showVideo) {
        PickerConfigManager.getInstance().setShowVideo(showVideo);
        return mImagePicker;
    }

    /**
     * 图片最大选择数
     *
     * @param maxCount
     * @return
     */
    public ImagePicker setMaxCount(int maxCount) {
        PickerConfigManager.getInstance().setMaxCount(maxCount);
        return mImagePicker;
    }

    /**
     * 设置单类型选择（只能选图片或者视频）
     *
     * @param isSingleType
     * @return
     */
    public ImagePicker setSingleType(boolean isSingleType) {
        PickerConfigManager.getInstance().setSingleType(isSingleType);
        return mImagePicker;
    }

    /**
     * 设置图片选择历史记录
     *
     * @param imagePaths
     * @return
     */
    public ImagePicker setImagePaths(ArrayList<String> imagePaths) {
        PickerConfigManager.getInstance().setImagePaths(imagePaths);
        return mImagePicker;
    }

    /**
     * 启动
     *
     * @param activity
     */
    public void start(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, ImagePickerActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }
}