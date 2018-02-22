package com.aswin.museon.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.aswin.museon.Constants;
import com.aswin.museon.R;
import com.aswin.museon.activities.MainActivity;
import com.aswin.museon.activities.SignUpActivity;
import com.aswin.museon.api.MuseOnClient;
import com.aswin.museon.models.FileModel;
import com.aswin.museon.models.ProfileResponse;
import com.aswin.museon.models.SignUpResponse;
import com.aswin.museon.models.UploadResponse;
import com.aswin.museon.recyclerview_components.FileRecyclerAdapter;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import gun0912.tedbottompicker.TedBottomPicker;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements Constants {

    private SharedPreferences preferences;
    private TextView username,mobile;
    private SimpleDraweeView profilePic;
    private FloatingActionButton uploadButton;
    private RecyclerView recyclerView;
    private String phone;
    private FileRecyclerAdapter adapter;
    private CoordinatorLayout baseLayout;
    private Uri imageUri;
    private Snackbar uploadSnackbar;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_profile, container, false);
        username=view.findViewById(R.id.username);
        mobile=view.findViewById(R.id.mobile);
        profilePic=view.findViewById(R.id.profilePic);
        recyclerView=view.findViewById(R.id.fileBrowserRecycler);
        uploadButton=view.findViewById(R.id.uploadButton);
        baseLayout=view.findViewById(R.id.baseLayout);

        adapter=new FileRecyclerAdapter();
        uploadSnackbar=Snackbar.make(baseLayout,"Uploading",Snackbar.LENGTH_INDEFINITE);

        preferences=getActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        username.setText(preferences.getString("username",""));
        phone=preferences.getString("mobile","");
        mobile.setText(phone);
        profilePic.setImageURI(Uri.fromFile(new File(preferences.getString("profilePic",""))));

        RecyclerView.ItemAnimator itemAnimator=new DefaultItemAnimator();
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getActivity());

        recyclerView.setItemAnimator(itemAnimator);
        recyclerView.setLayoutManager(layoutManager);

        getProfileDetails();

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });

        return view;
    }

    private void getProfileDetails() {
        Retrofit.Builder builder=new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit=builder.build();
        MuseOnClient client=retrofit.create(MuseOnClient.class);

        RequestBody body= RequestBody.create(okhttp3.MultipartBody.FORM, phone);
        Call<ProfileResponse> call=client.getProfileDetails(body);
        call.enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {


                    adapter.setFileList(response.body().getUploadedImages());
                    recyclerView.setAdapter(adapter);

            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {

            }
        });
    }

    private void pickImage(){
        TedBottomPicker tedBottomPicker = new TedBottomPicker.Builder(getActivity())
                .setOnImageSelectedListener(new TedBottomPicker.OnImageSelectedListener() {
                    @Override
                    public void onImageSelected(Uri uri) {

                        if(uri.getLastPathSegment().contains("png") || uri.getLastPathSegment().contains("jpg")){
                            imageUri=uri;
                            startUpload(phone);

                        }else {
                            Snackbar.make(baseLayout,"Select a png or jpg file",Snackbar.LENGTH_SHORT).show();
                        }
                    }
                })
                .create();

        tedBottomPicker.show(getActivity().getSupportFragmentManager());
    }

    private void startUpload(String mobile) {
        uploadSnackbar.show();
        MediaType mediaType=MediaType.parse("image/*");

        Retrofit.Builder builder=new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL);

        Retrofit retrofit=builder.build();
        MuseOnClient museOnClient=retrofit.create(MuseOnClient.class);

        File file=new File(imageUri.getPath());
        final RequestBody requestFile =
                RequestBody.create(
                        mediaType,
                        file
                );
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("userfile ", file.getName(), requestFile);



        RequestBody Mobile =
                RequestBody.create(
                        okhttp3.MultipartBody.FORM, mobile);

        Call<UploadResponse> call=museOnClient.uploadFile(Mobile,body);
        call.enqueue(new Callback<UploadResponse>() {
            @Override
            public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                uploadSnackbar.dismiss();
               if(response.body().getMsg().equals("Image uploaded!")){
                   Toast.makeText(getActivity(),"SUCCESS",Toast.LENGTH_SHORT).show();
                   getProfileDetails();
               }
            }

            @Override
            public void onFailure(Call<UploadResponse> call, Throwable t) {
                uploadSnackbar.dismiss();
                Snackbar.make(baseLayout,"Upload failed",Snackbar.LENGTH_SHORT).show();
            }
        });

    }

}
