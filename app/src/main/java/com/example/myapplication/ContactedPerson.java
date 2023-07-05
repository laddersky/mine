package com.example.myapplication;

import java.util.Comparator;

public class ContactedPerson {
    String name;
    long duration;
    String phoneNumber;
    public ContactedPerson(String name, long duration, String phoneNumber) {
        this.name = name;
        this.duration = duration;
        this.phoneNumber = phoneNumber;
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