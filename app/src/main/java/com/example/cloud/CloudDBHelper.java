package com.example.cloud;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CloudDBHelper extends SQLiteOpenHelper {
    private static final String TAG = "CloudDBHelper";
    public static final String sDBName = "Weather.db";
    public static final String sCityInfoTable = "cityInfo";
    public static final String sWeatherTable = "cityWeather";

    public static final String CREATE_TABLE_CITY_INFO
            = "create table cityInfo (locationID text primary key, "
            + "cityNamePinyin text, lastestQueryDate text)";
    public static final String CREATE_TABLE_CITY_WEATHER
            = "create table cityWeather (id integer primary key autoincrement, locationID text,"
            + " date text, textDay text, minTemperature text, maxTemperature text, humidity text, winSpeed text, pressure text)";

    private Context mContext;

    public CloudDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        Log.e(TAG, "create");
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CITY_INFO);
        db.execSQL(CREATE_TABLE_CITY_WEATHER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }
}
