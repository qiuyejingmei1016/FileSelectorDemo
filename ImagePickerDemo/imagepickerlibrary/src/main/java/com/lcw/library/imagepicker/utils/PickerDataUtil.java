package com.lcw.library.imagepicker.utils;

import com.lcw.library.imagepicker.data.PickerMediaFile;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据保存类
 * （随着Android版本的提高，对Intent传递数据的大小也做了不同的限制，在一些高版本或者低配机型上可能会发生
 * android.os.TransactionTooLargeException: data parcel size xxxx bytes异常，故用此方案适配）
 * Create by: chenWei.li
 * Date: 2019/1/24
 * Time: 12:38 AM
 * Email: lichenwei.me@foxmail.com
 */
public class PickerDataUtil {

    private static volatile PickerDataUtil mDataUtilInstance;
    private List<PickerMediaFile> mData = new ArrayList<>();

    private PickerDataUtil() {
    }

    public static PickerDataUtil getInstance() {
        if (mDataUtilInstance == null) {
            synchronized (PickerDataUtil.class) {
                if (mDataUtilInstance == null) {
                    mDataUtilInstance = new PickerDataUtil();
                }
            }
        }
        return mDataUtilInstance;
    }

    public List<PickerMediaFile> getMediaData() {
        return mData;
    }

    public void setMediaData(List<PickerMediaFile> data) {
        this.mData = data;
    }
}