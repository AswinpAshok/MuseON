package com.aswin.museon.recyclerview_components;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aswin.museon.R;
import com.aswin.museon.activities.ViewContactActivity;
import com.aswin.museon.models.UserModel;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * Created by ASWIN on 2/22/2018.
 */

public class ContactsRecyclerAdapter extends RecyclerView.Adapter<ContactsRecyclerAdapter.MyViewHolder>{

    private List<UserModel> userList;

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.contacts_row,parent,false);
        return new MyViewHolder(view);
    }

    public void setUserList(List<UserModel> userList) {
        this.userList = userList;
    }

    public ContactsRecyclerAdapter() {

    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final UserModel model=userList.get(position);
        holder.contactName.setText(model.getMobile());

        holder.contactPic.setImageURI(model.getProfile_pic());
        holder.baseLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(holder.baseLayout.getContext().getApplicationContext(), ViewContactActivity.class);
                intent.putExtra("phone",model.getMobile());
                holder.baseLayout.getContext().getApplicationContext().startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    protected class  MyViewHolder extends RecyclerView.ViewHolder{

        private SimpleDraweeView contactPic;
        private TextView contactName;
        private RelativeLayout baseLayout;

        public MyViewHolder(View itemView) {
            super(itemView);

            contactPic=itemView.findViewById(R.id.contactProfilePic);
            contactName=itemView.findViewById(R.id.contactName);
            baseLayout=itemView.findViewById(R.id.baseLayout);

        }
    }
}
