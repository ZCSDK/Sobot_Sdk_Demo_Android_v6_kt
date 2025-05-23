package com.sobot.chat.widget.attachment;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sobot.chat.api.model.SobotFileModel;
import com.sobot.chat.utils.LogUtils;

import java.util.ArrayList;

/**
 * 附件 适配器
 */
public class FileAttachmentAdapter extends RecyclerView.Adapter<FileAttachmentAdapter.ViewHolder> {

    private Context context;
    private ArrayList<SobotFileModel> arrayList;
    private int fileColor;
    private AttachmentView.Listener clickListener;

    public FileAttachmentAdapter(Context context, ArrayList<SobotFileModel> arrayList, int fileNameColor, AttachmentView.Listener clickListener) {
        this.context = context;
        this.arrayList = arrayList;
        fileColor = fileNameColor;
        this.clickListener = clickListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = new AttachmentView(context);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        SobotFileModel fileModel = arrayList.get(i);
        viewHolder.attachmentView.setFileName(fileModel.getFileName());
        LogUtils.e(i + "\t" + fileModel.getFileType() + "\t" + fileModel.getFileUrl());
        viewHolder.attachmentView.setFileUrl(fileModel.getFileUrl());
        viewHolder.attachmentView.setFileTypeIcon(FileTypeConfig.getFileType(fileModel.getFileType()));
        viewHolder.attachmentView.setFileNameColor(fileColor);
        viewHolder.attachmentView.setPosition(i);
        viewHolder.attachmentView.setListener(clickListener);
        viewHolder.attachmentView.setFileModel(fileModel);


    }

    @Override
    public int getItemCount() {
        if (null == arrayList) {
            return 0;
        }
        return arrayList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private AttachmentView attachmentView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            attachmentView = (AttachmentView) itemView;
        }
    }
}
