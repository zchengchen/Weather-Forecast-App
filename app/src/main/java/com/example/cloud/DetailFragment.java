package com.example.cloud;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class DetailFragment extends Fragment {
    private static final String TAG = "DetailFragment";
    private static final String ARGS_WEATHER_INFO = "WeatherInfo";

    private TextView mFriendlyDate;
    private TextView mConcreteDate;
    private TextView mMaxTemperature;
    private TextView mMinTemperature;
    private TextView mWeather;
    private TextView mHumidity;
    private TextView mPressure;
    private TextView mWind;
    private ImageView mWeatherIcon;

    private DayWeather mDayWeather;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_detail, container, false);

        mDayWeather = getArguments().getParcelable(ARGS_WEATHER_INFO);

        mFriendlyDate = (TextView) v.findViewById(R.id.detail_friendly_date);
        mFriendlyDate.setText(mDayWeather.getFriendlyDate());

        mConcreteDate = (TextView) v.findViewById(R.id.detail_date);
        mConcreteDate.setText(mDayWeather.getDate());

        mMaxTemperature = (TextView) v.findViewById(R.id.detail_max_temperature);
        mMaxTemperature.setText(mDayWeather.getTemperatureMax() + "°");

        mMinTemperature = (TextView) v.findViewById(R.id.detail_min_temperature);
        mMinTemperature.setText(mDayWeather.getTemperatureMin() + "°");

        mWeather = (TextView) v.findViewById(R.id.detail_weather);
        mWeatherIcon = (ImageView) v.findViewById(R.id.detail_weather_icon);

        int intday = Integer.parseInt(mDayWeather.getTextDay());
        if (intday == 100 || intday == 102 || intday == 103) {
            mWeather.setText("Sunny");
            mWeatherIcon.setImageResource(R.drawable.sunny);
        } else if (intday >= 300 && intday <= 500) {
            mWeather.setText("Rainy");
            mWeatherIcon.setImageResource(R.drawable.rainy);
        } else {
            mWeather.setText("Clouds");
            mWeatherIcon.setImageResource(R.drawable.other);
        }

        mHumidity = (TextView) v.findViewById(R.id.detail_humidity);
        mHumidity.setText("Humidity: " + mDayWeather.getHumidity() + " %");

        mPressure = (TextView) v.findViewById(R.id.detail_pressure);
        mPressure.setText("Pressure: " + mDayWeather.getPressure() + " hPa");

        mWind = (TextView) v.findViewById(R.id.detail_wind);
        mWind.setText("Wind: " + mDayWeather.getWindSpeed() + " km/h SE");

        return v;
    }

    public static Bundle newBundle(DayWeather dw) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARGS_WEATHER_INFO, dw);
        return bundle;
    }
}
