
package com.example.myapplication;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Detail_Contact extends AppCompatActivity {
    private Intent intent;
    private String name, photo, phone, email, note,contactID;
    boolean Is_add;
    private CircleImageView Photo;
    private EditText Name,Phone,Email,Note;
    FloatingActionButton back_btn,save_btn;
    ActivityResultLauncher<Intent> activityResultLauncher;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_contact); //전환된 xml

        intent = getIntent();
        name = intent.getStringExtra("name");
        phone = intent.getStringExtra("phone_number");
        photo = intent.getStringExtra("photo");
        email = intent.getStringExtra("email");
        note = intent.getStringExtra("note");
        contactID = intent.getStringExtra("id");
        Is_add = intent.getBooleanExtra("add",false);

        back_btn = findViewById(R.id.Back);
        save_btn = findViewById(R.id.Save);

        Name = findViewById(R.id.Name);
        Phone = findViewById(R.id.Phone);
        Photo = findViewById(R.id.Photo);
        Email = findViewById(R.id.Email);
        Note = findViewById(R.id.Note);

        Name.setText(name);
        Phone.setText(phone);
        Email.setText(email);
        Note.setText(note);


        Cursor mCursor;
        // The index of the lookup key column in the cursor
        int lookupKeyIndex;
        // The index of the contact's _ID value
        int idIndex;
        // The lookup key from the Cursor
        String currentLookupKey;
        // The _ID value from the Cursor
        long currentId;
        // A content URI pointing to the contact
        Uri selectedContactUri;


        if(photo != null){
            Picasso.get().load(photo).into(Photo);
        } else{
            Photo.setImageResource(R.drawable.phone_number);
        }
        save_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (!Is_add){
                    Store(Name.getText().toString(),Phone.getText().toString(), Email.getText().toString(),
                            Note.getText().toString(),contactID);
                    Toast.makeText(view.getContext(),"저장 되었습니다.",Toast.LENGTH_LONG).show();
                    finish();
                }
                else{
                    Add(Name.getText().toString(),Phone.getText().toString(), Email.getText().toString(),
                            Note.getText().toString(),contactID);
                    Toast.makeText(view.getContext(),"추가 되었습니다.",Toast.LENGTH_LONG).show();
                    finish();
                }

                //setResult(9001,intent);
            }
        });
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(v.getContext(), Fragment_1.class);
                //v.getContext().startActivity(intent);
                intent.putExtra("name_data", Name.getText().toString());
                intent.putExtra("phone_data", Phone.getText().toString());
                intent.putExtra("email_data", Email.getText().toString());
                intent.putExtra("note_data", Note.getText().toString());
                intent.putExtra("id_data", contactID);
                setResult(9001,intent);
                finish();

            }
        });




    }
    public void Store(String name, String phone, String email, String note, String id){
        ContentResolver contentResolver = getContentResolver();

        ContentValues name_content = new ContentValues();
        String where = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";

        name_content.put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name);
        String name_where_arg[] = new String[]{id,ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE};
        contentResolver.update(ContactsContract.Data.CONTENT_URI, name_content, where, name_where_arg);

        ContentValues phone_content = new ContentValues();
        phone_content.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phone);
        String phone_where_arg[] = new String[]{id,ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE};
        contentResolver.update(ContactsContract.Data.CONTENT_URI, phone_content, where, phone_where_arg);

        ContentValues email_content = new ContentValues();
        email_content.put(ContactsContract.CommonDataKinds.Email.ADDRESS, email);
        String email_where_arg[] = new String[]{id,ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE};
        contentResolver.update(ContactsContract.Data.CONTENT_URI, email_content, where, email_where_arg);

        ContentValues note_content = new ContentValues();
        note_content.put(ContactsContract.CommonDataKinds.Note.NOTE, note);
        String note_where_arg[] = new String[]{id,ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE};
        contentResolver.update(ContactsContract.Data.CONTENT_URI, note_content, where, note_where_arg);



    }
    public void Add(String name, String phone, String email, String note, String id){
        ContentResolver resolver = getContentResolver();

        // 새로운 연락처를 추가할 Operation 리스트를 생성합니다.
        ArrayList<ContentProviderOperation> operations = new ArrayList<>();

        // 연락처의 MIME 타입을 설정합니다.
        operations.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        // 연락처의 이름을 추가합니다.
        operations.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                .build());

        // 연락처의 전화번호를 추가합니다.
        operations.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                .build());

        operations.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Email.ADDRESS, email)
                .build());

        operations.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Note.NOTE, note)
                .build());

        // 연락처를 ContentProvider에 적용합니다.
        try {
            resolver.applyBatch(ContactsContract.AUTHORITY, operations);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
