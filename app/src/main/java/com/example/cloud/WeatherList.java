package com.example.cloud;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class WeatherList {
    private static final String TAG = "WeatherList";
    private static WeatherList sWeatherList;
    private List<DayWeather> mDayWeathers;

    public static WeatherList get(Context context) {
        if (sWeatherList == null) {
            sWeatherList = new WeatherList(context);
        }
        return sWeatherList;
    }

    public DayWeather getTodayWeather() {
        return mDayWeathers.get(0);
    }

    public DayWeather get(int index) {
        return mDayWeathers.get(index);
    }

    private WeatherList(Context context) {
        mDayWeathers = new ArrayList<>();
    }

    public List<DayWeather> getWeathers() {
        return mDayWeathers;
    }

    public void clear() {
        mDayWeathers.clear();
    }

    public void add(DayWeather dw) {
        mDayWeathers.add(dw);
    }

    public DayWeather getDayWeather(String date) {
        for (DayWeather dw : mDayWeathers) {
            if (dw.getDate().equals(date)) {
                return dw;
            }
        }
        return null;
    }

    public DayWeather getDayWeather(int index) {
        return mDayWeathers.get(index);
    }

    public ArrayList<DayWeather> getDWListForRecycleView() {
        ArrayList<DayWeather> ret = new ArrayList<DayWeather>();
        for (int i = 0; i < mDayWeathers.size(); ++i) {
            if (i == 0) {
                continue ;
            }
            ret.add(mDayWeathers.get(i));
        }
        return ret;
    }

    public List<DayWeather> getList() {
        return mDayWeathers;
    }

    public List<DayWeather> getDWListForRecycleViewPad() {
        return mDayWeathers;
    }
}
