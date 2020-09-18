package com.lcw.library.imagepicker.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lcw.library.imagepicker.R;
import com.lcw.library.imagepicker.adapter.ImagePreViewAdapter;
import com.lcw.library.imagepicker.data.PickerMediaFile;
import com.lcw.library.imagepicker.manager.PickerConfigManager;
import com.lcw.library.imagepicker.manager.PickerSelectionManager;
import com.lcw.library.imagepicker.provider.ImagePickerProvider;
import com.lcw.library.imagepicker.utils.PickerDataUtil;
import com.lcw.library.imagepicker.view.PickerHackyViewPager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 大图预览界面
 * Create by: chenWei.li
 * Date: 2018/10/3
 * Time: 下午11:32
 * Email: lichenwei.me@foxmail.com
 */
public class ImagePreActivity extends PickerBaseActivity {

    public static final String IMAGE_POSITION = "imagePosition";
    private List<PickerMediaFile> mMediaFileList;
    private int mPosition = 0;

    private TextView mTvTitle;
    private TextView mTvCommit;
    private ImageView mIvPlay;
    private PickerHackyViewPager mViewPager;
    private LinearLayout mLlPreSelect;
    private ImageView mIvPreCheck;
    private ImagePreViewAdapter mImagePreViewAdapter;


    @Override
    protected int bindLayout() {
        return R.layout.sdk_picker_activity_pre_image;
    }

    @Override
    protected void initView() {
        mTvTitle = findViewById(R.id.tv_actionBar_title);
        mTvCommit = findViewById(R.id.tv_actionBar_commit);
        mIvPlay = findViewById(R.id.iv_main_play);
        mViewPager = findViewById(R.id.vp_main_preImage);
        mLlPreSelect = findViewById(R.id.ll_pre_select);
        mIvPreCheck = findViewById(R.id.iv_item_check);
    }

    @Override
    protected void initListener() {

        findViewById(R.id.iv_actionBar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mTvTitle.setText(String.format("%d/%d", position + 1, mMediaFileList.size()));
                setIvPlayShow(mMediaFileList.get(position));
                updateSelectButton(mMediaFileList.get(position).getPath());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mLlPreSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //如果是单类型选取，判断添加类型是否满足（照片视频不能共存）
                if (PickerConfigManager.getInstance().isSingleType()) {
                    ArrayList<String> selectPathList = PickerSelectionManager.getInstance().getSelectPaths();
                    if (!selectPathList.isEmpty()) {
                        //判断选中集合中第一项是否为视频
                        if (!PickerSelectionManager.isCanAddSelectionPaths(mMediaFileList.get(mViewPager.getCurrentItem()).getPath(), selectPathList.get(0))) {
                            //类型不同
                            Toast.makeText(ImagePreActivity.this, getString(R.string.sdk_picker_single_type_choose), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }

                boolean addSuccess = PickerSelectionManager.getInstance().addImageToSelectList(mMediaFileList.get(mViewPager.getCurrentItem()).getPath());
                if (addSuccess) {
                    updateSelectButton(mMediaFileList.get(mViewPager.getCurrentItem()).getPath());
                    updateCommitButton();
                } else {
                    Toast.makeText(ImagePreActivity.this, String.format(getString(R.string.sdk_select_image_max), PickerSelectionManager.getInstance().getMaxCount()), Toast.LENGTH_SHORT).show();
                }
            }
        });

        mTvCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK, new Intent());
                finish();
            }
        });

        mIvPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //实现播放视频的跳转逻辑(调用原生视频播放器)
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = FileProvider.getUriForFile(ImagePreActivity.this, ImagePickerProvider.getFileProviderName(ImagePreActivity.this), new File(mMediaFileList.get(mViewPager.getCurrentItem()).getPath()));
                intent.setDataAndType(uri, "video/*");
                //给所有符合跳转条件的应用授权
                List<ResolveInfo> resInfoList = getPackageManager()
                        .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    grantUriPermission(packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                startActivity(intent);
            }
        });

    }

    @Override
    protected void getData() {
        mMediaFileList = PickerDataUtil.getInstance().getMediaData();
        mPosition = getIntent().getIntExtra(IMAGE_POSITION, 0);
        mTvTitle.setText(String.format("%d/%d", mPosition + 1, mMediaFileList.size()));
        mImagePreViewAdapter = new ImagePreViewAdapter(this, mMediaFileList);
        mViewPager.setAdapter(mImagePreViewAdapter);
        mViewPager.setCurrentItem(mPosition);
        //更新当前页面状态
        setIvPlayShow(mMediaFileList.get(mPosition));
        updateSelectButton(mMediaFileList.get(mPosition).getPath());
        updateCommitButton();
    }

    /**
     * 更新确认按钮状态
     */
    private void updateCommitButton() {

        int maxCount = PickerSelectionManager.getInstance().getMaxCount();

        //改变确定按钮UI
        int selectCount = PickerSelectionManager.getInstance().getSelectPaths().size();
        if (selectCount == 0) {
            mTvCommit.setEnabled(false);
            mTvCommit.setText(getString(R.string.sdk_confirm));
            return;
        }
        if (selectCount < maxCount) {
            mTvCommit.setEnabled(true);
            mTvCommit.setText(String.format(getString(R.string.sdk_confirm_msg), selectCount, maxCount));
            return;
        }
        if (selectCount == maxCount) {
            mTvCommit.setEnabled(true);
            mTvCommit.setText(String.format(getString(R.string.sdk_confirm_msg), selectCount, maxCount));
            return;
        }
    }

    /**
     * 更新选择按钮状态
     */
    private void updateSelectButton(String imagePath) {
        boolean isSelect = PickerSelectionManager.getInstance().isImageSelect(imagePath);
        if (isSelect) {
            mIvPreCheck.setImageDrawable(getResources().getDrawable(R.mipmap.sdk_ico_image_checked));
        } else {
            mIvPreCheck.setImageDrawable(getResources().getDrawable(R.mipmap.sdk_ico_image_check));
        }
    }

    /**
     * 设置是否显示视频播放按钮
     *
     * @param mediaFile
     */
    private void setIvPlayShow(PickerMediaFile mediaFile) {
        if (mediaFile.getDuration() > 0) {
            mIvPlay.setVisibility(View.VISIBLE);
        } else {
            mIvPlay.setVisibility(View.GONE);
        }
    }

}
