package com.example.myapplication;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ContactedPeopleAdapter extends RecyclerView.Adapter<ContactedPeopleAdapter.ViewHolder> {
    ArrayList<ContactedPerson> contactedPeople;
    Context context;

    public ContactedPeopleAdapter(Context context) {
        this.contactedPeople = new ArrayList<>();
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contacted_person, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ContactedPerson item = contactedPeople.get(position);
        holder.nameTextView.setText(item.name);
        holder.phoneNumberTextView.setText(item.phoneNumber);
        long duration = item.duration;
        int seconds = (int) (duration) % 60 ;
        int minutes = (int) ((duration / 60) % 60);
        int hours   = (int) ((duration / (60 * 60)) % 24);
        String durationText = " ";
        if (hours != 0) {
            durationText += hours + "h";
        }
        if (hours != 0 || minutes != 0) {
            durationText += " " + minutes + "m";
        }
        if (hours != 0 || minutes != 0 || seconds != 0) {
            durationText += " " + seconds + "s";
        }
        holder.durationTextView.setText("\uD83D\uDCDE" + durationText);
    }

    public void setContactedPeople(ArrayList<ContactedPerson> contactedPeople) {
        this.contactedPeople.clear();
        this.contactedPeople.addAll(contactedPeople);
        Log.d("contacted person adapter", "contacted person list length : " + contactedPeople.size());
        this.notifyDataSetChanged();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return contactedPeople.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private TextView phoneNumberTextView;
        private TextView durationTextView;
        public ViewHolder(View view) {
            super(view);
            nameTextView = view.findViewById(R.id.nameTextView);
            durationTextView = view.findViewById(R.id.durationTextView);
            phoneNumberTextView = view.findViewById(R.id.phoneNumber);
        }
    }
}
