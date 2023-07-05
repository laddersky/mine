package com.example.myapplication;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PermissionViewModel extends ViewModel {
    private MutableLiveData<Boolean> isGalleryAccepted = new MutableLiveData<Boolean>(false);
    private MutableLiveData<Boolean> isCalendarAccepted = new MutableLiveData<Boolean>(false);
    private MutableLiveData<Boolean> isCallLogAccepted = new MutableLiveData<Boolean>(false);

    public MutableLiveData<Boolean> getIsGalleryAccepted() {
        return isGalleryAccepted;
    }

    public MutableLiveData<Boolean> getIsCalendarAccepted() {
        return isCalendarAccepted;
    }

    public MutableLiveData<Boolean> getIsCallLogAccepted() {
        return isCallLogAccepted;
    }
}
