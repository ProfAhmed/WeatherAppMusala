package com.aosama.weatherapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ApiErrorModel {
    @SerializedName("message")
    @Expose
    public String message;
}
