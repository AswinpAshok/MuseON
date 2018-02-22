package com.aswin.museon.activities;

import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ViewSwitcher;

import com.aswin.museon.Constants;
import com.aswin.museon.R;
import com.aswin.museon.api.MuseOnClient;
import com.aswin.museon.models.ProfileResponse;
import com.aswin.museon.recyclerview_components.FileRecyclerAdapter;

import java.io.IOException;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ViewContactActivity extends AppCompatActivity implements Constants{

    private RecyclerView recyclerView;
    private String phone;
    private FileRecyclerAdapter adapter;
    private ViewSwitcher switcher;
    private Snackbar waitSnackbar;
    private CoordinatorLayout baseLayout;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_contact);

        Intent intent=getIntent();
        phone=intent.getStringExtra("phone");

        switcher=findViewById(R.id.switcher);
        recyclerView=findViewById(R.id.fileRecycler);
        baseLayout=findViewById(R.id.baseLayout);
        toolbar=findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(phone);
        }
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));


        adapter=new FileRecyclerAdapter();
        waitSnackbar=Snackbar.make(baseLayout,"Please wait..",Snackbar.LENGTH_INDEFINITE);

        RecyclerView.ItemAnimator itemAnimator=new DefaultItemAnimator();
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setItemAnimator(itemAnimator);
        recyclerView.setLayoutManager(layoutManager);

        getProfileDetails();
    }

    public void getProfileDetails() {

        waitSnackbar.show();
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

                waitSnackbar.dismiss();
                if(response.body().getUploadedImages()!=null) {
                    switcher.setDisplayedChild(0);
                    adapter.setFileList(response.body().getUploadedImages());
                    recyclerView.setAdapter(adapter);
                }else {
                    switcher.setDisplayedChild(1);
                }

            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                waitSnackbar.dismiss();
                Snackbar.make(baseLayout,"Something went wrong",Snackbar.LENGTH_SHORT).show();
            }
        });

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}
