package com.example.myapplication;

import android.graphics.Color;
import android.util.Log;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CalendarEventDecorator implements DayViewDecorator {
    ArrayList<CalendarDay> dates;
    public CalendarEventDecorator(ArrayList<CalendarDay> dates) {
        this.dates = new ArrayList<>(dates);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        Log.d("event decorator", "dates size: " + dates.size());
        for (int i = 0; i < dates.size(); i++) {
            CalendarDay item = dates.get(i);

            if (item.getDate().getTime() == day.getDate().getTime()) {
                Log.d("event decorator", "date: " + day.getCalendar().get(Calendar.YEAR) + "." + day.getCalendar().get(Calendar.MONTH) + "." + day.getCalendar().get(Calendar.DATE));
                Log.d("event decorator", "item: " + item.getCalendar().get(Calendar.YEAR) + "." + item.getCalendar().get(Calendar.MONTH) + "." + item.getCalendar().get(Calendar.DATE));
                return true;

            }
        }
        return false;
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new DotSpan(5F, Color.parseColor("#1D872A")));
    }

    public void addItem(CalendarDay calendarDay) {
        dates.add(calendarDay);
    }
}
