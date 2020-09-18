package com.lcw.library.imagepicker.cardcamera;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lcw.library.imagepicker.R;
import com.lcw.library.imagepicker.utils.CommonUtils;
import com.lcw.library.imagepicker.utils.ImageUtils;
import com.lcw.library.imagepicker.utils.ScreenUtils;
import com.lcw.library.imagepicker.view.cropper.CropImageView;
import com.lcw.library.imagepicker.view.cropper.CropListener;

import java.io.File;

/**
 * Author       wildma
 * Github       https://github.com/wildma
 * Date         2018/6/24
 * Desc	        ${拍照界面}
 */
public class CameraActivity extends Activity implements View.OnClickListener {

    private CameraPreview mCameraPreview;//相机预览
    private View mViewCameraCropLeft;

    private ImageView mIvCameraCrop;//相机裁剪区域
    private CropImageView mCropImageView;//手动裁剪区域view
    private TextView mViewCameraCropBottom;//触摸屏幕对焦

    private View mLlCameraOption;//拍照前操作布局
    private ImageView mIvCameraFlash;//闪光灯控制View
    private View mLlCameraResult;//拍照后操作布局

    private Bitmap mCropBitmap;

    private int mTakeType;//拍摄类型

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sdk_activity_camera);
        this.mTakeType = getIntent().getIntExtra(IDCardCamera.TAKE_TYPE, 0);

        initView();
        initListener();
    }

    private void initView() {
        mCameraPreview = (CameraPreview) findViewById(R.id.camera_preview);
        //相机裁剪区域左边
        mViewCameraCropLeft = findViewById(R.id.view_camera_crop_left);

        View llCameraCropContainer = findViewById(R.id.ll_camera_crop_container);
        //相机裁剪区域
        mIvCameraCrop = (ImageView) findViewById(R.id.iv_camera_crop);
        //手动裁剪区域view
        mCropImageView = (CropImageView)findViewById(R.id.crop_image_view);
        //触摸屏幕对焦
        mViewCameraCropBottom = (TextView) findViewById(R.id.view_camera_crop_bottom);

        //右侧操作布局
        FrameLayout flCameraOption = (FrameLayout) findViewById(R.id.fl_camera_option);
        //拍照前操作布局
        mLlCameraOption = findViewById(R.id.ll_camera_option);
        //闪光灯控制View
        mIvCameraFlash = (ImageView) findViewById(R.id.iv_camera_flash);
        //拍照后操作布局
        mLlCameraResult = findViewById(R.id.ll_camera_result);


        float screenMinSize = Math.min(ScreenUtils.getScreenWidth(this), ScreenUtils.getScreenHeight(this));
        float screenMaxSize = Math.max(ScreenUtils.getScreenWidth(this), ScreenUtils.getScreenHeight(this));
        float height = (int) (screenMinSize * 0.75);
        float width = (int) (height * 75.0f / 47.0f);
        //获取底部"操作区域"的宽度
        float flCameraOptionWidth = (screenMaxSize - width) / 2;
        LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams((int) width, ViewGroup.LayoutParams.MATCH_PARENT);
        LinearLayout.LayoutParams cropParams = new LinearLayout.LayoutParams((int) width, (int) height);
        LinearLayout.LayoutParams cameraOptionParams = new LinearLayout.LayoutParams((int) flCameraOptionWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        llCameraCropContainer.setLayoutParams(containerParams);

        mIvCameraCrop.setLayoutParams(cropParams);
        //获取"相机裁剪区域"的宽度来动态设置底部"操作区域"的宽度，使"相机裁剪区域"居中
        flCameraOption.setLayoutParams(cameraOptionParams);

        switch (mTakeType) {
            case IDCardCamera.TYPE_IDCARD_FRONT:
                mIvCameraCrop.setImageResource(R.mipmap.sdk_ico_camera_idcard_front);
                break;
            case IDCardCamera.TYPE_IDCARD_BACK:
                mIvCameraCrop.setImageResource(R.mipmap.sdk_ico_camera_idcard_back);
                break;
            case IDCardCamera.TYPE_BANK_CARD:
                mIvCameraCrop.setImageResource(R.mipmap.sdk_ico_camera_bank_card);
                break;
        }

        /*增加0.5秒过渡界面，解决个别手机首次申请权限导致预览界面启动慢的问题*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mCameraPreview.setVisibility(View.VISIBLE);
                    }
                });
            }
        }, 500);
    }

    private void initListener() {
        mCameraPreview.setOnClickListener(this);
        mIvCameraFlash.setOnClickListener(this);

        findViewById(R.id.iv_camera_close).setOnClickListener(this);
        findViewById(R.id.iv_camera_take).setOnClickListener(this);

        findViewById(R.id.iv_camera_result_ok).setOnClickListener(this);
        findViewById(R.id.iv_camera_result_cancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.camera_preview) {
            mCameraPreview.focus();
        } else if (id == R.id.iv_camera_close) {
            finish();
        } else if (id == R.id.iv_camera_take) {
            if (!CommonUtils.isFastClick()) {
                takePhoto();
            }
        } else if (id == R.id.iv_camera_flash) {
            if (CameraUtils.hasFlash(this)) {
                boolean isFlashOn = mCameraPreview.switchFlashLight();
                mIvCameraFlash.setImageResource(isFlashOn ? R.mipmap.sdk_ico_camera_flash_on : R.mipmap.sdk_ico_camera_flash_off);
            } else {
                Toast.makeText(this, R.string.no_flash, Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.iv_camera_result_ok) {
            confirm();
        } else if (id == R.id.iv_camera_result_cancel) {
            mCameraPreview.setEnabled(true);
            mCameraPreview.addCallback();
            mCameraPreview.startPreview();
            mIvCameraFlash.setImageResource(R.mipmap.sdk_ico_camera_flash_off);
            setTakePhotoLayout();
        }
    }

    /**
     * 拍照
     */
    private void takePhoto() {
        mCameraPreview.setEnabled(false);
        CameraUtils.getCamera().setOneShotPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(final byte[] bytes, Camera camera) {
                final Camera.Size size = camera.getParameters().getPreviewSize(); //获取预览大小
                camera.stopPreview();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final int w = size.width;
                        final int h = size.height;
                        Bitmap bitmap = ImageUtils.getBitmapFromByte(bytes, w, h);
                        cropImage(bitmap);
                    }
                }).start();
            }
        });
    }

    /**
     * 裁剪图片
     */
    private void cropImage(Bitmap bitmap) {
        /*计算扫描框的坐标点*/
        float left = mViewCameraCropLeft.getWidth();
        float top = mIvCameraCrop.getTop();
        float right = mIvCameraCrop.getRight() + left;
        float bottom = mIvCameraCrop.getBottom();

        /*计算扫描框坐标点占原图坐标点的比例*/
        float leftProportion = left / mCameraPreview.getWidth();
        float topProportion = top / mCameraPreview.getHeight();
        float rightProportion = right / mCameraPreview.getWidth();
        float bottomProportion = bottom / mCameraPreview.getBottom();

        /*自动裁剪*/
        mCropBitmap = Bitmap.createBitmap(bitmap,
                (int) (leftProportion * (float) bitmap.getWidth()),
                (int) (topProportion * (float) bitmap.getHeight()),
                (int) ((rightProportion - leftProportion) * (float) bitmap.getWidth()),
                (int) ((bottomProportion - topProportion) * (float) bitmap.getHeight()));

        /*设置成手动裁剪模式*/
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //将手动裁剪区域设置成与扫描框一样大
                mCropImageView.setLayoutParams(new LinearLayout.LayoutParams(mIvCameraCrop.getWidth(), mIvCameraCrop.getHeight()));
                setCropLayout();
                mCropImageView.setImageBitmap(mCropBitmap);
            }
        });
    }

    /**
     * 设置裁剪布局
     */
    private void setCropLayout() {
        mIvCameraCrop.setVisibility(View.GONE);
        mCameraPreview.setVisibility(View.GONE);
        mLlCameraOption.setVisibility(View.GONE);
        mCropImageView.setVisibility(View.VISIBLE);
        mLlCameraResult.setVisibility(View.VISIBLE);
        mViewCameraCropBottom.setText("");
    }

    /**
     * 设置拍照布局
     */
    private void setTakePhotoLayout() {
        mIvCameraCrop.setVisibility(View.VISIBLE);
        mCameraPreview.setVisibility(View.VISIBLE);
        mLlCameraOption.setVisibility(View.VISIBLE);
        mCropImageView.setVisibility(View.GONE);
        mLlCameraResult.setVisibility(View.GONE);
        mViewCameraCropBottom.setText(getString(R.string.touch_to_focus));

        mCameraPreview.focus();
    }

    /**
     * 点击确认，返回图片路径
     */
    private void confirm() {
        /*手动裁剪图片*/
        mCropImageView.crop(new CropListener() {
            @Override
            public void onFinish(Bitmap bitmap) {
                if (bitmap == null) {
                    Toast.makeText(getApplicationContext(), getString(R.string.crop_fail), Toast.LENGTH_SHORT).show();
                    finish();
                }
                /*保存图片到sdcard并返回图片路径*/
                File fileDir = new File(Environment.getExternalStorageDirectory(), "Pictures");
                if (!fileDir.exists()) {
                    fileDir.mkdir();
                }
                String imagePath = fileDir.getAbsolutePath() + "/IMG_" + System.currentTimeMillis() + ".jpg";

                if (ImageUtils.save(bitmap, imagePath, Bitmap.CompressFormat.JPEG)) {
                    Intent intent = new Intent();
                    intent.putExtra(IDCardCamera.IMAGE_PATH, imagePath);
                    setResult(IDCardCamera.RESULT_CODE, intent);
                    finish();
                }
            }
        }, true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mCameraPreview != null) {
            mCameraPreview.onStart();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mCameraPreview != null) {
            mCameraPreview.onStop();
        }
        if (mCropBitmap != null) {
            mCropBitmap.recycle();
        }
    }
}