package com.aswin.museon.models;

/**
 * Created by ASWIN on 2/22/2018.
 */

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProfileResponse {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("profile")
    @Expose
    private List<Profile> profile = null;
    @SerializedName("uploaded_images")
    @Expose
    private List<UploadedImage> uploadedImages = null;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<Profile> getProfile() {
        return profile;
    }

    public void setProfile(List<Profile> profile) {
        this.profile = profile;
    }

    public List<UploadedImage> getUploadedImages() {
        return uploadedImages;
    }

    public void setUploadedImages(List<UploadedImage> uploadedImages) {
        this.uploadedImages = uploadedImages;
    }


}
