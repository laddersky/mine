package com.example.myapplication;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.TimeZone;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.ViewHolder> {
    ArrayList<EventItem> eventList;
    Context context;

    public EventListAdapter(Context context) {
        this.eventList = new ArrayList<>();
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EventItem item = eventList.get(position);
        holder.titleTextView.setText(item.title);
        holder.calendarImageView.setImageTintList(ColorStateList.valueOf(item.color));
        if (item.allDay) {
            holder.timeTextView.setText("하루종일");
        }
        else {
            holder.timeTextView.setText(convertMStoTimeString(item.start) + " ~ " + convertMStoTimeString(item.end));
        }
    }

    public void setEventList(ArrayList<EventItem> eventList) {
        this.eventList.clear();
        this.eventList.addAll(eventList);
        Log.d("event list adapter", "event list length : " + eventList.size());
        this.notifyDataSetChanged();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    private String convertMStoTimeString(long ms) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.KOREA);
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        return formatter.format(ms);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private TextView timeTextView;
        private ImageView calendarImageView;
        public ViewHolder(View view) {
            super(view);
            titleTextView = view.findViewById(R.id.eventTitle);
            timeTextView = view.findViewById(R.id.timeTextView);
            calendarImageView = view.findViewById(R.id.calendarImageView);
        }
    }
}
