package com.yyh.fileselector.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.yyh.fileselector.R;
import com.yyh.fileselector.logic.FileSelecteKeys;
import com.yyh.fileselector.logic.bean.SelectedFiles;
import com.yyh.fileselector.util.PermissonUtils;
import com.yyh.fileselector.util.SDCardScanner;
import com.yyh.fileselector.util.StringUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @describe: 文件管理界面(内部存储 、 存储卡)
 * @author: yyh
 * @createTime: 2019/8/7 15:14
 * @className: FileHomeActivity
 */
public class FileHomeActivity extends Activity implements View.OnClickListener, PermissonUtils.PermissionCallback {

    private String mInternalPath;//内部存储路径
    private String mExternalPath;//外部存储路径(sd卡)

    private boolean mIsMulSelect;//是否支持多选标识

    private String[] mPermissions;//权限请求组

    private AlertDialog mPermissionDialog;//权限拒绝提示弹窗

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_select_home);
        initData();
        //请求权限
        requestPermission();
    }

    private void initData() {
        Intent intent = getIntent();
        this.mIsMulSelect = intent.getBooleanExtra(FileSelecteKeys.FILE_SELECT_ISMULSELECT, false);
    }

    /**
     * 请求权限
     */
    private void requestPermission() {
        mPermissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE};
        PermissonUtils.getInstance().setPermissionCallback(this);
        PermissonUtils.getInstance().needToFinsh(true).requestPermission(this, mPermissions);
    }

    private void initView() {
        View backView = this.findViewById(R.id.title_left_img_btn);
        backView.setVisibility(View.VISIBLE);
        backView.setOnClickListener(this);
        ((TextView) findViewById(R.id.title_text)).setText("全部文件");

        View internalView = findViewById(R.id.storage_internal_view);
        View externalView = findViewById(R.id.storage_external_view);
        View emptyTipView = findViewById(R.id.empty_tip_view);
        internalView.setOnClickListener(this);
        externalView.setOnClickListener(this);

        //获取内部存储路径信息
        List<String> paths = SDCardScanner.getExtSDCardPaths();
        if (paths != null && !paths.isEmpty()) {
            try {
                mInternalPath = paths.get(0);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("SDCardScanner", "Exception", e.fillInStackTrace());
            }
        }
        //获取外部存储路径信息
        mExternalPath = SDCardScanner.getStoragePath(this, true);
        if (StringUtil.isNullOrEmpty(mInternalPath) && StringUtil.isNullOrEmpty(mExternalPath)) {
            emptyTipView.setVisibility(View.VISIBLE);
            return;
        }
        if (StringUtil.isNullOrEmpty(mInternalPath)) {
            internalView.setVisibility(View.GONE);
        } else {
            internalView.setVisibility(View.VISIBLE);
        }
        if (StringUtil.isNullOrEmpty(mExternalPath)) {
            externalView.setVisibility(View.GONE);
        } else {
            externalView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.title_left_img_btn) {
            finish();
            return;
        } else if (viewId == R.id.storage_internal_view) {
            FileSelecteKeys.FILE_ROOT_PATH = mInternalPath;
        } else if (viewId == R.id.storage_external_view) {
            FileSelecteKeys.FILE_ROOT_PATH = mExternalPath;
        }
        Intent intent = new Intent(this, FileSelectActivity.class);
        intent.putExtra(FileSelecteKeys.FILE_SELECT_ISMULSELECT, mIsMulSelect);
        startActivityForResult(intent, FileSelecteKeys.FILE_SELECT_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PermissonUtils.PERMISSION_REQUESTCODE) {//设置权限后返回
            if (PermissonUtils.getInstance().hasPermissions(this, mPermissions)) {
                initView();
                return;
            }
            finish();
        } else if (requestCode == FileSelecteKeys.FILE_SELECT_REQUEST_CODE) {//选择文件后返回
            if (SelectedFiles.files != null && !SelectedFiles.files.isEmpty()) {
                ArrayList<String> list = new ArrayList<>();
                for (File file : SelectedFiles.files.values()) {
                    if (file != null) {
                        list.add(file.getAbsolutePath());
                    }
                }
                Intent intent = new Intent();
                intent.putStringArrayListExtra(FileSelecteKeys.FILE_SELECT_RESULT, list);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
            SelectedFiles.files = null;
            SelectedFiles.totalFileSize = 0;
            FileSelecteKeys.FILE_ROOT_PATH = "";
        }
    }

    /**
     * 权限申请回调
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissonUtils.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 权限获取完成
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (perms != null && perms.size() == mPermissions.length) {
            initView();
        }
    }

    /**
     * 权限被拒绝
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        mPermissionDialog = PermissonUtils.getInstance().showGoSettingDialog(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPermissionDialog != null) {
            if (mPermissionDialog.isShowing()) {
                mPermissionDialog.dismiss();
            }
            mPermissionDialog = null;
        }
    }
}