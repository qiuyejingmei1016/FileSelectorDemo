package com.lcw.library.imagepicker.data;

import java.util.ArrayList;

/**
 * 图片文件夹实体类
 * Create by: chenWei.li
 * Date: 2018/8/23
 * Time: 上午12:56
 * Email: lichenwei.me@foxmail.com
 */
public class PickerMediaFolder {

    private int folderId;
    private String folderName;
    private String folderCover;
    private boolean isCheck;
    private ArrayList<PickerMediaFile> mediaFileList;

    public PickerMediaFolder(int folderId, String folderName, String folderCover, ArrayList<PickerMediaFile> mediaFileList) {
        this.folderId = folderId;
        this.folderName = folderName;
        this.folderCover = folderCover;
        this.mediaFileList = mediaFileList;
    }

    public int getFolderId() {
        return folderId;
    }

    public void setFolderId(int folderId) {
        this.folderId = folderId;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getFolderCover() {
        return folderCover;
    }

    public void setFolderCover(String folderCover) {
        this.folderCover = folderCover;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public ArrayList<PickerMediaFile> getMediaFileList() {
        return mediaFileList;
    }

    public void setMediaFileList(ArrayList<PickerMediaFile> mediaFileList) {
        this.mediaFileList = mediaFileList;
    }
}