package com.aswin.museon.models;

/**
 * Created by ASWIN on 2/21/2018.
 */

public class FileModel {
    String name;
    boolean isDirectory;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setDirectory(boolean directory) {
        isDirectory = directory;
    }

    public FileModel() {

    }
}
