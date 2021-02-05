package com.aosama.weatherapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MainModel {
    @SerializedName("temp")
    @Expose
    public Double temp;
}
