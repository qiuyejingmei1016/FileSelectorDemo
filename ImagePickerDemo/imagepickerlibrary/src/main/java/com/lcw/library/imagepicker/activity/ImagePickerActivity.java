package com.lcw.library.imagepicker.activity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lcw.library.imagepicker.ImagePicker;
import com.lcw.library.imagepicker.R;
import com.lcw.library.imagepicker.adapter.ImageFoldersAdapter;
import com.lcw.library.imagepicker.adapter.ImagePickerAdapter;
import com.lcw.library.imagepicker.data.PickerMediaFile;
import com.lcw.library.imagepicker.data.PickerMediaFolder;
import com.lcw.library.imagepicker.executors.PickerCommonExecutor;
import com.lcw.library.imagepicker.listener.PickerMediaLoadCallback;
import com.lcw.library.imagepicker.manager.PickerConfigManager;
import com.lcw.library.imagepicker.manager.PickerSelectionManager;
import com.lcw.library.imagepicker.provider.ImagePickerProvider;
import com.lcw.library.imagepicker.task.PickerImageLoadTask;
import com.lcw.library.imagepicker.task.PickerMediaLoadTask;
import com.lcw.library.imagepicker.task.PickerVideoLoadTask;
import com.lcw.library.imagepicker.utils.FileTypeUtils;
import com.lcw.library.imagepicker.utils.PickerDataUtil;
import com.lcw.library.imagepicker.utils.PickerMediaFileUtil;
import com.lcw.library.imagepicker.utils.PickerPermissionUtil;
import com.lcw.library.imagepicker.utils.PickerUtils;
import com.lcw.library.imagepicker.utils.StringEmptyUtil;
import com.lcw.library.imagepicker.view.ImageFolderPopupWindow;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 多图选择器主页面
 * Create by: chenWei.li
 * Date: 2018/8/23
 * Time: 上午1:10
 * Email: lichenwei.me@foxmail.com
 */
public class ImagePickerActivity extends PickerBaseActivity implements
        ImagePickerAdapter.OnItemClickListener, ImageFoldersAdapter.OnImageFolderChangeListener {

    /**
     * 启动参数
     */
    private String mTitle;
    private boolean isShowCamera;
    private boolean isShowImage;
    private boolean isShowWebpImage;
    private boolean isShowVideo;
    private boolean isSingleType;
    private int mMaxCount;
    private List<String> mImagePaths;

    /**
     * 界面UI
     */
    private TextView mTvTitle;
    private TextView mTvCommit;
    private TextView mTvImageTime;
    private RecyclerView mRecyclerView;
    private TextView mTvImageFolders;
    private ImageFolderPopupWindow mImageFolderPopupWindow;
    private ProgressDialog mProgressDialog;
    private RelativeLayout mRlBottom;

    private GridLayoutManager mGridLayoutManager;
    private ImagePickerAdapter mImagePickerAdapter;

    //图片数据源
    private List<PickerMediaFile> mMediaFileList;
    //文件夹数据源
    private List<PickerMediaFolder> mMediaFolderList;

    //是否显示时间
    private boolean isShowTime;

    //表示屏幕亮暗
    private static final int LIGHT_OFF = 0;
    private static final int LIGHT_ON = 1;

    private Handler mMyHandler = new Handler();
    private Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hideImageTime();
        }
    };


    /**
     * 大图预览页相关
     */
    private static final int REQUEST_SELECT_IMAGES_CODE = 0x01;//用于在大图预览页中点击提交按钮标识


    /**
     * 拍照相关
     */
    private String mFilePath;
    private static final int REQUEST_CODE_CAPTURE = 0x02;//点击拍照标识

    /**
     * 权限相关
     */
    private static final int REQUEST_PERMISSION_CAMERA_CODE = 0x03;


    @Override
    protected int bindLayout() {
        return R.layout.sdk_picker_activity_image;
    }


    /**
     * 初始化配置
     */
    @Override
    protected void initConfig() {
        mTitle = PickerConfigManager.getInstance().getTitle();
        isShowCamera = PickerConfigManager.getInstance().isShowCamera();
        isShowImage = PickerConfigManager.getInstance().isShowImage();
        isShowWebpImage = PickerConfigManager.getInstance().isShowWebPImage();
        isShowVideo = PickerConfigManager.getInstance().isShowVideo();
        isSingleType = PickerConfigManager.getInstance().isSingleType();
        mMaxCount = PickerConfigManager.getInstance().getMaxCount();
        PickerSelectionManager.getInstance().setMaxCount(mMaxCount);

        //载入历史选择记录
        mImagePaths = PickerConfigManager.getInstance().getImagePaths();
        if (mImagePaths != null && !mImagePaths.isEmpty()) {
            PickerSelectionManager.getInstance().addImagePathsToSelectList(mImagePaths);
        }
    }


    /**
     * 初始化布局控件
     */
    @Override
    protected void initView() {

        mProgressDialog = ProgressDialog.show(this, null, getString(R.string.sdk_picker_scanner_image));

        //顶部栏相关
        mTvTitle = findViewById(R.id.tv_actionBar_title);
        if (TextUtils.isEmpty(mTitle)) {
            mTvTitle.setText(getString(R.string.sdk_image_picker_text));
        } else {
            mTvTitle.setText(mTitle);
        }
        mTvCommit = findViewById(R.id.tv_actionBar_commit);

        //滑动悬浮标题相关
        mTvImageTime = findViewById(R.id.tv_image_time);

        //底部栏相关
        mRlBottom = findViewById(R.id.rl_main_bottom);
        mTvImageFolders = findViewById(R.id.tv_main_imageFolders);

        //列表相关
        mRecyclerView = findViewById(R.id.rv_main_images);
        mGridLayoutManager = new GridLayoutManager(this, 4);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        //注释说当知道Adapter内Item的改变不会影响RecyclerView宽高的时候，可以设置为true让RecyclerView避免重新计算大小。
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(60);

        mMediaFileList = new ArrayList<>();
        mImagePickerAdapter = new ImagePickerAdapter(this, mMediaFileList);
        mImagePickerAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mImagePickerAdapter);


    }

    /**
     * 初始化控件监听事件
     */
    @Override
    protected void initListener() {

        findViewById(R.id.iv_actionBar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickerSelectionManager.getInstance().removeAll();//清空选中记录
                setResult(RESULT_CANCELED);
                finish();
//                commitSelection();//iv_actionBar_back
            }
        });

        mTvCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commitSelection();//mTvCommit
            }
        });

        mTvImageFolders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mImageFolderPopupWindow != null) {
                    setLightMode(LIGHT_OFF);
                    mImageFolderPopupWindow.showAsDropDown(mRlBottom, 0, 0);
                }
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                updateImageTime();
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                updateImageTime();
            }
        });

    }

    /**
     * 获取数据源
     */
    @Override
    protected void getData() {
        //进行权限的判断
        boolean hasPermission = PickerPermissionUtil.checkPermission(this);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CAMERA_CODE);
        } else {
            startScannerTask();
        }
    }

    /**
     * 权限申请回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CAMERA_CODE) {
            if (grantResults.length >= 1) {
                int cameraResult = grantResults[0];//相机权限
                int sdResult = grantResults[1];//sd卡权限
                boolean cameraGranted = cameraResult == PackageManager.PERMISSION_GRANTED;//拍照权限
                boolean sdGranted = sdResult == PackageManager.PERMISSION_GRANTED;//拍照权限
                if (cameraGranted && sdGranted) {
                    //具有拍照权限，sd卡权限，开始扫描任务
                    startScannerTask();
                } else {
                    //没有权限
                    Toast.makeText(this, getString(R.string.sdk_picker_permission_tip), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }


    /**
     * 开启扫描任务
     */
    private void startScannerTask() {
        Runnable mediaLoadTask = null;

        //照片、视频全部加载
        if (isShowImage && isShowVideo) {
            mediaLoadTask = new PickerMediaLoadTask(this, new MediaLoader());
        }

        //只加载视频
        if (!isShowImage && isShowVideo) {
            mediaLoadTask = new PickerVideoLoadTask(this, new MediaLoader());
        }

        //只加载图片
        if (isShowImage && !isShowVideo) {
            mediaLoadTask = new PickerImageLoadTask(this, new MediaLoader());
        }

        //不符合以上场景，采用照片、视频全部加载
        if (mediaLoadTask == null) {
            mediaLoadTask = new PickerMediaLoadTask(this, new MediaLoader());
        }

        PickerCommonExecutor.getInstance().execute(mediaLoadTask);
    }


    /**
     * 处理媒体数据加载成功后的UI渲染
     */
    class MediaLoader implements PickerMediaLoadCallback {

        @Override
        public void loadMediaSuccess(final List<PickerMediaFolder> mediaFolderList) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!mediaFolderList.isEmpty()) {
                        ArrayList<PickerMediaFile> mediaFileList = mediaFolderList.get(0).getMediaFileList();
                        selectFilterWebpImage(mediaFileList);//加载成功过滤webp图片
                        mMediaFileList.addAll(mediaFileList);
                        //默认加载全部照片
//                        mMediaFileList.addAll(mediaFolderList.get(0).getMediaFileList());
                        mImagePickerAdapter.notifyDataSetChanged();
                        //图片文件夹数据
                        mMediaFolderList = new ArrayList<>(mediaFolderList);
                        mImageFolderPopupWindow = new ImageFolderPopupWindow(ImagePickerActivity.this, mMediaFolderList);
                        mImageFolderPopupWindow.setAnimationStyle(R.style.sdk_picker_image_folder_animator);
                        mImageFolderPopupWindow.getAdapter().setOnImageFolderChangeListener(ImagePickerActivity.this);
                        mImageFolderPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                setLightMode(LIGHT_ON);
                            }
                        });
                        updateCommitButton();
                    }
                    mProgressDialog.cancel();
                }
            });
        }
    }


    /**
     * 隐藏时间
     */
    private void hideImageTime() {
        if (isShowTime) {
            isShowTime = false;
            ObjectAnimator.ofFloat(mTvImageTime, "alpha", 1, 0).setDuration(300).start();
        }
    }

    /**
     * 显示时间
     */
    private void showImageTime() {
        if (!isShowTime) {
            isShowTime = true;
            ObjectAnimator.ofFloat(mTvImageTime, "alpha", 0, 1).setDuration(300).start();
        }
    }

    /**
     * 更新时间
     */
    private void updateImageTime() {
        int position = mGridLayoutManager.findFirstVisibleItemPosition();
        if (position != RecyclerView.NO_POSITION) {
            PickerMediaFile mediaFile = mImagePickerAdapter.getMediaFile(position);
            if (mediaFile != null) {
                if (mTvImageTime.getVisibility() != View.VISIBLE) {
                    mTvImageTime.setVisibility(View.VISIBLE);
                }
                String time = PickerUtils.getImageTime(mediaFile.getDateToken());
                mTvImageTime.setText(time);
                showImageTime();
                mMyHandler.removeCallbacks(mHideRunnable);
                mMyHandler.postDelayed(mHideRunnable, 1500);
            }
        }
    }

    /**
     * 设置屏幕的亮度模式
     *
     * @param lightMode
     */
    private void setLightMode(int lightMode) {
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        switch (lightMode) {
            case LIGHT_OFF:
                layoutParams.alpha = 0.7f;
                break;
            case LIGHT_ON:
                layoutParams.alpha = 1.0f;
                break;
        }
        getWindow().setAttributes(layoutParams);
    }

    /**
     * 点击图片
     *
     * @param view
     * @param position
     */
    @Override
    public void onMediaClick(View view, int position) {
        if (isShowCamera) {
            if (position == 0) {
                if (!PickerSelectionManager.getInstance().isCanChoose()) {
                    Toast.makeText(this, String.format(getString(R.string.sdk_select_image_max), mMaxCount), Toast.LENGTH_SHORT).show();
                    return;
                }
                showCamera();
                return;
            }
        }

        if (mMediaFileList != null) {
            PickerDataUtil.getInstance().setMediaData(mMediaFileList);
            Intent intent = new Intent(this, ImagePreActivity.class);
            if (isShowCamera) {
                intent.putExtra(ImagePreActivity.IMAGE_POSITION, position - 1);
            } else {
                intent.putExtra(ImagePreActivity.IMAGE_POSITION, position);
            }
            startActivityForResult(intent, REQUEST_SELECT_IMAGES_CODE);
        }
    }

    /**
     * 过滤webp图片
     */
    private void selectFilterWebpImage(ArrayList<PickerMediaFile> mediaFileList) {
        if (!isShowWebpImage) {
            try {
                if (mediaFileList != null && !mediaFileList.isEmpty()) {
                    ArrayList<PickerMediaFile> tempList = new ArrayList<PickerMediaFile>();
                    for (int position = 0; position < mediaFileList.size(); position++) {
                        PickerMediaFile mediaFile = mediaFileList.get(position);
                        if (mediaFile != null) {
                            FileTypeUtils.FileType type = FileTypeUtils.getType(mediaFile.getPath());
                            if (StringEmptyUtil.equalsIgnoreNotNull(type.getValue(), FileTypeUtils.FileType.WEBP.getValue())) {
                                tempList.add(mediaFile);
                            }
                        }
                    }
                    if (!tempList.isEmpty()) {
                        mediaFileList.removeAll(tempList);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("FilterWebpImageExc", e.toString());
            }
        }
    }

    /**
     * 选中/取消选中图片
     *
     * @param view
     * @param position
     */
    @Override
    public void onMediaCheck(View view, int position) {
        if (isShowCamera) {
            if (position == 0) {
                if (!PickerSelectionManager.getInstance().isCanChoose()) {
                    Toast.makeText(this, String.format(getString(R.string.sdk_select_image_max), mMaxCount), Toast.LENGTH_SHORT).show();
                    return;
                }
                showCamera();
                return;
            }
        }

        //执行选中/取消操作
        PickerMediaFile mediaFile = mImagePickerAdapter.getMediaFile(position);
        if (mediaFile != null) {
            String imagePath = mediaFile.getPath();
            if (isSingleType) {
                //如果是单类型选取，判断添加类型是否满足（照片视频不能共存）
                ArrayList<String> selectPathList = PickerSelectionManager.getInstance().getSelectPaths();
                if (!selectPathList.isEmpty()) {
                    //判断选中集合中第一项是否为视频
                    if (!PickerSelectionManager.isCanAddSelectionPaths(imagePath, selectPathList.get(0))) {
                        //类型不同
                        Toast.makeText(this, getString(R.string.sdk_picker_single_type_choose), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
            boolean addSuccess = PickerSelectionManager.getInstance().addImageToSelectList(imagePath);
            if (addSuccess) {
                mImagePickerAdapter.notifyItemChanged(position);
            } else {
                Toast.makeText(this, String.format(getString(R.string.sdk_select_image_max), mMaxCount), Toast.LENGTH_SHORT).show();
            }
        }
        updateCommitButton();
    }

    /**
     * 更新确认按钮状态
     */
    private void updateCommitButton() {
        //改变确定按钮UI
        int selectCount = PickerSelectionManager.getInstance().getSelectPaths().size();
        if (selectCount == 0) {
            mTvCommit.setEnabled(false);
            mTvCommit.setText(getString(R.string.sdk_confirm));
            return;
        }
        if (selectCount < mMaxCount) {
            mTvCommit.setEnabled(true);
            mTvCommit.setText(String.format(getString(R.string.sdk_confirm_msg), selectCount, mMaxCount));
            return;
        }
        if (selectCount == mMaxCount) {
            mTvCommit.setEnabled(true);
            mTvCommit.setText(String.format(getString(R.string.sdk_confirm_msg), selectCount, mMaxCount));
            return;
        }
    }

    /**
     * 跳转相机拍照
     */
    private void showCamera() {

        if (isSingleType) {
            //如果是单类型选取，判断添加类型是否满足（照片视频不能共存）
            ArrayList<String> selectPathList = PickerSelectionManager.getInstance().getSelectPaths();
            if (!selectPathList.isEmpty()) {
                if (PickerMediaFileUtil.isVideoFileType(selectPathList.get(0))) {
                    //如果存在视频，就不能拍照了
                    Toast.makeText(this, getString(R.string.sdk_picker_single_type_choose), Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }

        //拍照存放路径
        File fileDir = new File(Environment.getExternalStorageDirectory(), "Pictures");
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
        mFilePath = fileDir.getAbsolutePath() + "/IMG_" + System.currentTimeMillis() + ".jpg";

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri uri;
        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(this, ImagePickerProvider.getFileProviderName(this), new File(mFilePath));
        } else {
            uri = Uri.fromFile(new File(mFilePath));
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, REQUEST_CODE_CAPTURE);
    }

    /**
     * 当图片文件夹切换时，刷新图片列表数据源
     *
     * @param view
     * @param position
     */
    @Override
    public void onImageFolderChange(View view, int position) {
        PickerMediaFolder mediaFolder = mMediaFolderList.get(position);
        //更新当前文件夹名
        String folderName = mediaFolder.getFolderName();
        if (!TextUtils.isEmpty(folderName)) {
            mTvImageFolders.setText(folderName);
        }
        //更新图片列表数据源
        mMediaFileList.clear();
        ArrayList<PickerMediaFile> mediaFileList = mediaFolder.getMediaFileList();
        selectFilterWebpImage(mediaFileList);//文件夹切换时过滤webp图片

//        mMediaFileList.addAll(mediaFolder.getMediaFileList());
        mMediaFileList.addAll(mediaFileList);
        mImagePickerAdapter.notifyDataSetChanged();

        mImageFolderPopupWindow.dismiss();
    }

    /**
     * 拍照回调
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_CAPTURE) {
                //通知媒体库刷新
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + mFilePath)));
                //添加到选中集合
                PickerSelectionManager.getInstance().addImageToSelectList(mFilePath);

                ArrayList<String> list = new ArrayList<>(PickerSelectionManager.getInstance().getSelectPaths());
                Intent intent = new Intent();
                intent.putStringArrayListExtra(ImagePicker.EXTRA_SELECT_IMAGES, list);
                setResult(RESULT_OK, intent);
                PickerSelectionManager.getInstance().removeAll();//清空选中记录
                finish();
            }

            if (requestCode == REQUEST_SELECT_IMAGES_CODE) {
                commitSelection();//onActivityResult
            }
        }
    }

    /**
     * 选择图片完毕，返回
     */
    private void commitSelection() {
        ArrayList<String> list = new ArrayList<>(PickerSelectionManager.getInstance().getSelectPaths());
        Intent intent = new Intent();
        intent.putStringArrayListExtra(ImagePicker.EXTRA_SELECT_IMAGES, list);
        setResult(RESULT_OK, intent);
        PickerSelectionManager.getInstance().removeAll();//清空选中记录
        finish();
    }


    @Override
    protected void onResume() {
        super.onResume();
        mImagePickerAdapter.notifyDataSetChanged();
        updateCommitButton();
    }

    @Override
    public void onBackPressed() {
        PickerSelectionManager.getInstance().removeAll();//清空选中记录
        setResult(RESULT_CANCELED);
//        commitSelection();//onBackPressed
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            PickerConfigManager.getInstance().getImageLoader().clearMemoryCache();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}