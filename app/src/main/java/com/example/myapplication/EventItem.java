package com.example.myapplication;

public class EventItem {
    String title;
    boolean allDay;
    long start;
    long end;
    String desc;
    String location;
    int color;

    public EventItem(String title, boolean allDay, long start, long end, String location, String desc, int color) {
        this.title = title;
        this.allDay = allDay;
        this.start = start;
        this.end = end;
        this.desc = desc;
        this.location = location;
        this.color = color;
    }
}
