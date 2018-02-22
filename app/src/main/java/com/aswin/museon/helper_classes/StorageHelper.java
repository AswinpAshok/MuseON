package com.aswin.museon.helper_classes;

import android.os.Environment;
import android.util.Log;

import com.aswin.museon.models.FileModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ASWIN on 2/22/2018.
 */

public class StorageHelper {
    public static List<FileModel> listFiles(String folderName){
        List<FileModel> folderList=new ArrayList<>();
        List<FileModel> fileList=new ArrayList<>();
        String path;
        if(folderName.equals("")){
            path = Environment.getExternalStorageDirectory().toString();
        }else {
            path = Environment.getExternalStorageDirectory().toString() + "/" + folderName;
        }
//        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
//        Log.d("Files", "Size: "+ files.length);
        for (int i = 0; i < files.length; i++)
        {
            File file=new File(path+"/"+files[i].getName());
            boolean isDirectory=file.isDirectory();
            FileModel model=new FileModel();
            model.setName(files[i].getName());
            model.setDirectory(isDirectory);
            if(isDirectory){
                folderList.add(model);
            }else {
                fileList.add(model);
            }
//            Log.d("Files", "FileName:" + files[i].getName()+" "+isDirectory);
        }
        folderList.addAll(fileList);
        return folderList;
    }
}
