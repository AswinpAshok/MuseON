package com.aswin.museon.fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ViewSwitcher;

import com.aswin.museon.Constants;
import com.aswin.museon.R;
import com.aswin.museon.activities.SignUpActivity;
import com.aswin.museon.api.MuseOnClient;
import com.aswin.museon.models.ContactModel;
import com.aswin.museon.models.RegisteredUsers;
import com.aswin.museon.models.UserModel;
import com.aswin.museon.recyclerview_components.ContactsRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ContactsFragment extends Fragment  implements Constants{

    private static final int REQ_CONTACTS=12584;
    private List<ContactModel> contactList;
    private LinearLayout baseLayout;
    private boolean isContactsFetched=false,isUsersFetched=false;
    private List<UserModel> userList;
    private List<UserModel> registeredContactsList;
    private Dialog waitDialog;
    private RecyclerView contactRecycler;
    private ContactsRecyclerAdapter adapter;
    private RecyclerView.ItemAnimator itemAnimator;
    private RecyclerView.LayoutManager layoutManager;
    private ViewSwitcher switcher;


    public ContactsFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_contacts, container, false);

        contactList=new ArrayList<>();
        registeredContactsList=new ArrayList<>();
        baseLayout=view.findViewById(R.id.baseLayout);
        waitDialog=new Dialog(getActivity());
        waitDialog.setCancelable(false);
        waitDialog.setContentView(R.layout.progress_dialog_layout);
        contactRecycler=view.findViewById(R.id.contactsRecycler);
        switcher=view.findViewById(R.id.switcher);
        adapter=new ContactsRecyclerAdapter();
        itemAnimator=new DefaultItemAnimator();
        layoutManager=new LinearLayoutManager(getActivity());
        contactRecycler.setItemAnimator(itemAnimator);
        contactRecycler.setLayoutManager(layoutManager);

        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_CONTACTS)) {

                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},REQ_CONTACTS);

            } else {

                requestPermissions(
                        new String[]{Manifest.permission.READ_CONTACTS},
                        REQ_CONTACTS);

            }
        }else {
           Contacts contacts=new Contacts();
           contacts.execute();
        }


        getUsers();

        return view;
    }

    private void getUsers() {
        Retrofit.Builder builder=new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL);
        Retrofit retrofit=builder.build();
        MuseOnClient client=retrofit.create(MuseOnClient.class);
        Call<RegisteredUsers> call=client.getRegisteredUsers();
        call.enqueue(new Callback<RegisteredUsers>() {
            @Override
            public void onResponse(Call<RegisteredUsers> call, Response<RegisteredUsers> response) {

                isUsersFetched=true;
                userList=response.body().getList();

                if(isContactsFetched){
                    CrossCheck crossCheck=new CrossCheck();
                    crossCheck.execute();
                }
            }

            @Override
            public void onFailure(Call<RegisteredUsers> call, Throwable t) {
                final Snackbar snackBar = Snackbar.make(baseLayout, "Something went wrong..", Snackbar.LENGTH_INDEFINITE);

                snackBar.setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snackBar.dismiss();
                        getUsers();
                    }
                });
                snackBar.show();

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==REQ_CONTACTS && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            Contacts contacts=new Contacts();
            contacts.execute();

        }else {
            Snackbar.make(baseLayout,"Permission denied",Snackbar.LENGTH_SHORT).show();
        }
    }

    private class Contacts extends AsyncTask<String,String,String>{

        @Override
        protected void onPreExecute() {
            waitDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            ContentResolver cr = getActivity().getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                    null, null, null, null);

            if ((cur != null ? cur.getCount() : 0) > 0) {
                while (cur != null && cur.moveToNext()) {
                    String id = cur.getString(
                            cur.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cur.getString(cur.getColumnIndex(
                            ContactsContract.Contacts.DISPLAY_NAME));

                    if (cur.getInt(cur.getColumnIndex(
                            ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                        Cursor pCur = cr.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{id}, null);
                        while (pCur.moveToNext()) {
                            String phoneNo = pCur.getString(pCur.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER));
                            ContactModel model=new ContactModel();
                            model.setName(name);
                            model.setPhone(phoneNo);
                            contactList.add(model);
                        }
                        pCur.close();
                    }
                }
            }
            if(cur!=null){
                cur.close();
            }
            return null;
        }



        @Override
        protected void onPostExecute(String s) {
            isContactsFetched=true;
            if(isUsersFetched){
                CrossCheck crossCheck=new CrossCheck();
                crossCheck.execute();
            }
        }
    }

    private class CrossCheck extends AsyncTask{

        @Override
        protected Object doInBackground(Object[] objects) {
            for(int i=0;i<userList.size();i++){

                UserModel userModel=userList.get(i);

                if(!userModel.getMobile().equals("")) {
                    innerloop:
                    for (int j = 0; j < contactList.size(); j++) {

                        ContactModel contactModel = contactList.get(j);
                        if (contactModel.getPhone().contains(userModel.getMobile())) {
                            if(!registeredContactsList.contains(userModel)) {
                                registeredContactsList.add(userModel);
                            }

                            break innerloop;
                        }
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            waitDialog.dismiss();

            if(registeredContactsList.size()<1){
                switcher.setDisplayedChild(1);
            }else {
                switcher.setDisplayedChild(0);
                adapter.setUserList(registeredContactsList);
                contactRecycler.setAdapter(adapter);
            }
        }
    }

}
