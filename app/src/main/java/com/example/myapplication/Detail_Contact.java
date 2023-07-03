
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

public class Detail_Contact extends AppCompatActivity {
    private Intent intent;
    private String name, photo, phone, email, note,contactID;
    private ImageView Photo;
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

                /*
                ArrayList<ContentProviderOperation> operations = new ArrayList<>();
                ContentValues values = new ContentValues();
                values.put(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, Name.getText().toString());
                ContentResolver contentResolver = getContentResolver();
                Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(contactID));
                ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(contactUri)
                        .withValues(values);
                operations.add(builder.build());
                try {
                    ContentProviderResult[] results = contentResolver.applyBatch(ContactsContract.AUTHORITY, operations);
                    // 결과 처리
                } catch (RemoteException | OperationApplicationException e) {
                    e.printStackTrace();
                    // 예외 처리
                }*/
                /*
                ContentValues values = new ContentValues();
                values.put(ContactsContract.Contacts.DISPLAY_NAME, Name.getText().toString());
                getContentResolver().update(
                        ContactsContract.Contacts.CONTENT_URI,
                        values,
                        ContactsContract.Contacts._ID + " = ?",
                        new String[]{contactID}
                );*/
                Toast.makeText(view.getContext(), Name.getText().toString(), Toast.LENGTH_SHORT).show();

            }
        });
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                intent = new Intent(v.getContext(), Fragment_1.class);
                intent.putExtra("name_data", Name.getText().toString());
                intent.putExtra("phone_data", Phone.getText().toString());
                intent.putExtra("email_data", Email.getText().toString());
                intent.putExtra("note_data", Note.getText().toString());
                intent.putExtra("id_data", contactID);

                setResult(9001,intent);
                /*
                intent.putExtra("phone_number",Phone.getText().toString());
                //intent.putExtra("photo", Photo.getText().toString());
                if (Email.getText().toString()!= null){
                    intent.putExtra("email",Email.getText().toString());
                    //Toast.makeText(view.getContext(), "됐다", Toast.LENGTH_SHORT).show();
                }
                if (Note.getText().toString() != null){
                    intent.putExtra("note",Note.getText().toString());
                    //Toast.makeText(view.getContext(), "됐다", Toast.LENGTH_SHORT).show();
                }
                //Toast.makeText(v.getContext(), Name.getText().toString(), Toast.LENGTH_SHORT).show();
                //v.getContext().startActivity(intent);

                activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result ->{
                    if (result.getResultCode() == 9001){
                        Intent intent = result.getData();
                        String name = Name.getText().toString();
                    }
                });*/


                finish();

            }
        });




    }

}
