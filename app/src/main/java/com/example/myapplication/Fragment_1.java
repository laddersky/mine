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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

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
    private SwipeRefreshLayout swipeRefreshLayout;
    ActivityResultLauncher<Intent> activityResultLauncher;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment1, container, false);
        recyclerView = view.findViewById(R.id.contacts_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        PhDividerItemDecoration itemDecoration = new PhDividerItemDecoration(1F, Color.GRAY);
        recyclerView.addItemDecoration(itemDecoration);
        contactList = new ArrayList<>();
        adapter = new ContactAdapter(getActivity(), contactList);

        recyclerView.setAdapter(adapter);

        swipeRefreshLayout = view.findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                getContacts();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        adapter.setOnItemClickListener(new ContactAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {

                intent = new Intent(v.getContext(), Detail_Contact.class);
                intent.putExtra("name", contactList.get(pos).getName());
                intent.putExtra("phone_number", contactList.get(pos).getPhone());
                intent.putExtra("photo", contactList.get(pos).getPhoto());
                intent.putExtra("id", contactList.get(pos).getId());
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
                //v.getContext().startActivity(intent);
                activityResultLauncher.launch(intent);

            }
        });

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result ->{
            if (result.getResultCode() == 9001){
                Intent intent = result.getData();
                String name = intent.getStringExtra("name_data");
                String email = intent.getStringExtra("email_data");
                String phone = intent.getStringExtra("phone_data");
                String note = intent.getStringExtra("note_data");
                String contactID = intent.getStringExtra("id_data");

                Toast.makeText(getActivity(),name, Toast.LENGTH_SHORT).show();
                Toast.makeText(getActivity(),phone, Toast.LENGTH_SHORT).show();
                change(name,phone,email,note,contactID);
            }
        });
        /*
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<ContentProviderOperation> contentProviderOperations
                        = new ArrayList<ContentProviderOperation>();

                contentProviderOperations.add(ContentProviderOperation
                        .newUpdate(ContactsContract.Data.CONTENT_URI)
                        .withSelection(ContactsContract.Data.DISPLAY_NAME + " = ? AND " +
                                        ContactsContract.Data.MIMETYPE + " = ?",
                                new String[]{"가",
                                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE})
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,
                                "010-123-4567")
                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                        .build());
                try {
                    getActivity().getContentResolver().applyBatch(ContactsContract.AUTHORITY,contentProviderOperations);
                    Toast.makeText(getContext(),"success",Toast.LENGTH_LONG).show();

                }
                catch (OperationApplicationException e){
                    e.printStackTrace();
                }
                catch (RemoteException e){
                    e.printStackTrace();
                }
            }
        });*/
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

    public void change(String name, String phone, String email, String note, String id) {

        // Get the contact name and number from the edit text views.
        // Create a new contact with the given name and number.

        // Finish the activity.

        ArrayList<ContentProviderOperation> contentProviderOperations
                = new ArrayList<ContentProviderOperation>();

        contentProviderOperations.add(ContentProviderOperation
                .newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(ContactsContract.Data.DISPLAY_NAME + " = ? AND " +
                                ContactsContract.Data.MIMETYPE + " = ?",
                        new String[]{"가",
                                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE})
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,
                        "00")
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                .build());
        try {
            getActivity().getContentResolver().applyBatch(ContactsContract.AUTHORITY,contentProviderOperations);
            Toast.makeText(getContext(),"success",Toast.LENGTH_LONG).show();

        }
        catch (OperationApplicationException e){
            e.printStackTrace();
        }
        catch (RemoteException e){
            e.printStackTrace();
        }
    }
    private void getContacts() {
        contactList.clear();
        Cursor phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,null,null,null);
        Set<String> ID_list = new HashSet<>();
        while (phones.moveToNext()){
            int pos = phones.getPosition();

            //고모 140 금준령 131
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            //String phoneNumber = get_Note(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            //String phoneNumber = null;
            String phoneUri = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));

            String Email = get_email(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)));
            //String Note = null;
            String Note = get_Note(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)));
            String Id = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));

            Contact contact = new Contact(name, phoneNumber,phoneUri, Email, Note, Id);
            if (ID_list.contains(Id)){

            }
            else {
                contactList.add(contact);
                adapter.notifyDataSetChanged();
                ID_list.add(Id);
            }
            //contactList.add(contact);
            //adapter.notifyDataSetChanged();
        }
        Collections.sort(contactList);
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
        adapter.notifyDataSetChanged();
        //contactList.sort(contactList.spliterator().getComparator());
        //contactList.sort(Collections.reverseOrder());
        //adapter.notifyDataSetChanged();
    }
    private String get_email(String contact_id) {
        Cursor email = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                null,null,null,null);
        while (email.moveToNext()){
            if (contact_id.equals(email.getString(email.getColumnIndex(ContactsContract.CommonDataKinds.Email.CONTACT_ID)))){
                return email.getString(email.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
            }

        }
        return null;
    }
    private String get_Note(String contact_id) {
        String noteWhere = ContactsContract.Data.CONTACT_ID
                + " = ? AND " + ContactsContract.Data.MIMETYPE
                + " = ?";
        String[] noteWhereParams = new String[] {
                contact_id,
                ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE };

        Cursor note = getActivity().getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                null,noteWhere,noteWhereParams,null);
        while (note.moveToNext()){
            if (contact_id.equals(note.getString(note.getColumnIndex(ContactsContract.CommonDataKinds.Note.CONTACT_ID)))){
                return note.getString(note.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
            }

        }
        return null;
    }
    public void updateContact(String contactId, String newDisplayName) {
        // 연락처의 URI를 가져옴
        Uri contactUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, contactId);

        // ContentResolver 인스턴스 가져오기

        // 수정할 연락처 정보 설정
        ContentValues values = new ContentValues();
        values.put(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, newDisplayName);

        // 연락처 수정 작업 구성
        ArrayList<ContentProviderOperation> operations = new ArrayList<>();
        operations.add(ContentProviderOperation.newUpdate(contactUri)
                .withValues(values)
                .build());

        try {
            // 수정 작업을 ContentProvider에 일괄 적용
            getActivity().getContentResolver().applyBatch(ContactsContract.AUTHORITY, operations);
        } catch (RemoteException | OperationApplicationException e) {
            e.printStackTrace();
            // 예외 처리
        }
    }


}