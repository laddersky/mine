package com.example.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.text.style.ForegroundColorSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

public class CalendarMinMaxDecorator implements DayViewDecorator {
    private CalendarDay maxDay;
    Context context;
    public CalendarMinMaxDecorator(CalendarDay maxDay, Context context) {
        this.maxDay = maxDay;
        this.context = context;
    }
    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return (day.getDate().getTime() > maxDay.getDate().getTime());
    }

    @Override
    public void decorate(DayViewFacade view) {

        view.addSpan(new ForegroundColorSpan(context.getColor(R.color.lightGray)));
        view.setDaysDisabled(false);
    }

    public CalendarDay getMaxDay() {
        return this.maxDay;
    }
}
