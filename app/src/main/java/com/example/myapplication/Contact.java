package com.example.myapplication;

import androidx.activity.result.contract.ActivityResultContracts;

public class Contact implements Comparable<Contact>{
    private String name, photo, phone, email, note,id;
    public Contact(){

    }

    public Contact(String name, String phone, String photo, String email, String note, String id){
        this.name = name;
        this.phone = phone;
        this.photo = photo;
        this.email = email;
        this.note = note;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getPhone(){
        return phone;
    }
    public void setPhone(String phone){
        this.phone = phone;
    }
    public String getPhoto(){
        return photo;
    }

    @Override
    public int compareTo(Contact contact) {
        if (contact.name.compareTo(this.name) > 0) {
            return -1;
        }
        return 1;
    }
}
