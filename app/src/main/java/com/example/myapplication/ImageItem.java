package com.example.myapplication;

public class ImageItem {
    String path;
    long dateAdded;

    public ImageItem(String path, long dateAdded) {
        this.path = path;
        this.dateAdded = dateAdded;
    }

    public String getPath() {
        return this.path;
    }

}
