package com.yyh.fileselector.util;

import java.io.File;
import java.io.FileFilter;

/**
 * @describe: 文件夹过滤(去除隐藏文件 空文件过滤器)
 * @author: yyh
 * @createTime: 2019/8/6 13:58
 * @className: EmptyFileFilter
 */
public class EmptyFileFilter implements FileFilter {

    @Override
    public boolean accept(File file) {
        return file != null && !file.getName().startsWith(".") && file.length() != 0;
    }
}