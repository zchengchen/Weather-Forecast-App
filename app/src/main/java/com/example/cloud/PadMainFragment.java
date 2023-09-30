package com.example.cloud;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PadMainFragment extends Fragment {
    private static final String TAG = "MainFragment";

    private RecyclerView mRecyclerView;
    private List<DayWeather> mDayWeathers;
    private Callbacks mCallbacks;

    public static WeatherItemAdapter sAdapter;

    private SQLiteDatabase mClouDB;

    public interface Callbacks {
        void onWeatherSelected(DayWeather day);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = ((Callbacks) context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new FetchWeatherTask().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_padmain, container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.main_recycler_view);

        CloudDBHelper dbHelper = new CloudDBHelper(getContext(), CloudDBHelper.sDBName, null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(CloudDBHelper.sCityInfoTable, null,
                "cityNamePinyin=?", new String[] { Cache.getLocation() },
                null, null, null);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(new Date());
        mClouDB = db;

        if (cursor.moveToFirst()) {
            String queryDate = cursor.getString(cursor.getColumnIndex("lastestQueryDate"));
            Cache.setLocationID(cursor.getString(cursor.getColumnIndex("locationID")));

            if (today.equals(queryDate)) {
                Cursor result = mClouDB.query(CloudDBHelper.sWeatherTable, null, "locationID=?", new String[] {Cache.getLocationID()}, null, null, null);
                Log.e(TAG, "locationID: " + cursor.getString(cursor.getColumnIndex("locationID")));
                Log.e(TAG, "Length of result: " + String.valueOf(result.getCount()));
                if (result.moveToFirst()) {
                    Log.e(TAG, "Get weather from DB");
                    do {
                        DayWeather dw = new DayWeather();

                        dw.setDate(result.getString(result.getColumnIndex("date")));
                        dw.setWindSpeed(result.getString(result.getColumnIndex("winSpeed")));
                        dw.setPressure(result.getString(result.getColumnIndex("pressure")));
                        dw.setHumidity(result.getString(result.getColumnIndex("humidity")));
                        dw.setTemperatureMin(result.getString(result.getColumnIndex("minTemperature")));
                        dw.setTemperatureMax(result.getString(result.getColumnIndex("maxTemperature")));
                        dw.setTextDay(result.getString(result.getColumnIndex("textDay")));

                        Log.e(TAG, dw.toString());
                        WeatherList.get(getContext()).add(dw);
                    } while (result.moveToNext());
                }
                cursor.close();

                mDayWeathers = WeatherList.get(getContext()).getList();
                Log.e(TAG, "List length: " + String.valueOf(mDayWeathers.size()));
                updateUI();
                setupAdapter();
            }  else {
                ContentValues values = new ContentValues();
                values.put("lastestQueryDate", today);
                mClouDB.update(CloudDBHelper.sCityInfoTable, values, "locationID=?", new String[] {Cache.getLocationID()});
                new FetchWeatherTask().execute();
            }
        } else {
            // City Info Not Found
            Log.e(TAG, "update CityInfo");
            new PadMainFragment.FetchCityInfoTask().execute();
            new PadMainFragment.FetchWeatherTask().execute();
        }

        return v;
    }

    public class WeatherItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private int mIndex;
        private TextView mMaxTemperature;
        private TextView mMinTemperature;
        private TextView mDate;
        private TextView mWeather;
        private ImageView mWeatherIcon;

        public WeatherItemHolder(View view) {
            super(view);

            mMaxTemperature = (TextView) view.findViewById(R.id.item_max_tempature);
            mMinTemperature = (TextView) view.findViewById(R.id.item_min_tempature);

            mDate = (TextView) view.findViewById(R.id.item_weather_date);
            mWeather = (TextView) view.findViewById(R.id.item_weather_day);

            mWeatherIcon = (ImageView) view.findViewById(R.id.item_weather_icon);
        }

        @Override
        public void onClick(View view) {

        }
    }


    public class WeatherItemAdapter extends RecyclerView.Adapter<PadMainFragment.WeatherItemHolder> {
        private List<DayWeather> mDW;

        public WeatherItemAdapter(List<DayWeather> list) {
            mDW = list;
        }

        @Override
        public PadMainFragment.WeatherItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_item, parent, false);
            PadMainFragment.WeatherItemHolder holder = new PadMainFragment.WeatherItemHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(PadMainFragment.WeatherItemHolder holder, @SuppressLint("RecyclerView") final int position) {
            holder.mIndex = position;
            if (position == 0) {
                holder.itemView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            }

            final DayWeather bv = mDW.get(position);

            holder.mDate.setText(bv.getDate());
            holder.mMaxTemperature.setText(bv.getTemperatureMax() + "°");
            holder.mMinTemperature.setText(bv.getTemperatureMin() + "°");

            int intday = Integer.parseInt(bv.getTextDay());
            if (intday == 100 || intday == 102 || intday == 103) {
                holder.mWeather.setText("Sunny");
                holder.mWeatherIcon.setImageResource(R.drawable.sunny);
            } else if (intday >= 300 && intday <= 500) {
                holder.mWeather.setText("Rainy");
                holder.mWeatherIcon.setImageResource(R.drawable.rainy);
            } else {
                holder.mWeather.setText("Clouds");
                holder.mWeatherIcon.setImageResource(R.drawable.other);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCallbacks.onWeatherSelected(bv);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mDW.size();
        }
    }

    private void setupAdapter() {
        if (isAdded()) {
            WeatherItemAdapter adapter = new WeatherItemAdapter(mDayWeathers);
            sAdapter = adapter;
            mRecyclerView.setAdapter(adapter);
        }
    }

    private void updateUI() {
        mDayWeathers = WeatherList.get(getContext()).getDWListForRecycleViewPad();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private class FetchWeatherTask extends AsyncTask<Void, Void, List<DayWeather>> {
        @Override
        protected List<DayWeather> doInBackground(Void... voids) {
            ArrayList<DayWeather> dwlist = new ArrayList<>();

            try {
                HttpsRequest hr = new HttpsRequest();
                String data = hr.getUrlString(Cache.WeatherInfoQueryUrl());

                JSONObject jsonObject = new JSONObject(data);
                JSONArray array = jsonObject.getJSONArray("daily");
                WeatherList.get(getContext()).clear();

                for(int i = 0; i < 7; ++i) {
                    DayWeather dw = new DayWeather();
                    JSONObject item = array.getJSONObject(i);
                    dw.setDate(item.getString("fxDate"));
                    dw.setTextDay(item.getString("iconDay"));
                    dw.setTemperatureMax(item.getString("tempMax"));
                    dw.setTemperatureMin(item.getString("tempMin"));
                    dw.setHumidity(item.getString("humidity"));
                    dw.setPressure(item.getString("pressure"));
                    dw.setWindSpeed(item.getString("windSpeedDay"));

                    WeatherList.get(getContext()).add(dw);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return WeatherList.get(getContext()).getList();
        }

        @Override
        protected void onPostExecute(List<DayWeather> items) {
            mDayWeathers = items;
            mClouDB.delete(CloudDBHelper.sWeatherTable, "locationID=?", new String[] {Cache.getLocationID()});
            for (int i = 0; i < mDayWeathers.size(); ++i) {
                DayWeather dw = mDayWeathers.get(i);

                ContentValues values = new ContentValues();
                values.put("locationID", Cache.getLocationID());
                values.put("date", dw.getDate(0));
                values.put("textDay", dw.getTextDay());
                values.put("minTemperature", dw.getTemperatureMin());
                values.put("maxTemperature", dw.getTemperatureMax());
                values.put("humidity", dw.getHumidity());
                values.put("winSpeed", dw.getWindSpeed());
                values.put("pressure", dw.getPressure());

                Log.e(TAG, "Insert content: ");
                Log.e(TAG, values.toString());
                mClouDB.insert(CloudDBHelper.sWeatherTable, null, values);
            }
            updateUI();
            setupAdapter();
        }
    }

    private class FetchCityInfoTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                HttpsRequest hr = new HttpsRequest();
                String str = hr.getUrlString(Cache.locationInfoQueryUrl());
                if(getLocationIDFromJSON(str) != null) {
                    Cache.setLocationID(getLocationIDFromJSON(str));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
            if (Cache.getLocationID() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String today = sdf.format(new Date());

                ContentValues values = new ContentValues();
                values.put("locationID", Cache.getLocationID());
                values.put("cityNamePinyin", Cache.getLocation());
                values.put("lastestQueryDate", today);

                mClouDB.insert(CloudDBHelper.sCityInfoTable, null, values);
            } else {
                Toast.makeText(getContext(), "Network is bad. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static String getLocationIDFromJSON(String networkData) {
        try {
            JSONObject jsonObject = new JSONObject(networkData);
            JSONArray array = jsonObject.getJSONArray("location");
            return array.getJSONObject(0).getString("id");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
