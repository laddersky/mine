package com.example.myapplication;

import android.content.Context;
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
    Context context;
    public CalendarEventDecorator(ArrayList<CalendarDay> dates, Context context) {
        this.dates = new ArrayList<>(dates);
        this.context = context;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        for (int i = 0; i < dates.size(); i++) {
            CalendarDay item = dates.get(i);
            if (item.getDate().getTime() == day.getDate().getTime()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new DotSpan(6F, context.getColor(R.color.colorPrimary)));
    }

    public void addItem(CalendarDay calendarDay) {
        dates.add(calendarDay);
    }
}
