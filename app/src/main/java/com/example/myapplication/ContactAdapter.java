package com.example.myapplication;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder>{
    Context mContext;
    private Intent intent;
    List<Contact> contactList;
    private OnItemClickListener mListener = null;
    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }
    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }


    public ContactAdapter(Context mContext, List<Contact> contactList) {
        this.mContext = mContext;
        this.contactList = contactList;
    }

    int a = 0;
    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_contact,parent,false);
        if (a == 0){
            Toast.makeText(this.mContext, "myData", Toast.LENGTH_SHORT).show();
            a++;
        }
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contactList.get(position);
        holder.name_contact.setText(contact.getName());
        holder.phone_contact.setText(contact.getPhone());

        if(contact.getPhoto() != null){
            Picasso.get().load(contact.getPhoto()).into(holder.img_contact);
        } else{
            holder.img_contact.setImageResource(R.drawable.phone_number);
        }


    }



    @Override
    public int getItemCount() {
        return contactList.size();
    }
    public class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView name_contact, phone_contact;
        CircleImageView img_contact;

        public ContactViewHolder(@NonNull View itemView){
            super(itemView);
            name_contact = itemView.findViewById(R.id.name_contact);
            phone_contact = itemView.findViewById(R.id.phone_contact);
            img_contact = itemView.findViewById(R.id.img_contact);

            itemView.setOnClickListener(new View.OnClickListener() {

                /*public void new_page(View view, int pos){

                    intent = new Intent(view.getContext(), Detail_Contact.class);
                    intent.putExtra("name", contactList.get(pos).getName());
                    intent.putExtra("phone_number", contactList.get(pos).getPhone());
                    intent.putExtra("photo", contactList.get(pos).getPhoto());
                    if (contactList.get(pos).getEmail() != null){
                        intent.putExtra("email",contactList.get(pos).getEmail());
                        //Toast.makeText(view.getContext(), "됐다", Toast.LENGTH_SHORT).show();
                    }
                    if (contactList.get(pos).getNote() != null){
                        intent.putExtra("note",contactList.get(pos).getNote());
                        //Toast.makeText(view.getContext(), "됐다", Toast.LENGTH_SHORT).show();
                    }
                    //intent.putExtra("email",contactList.get(pos).getEmail());
                    //Toast.makeText(view.getContext(), null, Toast.LENGTH_SHORT).show();
                    //startActivityForResult(,intent, 1234,null);
                    view.getContext().startActivity(intent);
                }*/
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION){
                        if (mListener != null){
                            mListener.onItemClick(view,pos);

                            //new_page(view, pos);
                        }
                    }
                }
            });
        }
    }



}























