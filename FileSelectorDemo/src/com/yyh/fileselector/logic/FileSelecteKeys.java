package com.yyh.fileselector.logic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.yyh.fileselector.ui.activity.FileHomeActivity;

/**
 * @describe: 文件选择配置定义类
 * @author: yyh
 * @createTime: 2019/8/6 14:04
 * @className: FileSelecteKeys
 */
public class FileSelecteKeys {

    public static final int FILE_SELECT_REQUEST_CODE = 10000;
    private static final String FILE_SELECT_RESULT_FLAG = "file_select_flag";
    public static final String FILE_SELECT_RESULT = "file_select_result";
    public static final String FILE_SELECT_ISMULSELECT = "file_select_ismulselect";
    public static String FILE_ROOT_PATH = "";//需要选择存储目录的路径信息

    /**
     * 调整文件选择界面
     *
     * @param context      上下文对象
     * @param requestCode  请求码
     * @param selectResult 选择结果，用于获取所选择文件
     * @param isMulSelect  是否支持多选
     */
    public static void actionSelectFile(Context context, int requestCode, String selectResult, boolean isMulSelect) {
        Intent intent = new Intent(context, FileHomeActivity.class);
        intent.putExtra(FILE_SELECT_RESULT_FLAG, selectResult);
        intent.putExtra(FILE_SELECT_ISMULSELECT, isMulSelect);
        ((Activity) context).startActivityForResult(intent, requestCode);
    }
}