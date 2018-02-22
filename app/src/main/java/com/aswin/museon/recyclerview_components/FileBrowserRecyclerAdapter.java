package com.aswin.museon.recyclerview_components;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aswin.museon.FolderClickListener;
import com.aswin.museon.R;
import com.aswin.museon.models.FileModel;

import java.util.List;

/**
 * Created by ASWIN on 2/21/2018.
 */

public class FileBrowserRecyclerAdapter extends RecyclerView.Adapter<FileBrowserRecyclerAdapter.MyViewHolder>{

    private List<FileModel> fileList;
    private FolderClickListener listener;

    public void setListener(FolderClickListener listener) {
        this.listener = listener;
    }

    public FileBrowserRecyclerAdapter() {
    }

    public void setFileList(List<FileModel> fileList) {
        this.fileList = fileList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.file_browser_row,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final FileModel model=fileList.get(position);
        if(model.isDirectory()){
            holder.icon.setImageResource(R.drawable.folder);
        }else {
            holder.icon.setImageResource(R.drawable.file);
        }
        holder.fileName.setText(model.getName());

        holder.baseLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(model.isDirectory()){

                    listener.onFolderClicked(model.getName());

                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    protected class MyViewHolder extends RecyclerView.ViewHolder{

        private ImageView icon;
        private TextView fileName;
        private RelativeLayout baseLayout;

        public MyViewHolder(View itemView) {
            super(itemView);

            fileName=itemView.findViewById(R.id.fileName);
            icon=itemView.findViewById(R.id.icon);
            baseLayout=itemView.findViewById(R.id.baseLayout);
        }
    }
}
