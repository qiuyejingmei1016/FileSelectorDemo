package com.yyh.fileselector.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.yyh.fileselector.R;
import com.yyh.fileselector.logic.FileSelecteKeys;
import com.yyh.fileselector.util.StringUtil;

import java.util.ArrayList;

/**
 * @describe: 文件选择测试界面
 * @author: yyh
 * @createTime: 2019/8/7 15:12
 * @className: TestActivity
 */
public class TestActivity extends Activity implements OnClickListener {

    private TextView mResultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        TextView titleView = (TextView) findViewById(R.id.title_text);
        titleView.setText("选择文件");
        mResultView = (TextView) findViewById(R.id.tv_main_content);
        findViewById(R.id.btn_main_select).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.btn_main_select) {
            FileSelecteKeys.actionSelectFile(this, FileSelecteKeys.FILE_SELECT_REQUEST_CODE,
                    FileSelecteKeys.FILE_SELECT_RESULT, true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FileSelecteKeys.FILE_SELECT_REQUEST_CODE) {//获取文件选择结果
            if (data == null) {
                return;
            }
            ArrayList<String> list = data.getStringArrayListExtra(FileSelecteKeys.FILE_SELECT_RESULT);
            if (list != null && !list.isEmpty()) {
                StringBuilder builder = new StringBuilder();
                for (String path : list) {
                    if (!StringUtil.isNullOrEmpty(path)) {
                        builder.append(path).append("\r\n");
                    }
                }
                mResultView.setText(builder.toString());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}