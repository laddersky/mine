package com.example.myapplication;

import java.util.Comparator;

public class ContactedPerson {
    String name;
    long duration;
    public ContactedPerson(String name, long duration) {
        this.name = name;
        this.duration = duration;
    }
    String getName() {
        return this.name;
    }
    void addDuration(long duration) {
        this.duration += duration;
    }

}

class ContactedPersonDurationComparator implements Comparator<ContactedPerson> {
    @Override
    public int compare(ContactedPerson f1, ContactedPerson f2) {
        if (f1.duration < f2.duration) {
            return 1;
        } else if (f1.duration > f2.duration) {
            return -1;
        }
        return 0;
    }
}