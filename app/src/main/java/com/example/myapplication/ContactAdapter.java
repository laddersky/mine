package com.example.myapplication;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.squareup.picasso.Picasso;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder>{
    Context mContext;
    private Intent intent;
    List<Contact> contactList;
    boolean display = false;
    public Set<String> delete_list = new HashSet<String>() {};
    View this_view;
    public boolean isImageChanged = false;
    private OnItemClickListener mListener = null;
    public interface OnItemLongClickListener {
        void onItemLongClick(View v,int position);
    }
    private OnItemLongClickListener long_listener;
    public void setOnItemClickListener(OnItemLongClickListener listener) {
        this.long_listener = listener;
    }
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
    public void setFilteredList(List<Contact> filteredList){
        this.contactList = filteredList;
        notifyDataSetChanged();
    }
    public void remove(List<Contact> filteredList){
        this.contactList = filteredList;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_contact,parent,false);

        this.this_view = view;

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
        holder.display(holder.itemView);
        if (display){
            if (delete_list.contains(contactList.get(position).getId())){
                holder.check_box.setImageResource(R.drawable.baseline_check_box_24);
                holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.itemBackgroundSelected));
            }
            else{
                holder.check_box.setImageResource(R.drawable.baseline_crop_square_24);
                holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.itemBackgroundDefault));
            }
        }

    }
    @Override
    public int getItemCount() {
        return contactList.size();
    }
    public class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView name_contact, phone_contact;
        CircleImageView img_contact;
        ImageView check_box;
        public ContactViewHolder(@NonNull View itemView){
            super(itemView);
            name_contact = itemView.findViewById(R.id.name_contact);
            phone_contact = itemView.findViewById(R.id.phone_contact);
            img_contact = itemView.findViewById(R.id.img_contact);
            check_box = itemView.findViewById(R.id.imageView2);
            itemView.setOnClickListener(new View.OnClickListener() {
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
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (long_listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            long_listener.onItemLongClick(v,position);

                            //check_box = v.findViewById(R.id.imageView2);
                            //check_box.setVisibility(View.INVISIBLE);
                            return true;
                        }
                    }
                    return false;
                }
            });
        }
        public void display(View itemview) {
            if (display) {
                check_box.setVisibility(View.VISIBLE);
            }
            else{
                check_box.setVisibility(View.INVISIBLE);
                itemview.setBackgroundColor(ContextCompat.getColor(itemview.getContext(), R.color.itemBackgroundDefault));
                delete_list.clear();
            }
        }
    }
}























