package com.example.cloud;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.CircleOptions;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.ServiceSettings;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;

import java.util.Locale;

public class MapActivity extends AppCompatActivity implements GeocodeSearch.OnGeocodeSearchListener {
    private static final String TAG = "MapActivity";
    private static final String EXTRA_LOCATION_NAME = "EXTRA_LOCATION_NAME";

    private AMap mMap;
    private MapView mMapView;
    private String mLocation;

    public static Intent newIntent(Context context, String location) {
        Intent intent = new Intent(context, MapActivity.class);
        if(location.equals("") || location == null) {
            location = "changsha";
        }
        location = location.toLowerCase(Locale.ROOT);
        intent.putExtra(EXTRA_LOCATION_NAME, Cache.pinyinToCharater(location));
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);// 此方法必须重写
        mMap = mMapView.getMap();

        mLocation = getIntent().getStringExtra(EXTRA_LOCATION_NAME);

        try {
            ServiceSettings.updatePrivacyShow(this, true, true);
            ServiceSettings.updatePrivacyAgree(this, true);
            GeocodeSearch search = new GeocodeSearch(MapActivity.this);
            search.setOnGeocodeSearchListener(MapActivity.this);
            GeocodeQuery query = new GeocodeQuery(mLocation, "");//空表示全国
            search.getFromLocationNameAsyn(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        // 必须回调MapView的onResume()方法
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 必须回调MapView的onPause()方法
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // 必须回调MapView的onSaveInstanceState()方法
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 必须回调MapView的onDestroy()方法
        mMapView.onDestroy();
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) { }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
        // 获取解析得到的第一个地址
        GeocodeAddress geo = geocodeResult.getGeocodeAddressList().get(0);
        // 获取解析得到的经纬度
        LatLonPoint pos = geo.getLatLonPoint();
        LatLng targetPos = new LatLng(pos.getLatitude(), pos.getLongitude());
        // 创建一个设置经纬度的CameraUpdate
        CameraUpdate cu = CameraUpdateFactory.changeLatLng(targetPos);
        // 更新地图的显示区域
        mMap.moveCamera(cu);
        // 创建一个CircleOptions（用于向地图上添加圆形）
        CircleOptions cOptions = new CircleOptions().center(targetPos)  // 设置圆心
                .fillColor(0x80ffff00)  // 设置圆形的填充颜色
                .radius(80)  // 设置圆形的半径
                .strokeWidth(1)  // 设置圆形的线条宽度
                .strokeColor(0xff000000);  // 设置圆形的线条颜色
        mMap.addCircle(cOptions);
    }
}
