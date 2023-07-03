package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CallLog;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Dimension;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class Fragment_3 extends Fragment {
    ImageListAdapter imageListAdapter;
    EventListAdapter eventListAdapter;
    ContactedPeopleAdapter contactedPeopleAdapter;
    TextView dateText;
    TextView titleText;
    TextView imageEmptyText;
    TextView eventEmptyText;
    TextView contactEmptyText;
    TextView memoTextView;
    Button writeButton;
    LinearLayout memoLayout;
    String MEMO_KEY = "memo";
    String fullDateString;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    ActivityResultLauncher<String> galleryResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
        if (result) {
            onGalleryPermissionAccepted(null);
        }
        else {
            Toast.makeText(getContext(),  "설정에서 접근 권한을 허용해주세요.", Toast.LENGTH_SHORT).show();
        }
    });
    ActivityResultLauncher<String> calendarResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
        if (result) {
            onCalendarPermissionAccepted(null);
        }
        else {
            Toast.makeText(getContext(),  "설정에서 접근 권한을 허용해주세요.", Toast.LENGTH_SHORT).show();
        }
    });
    ActivityResultLauncher<String> callLogResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
        if (result) {
            onCallLogPermissionAccepted(null);
        }
        else {
            Toast.makeText(getContext(), "설정에서 접근 권한을 허용해주세요", Toast.LENGTH_SHORT).show();
        }
    });
    public Fragment_3() {
    }

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment3, container, false);
        dateText = view.findViewById(R.id.dateText);
        titleText = view.findViewById(R.id.titleText);
        imageEmptyText = view.findViewById(R.id.imageEmptyText);
        eventEmptyText = view.findViewById(R.id.eventEmptyText);
        contactEmptyText = view.findViewById(R.id.contactEmptyText);
        writeButton = view.findViewById(R.id.writeButton);
        imageEmptyText.setTextColor(Color.GRAY);
        eventEmptyText.setTextColor(Color.GRAY);
        contactEmptyText.setTextColor(Color.GRAY);
        CalendarView calendarView = view.findViewById(R.id.calendarView);
        Calendar cal = Calendar.getInstance();
        calendarView.setMaxDate(cal.getTimeInMillis());
        this.fullDateString = changeDateText(calendarView.getDate());
        calendarView.setOnDateChangeListener((calendarView1, i, i1, i2) -> {
            this.fullDateString = changeDateText(dateToTimestamp(i2, i1 + 1, i));
            String minTimestamp = String.valueOf(dateToTimestamp(i2, i1 + 1, i));
            String maxTimestamp = String.valueOf(dateToTimestamp(i2 + 1, i1 + 1, i));
            getImagePathsByDate(minTimestamp, maxTimestamp);
            if (cal.get(Calendar.YEAR) == i && cal.get(Calendar.MONTH) == i1 && cal.get(Calendar.DATE) == i2) {
                titleText.setText("오늘은 어떤 하루였나요?");
            }
            else {
                titleText.setText("어떤 하루였나요?");
            }
            getEventsByDate(dateToTimestamp(i2, i1 + 1, i));
            // TODO: get call and message log
            getCallLogsByDate(minTimestamp, maxTimestamp);
            getMemoByDate(this.fullDateString);
        });
        RecyclerView imageListView = view.findViewById(R.id.imageListByDate);
        imageListAdapter = new ImageListAdapter(getContext(), 1);
        imageListView.setAdapter(imageListAdapter);
        imageListView.addItemDecoration(new CirclePagerIndicatorDecoration());
        imageListView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(imageListView);
        checkGalleryPermission(view);
        Button getImageButton = view.findViewById(R.id.getImageButton);
        getImageButton.setOnClickListener(view1 -> {
            String permission;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permission = Manifest.permission.READ_MEDIA_IMAGES;
            }
            else {
                permission = Manifest.permission.READ_EXTERNAL_STORAGE;
            }
            galleryResultLauncher.launch(permission);
        });

        RecyclerView eventListView = view.findViewById(R.id.eventListView);
        eventListAdapter = new EventListAdapter(getContext());
        eventListView.setAdapter(eventListAdapter);
        eventListView.setLayoutManager(new LinearLayoutManager(getContext()));
        eventListView.addItemDecoration(new ItemBorderDecoration(1F, Color.GRAY));
        checkCalendarPermission(view);
        Button getEventButton = view.findViewById(R.id.getEventButton);
        getEventButton.setOnClickListener(view14 -> calendarResultLauncher.launch(Manifest.permission.READ_CALENDAR));

        RecyclerView contactedPeopleView = view.findViewById(R.id.contactedPeopleView);
        contactedPeopleAdapter = new ContactedPeopleAdapter(getContext());
        contactedPeopleView.setAdapter(contactedPeopleAdapter);
        contactedPeopleView.setLayoutManager(new LinearLayoutManager(getContext()));
        contactedPeopleView.addItemDecoration(new ItemBorderDecoration(1F, Color.GRAY));
        checkCallLogPermission(view);

        pref = getActivity().getSharedPreferences(MEMO_KEY, Activity.MODE_PRIVATE);
        editor = pref.edit();

        memoTextView = view.findViewById(R.id.memoTextView);
        memoLayout = view.findViewById(R.id.memoLayout);
        memoTextView.setTextSize(Dimension.SP, 19);
        getMemoByDate(this.fullDateString);
        EditText multilineText = view.findViewById(R.id.multilineText);
        ConstraintLayout formLayout = view.findViewById(R.id.formLayout);
        Button saveButton = view.findViewById(R.id.saveButton);
        Button cancelButton = view.findViewById(R.id.cancelButton);
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        writeButton.setOnClickListener(view1 -> {
            if (writeButton.getText().equals("수정하기")) {
                multilineText.setText(memoTextView.getText());
            }
            formLayout.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.VISIBLE);
            cancelButton.setVisibility(View.VISIBLE);
            writeButton.setVisibility(View.GONE);
            memoLayout.setVisibility(View.GONE);
        });
        cancelButton.setOnClickListener(view12 -> {
            multilineText.setText("");
            writeButton.setVisibility(View.VISIBLE);
            formLayout.setVisibility(View.GONE);
            saveButton.setVisibility(View.GONE);
            cancelButton.setVisibility(View.GONE);
            getMemoByDate(this.fullDateString);
            imm.hideSoftInputFromWindow(multilineText.getWindowToken(), 0);
        });
        saveButton.setOnClickListener(view13 -> {
            String text = multilineText.getText().toString();
            if (text.length() == 0) {
                Toast.makeText(getContext(), "내용을 입력해주세요", Toast.LENGTH_SHORT).show();
            }
            else {
                editor.putString(this.fullDateString, text);
                editor.apply();
                getMemoByDate(this.fullDateString);
                multilineText.setText("");
                writeButton.setVisibility(View.VISIBLE);
                writeButton.setText("수정하기");
                formLayout.setVisibility(View.GONE);
                saveButton.setVisibility(View.GONE);
                cancelButton.setVisibility(View.GONE);
            }
            imm.hideSoftInputFromWindow(multilineText.getWindowToken(), 0);
        });
        return view;
    }

    private void getEventsByDate(long time) {
        String[] projection = new String[] { CalendarContract.Events.CALENDAR_ID, CalendarContract.Events.TITLE, CalendarContract.Events.DESCRIPTION, CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND, CalendarContract.Events.ALL_DAY, CalendarContract.Events.EVENT_LOCATION, CalendarContract.Events.DISPLAY_COLOR };

        Calendar startTime = Calendar.getInstance();
        startTime.setTimeInMillis(time);
        startTime.set(Calendar.HOUR_OF_DAY,0);
        startTime.set(Calendar.MINUTE,0);
        startTime.set(Calendar.SECOND, 0);

        Calendar endTime= Calendar.getInstance();
        endTime.setTimeInMillis(startTime.getTimeInMillis());
        endTime.add(Calendar.DATE, 1);

        String selection = "(( " + CalendarContract.Events.DTSTART + " >= " + startTime.getTimeInMillis() + " ) AND ( " + CalendarContract.Events.DTSTART + " < " + endTime.getTimeInMillis() + " ) AND ( deleted != 1 ))";
        Cursor cursor = getContext().getContentResolver().query(CalendarContract.Events.CONTENT_URI, projection, selection, null, CalendarContract.Events.DTSTART + " ASC");

        ArrayList<EventItem> events = new ArrayList<>();
        int titleIndex = cursor.getColumnIndexOrThrow(CalendarContract.Events.TITLE);
        int allDayIndex = cursor.getColumnIndexOrThrow(CalendarContract.Events.ALL_DAY);
        int startIndex = cursor.getColumnIndexOrThrow(CalendarContract.Events.DTSTART);
        int endIndex = cursor.getColumnIndexOrThrow(CalendarContract.Events.DTEND);
        int locationIndex = cursor.getColumnIndexOrThrow(CalendarContract.Events.EVENT_LOCATION);
        int descIndex = cursor.getColumnIndexOrThrow(CalendarContract.Events.DESCRIPTION);
        int colorIndex = cursor.getColumnIndexOrThrow(CalendarContract.Events.DISPLAY_COLOR);

        while (cursor.moveToNext()) {
            String title = cursor.getString(titleIndex);
            boolean allDay = cursor.getInt(allDayIndex) != 0;
            long start = cursor.getLong(startIndex);
            long end = cursor.getLong(endIndex);
            String location = cursor.getString(locationIndex);
            String desc = cursor.getString(descIndex);
            int color = cursor.getInt(colorIndex);
            events.add(new EventItem(title, allDay, start, end, location, desc, color));
        }
        if (events.size() != 0) {
            eventEmptyText.setVisibility(View.GONE);
        }
        else {
            eventEmptyText.setVisibility(View.VISIBLE);
        }
        cursor.close();
        eventListAdapter.setEventList(events);
    }
    private void getImagePathsByDate(String minTimestamp, String maxTimestamp) {
        ArrayList<ImageItem> imageList = new ArrayList<>();
        String selection = MediaStore.Images.Media.DATE_TAKEN + " >= ?" + " AND " + MediaStore.Images.Media.DATE_TAKEN + " < ?";
        String[] selectionArgs = { minTimestamp, maxTimestamp };
        // Log.d("timestamp", minTimestamp + " " + maxTimestamp);
        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.DATE_TAKEN};
        Cursor cursor = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, MediaStore.Images.Media.DATE_TAKEN + " DESC");
        int columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        while (cursor.moveToNext()) {
            String imagePath = cursor.getString(columnIndexData);
            imageList.add(new ImageItem(imagePath));
        }
        if (imageList.size() != 0) {
            imageEmptyText.setVisibility(View.GONE);
        }
        else {
            imageEmptyText.setVisibility(View.VISIBLE);
        }
        cursor.close();
        imageListAdapter.setImageList(imageList);
    }
    private void getCallLogsByDate(String minTimestamp, String maxTimestamp) {
        String[] projection = { CallLog.Calls.DATE, CallLog.Calls.CACHED_NAME, CallLog.Calls.DURATION, CallLog.Calls.NUMBER, CallLog.Calls._ID };
        String selection = CallLog.Calls.DATE + " >= ?" + " AND " + CallLog.Calls.DATE + " < ?";
        String[] selectionArgs = { minTimestamp, maxTimestamp };
        Cursor cursor = getActivity().getContentResolver().query(CallLog.Calls.CONTENT_URI, projection, selection, selectionArgs, CallLog.Calls.DATE + " ASC");
        int nameIndex = cursor.getColumnIndexOrThrow(CallLog.Calls.CACHED_NAME);
        int durationIndex = cursor.getColumnIndexOrThrow(CallLog.Calls.DURATION);
        ArrayList<ContactedPerson> contactedPeople = new ArrayList<>();
        while (cursor.moveToNext()) {
            boolean isExist = false;
            String name = cursor.getString(nameIndex);
            if (name == null) continue;
            long duration = cursor.getLong(durationIndex);
            for (int i = 0; i < contactedPeople.size(); i++) {
                ContactedPerson item = contactedPeople.get(i);
                if (item.getName().equals(name)) {
                    item.addDuration(duration);
                    isExist = true;
                    break;
                }
            }
            if (!isExist) {
                contactedPeople.add(new ContactedPerson(name, duration));
            }
        }
        if (contactedPeople.size() != 0) {
            contactEmptyText.setVisibility(View.GONE);
        }
        else {
            contactEmptyText.setVisibility(View.VISIBLE);
        }
        cursor.close();
        contactedPeople.sort(new ContactedPersonDurationComparator());
        contactedPeopleAdapter.setContactedPeople(contactedPeople);
    }
    private void getMemoByDate(String fullDateString) {
        String memo = pref.getString(fullDateString, "");
        if (memo.equals("")) {
            this.memoLayout.setVisibility(View.GONE);
            this.writeButton.setText("기록하기");
        }
        else {
            this.memoLayout.setVisibility(View.VISIBLE);
            this.memoTextView.setText(memo);
            this.writeButton.setText("수정하기");
        }
    }
    private void checkGalleryPermission(View view) {
        String permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permission = Manifest.permission.READ_MEDIA_IMAGES;
        }
        else {
            permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        }

        if (ContextCompat.checkSelfPermission(getActivity(), permission)
                == PackageManager.PERMISSION_GRANTED) {
            onGalleryPermissionAccepted(view);
        }
        else if (shouldShowRequestPermissionRationale(permission)) {
            Toast.makeText(getContext(), "저장공간 접근 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
        }
        else {
            galleryResultLauncher.launch(permission);
        }
    }
    private void checkCalendarPermission(View view) {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            onCalendarPermissionAccepted(view);
        }
        else if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CALENDAR)) {
            Toast.makeText(getContext(), "캘린더 접근 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
        }
        else {
            calendarResultLauncher.launch(Manifest.permission.READ_CALENDAR);
        }
    }
    private void checkCallLogPermission(View view) {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
            onCallLogPermissionAccepted(view);
        }
        else if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CALL_LOG)) {
            Toast.makeText(getContext(), "통화 기록 접근 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
        }
        else {
            callLogResultLauncher.launch(Manifest.permission.READ_CALL_LOG);
        }
    }
    private void onGalleryPermissionAccepted(View view) {
        Button button;
        if (view == null) {
            button = getView().findViewById(R.id.getImageButton);
        }
        else {
            button = view.findViewById(R.id.getImageButton);
        }
        button.setVisibility(View.GONE);
        Calendar cal = Calendar.getInstance();
        String minTimestamp = String.valueOf(dateToTimestamp(cal.get(Calendar.DATE), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR)));
        String maxTimestamp = String.valueOf(dateToTimestamp(cal.get(Calendar.DATE) + 1, cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR)));
        getImagePathsByDate(minTimestamp, maxTimestamp);
    }
    private void onCalendarPermissionAccepted(View view) {
        Button button;
        if (view == null) {
            button = getView().findViewById(R.id.getEventButton);
        }
        else {
            button = view.findViewById(R.id.getEventButton);
        }
        button.setVisibility(View.GONE);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        getEventsByDate(cal.getTimeInMillis());
    }
    private void onCallLogPermissionAccepted(View view) {
        Button button;
        if (view == null) {
            button = getView().findViewById(R.id.getContactButton);
        }
        else {
            button = view.findViewById(R.id.getContactButton);
        }
        button.setVisibility(View.GONE);
        Calendar cal = Calendar.getInstance();
        String minTimestamp = String.valueOf(dateToTimestamp(cal.get(Calendar.DATE), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR)));
        String maxTimestamp = String.valueOf(dateToTimestamp(cal.get(Calendar.DATE) + 1, cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR)));
        getCallLogsByDate(minTimestamp, maxTimestamp);
    }
    private long dateToTimestamp(int day, int month, int year) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        try {
            Date date = formatter.parse(day + "." + month + "." + year);
            if (date != null) {
                return date.getTime();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
    private String changeDateText(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        String monthString = Integer.toString(cal.get(Calendar.MONTH) + 1);
        String dateString = Integer.toString(cal.get(Calendar.DATE));
        String fullDateString = cal.get(Calendar.YEAR) + "." + (monthString.length() == 1 ? "0" + monthString : monthString) + "." + (dateString.length() == 1 ? "0" + dateString : dateString);
        dateText.setText(fullDateString);
        return fullDateString;
    }
}