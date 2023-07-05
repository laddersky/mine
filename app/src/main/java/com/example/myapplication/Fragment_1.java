package com.example.myapplication;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.graphics.Color;
import android.icu.text.Collator;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class Fragment_1 extends Fragment {
    public Fragment_1() {
    }
    RecyclerView recyclerView;
    List<Contact> contactList;
    ContactAdapter adapter;
    private Intent intent;
    boolean clicked = false;
    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }
    private SwipeRefreshLayout swipeRefreshLayout;
    private SearchView searchView;
    private boolean isImageChanged = false;
    private Set<Integer> delete_list;
    private LinearLayout Bottom_bar;

    FloatingActionButton add_contact,add_btn,search_btn,delete_btn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment1, container, false);
        Bottom_bar = view.findViewById(R.id.bottom_bar);

        recyclerView = view.findViewById(R.id.contacts_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        PhDividerItemDecoration itemDecoration = new PhDividerItemDecoration(1F, Color.GRAY);
        recyclerView.addItemDecoration(itemDecoration);

        contactList = new ArrayList<>();
        adapter = new ContactAdapter(getActivity(), contactList);
        recyclerView.setAdapter(adapter);

        Animation rotate_open = AnimationUtils.loadAnimation(getContext(),R.anim.rotate_open_ani);
        Animation rotate_close = AnimationUtils.loadAnimation(getContext(),R.anim.rotate_close_ani);
        Animation from_bottom = AnimationUtils.loadAnimation(getContext(),R.anim.from_bottom_ani);
        Animation to_bottom = AnimationUtils.loadAnimation(getContext(),R.anim.to_bottom_ani);
        Animation[] ani_list = new Animation[]{rotate_open, rotate_close,from_bottom, to_bottom};
        add_contact = view.findViewById(R.id.list);
        add_btn = view.findViewById(R.id.add);
        search_btn = view.findViewById(R.id.search);
        delete_btn = view.findViewById(R.id.delete);



        searchView = view.findViewById(R.id.search_view);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query){
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText){
                filterList(newText);
                return true;
            }
        });
        searchView.setVisibility(View.INVISIBLE);
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                Toast.makeText(getContext(),"Tlqkf",Toast.LENGTH_SHORT).show();
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    // 뒤로가기 버튼이 눌렸을 때의 동작 처리
                    searchView.setVisibility(View.INVISIBLE);
                    return true;
                }
                return false;
            }
        });

        swipeRefreshLayout = view.findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getContacts();
                new Handler().postDelayed(() -> swipeRefreshLayout.setRefreshing(false), 1000);
            }
        });


        adapter.setOnItemClickListener(new ContactAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View v, int position) {
                if (!adapter.display){
                    //delete_list.add(position);
                    //adapter.delete_list = delete_list;
                    adapter.display = true;
                    adapter.delete = position;
                    adapter.delete_list.add(position);
                    adapter.notifyDataSetChanged();
                }

                //adapter.delete(position);
                //contactList..setImageResource(R.drawable.phone_number);
                // 롱클릭된 아이템 처리
                //contactList.get(position).setPhoto(String.valueOf(R.drawable.calendar));
                //contactList.get(position).setPhoto();
                //Photo.setImageResource(R.drawable.phone_number);
                //Toast.makeText(getContext(), "선택된 아이템: " + selectedItem, Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new ContactAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                if (!adapter.display ){
                    intent = new Intent(v.getContext(), Detail_Contact.class);
                    //v.getContext().startActivity(intent);
                    intent.putExtra("add", false);
                    if (contactList.get(pos).getName() != null){
                        intent.putExtra("name", contactList.get(pos).getName());
                        //Toast.makeText(view.getContext(), "됐다", Toast.LENGTH_SHORT).show();
                    }
                    if (contactList.get(pos).getPhone() != null){
                        intent.putExtra("phone_number", contactList.get(pos).getPhone());
                        //Toast.makeText(view.getContext(), "됐다", Toast.LENGTH_SHORT).show();
                    }
                    if (contactList.get(pos).getPhoto() != null){
                        intent.putExtra("photo", contactList.get(pos).getPhoto());
                        //Toast.makeText(view.getContext(), "됐다", Toast.LENGTH_SHORT).show();
                    }
                    intent.putExtra("id", contactList.get(pos).getId());
                    if (contactList.get(pos).getEmail() != null){
                        intent.putExtra("email",contactList.get(pos).getEmail());
                        //Toast.makeText(view.getContext(), "됐다", Toast.LENGTH_SHORT).show();
                    }
                    if (contactList.get(pos).getNote() != null){
                        intent.putExtra("note",contactList.get(pos).getNote());
                    }
                    v.getContext().startActivity(intent);
                }
                else{
                    if (adapter.delete_list.contains(pos)){
                        adapter.delete_list.remove(pos);
                    }
                    else{
                        adapter.delete_list.add(pos);
                    }
                    adapter.notifyDataSetChanged();
                }

            }
        });
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result ->{
            if (result.getResultCode() == 9001){
                Intent intent = result.getData();
                String name = intent.getStringExtra("name_data");
                String email = intent.getStringExtra("email_data");
                String phone = intent.getStringExtra("phone_data");
                String note = intent.getStringExtra("note_data");
                String contactID = intent.getStringExtra("id_data");

                //Toast.makeText(getActivity(),name, Toast.LENGTH_SHORT).show();
                //Toast.makeText(getActivity(),phone, Toast.LENGTH_SHORT).show();
                //change(name,phone,email,note,contactID);
            }
        });

        add_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                on_add_contact_clicked(view, ani_list);
            }
        });

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                intent = new Intent(view.getContext(), Detail_Contact.class);
                intent.putExtra("add", true);
                activityResultLauncher.launch(intent);
                on_add_contact_clicked(view, ani_list);
            }
        });
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                searchView.setVisibility(View.VISIBLE);

                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) searchView.getLayoutParams();
                layoutParams.height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
                searchView.setLayoutParams(layoutParams);


                on_add_contact_clicked(view, ani_list);
            }
        });
        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.display = true;
                Bottom_bar.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
                add_contact.setVisibility(View.INVISIBLE);
                on_add_contact_clicked(view, ani_list);
            }
        });
        Dexter.withActivity(getActivity()).withPermission(Manifest.permission.READ_CONTACTS)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        if (response.getPermissionName().equals(Manifest.permission.READ_CONTACTS)){
                            getContacts();
                        }
                    }
                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(getActivity(),"Permission should be granted", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
        return view;
    }

    private void filterList(String newText) {
        List<Contact> filteredList = new ArrayList<>();
        for (Contact contact : contactList){
            if (contact.getName().toLowerCase().contains(newText.toLowerCase())){
                filteredList.add(contact);
            }
        }
        if (filteredList.isEmpty()){
            //Toast.makeText(getContext(),"No data found", Toast.LENGTH_SHORT).show();
        }
        else{
            adapter.setFilteredList(filteredList);
        }
    }

    private void getContacts() {
        contactList.clear();
        Cursor phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,null,null,null);
        Set<String> ID_list = new HashSet<>();
        while (phones.moveToNext()){
            String Id = phones.getString(phones.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
            if (!ID_list.contains(Id)){

                String name = phones.getString(phones.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = phones.getString(phones.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String phoneUri = phones.getString(phones.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                String Email = get_email(phones.getString(phones.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)));
                String Note = get_Note(phones.getString(phones.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)));

                //Contact contact = new Contact(name, phoneNumber,phoneUri, Email, Note, Id);
                Contact contact = new Contact(name, phoneNumber,phoneUri, Email,Note, Id);
                contactList.add(contact);
                adapter.notifyDataSetChanged();
                ID_list.add(Id);
            }
        }

        Collections.sort(contactList);
        adapter.notifyDataSetChanged();

        /*
        int size = contactList.size();

        for(int i = 0 ; i < size ; i++){
            if (i+1 == size){
                break;
            }
            if (contactList.get(i).getPhone().equals(contactList.get(i+1).getPhone())){
                size--;
                contactList.remove(i);
            }
        }*/

        //contactList.sort(contactList.spliterator().getComparator());
        //contactList.sort(Collections.reverseOrder());
        //adapter.notifyDataSetChanged();
    }
    private String get_email(String contact_id) {
        String where = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
        String email_where_arg[] = new String[]{contact_id,ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE};
        Cursor email = getActivity().getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                null,where,email_where_arg,null);

        if (email.moveToFirst()){
            return  email.getString(email.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.ADDRESS));
        }
        return null;
    }
    private String get_Note(String contact_id) {
        String where = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
        String note_where_arg[] = new String[]{contact_id,ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE};
        Cursor note = getActivity().getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                null,where,note_where_arg,null);

        if (note.moveToFirst()){
            return  note.getString(note.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Note.NOTE));
        }
        return null;
    }
    private void on_add_contact_clicked(View view , Animation[] ani_list){
        setVisibility(view, clicked);
        setAimation(view, clicked, ani_list);
        clicked = !clicked;
    }
    private void setVisibility(View view , Boolean clicked){
        if(!clicked){
            add_btn.setVisibility(view.VISIBLE);
            search_btn.setVisibility(view.VISIBLE);
            delete_btn.setVisibility(view.VISIBLE);
        }
        else{
            add_btn.setVisibility(view.INVISIBLE);
            search_btn.setVisibility(view.INVISIBLE);
            delete_btn.setVisibility(view.INVISIBLE);
        }
    }
    private void setAimation(View view, Boolean clicked , Animation[] ani_list){
        if (!clicked){
            add_btn.startAnimation(ani_list[2]);
            search_btn.startAnimation(ani_list[2]);
            delete_btn.startAnimation(ani_list[2]);
            add_contact.startAnimation(ani_list[0]);

        }else{
            add_btn.startAnimation(ani_list[3]);
            search_btn.startAnimation(ani_list[3]);
            delete_btn.startAnimation(ani_list[3]);
            add_contact.startAnimation(ani_list[1]);

        }
    }

}