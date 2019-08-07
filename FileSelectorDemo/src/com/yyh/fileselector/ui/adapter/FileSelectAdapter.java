package com.yyh.fileselector.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yyh.fileselector.R;
import com.yyh.fileselector.logic.bean.FileMode;
import com.yyh.fileselector.util.StringUtil;

import java.io.File;
import java.util.List;

/**
 * @describe: 文件列表展示适配器
 * @author: yyh
 * @createTime: 2019/8/6 14:05
 * @className: FileSelectAdapter
 */
public class FileSelectAdapter extends BaseAdapter {

    private List<FileMode> mList;

    private Context mContext;

    public FileSelectAdapter(Context context, List<FileMode> data) {
        this.mList = data;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return (null == mList) ? 0 : mList.size();
    }

    @Override
    public FileMode getItem(int position) {
        if (mList.get(position) != null) {
            return mList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_file_folder_view, null);
            holder = new ViewHolder();
            holder.filefolderView = convertView.findViewById(R.id.file_folder_view);
            holder.filefolderNameView = (TextView) convertView.findViewById(R.id.file_folder_nameview);

            holder.fileRootView = convertView.findViewById(R.id.file_item_root_view);
            holder.fileCheckedView = (ImageView) convertView.findViewById(R.id.file_item_checked_view);
            holder.fileItemPreview = (ImageView) convertView.findViewById(R.id.file_item_preview);
            holder.fileItemNameview = (TextView) convertView.findViewById(R.id.file_item_nameview);
            holder.fileItemSizeview = (TextView) convertView.findViewById(R.id.folder_item_file_size);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        FileMode fileMode = getItem(position);

        if (fileMode != null) {
            File file = fileMode.file;
            if (file.isDirectory()) {//file类型为文件夹
                holder.filefolderView.setVisibility(View.VISIBLE);
                holder.fileRootView.setVisibility(View.GONE);
                holder.filefolderNameView.setText(StringUtil.getNotNullString(file.getName()));
            } else {//file类型为文件
                holder.filefolderView.setVisibility(View.GONE);
                holder.fileRootView.setVisibility(View.VISIBLE);
                if (fileMode.checked) {
                    holder.fileCheckedView.setImageResource(R.drawable.ico_checkbox_selected);
                } else {
                    holder.fileCheckedView.setImageResource(R.drawable.ico_checkbox_normal);
                }
                String fileName = file.getName();
                holder.fileItemNameview.setText(StringUtil.getNotNullString(fileName));
                holder.fileItemSizeview.setText(StringUtil.getNotNullString(StringUtil.FormetFileSize(file.length())));
                getFileBySuffix(file, fileName, holder.fileItemPreview);
            }
        }

        return convertView;
    }

    private void getFileBySuffix(File file, String fileName, ImageView view) {
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
        if (suffix.equalsIgnoreCase("doc") || suffix.equalsIgnoreCase("docx")) {
            view.setImageResource(R.drawable.ico_doc_file);
        } else if (suffix.equalsIgnoreCase("xls") || suffix.equalsIgnoreCase("xlsx")) {
            view.setImageResource(R.drawable.ico_xls_file);
        } else if (suffix.equalsIgnoreCase("ppt") || suffix.equalsIgnoreCase("pptx")) {
            view.setImageResource(R.drawable.ico_ppt_file);
        } else if (suffix.equalsIgnoreCase("pdf")) {
            view.setImageResource(R.drawable.ico_pdf_file);
        } else if (suffix.equalsIgnoreCase("txt")) {
            view.setImageResource(R.drawable.ico_txt_file);
        } else if (suffix.equalsIgnoreCase("zip") || suffix.equalsIgnoreCase("rar")) {
            view.setImageResource(R.drawable.ico_rar_file);
        } else if (suffix.equalsIgnoreCase("apk")) {
            view.setImageResource(R.drawable.ico_apk_file);
        } else if (suffix.equalsIgnoreCase("mp3") || suffix.equalsIgnoreCase("wav") || suffix.equalsIgnoreCase("ape") || suffix.equalsIgnoreCase("flac") || suffix.equalsIgnoreCase("wave") || suffix.equalsIgnoreCase("amr") || suffix.equalsIgnoreCase("aac") || suffix.equalsIgnoreCase("mid")) {
            view.setImageResource(R.drawable.ico_music_file);
        } else if (suffix.equalsIgnoreCase("png") || suffix.equalsIgnoreCase("jpg")
                || suffix.equalsIgnoreCase("jpeg") || suffix.equalsIgnoreCase("bmp")
                || suffix.equalsIgnoreCase("webp")
                || suffix.equalsIgnoreCase("gif")) {
            Glide.with(mContext).load(file.getAbsolutePath()).placeholder(R.drawable.ico_img_file)
                    .error(R.drawable.ico_img_file).into(view);
        } else if (suffix.equalsIgnoreCase("mp4") || suffix.equalsIgnoreCase("avi")
                || suffix.equalsIgnoreCase("rvmb") || suffix.equalsIgnoreCase("mkv")
                || suffix.equalsIgnoreCase("rm") || suffix.equalsIgnoreCase("mpg")
                || suffix.equalsIgnoreCase("3gp") || suffix.equalsIgnoreCase("vob")
                || suffix.equalsIgnoreCase("mpeg") || suffix.equalsIgnoreCase("mov")
                || suffix.equalsIgnoreCase("flv") || suffix.equalsIgnoreCase("wmv")) {
            Glide.with(mContext).load(file.getAbsolutePath()).placeholder(R.drawable.ico_video_file)
                    .error(R.drawable.ico_video_file).into(view);
        } else {
            view.setImageResource(R.drawable.ico_default_file);
        }
    }

    private class ViewHolder {
        View filefolderView;
        TextView filefolderNameView;

        View fileRootView;
        ImageView fileCheckedView;
        ImageView fileItemPreview;
        TextView fileItemNameview;
        TextView fileItemSizeview;
    }
}