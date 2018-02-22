package com.aswin.museon.api;

import com.aswin.museon.models.SignUpResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by ASWIN on 2/21/2018.
 */

public interface MuseOnClient {
    @Multipart
    @POST("reg")
    Call<SignUpResponse> registerUser(
            @Part("name") RequestBody username,
            @Part("mobile") RequestBody mobile,
            @Part MultipartBody.Part file
    );
}
