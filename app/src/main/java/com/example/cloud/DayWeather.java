package com.example.cloud;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DayWeather implements Comparable<DayWeather>, Parcelable {
    private static final String TAG = "DayWeather";
    private String mDate;
    private String mTextDay;
    private String mTemperatureMin;
    private String mTemperatureMax;
    private String mHumidity;
    private String mWindSpeed;
    private String mPressure;

    public DayWeather() { }

    public int compareTo(DayWeather dw) {
        return this.mDate.compareTo(dw.mDate);
    }

    public String getDate(int mode) {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public String getTextDay() {
        return mTextDay;
    }

    public void setTextDay(String textDay) {
        mTextDay = textDay;
    }

    public String getTemperatureMin() {
        if (Cache.getUnit().equals("Celsius")) {
            return mTemperatureMin;
        } else {
            int tmp = Integer.parseInt(mTemperatureMin);
            return String.format("%.0f", 1.8 * tmp + 32);
        }
    }

    public void setTemperatureMin(String tempatureMin) {
        mTemperatureMin = tempatureMin;
    }

    public String getTemperatureMax() {
        if (Cache.getUnit().equals("Celsius")) {
            return mTemperatureMax;
        } else {
            int tmp = Integer.parseInt(mTemperatureMax);
            return String.format("%.0f", 1.8 * tmp + 32);
        }
    }

    public void setTemperatureMax(String tempatureMax) {
        mTemperatureMax = tempatureMax;
    }

    public String getHumidity() {
        return mHumidity;
    }

    public void setHumidity(String humidity) {
        mHumidity = humidity;
    }

    public String getWindSpeed() {
        return mWindSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        mWindSpeed = windSpeed;
    }

    public String getPressure() {
        return mPressure;
    }

    public void setPressure(String pressure) {
        mPressure = pressure;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mDate);
        parcel.writeString(this.mTextDay);
        parcel.writeString(this.mTemperatureMax);
        parcel.writeString(this.mTemperatureMin);
        parcel.writeString(this.mHumidity);
        parcel.writeString(this.mWindSpeed);
        parcel.writeString(this.mPressure);
    }

    protected DayWeather(Parcel parcel) {
        this.mDate = parcel.readString();
        this.mTextDay = parcel.readString();
        this.mTemperatureMax = parcel.readString();
        this.mTemperatureMin = parcel.readString();
        this.mHumidity = parcel.readString();
        this.mWindSpeed = parcel.readString();
        this.mPressure = parcel.readString();
    }

    public static final Parcelable.Creator<DayWeather> CREATOR = new Parcelable.Creator<DayWeather>() {

        @Override
        public DayWeather createFromParcel(Parcel parcel) {
            return new DayWeather(parcel);
        }

        @Override
        public DayWeather[] newArray(int size) {
            return new DayWeather[size];
        }
    };

    public String getDate() {
        String[] m = new String[] {"Jan.", "Feb.", "Mar.", "Apr.", "May.", "Jun.", "Jul.", "Aug.", "Sept.", "Oct.", "Nov.", "Dec."};
        String[] s = mDate.split("-");
        int month = Integer.parseInt(s[1]);
        String ret = "";
        ret += s[2];
        ret += ", ";
        ret += m[month - 1];
        return ret;
    }

    public String getFriendlyDate() {
        SimpleDateFormat f = new SimpleDateFormat("yy-MM-dd");
        String[] weekDays = { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };

        Calendar cal = Calendar.getInstance(); // 获得一个日历
        Date date = null;
        try {
            date = f.parse(mDate);
            cal.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.e(TAG, mDate);
        int w = cal.get(Calendar.DAY_OF_WEEK); // 指示一个星期中的某天。
        w = (w < 1) ? 0 : w - 1;
        Log.e(TAG, String.valueOf(w));
        return weekDays[w];
    }

    public String getWeather() {
        int intday = Integer.parseInt(mTextDay);
        if (intday == 100 || intday == 102 || intday == 103) {
            return "Sunny";
        } else if (intday >= 300 && intday <= 500) {
            return "Rainy";
        } else {
            return "Cloudy";
        }
    }

    public String toString() {
        String msg = "";
        msg += getFriendlyDate() + ", " + getDate() + "\n";
        msg += "Max Temperature: " + getTemperatureMax() + "°\nMin Temperature: " + getTemperatureMin() + "°\n";
        msg += "Weather: " + getWeather() + "\n";
        msg += "Pressure: " + getPressure() + " hPa\n";
        msg += "Wind: " + getWindSpeed() + " km\\h SE\n";
        return msg;
    }
}
