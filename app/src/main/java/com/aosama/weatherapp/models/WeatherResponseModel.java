package com.aosama.weatherapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherResponseModel {
    @SerializedName("weather")
    @Expose
    public List<WeatherModel> weather = null;
    @SerializedName("main")
    @Expose
    public MainModel main;
    @SerializedName("name")
    @Expose
    public String name;

}
