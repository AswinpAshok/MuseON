package com.aswin.museon.models;

import java.util.List;

/**
 * Created by ASWIN on 2/22/2018.
 */

public class RegisteredUsers {
    private List<UserModel> list;
    private String msg;
    private int status;
    public RegisteredUsers() {
    }

    public List<UserModel> getList() {
        return list;
    }

    public String getMsg() {
        return msg;
    }

    public int getStatus() {
        return status;
    }
}
