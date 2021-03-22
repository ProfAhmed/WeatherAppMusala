package com.aosama.weatherapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WeatherModel {
    @SerializedName("icon")
    @Expose
    public String icon;
}
