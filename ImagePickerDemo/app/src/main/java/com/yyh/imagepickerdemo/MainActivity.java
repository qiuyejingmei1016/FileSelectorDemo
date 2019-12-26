package com.yyh.imagepickerdemo;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.lcw.library.imagepicker.ImagePicker;
import com.lcw.library.imagepicker.PickerGlideLoader;
import com.lcw.library.imagepicker.utils.StringEmptyUtil;
import com.yyh.imagepickerdemo.utils.PermissonUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PermissonUtils.PermissionCallback {

    private static final int REQUEST_SELECT_IMAGES_CODE = 10000;//选择图片返回码

    private PickerGlideLoader mPickerGlideLoader;
    private ArrayList<String> mImagePaths = new ArrayList<String>();//图片已选择集合

    private ImageView mResuleView;

    private String[] mPermissions;//权限组
    private AlertDialog mPermissionDialog;//权限被拒绝提示弹窗

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();
    }

    private void initView() {
        this.mPickerGlideLoader = new PickerGlideLoader(this);

        this.findViewById(R.id.select_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select();
            }
        });

        this.mResuleView = (ImageView) findViewById(R.id.resule_view);
    }

    /**
     * 请求权限
     */
    private void requestPermission() {
        mPermissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA};
        PermissonUtils.getInstance().setPermissionCallback(this);
        PermissonUtils.getInstance().needToFinsh(true).requestPermission(this, mPermissions);
    }

    private void select() {
        //说明：修改了类库源码，相比原来新增根据文件头是否展示webp格式图片、直接过滤了gif图片；
        // 如需展示gif图片，则直接去PickerImageScanner媒体库扫描类修改图片扫描类型；
        ImagePicker.getInstance()
                .setTitle("选择图片")//设置标题
                .showCamera(true)//设置是否显示拍照按钮
                .showImage(true)//设置是否展示图片
                .showVideo(true)//设置是否展示视频
                .setMaxCount(1)//设置最大选择图片数目(默认为1，单选)
                .setImagePaths(mImagePaths)//设置图片选择历史记录
                .setSingleType(true)//设置图片视频不能同时选择
                .setImageLoader(mPickerGlideLoader)//设置自定义图片加载器
                .start(this, REQUEST_SELECT_IMAGES_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SELECT_IMAGES_CODE && resultCode == RESULT_OK) {
            if (data == null) {
                return;
            }
            mImagePaths = data.getStringArrayListExtra(ImagePicker.EXTRA_SELECT_IMAGES);
            if (mImagePaths != null && !mImagePaths.isEmpty()) {
                String imagePath = mImagePaths.get(0);
                if (!StringEmptyUtil.isNullOrEmpty(imagePath)) {
                    Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                    mResuleView.setImageBitmap(bitmap);
                }
            }
        } else if (requestCode == PermissonUtils.PERMISSION_REQUESTCODE) {
            if (PermissonUtils.getInstance().hasPermissions(this, mPermissions)) {
                initView();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
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
        mImagePaths.clear();
        if (mPickerGlideLoader != null) {
            mPickerGlideLoader.clearMemoryCache();
            mPickerGlideLoader = null;
        }
        if (mPermissionDialog != null) {
            if (mPermissionDialog.isShowing()) {
                mPermissionDialog.dismiss();
            }
            mPermissionDialog = null;
        }
    }
}