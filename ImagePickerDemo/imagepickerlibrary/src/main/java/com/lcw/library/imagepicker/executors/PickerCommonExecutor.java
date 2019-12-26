package com.lcw.library.imagepicker.executors;

import android.support.annotation.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * 公用线程池，执行一些耗时操作
 * Create by: chenWei.li
 * Date: 2018/8/24
 * Time: 下午7:53
 * Email: lichenwei.me@foxmail.com
 */
public class PickerCommonExecutor {

    private static volatile PickerCommonExecutor mCommonExecutor;
    private ExecutorService mExecutorService;

    private PickerCommonExecutor() {
        mExecutorService = Executors.newCachedThreadPool(new ThreadFactory() {
            @Override
            public Thread newThread(@NonNull Runnable runnable) {
                Thread thread = new Thread(runnable);
                thread.setName("PickerCommonExecutor");
                return thread;
            }
        });
    }

    public static PickerCommonExecutor getInstance() {
        if (mCommonExecutor == null) {
            synchronized (PickerCommonExecutor.class) {
                if (mCommonExecutor == null) {
                    mCommonExecutor = new PickerCommonExecutor();
                }
            }
        }
        return mCommonExecutor;
    }

    public void execute(Runnable runnable) {
        mExecutorService.execute(runnable);
    }
}