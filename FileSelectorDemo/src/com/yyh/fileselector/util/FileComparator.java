package com.yyh.fileselector.util;

import java.io.File;
import java.util.Comparator;

/**
 * @describe: 文件夹及文件按字母排序规则类
 * @author: yyh
 * @createTime: 2019/8/6 13:58
 * @className: FileComparator
 */
public class FileComparator implements Comparator<File> {

    @Override
    public int compare(File file1, File file2) {
        //1.先比较文件夹，让文件夹排在列表的最前边，并且以A-Z的字典顺序排列
        if (file1.isDirectory() && file2.isDirectory()) {
            return file1.getName().compareToIgnoreCase(file2.getName());
        } else {
            if (file1.isDirectory() && file2.isFile()) {//2.比较文件夹和文件
                return -1;
            } else if (file1.isFile() && file2.isDirectory()) {//3.比较文件和文件夹
                return 1;
            } else {
                return file1.getName().compareToIgnoreCase(file2.getName());//4.比较文件
            }
        }
    }
}