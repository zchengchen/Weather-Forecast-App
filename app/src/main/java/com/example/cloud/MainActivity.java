package com.example.cloud;

import static com.example.cloud.SettingsFragment.getNotificationKey;
import static com.example.cloud.SettingsFragment.getSharedPreferencsFileName;
import static com.example.cloud.SettingsFragment.getUnitKey;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements PadMainFragment.Callbacks {
    private static final String TAG = "MainActivity";

    public static Boolean isPad;
    private Toolbar mToolbar;
    private TextView mAppName;
    private ImageView mAppIcon;
    private CloudDBHelper mDBHelper;

    private static DayWeather sSelectedDayWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_masterdetail);

        initSettings();
        processPinyin();

        SharedPreferences sp = getSharedPreferences(getSharedPreferencsFileName(), MODE_PRIVATE);
        String unit = sp.getString(getUnitKey(), "Celsius");
        Cache.setUnit(unit);

        boolean isNotify = sp.getBoolean(getNotificationKey(), true);
        if (isNotify) {
            BackgroundService.setServiceAlarm(MainActivity.this, true);
        } else {
            BackgroundService.setServiceAlarm(MainActivity.this, false);
        }

        isPad = (findViewById(R.id.detail_fragment_container) != null);

        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        mToolbar.inflateMenu(R.menu.activity_toolbar);
        Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/TypoPRO-DancingScript-Regular.ttf");
        if (!isPad) {
            mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    if (menuItem.getItemId() == R.id.settings) {
                        Intent intent = SettingsActivity.newIntent(MainActivity.this);
                        startActivity(intent);
                        return true;
                    } else if (menuItem.getItemId() == R.id.locatoin) {
                        Intent intent = MapActivity.newIntent(MainActivity.this, Cache.getLocation());
                        startActivity(intent);
                        return true;
                    }
                    return false;
                }
            });
        } else {
            mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    if (menuItem.getItemId() == R.id.settings) {
                        Intent intent = SettingsActivity.newIntent(MainActivity.this);
                        startActivity(intent);
                        return true;
                    } else if (menuItem.getItemId() == R.id.locatoin) {
                        Intent intent = MapActivity.newIntent(MainActivity.this, Cache.getLocation());
                        startActivity(intent);
                        return true;
                    } else if (menuItem.getItemId() == R.id.email) {
                        if (sSelectedDayWeather == null) {
                            Toast.makeText(MainActivity.this, "Please Select a item", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("email/*");
                        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Weather share.");
                        intent.putExtra(android.content.Intent.EXTRA_TEXT, sSelectedDayWeather.toString());
                        startActivity(Intent.createChooser(intent, "Choose an application to send mail"));
                    } else if (menuItem.getItemId() == R.id.message) {
                        if (sSelectedDayWeather == null) {
                            Toast.makeText(MainActivity.this, "Please Select a item", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        Uri smsToUri = Uri.parse("smsto:");
                        Intent intent =  new Intent(Intent.ACTION_VIEW, smsToUri);
                        intent.putExtra("sms_body", sSelectedDayWeather.toString());
                        startActivity(Intent.createChooser(intent, "Choose an application to send message"));
                    }
                    return false;
                }
            });
        }
        mAppName = (TextView) findViewById(R.id.app_name);
        mAppName.setTypeface(typeFace);

        mAppIcon = (ImageView) findViewById(R.id.app_icon);
        mAppIcon.setImageResource(R.drawable.app_icon);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            fragment = isPad ? new PadMainFragment() : new MainFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    public void onWeatherSelected(DayWeather weather) {
        if (!isPad) {
            Intent intent = DetailActivity.newIntent(MainActivity.this, weather);
            startActivity(intent);
        } else {
            sSelectedDayWeather = weather;
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();

            Bundle bundle = DetailFragment.newBundle(weather);

            Fragment oldDetail = fm.findFragmentById(R.id.detail_fragment_container);
            Fragment newDetail = new DetailFragment();
            newDetail.setArguments(bundle);

            if (oldDetail != null) {
                ft.remove(oldDetail);
            }

            ft.add(R.id.detail_fragment_container, newDetail);
            ft.commit();
        }
    }

    private void initSettings() {
        String filename = SettingsFragment.getSharedPreferencsFileName();
        SharedPreferences sp = getSharedPreferences(filename, MODE_PRIVATE);
        String location = sp.getString(SettingsFragment.getLocationKey(), "Changsha");
        String unit = sp.getString(getUnitKey(), "Celsius");
        boolean isNotification = sp.getBoolean(SettingsFragment.getNotificationKey(), false);
        Cache.updateSettings(location, unit, isNotification);
    }

    private void processPinyin() {
        try {
            AssetManager assetManager = getAssets();//获得assets资源管理器（assets中的文件无法直接访问，可以使用AssetManager访问）
            InputStreamReader inputStreamReader = new InputStreamReader(assetManager.open("data/pinyin2location.json"),"UTF-8"); //使用IO流读取json文件内容
            BufferedReader br = new BufferedReader(inputStreamReader);//使用字符高效流
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = br.readLine())!=null){
                builder.append(line);
            }
            br.close();
            inputStreamReader.close();

            JSONObject testJson = new JSONObject(builder.toString()); // 从builder中读取了json中的数据。
            // 直接传入JSONObject来构造一个实例
            JSONArray array = testJson.getJSONArray("location");

            for (int i = 0; i < array.length(); i++){
                JSONObject jsonObject = array.getJSONObject(i);
                String pinyin = jsonObject.getString("cityPhonetic");
                String cityname = jsonObject.getString("cityName");
                Cache.addToPinyinToCharater(pinyin.toLowerCase(Locale.ROOT), cityname);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, MapActivity.class);
    }
}