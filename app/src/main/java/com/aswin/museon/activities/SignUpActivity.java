package com.aswin.museon.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.aswin.museon.Constants;
import com.aswin.museon.R;
import com.aswin.museon.api.MuseOnClient;
import com.aswin.museon.models.SignUpResponse;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

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

public class SignUpActivity extends AppCompatActivity implements Constants {

    private RelativeLayout profilePicLayout;
    private SimpleDraweeView profilePicView;
    private static final int REQ_STORAGE=1574;
    private EditText ed_userName,ed_mobile;
    private Button signupButton;
    private boolean isProfilePicSet=false,isPNG=true;
    private CoordinatorLayout baseLayout;
    private Uri profilePicUri;
    private SharedPreferences preferences;
    private String uname,umobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        profilePicView=findViewById(R.id.profilePic);
        profilePicLayout=findViewById(R.id.profilePicLayout);
        ed_mobile=findViewById(R.id.ed_Mobile);
        ed_userName=findViewById(R.id.ed_userName);
        signupButton=findViewById(R.id.signUpButton);
        baseLayout=findViewById(R.id.baseLayout);
        preferences=getSharedPreferences("myPrefs",MODE_PRIVATE);

        profilePicLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(SignUpActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(SignUpActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {

                        ActivityCompat.requestPermissions(SignUpActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQ_STORAGE);

                    } else {

                        ActivityCompat.requestPermissions(SignUpActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                REQ_STORAGE);

                    }
                }else {
                    pickImage();
                }
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username,mobile;
                boolean isValid=true;
                username=ed_userName.getText().toString().trim();
                mobile=ed_mobile.getText().toString().trim();

                if(username.equals("")){
                    ed_userName.setError("Enter username");
                    isValid=false;
                }

                if(mobile.equals("")){
                    ed_mobile.setError("Enter phone number");
                    isValid=false;
                }else if(mobile.length()!=10){
                    ed_mobile.setError("Enter valid phone number");
                    isValid=false;
                }
                if(isValid){
                    if(isProfilePicSet){

                        uname=username;
                        umobile=mobile;
                        startRegistration(username,mobile);

                    }else {
                        Snackbar.make(baseLayout,"Please set a profile picture",Snackbar.LENGTH_SHORT).show();
                    }
                }else {
                    Snackbar.make(baseLayout,"There are errors in form",Snackbar.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void startRegistration(final String username, String mobile) {

        MediaType mediaType=MediaType.parse("image/*");

        Retrofit.Builder builder=new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL);

        Retrofit retrofit=builder.build();
        MuseOnClient museOnClient=retrofit.create(MuseOnClient.class);

        File file=new File(profilePicUri.getPath());
        RequestBody requestFile =
                RequestBody.create(
                        mediaType,
                        file
                );
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("userfile ", file.getName(), requestFile);

        RequestBody userName =
                RequestBody.create(
                        okhttp3.MultipartBody.FORM, username);

        RequestBody Mobile =
                RequestBody.create(
                        okhttp3.MultipartBody.FORM, mobile);

        Call<SignUpResponse> call=museOnClient.registerUser(userName, Mobile,body);
        call.enqueue(new Callback<SignUpResponse>() {
            @Override
            public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
                    String msg=response.body().getMsg();
                    if(msg.equals("Client Successfully Registered!")) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("username", uname);
                        editor.putString("mobile", umobile);
                        editor.putString("profilePic", profilePicUri.getPath());
                        editor.apply();
                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
            }

            @Override
            public void onFailure(Call<SignUpResponse> call, Throwable t) {
                Log.d("######", "onFailure: failed");
            }
        });

    }


    private void pickImage(){
        TedBottomPicker tedBottomPicker = new TedBottomPicker.Builder(SignUpActivity.this)
                .setOnImageSelectedListener(new TedBottomPicker.OnImageSelectedListener() {
                    @Override
                    public void onImageSelected(Uri uri) {

                        if(uri.getLastPathSegment().contains("png") || uri.getLastPathSegment().contains("jpg")){
                            profilePicUri=uri;
                            profilePicView.setImageURI(profilePicUri);
                            isProfilePicSet=true;
                        }else {
                            Snackbar.make(baseLayout,"Select a png or jpg file",Snackbar.LENGTH_SHORT).show();
                        }
                    }
                })
                .create();

        tedBottomPicker.show(getSupportFragmentManager());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==REQ_STORAGE && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            pickImage();
        }else {
            Snackbar.make(baseLayout,"Permission denied",Snackbar.LENGTH_SHORT).show();
        }
    }
}
