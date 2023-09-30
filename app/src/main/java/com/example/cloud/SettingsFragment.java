package com.example.cloud;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.CheckBoxPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SettingsFragment extends PreferenceFragmentCompat {
    private static final String TAG = "SettingsFragment";

    private EditTextPreference mLocationEdit;
    private ListPreference mUnitListChoose;
    private CheckBoxPreference mNotificationCheck;
    private SQLiteDatabase mClouDB;
    private List<DayWeather> mDayWeathers;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference_settings);

        final SharedPreferences sp = getPreferenceManager().getSharedPreferences();
        String location = sp.getString(getLocationKey(), "Changsha");
        String unit = sp.getString(getUnitKey(), "Celsius");
        boolean notification = sp.getBoolean(getNotificationKey(), true);

        mLocationEdit = (EditTextPreference) findPreference("setting_location");
        mLocationEdit.setDefaultValue(location);
        mLocationEdit.setSummary(location);

        mLocationEdit.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (Cache.isCityValid(newValue.toString().toLowerCase(Locale.ROOT))) {
                    mLocationEdit.setSummary(newValue.toString());
                    mLocationEdit.setDefaultValue(newValue);
                    Cache.setLocation(newValue.toString());
                    reboot();
                } else {
                    Toast.makeText(getContext(), "Invalid City", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        mUnitListChoose = (ListPreference) findPreference("temperature_unit");
        mUnitListChoose.setSummary(unit);
        mUnitListChoose.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                mUnitListChoose.setSummary(newValue.toString());
                mUnitListChoose.setDefaultValue(newValue);
                int index = (newValue.toString().equals("Celsius")) ? 0 : 1;
                mUnitListChoose.setValueIndex(index);
                Cache.setUnit(newValue.toString());
                reboot();
                return true;
            }
        });

        mNotificationCheck = (CheckBoxPreference) findPreference("setting_notification");
        mNotificationCheck.setChecked(notification);
        String summary = (notification == true) ? "Enable" : "Unable";
        mNotificationCheck.setSummary(summary);
        mNotificationCheck.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Cache.setIsNotification((boolean) newValue);
                String summary = (((boolean) newValue) == true) ? "Enable" : "Unable";
                mNotificationCheck.setSummary(summary);
                if ((boolean) newValue) {
                    BackgroundService.setServiceAlarm(getContext(), true);
                } else {
                    BackgroundService.setServiceAlarm(getContext(), false);
                }
                Log.e(TAG, String.valueOf((boolean) newValue));
                return true;
            }
        });
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

    }

    public static String getSharedPreferencsFileName() {
        return "com.example.cloud_preferences";
    }

    public static String getLocationKey() {
        return "setting_location";
    }

    public static String getUnitKey() {
        return "temperature_unit";
    }

    public static String getNotificationKey() {
        return "setting_notification";
    }

    private void reboot() {
        Intent intent = getContext().getPackageManager().getLaunchIntentForPackage(getContext().getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("REBOOT","reboot");
        startActivity(intent);
    }
}