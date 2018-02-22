package com.aswin.museon.recyclerview_components;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aswin.museon.FolderClickListener;
import com.aswin.museon.R;
import com.aswin.museon.activities.ViewContactActivity;
import com.aswin.museon.models.FileModel;
import com.aswin.museon.models.UploadedImage;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * Created by ASWIN on 2/21/2018.
 */

public class FileRecyclerAdapter extends RecyclerView.Adapter<FileRecyclerAdapter.MyViewHolder>{

    private List<UploadedImage> fileList;



    public FileRecyclerAdapter() {
    }

    public void setFileList(List<UploadedImage> fileList) {
        this.fileList = fileList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.file_row,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        UploadedImage image=fileList.get(position);
        holder.thumbnail.setImageURI(image.getImage());
        String[] parts=image.getImage().split("/");
        holder.fileName.setText(parts[parts.length-1]);

    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    protected class MyViewHolder extends RecyclerView.ViewHolder{

        private SimpleDraweeView thumbnail;
        private TextView fileName;


        public MyViewHolder(View itemView) {
            super(itemView);

            fileName=itemView.findViewById(R.id.fileName);
            thumbnail=itemView.findViewById(R.id.thumbNail);

        }
    }
}
