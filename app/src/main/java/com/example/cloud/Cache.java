package com.example.cloud;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Locale;

public class Cache extends AppCompatActivity {
    private static HashMap<String, String> sPinyinToCharater;
    private static String sLocation;
    private static String sLocationID;
    private static String sUnit;
    private static boolean isNotification;
    private static final String KEY = "9519361969dc4d6494ccb031a086c47e";

    public static boolean isCityValid(String city) {
        return sPinyinToCharater.containsKey(city);
    }

    public static String pinyinToCharater(String pinyin) {
        return sPinyinToCharater.get(pinyin.toLowerCase(Locale.ROOT));
    }

    public static String getLocationID() {
        return sLocationID;
    }

    public static void setLocationID(String locationID) {
        sLocationID = locationID;
    }

    public static void addToPinyinToCharater(String pinyin, String cityName) {
        if(sPinyinToCharater == null) {
            sPinyinToCharater = new HashMap<String, String>();
        }
        sPinyinToCharater.put(pinyin, cityName);
    }

    public static String getLocation() {
        return sLocation;
    }

    public static void setLocation(String mLocation) {
        Cache.sLocation = mLocation;
    }

    public static String getUnit() {
        return sUnit;
    }

    public static void setUnit(String mUnit) {
        Cache.sUnit = mUnit;
    }

    public static boolean isNotification() {
        return isNotification;
    }

    public static void setIsNotification(boolean isNotification) {
        Cache.isNotification = isNotification;
    }

    public static void updateSettings(String location, String unit, boolean isNotification) {
        setIsNotification(isNotification);
        setLocation(location);
        setUnit(unit);
    }

    public static String locationInfoQueryUrl() {
        return "https://geoapi.qweather.com/v2/city/lookup?location=" + pinyinToCharater(sLocation) + "&key=" + KEY;
    }

    public static String WeatherInfoQueryUrl() {
        return "https://devapi.qweather.com/v7/weather/7d?location=" + sLocationID + "&key=" + KEY;
    }
}
