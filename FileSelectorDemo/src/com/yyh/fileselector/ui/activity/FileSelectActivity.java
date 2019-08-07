package com.yyh.fileselector.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yyh.fileselector.R;
import com.yyh.fileselector.logic.FileSelecteKeys;
import com.yyh.fileselector.logic.bean.FileMode;
import com.yyh.fileselector.logic.bean.SelectedFiles;
import com.yyh.fileselector.ui.adapter.FileSelectAdapter;
import com.yyh.fileselector.util.EmptyFileFilter;
import com.yyh.fileselector.util.FileComparator;
import com.yyh.fileselector.util.StringUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * @describe: 文件选择界面
 * @author: yyh
 * @createTime: 2019/8/6 14:05
 * @className: FileSelectActivity
 */
public class FileSelectActivity extends Activity implements OnClickListener, OnItemClickListener {

    private ArrayList<FileMode> mFileLists = new ArrayList<FileMode>();//文件选择集合

    private boolean mIsMulSelect;//是否支持多选
    private String mRootPath;//进入文件夹时的根路径，用于判断按返回键时何时返回上一界面以及后退到上一文件夹
    private String mCurrentPath;//当前选中的文件夹路径

    private TextView mTitleView;//标题view
    private TextView mPathTipView;//当前路径提示view
    private TextView mEmptyTipView;//无文件时提示view
    private ListView mListView;//文件展示listview

    private FileSelectAdapter mFileAdapter;//文件展示适配器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_file);

        Intent intent = getIntent();
        this.mIsMulSelect = intent.getBooleanExtra(FileSelecteKeys.FILE_SELECT_ISMULSELECT, false);

        initView();
    }

    private void initView() {
        View backView = this.findViewById(R.id.title_left_img_btn);
        backView.setVisibility(View.VISIBLE);
        backView.setOnClickListener(this);
        View confirmView = this.findViewById(R.id.select_confim_view);
        confirmView.setVisibility(View.VISIBLE);
        confirmView.setOnClickListener(this);
        this.mTitleView = (TextView) findViewById(R.id.title_text);

        this.mPathTipView = (TextView) findViewById(R.id.path_tip_view);
        this.mEmptyTipView = (TextView) findViewById(R.id.empty_tip_view);
        this.mListView = (ListView) findViewById(R.id.list_view);

        if (!StringUtil.isNullOrEmpty(FileSelecteKeys.FILE_ROOT_PATH)) {
            mRootPath = FileSelecteKeys.FILE_ROOT_PATH;
            mCurrentPath = mRootPath;
            //重置文件选择信息
            SelectedFiles.files = new HashMap<String, File>();
            SelectedFiles.totalFileSize = 0;
        } else {
            Toast.makeText(this, "文件管理暂不可用！", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        refreshFileListView();
        refreshSelectTipView();
    }

    /**
     * 刷新文件列表界面
     */
    private void refreshFileListView() {
        //刷新列表
        refreshData();
        if (mFileAdapter == null) {
            mFileAdapter = new FileSelectAdapter(this, mFileLists);
            mListView.setAdapter(mFileAdapter);
            mListView.setOnItemClickListener(this);
        } else {
            mFileAdapter.notifyDataSetChanged();
        }
        mEmptyTipView.setVisibility(mFileAdapter.getCount() == 0 ? View.VISIBLE : View.GONE);
        mPathTipView.setText(mCurrentPath);
    }

    /**
     * 刷新已选提示view
     */
    private void refreshSelectTipView() {
        if (SelectedFiles.files.size() != 0) {//选中文件
            mTitleView.setText(String.format(Locale.getDefault(), "已选%1$d个", SelectedFiles.files.size()));
        } else if (mCurrentPath.equals(mRootPath)) {//未选中任何文件且位于根目录位置
            mTitleView.setText("全部文件");
        } else {//未选中任何文件且不位于根目录位置
            mTitleView.setText("返回上级");
        }
    }

    /**
     * 文件数据刷新
     */
    private void refreshData() {
        mFileLists.clear();
        List<File> fileList = getFileList(mCurrentPath);
        if (fileList != null) {
            for (File file : fileList) {
                if (file != null) {
                    FileMode temp = new FileMode();
                    if (SelectedFiles.files.containsKey(file.getAbsolutePath())) {//若文件已选中过，则标记为选中
                        temp.checked = true;
                    }
                    temp.file = file;
                    mFileLists.add(temp);
                }
            }
        }
    }

    /**
     * 获取当前路径下的文件列表
     */
    private List<File> getFileList(String path) {
        File file = new File(path);
        if (file.exists()) {
            //空文件过滤器
            File[] fileArray = file.listFiles(new EmptyFileFilter());//不显示隐藏文件
            if (fileArray != null && fileArray.length != 0) {//不显示空文件夹，此处不做判断，有些情况会崩溃
                List<File> fileList = Arrays.asList(fileArray);//将File[]转换为List<File>,以便调用Collections.sort进行排序
                Collections.sort(fileList, new FileComparator());//文件排序
                return fileList;
            }
        }
        return null;
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.title_left_img_btn) {
            onBackPressed();
        } else if (viewId == R.id.select_confim_view) {
            okAndFinish();
        }
    }

    private void okAndFinish() {
        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mFileLists.isEmpty()) {
            return;
        }
        FileMode fileMode = mFileLists.get(position);
        if (fileMode == null) {
            return;
        }
        File file = fileMode.file;
        if (file == null) {
            return;
        }
        if (file.isDirectory()) {
            mCurrentPath = file.getAbsolutePath();
            refreshFileListView();
        } else {
            fileMode.checked = !fileMode.checked;
            ImageView checkView = (ImageView) view.findViewById(R.id.file_item_checked_view);
            if (fileMode.checked) {
                checkView.setImageResource(R.drawable.ico_checkbox_selected);
                SelectedFiles.files.put(file.getAbsolutePath(), file);//在全局静态变量中添加文件
                SelectedFiles.totalFileSize += file.length();//重新计算文件综合大小
                if (!mIsMulSelect) {
                    okAndFinish();
                    return;
                }
            } else {
                checkView.setImageResource(R.drawable.ico_checkbox_normal);
                SelectedFiles.files.remove(file.getAbsolutePath());//在全局静态变量中移除文件
                SelectedFiles.totalFileSize -= file.length();//重新计算文件综合大小
            }
        }
        refreshSelectTipView();
    }

    @Override
    public void onBackPressed() {
        if (!mCurrentPath.equals(mRootPath)) {//若未到达根目录，则返回文件上层目录
            File file = new File(mCurrentPath);
            mCurrentPath = file.getParentFile().getAbsolutePath();
            refreshFileListView();
            refreshSelectTipView();
        } else {//到达根目录，直接返回
            clearSelectedFiles();
            super.onBackPressed();//等同于直接finish();
        }
    }

    /**
     * 清空全局变量SelectedFiles中的数据
     */
    private void clearSelectedFiles() {
        SelectedFiles.files = null;
        SelectedFiles.totalFileSize = 0;
    }
}