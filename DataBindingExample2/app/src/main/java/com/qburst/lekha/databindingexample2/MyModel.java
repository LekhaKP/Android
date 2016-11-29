package com.qburst.lekha.databindingexample2;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

public class MyModel extends BaseObservable {
    @Bindable
    private String data;

    public MyModel(String data) {
        setData(data);
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
        notifyPropertyChanged(com.qburst.lekha.databindingexample2.BR.data);
    }
}
