package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.provider.CalendarContract;
import android.provider.CallLog;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.QuoteSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
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
    int year;
    int month;
    int date;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    PermissionViewModel permissionViewModel;
    ActivityResultLauncher<String> galleryResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
        if (result) {
            permissionViewModel.getIsGalleryAccepted().postValue(true);
        }
        else {
            Toast.makeText(getContext(),  "저장공간 접근 권한을 허용해주세요.", Toast.LENGTH_SHORT).show();
        }
    });
    ActivityResultLauncher<String> calendarResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
        if (result) {
            permissionViewModel.getIsCalendarAccepted().postValue(true);
        }
        else {
            Toast.makeText(getContext(),  "캘린더 접근 권한을 허용해주세요.", Toast.LENGTH_SHORT).show();
        }
    });
    ActivityResultLauncher<String> callLogResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
        if (result) {
            permissionViewModel.getIsCallLogAccepted().postValue(true);
        }
        else {
            Toast.makeText(getContext(), "통화기록 접근 권한을 허용해주세요.", Toast.LENGTH_SHORT).show();
        }
    });
    public Fragment_3() {
    }

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment3, container, false);
        pref = getActivity().getSharedPreferences(MEMO_KEY, Activity.MODE_PRIVATE);
        editor = pref.edit();
        dateText = view.findViewById(R.id.dateText);
        titleText = view.findViewById(R.id.titleText);
        imageEmptyText = view.findViewById(R.id.imageEmptyText);
        eventEmptyText = view.findViewById(R.id.eventEmptyText);
        contactEmptyText = view.findViewById(R.id.contactEmptyText);
        writeButton = view.findViewById(R.id.writeButton);
        EditText multilineText = view.findViewById(R.id.multilineText);
        ConstraintLayout formLayout = view.findViewById(R.id.formLayout);
        Button saveButton = view.findViewById(R.id.saveButton);
        Button cancelButton = view.findViewById(R.id.cancelButton);
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imageEmptyText.setTextColor(Color.GRAY);
        eventEmptyText.setTextColor(Color.GRAY);
        contactEmptyText.setTextColor(Color.GRAY);

        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);
        int currentMonth = cal.get(Calendar.MONTH);
        int currentDate = cal.get(Calendar.DATE);
        CalendarDay todayCalendarDay = CalendarDay.from(currentYear, currentMonth, currentDate);
        MaterialCalendarView calendar = view.findViewById(R.id.calendar);
        LinearLayout linearLayout = view.findViewById(R.id.linearLayout);
        calendar.setSelectedDate(todayCalendarDay);
        calendar.state().edit()
                .isCacheCalendarPositionEnabled(false)
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMaximumDate(CalendarDay.from(currentYear, currentMonth, cal.getActualMaximum(Calendar.DAY_OF_MONTH)))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();
        CalendarMinMaxDecorator minMaxDecorator = new CalendarMinMaxDecorator(todayCalendarDay, getContext());
        CalendarTodayDecorator todayDecorator = new CalendarTodayDecorator(getContext());

        this.fullDateString = changeDateText(cal.getTimeInMillis());
        int lastDate = todayCalendarDay.getCalendar().getActualMaximum(Calendar.DAY_OF_MONTH);
        ArrayList<CalendarDay> calendarDayArrayList = new ArrayList<>();
        for (int i = 1; i <= lastDate; i++) {
            CalendarDay calendarDay = CalendarDay.from(currentYear, currentMonth, currentDate);
            calendarDay.getCalendar().set(Calendar.DATE, i);

            String memo = pref.getString(changeDateText(calendarDay.getCalendar().getTimeInMillis()), "");
            if (!memo.equals("")) {
                Log.d("fragment 3", "add decorator" + calendarDay.getCalendar().get(Calendar.YEAR) + "." + calendarDay.getCalendar().get(Calendar.MONTH) + "." + calendarDay.getCalendar().get(Calendar.DATE));
                calendarDayArrayList.add(calendarDay);
            }
        }
        calendar.addDecorators(minMaxDecorator, todayDecorator, new CalendarEventDecorator(calendarDayArrayList));
        calendar.setOnDateChangedListener((widget, date, selected) -> {
            if (date.getDate().getTime() > minMaxDecorator.getMaxDay().getDate().getTime()) {
                linearLayout.setVisibility(View.GONE);
                Toast.makeText(getContext(), "미래의 한줄 기록은 작성할 수 없어요", Toast.LENGTH_SHORT).show();
            }
            else {
                linearLayout.setVisibility(View.VISIBLE);
                this.year = date.getYear();
                this.month = date.getMonth() + 1;
                this.date = date.getDay();

                this.fullDateString = changeDateText(dateToTimestamp(this.year, this.month, this.date));
                if (cal.get(Calendar.YEAR) == this.year && cal.get(Calendar.MONTH) == this.month - 1 && cal.get(Calendar.DATE) == this.date) {
                    titleText.setText("오늘은 어떤 하루였나요?");
                }
                else {
                    titleText.setText("어떤 하루였나요?");
                }
                getImagePathsByDate(this.year, this.month, this.date);
                getEventsByDate(this.year, this.month, this.date);
                getCallLogsByDate(this.year, this.month, this.date);
                getMemoByDate(this.fullDateString);
                multilineText.setText("");
                writeButton.setVisibility(View.VISIBLE);
                formLayout.setVisibility(View.GONE);
                saveButton.setVisibility(View.GONE);
                cancelButton.setVisibility(View.GONE);
                imm.hideSoftInputFromWindow(multilineText.getWindowToken(), 0);
            }
        });
        calendar.setOnMonthChangedListener((widget, date) -> {
            int lastDate1 = date.getCalendar().getActualMaximum(Calendar.DAY_OF_MONTH);
            ArrayList<CalendarDay> calendarDayArrayList1 = new ArrayList<>();
            for (int i = 1; i <= lastDate1; i++) {
                CalendarDay calendarDay = CalendarDay.from(date.getYear(), date.getMonth(), date.getDay());
                calendarDay.getCalendar().set(Calendar.DATE, i);
                String memo = pref.getString(changeDateText(calendarDay.getCalendar().getTimeInMillis()), "");
                if (!memo.equals("")) {
                    Log.d("fragment 3", "add decorator" + calendarDay.getCalendar().get(Calendar.YEAR) + "." + calendarDay.getCalendar().get(Calendar.MONTH) + "." + calendarDay.getCalendar().get(Calendar.DATE));
                    calendarDayArrayList1.add(calendarDay);
                }
            }
            calendar.addDecorator(new CalendarEventDecorator(calendarDayArrayList1));
        });
        RecyclerView imageListView = view.findViewById(R.id.imageListByDate);
        imageListAdapter = new ImageListAdapter(getContext(), 1);
        imageListView.setAdapter(imageListAdapter);
        imageListView.addItemDecoration(new CirclePagerIndicatorDecoration(getContext()));
        imageListView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(imageListView);
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
        Button getEventButton = view.findViewById(R.id.getEventButton);
        getEventButton.setOnClickListener(view14 -> calendarResultLauncher.launch(Manifest.permission.READ_CALENDAR));

        RecyclerView contactedPeopleView = view.findViewById(R.id.contactedPeopleView);
        contactedPeopleAdapter = new ContactedPeopleAdapter(getContext());
        contactedPeopleView.setAdapter(contactedPeopleAdapter);
        contactedPeopleView.setLayoutManager(new LinearLayoutManager(getContext()));
        contactedPeopleView.addItemDecoration(new ItemBorderDecoration(1F, Color.GRAY));
        Button getContactButton = view.findViewById(R.id.getContactButton);
        getContactButton.setOnClickListener(view15 -> callLogResultLauncher.launch(Manifest.permission.READ_CALL_LOG));


        memoTextView = view.findViewById(R.id.memoTextView);
        memoLayout = view.findViewById(R.id.memoLayout);
        getMemoByDate(this.fullDateString);

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
                ArrayList<CalendarDay> calendarDays = new ArrayList<>();
                calendarDays.add(CalendarDay.from(this.year, this.month - 1, this.date));
                calendar.addDecorator(new CalendarEventDecorator(calendarDays));
                imm.hideSoftInputFromWindow(multilineText.getWindowToken(), 0);
            }
        });

        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            getImagePathsByDate(this.year, this.month, this.date);
            getEventsByDate(this.year, this.month, this.date);
            getCallLogsByDate(this.year, this.month, this.date);
            new Handler().postDelayed(() -> swipeRefreshLayout.setRefreshing(false), 1000);
        });
        NestedScrollView scrollView = view.findViewById(R.id.scrollView);
        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                if (i1 == 0) {
                    swipeRefreshLayout.setEnabled(true);
                }
                else {
                    swipeRefreshLayout.setEnabled(false);
                }
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Calendar cal = Calendar.getInstance();
        this.year = cal.get(Calendar.YEAR);
        this.month = cal.get(Calendar.MONTH) + 1;
        this.date = cal.get(Calendar.DATE);
        permissionViewModel = new ViewModelProvider(getActivity()).get(PermissionViewModel.class);
        permissionViewModel.getIsGalleryAccepted().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                onGalleryPermissionAccepted(view);
            }
        });
        permissionViewModel.getIsCalendarAccepted().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                onCalendarPermissionAccepted(view);
            }
        });
        permissionViewModel.getIsCallLogAccepted().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                onCallLogPermissionAccepted(view);
            }
        });
    }

    private void getEventsByDate(int year, int month, int date) {
        if (!permissionViewModel.getIsCalendarAccepted().getValue()) return;
        long time = dateToTimestamp(year, month, date);
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
    private void getImagePathsByDate(int year, int month, int date) {
        if (!permissionViewModel.getIsGalleryAccepted().getValue()) return;
        Log.d("getImagePathsByDate0", year + "." + month + "." + this.date);
        String minTimestamp = String.valueOf(dateToTimestamp(year, month, date));
        String maxTimestamp = String.valueOf(dateToTimestamp(year, month, date + 1));
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
    private void getCallLogsByDate(int year, int month, int date) {
        if (!permissionViewModel.getIsCallLogAccepted().getValue()) return;
        String minTimestamp = String.valueOf(dateToTimestamp(year, month, date));
        String maxTimestamp = String.valueOf(dateToTimestamp(year, month, date + 1));
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
            Parcel parcel = Parcel.obtain();
            parcel.writeInt(getContext().getColor(R.color.colorAccent)); // quote span stripe color .. adjust this as per your liking
            parcel.writeInt(10); // quote span stripe width .. adjust this as per your liking
            parcel.writeInt(16); // quote span gap with .. adjust this as per your liking
            parcel.setDataPosition(0);
            SpannableString string = new SpannableString(memo);
            QuoteSpan quoteSpan = new QuoteSpan(parcel);
            string.setSpan(quoteSpan, 0, string.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            this.memoTextView.setText("\"" + memo + "\"");
            parcel.recycle(); // put the parcel object back in the pool
            this.writeButton.setText("수정하기");
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
        getImagePathsByDate(year, month, date);
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
        getEventsByDate(year, month, date);
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
        getCallLogsByDate(year, month, date);
    }
    private long dateToTimestamp(int year, int month, int day) {
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