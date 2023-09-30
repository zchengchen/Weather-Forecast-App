package com.example.cloud;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class DetailActivity extends AppCompatActivity {
    private static final String EXTRA_WEATHER_INFO = "weatherInfo";

    private Toolbar mToolbar;
    private ImageView mBackImage;
    private DayWeather mDayWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        DayWeather dw = getIntent().getParcelableExtra(EXTRA_WEATHER_INFO);
        mDayWeather = dw;

        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        mToolbar.inflateMenu(R.menu.toolbar_detail);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(menuItem.getItemId() == R.id.settings) {
                    Intent intent = SettingsActivity.newIntent(DetailActivity.this);
                    startActivity(intent);
                    return true;
                } else if (menuItem.getItemId() == R.id.locatoin) {
                    Intent intent = MapActivity.newIntent(DetailActivity.this, Cache.getLocation());
                    startActivity(intent);
                    return true;
                } else if (menuItem.getItemId() == R.id.email) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("email/*");
                    intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Weather share.");
                    intent.putExtra(android.content.Intent.EXTRA_TEXT, mDayWeather.toString());
                    startActivity(Intent.createChooser(intent, "Choose an application to send mail"));
                } else if (menuItem.getItemId() == R.id.message) {
                    Uri smsToUri = Uri.parse("smsto:");
                    Intent intent =  new Intent(Intent.ACTION_VIEW, smsToUri);
                    intent.putExtra("sms_body", mDayWeather.toString());
                    startActivity(Intent.createChooser(intent, "Choose an application to send message"));
                }
                return false;
            }
        });

        mBackImage = (ImageView) findViewById(R.id.back_icon);
        mBackImage.setImageResource(R.drawable.back);
        mBackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        Bundle bundle = DetailFragment.newBundle(dw);

        if (fragment == null) {
            fragment = new DetailFragment();
            fragment.setArguments(bundle);
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    public static Intent newIntent(Context context, DayWeather dw) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(EXTRA_WEATHER_INFO, dw);
        return intent;
    }
}
