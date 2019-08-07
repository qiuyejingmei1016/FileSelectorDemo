package com.yyh.fileselector.logic.bean;

import java.io.File;
import java.util.HashMap;

/**
 * @describe: 已选文件全局配置类
 * @author: yyh
 * @createTime: 2019/8/6 14:03
 * @className: SelectedFiles
 */
public class SelectedFiles {
    public static HashMap<String, File> files = null;//文件路径-文件
    public static long totalFileSize = 0;//选中文件大小总和
}