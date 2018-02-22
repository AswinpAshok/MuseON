package com.aswin.museon.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aswin.museon.FolderClickListener;
import com.aswin.museon.R;
import com.aswin.museon.helper_classes.StorageHelper;
import com.aswin.museon.models.FileModel;
import com.aswin.museon.recyclerview_components.FileBrowserRecyclerAdapter;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements FolderClickListener {

    private SharedPreferences preferences;
    private TextView username,mobile;
    private SimpleDraweeView profilePic;
    private List<FileModel> fileList,folderList;
    private RecyclerView recyclerView;
    private FileBrowserRecyclerAdapter adapter;
    private String currentPath;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_profile, container, false);
        username=view.findViewById(R.id.username);
        mobile=view.findViewById(R.id.mobile);
        profilePic=view.findViewById(R.id.profilePic);
        recyclerView=view.findViewById(R.id.fileBrowserRecycler);
        currentPath="";

        fileList=new ArrayList<>();
        folderList=new ArrayList<>();
        adapter=new FileBrowserRecyclerAdapter();
        adapter.setListener(ProfileFragment.this);


        preferences=getActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        username.setText(preferences.getString("username",""));
        mobile.setText(preferences.getString("mobile",""));
        profilePic.setImageURI(Uri.fromFile(new File(preferences.getString("profilePic",""))));

        folderList= StorageHelper.listFiles(currentPath);
        adapter.setFileList(folderList);
        RecyclerView.ItemAnimator itemAnimator=new DefaultItemAnimator();
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getActivity());

        recyclerView.setItemAnimator(itemAnimator);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onFolderClicked(String folderName) {
        currentPath=currentPath+"/"+folderName;
        folderList= StorageHelper.listFiles(currentPath);
        adapter.setFileList(folderList);
        adapter.notifyDataSetChanged();
    }
}
