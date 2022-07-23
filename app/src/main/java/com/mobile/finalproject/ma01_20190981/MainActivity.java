package com.mobile.finalproject.ma01_20190981;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    final static String TAG = "MainActivity";
    final static int PERMISSION_REQ_CODE = 100;

    private TextView tvLv;
    private TextView tvKm;
    private TextView tvKcal;
    private TextView tvHour;
    private TextView tvMin;
    private TextView tvSec;

    private GoogleMap mGoogleMap;
    private LocationManager locationManager;

    private Marker centerMarker;

    boolean updateFlag = true;

    WalkInfoDBHelper helper;
    WalkInfoDBManager manager;
    WalkInfo walkInfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvLv = findViewById(R.id.tvLv);
        tvKm = findViewById(R.id.tvKm);
        tvKcal = findViewById(R.id.tvKcal);
        tvHour = findViewById(R.id.tvHour);
        tvMin = findViewById(R.id.tvMin);
        tvSec = findViewById(R.id.tvSec);

        helper = new WalkInfoDBHelper(this);
        manager = new WalkInfoDBManager(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(mapReadyCallBack);

        Places.initialize(getApplicationContext(), getResources().getString(R.string.api_key)); // getResources는 생략 가능

        locationUpdate();
    }

    @Override
    protected void onResume() {
        super.onResume();
//      DB에서 가장 최근 걷기 데이터를 읽어옴
        if (updateFlag) {
            walkInfo = manager.getRecentWalkInfo();

            Calendar todayCal = Calendar.getInstance();
            todayCal.setTimeInMillis(System.currentTimeMillis());
            todayCal.set(Calendar.HOUR_OF_DAY, 0);
            todayCal.set(Calendar.MINUTE,0);
            todayCal.set(Calendar.SECOND, 0);

            Calendar recentCal = Calendar.getInstance();
            recentCal.setTimeInMillis(walkInfo.getDate());
            recentCal.set(Calendar.HOUR_OF_DAY, 0);
            recentCal.set(Calendar.MINUTE,0);
            recentCal.set(Calendar.SECOND, 0);

            if ((todayCal.getTimeInMillis() - recentCal.getTimeInMillis()) / 1000 / (24*60*60) >= 1) { // 날짜 차이가 1일 이상이라면
                manager.addNewWalkInfo(new WalkInfo(-1, 0, walkInfo.getLevel(), 0, 0, System.currentTimeMillis(), null));
                walkInfo = manager.getRecentWalkInfo();
            }

            tvLv.setText(Integer.toString(walkInfo.getLevel()));
            tvKm.setText(Float.toString(walkInfo.getMoveKm()));
            tvKcal.setText(Float.toString(walkInfo.getKcal()));
            tvHour.setText(Integer.toString(walkInfo.getTime() / 3600));
            tvMin.setText(Integer.toString(walkInfo.getTime() / 60 % 60));
            tvSec.setText(Integer.toString(walkInfo.getTime() % 60));

            manager.close();
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStart:
                Intent intent = new Intent(MainActivity.this, StartWalkActivity.class);
                intent.putExtra("walkInfo", (WalkInfo) walkInfo);
                startActivity(intent);
                break;
            case R.id.btnCal:
                intent = new Intent(MainActivity.this, WalkCalendarActivity.class);
                startActivity(intent);
                break;
            case R.id.btnPlace:
                intent = new Intent(MainActivity.this, TrackingInfoActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void locationUpdate() {
        if (checkPermission()) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, locationListener);
        }
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            LatLng currentLoc = new LatLng(location.getLatitude(), location.getLongitude());
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 17));
            centerMarker.setPosition(currentLoc);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    OnMapReadyCallback mapReadyCallBack = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mGoogleMap = googleMap;

            LatLng currentLoc = new LatLng(37.606320, 127.041808);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 17));
            // mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 17));

            MarkerOptions centerMarkerOptions = new MarkerOptions();
            centerMarkerOptions.position(currentLoc);
            centerMarkerOptions.title("현재 위치");
            centerMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

            centerMarker = mGoogleMap.addMarker(centerMarkerOptions);
            // centerMarker.showInfoWindow(); // 생략 시 마커 터치해야 윈도우가 나타남
        }
    };

    /* 필요 permission 요청 */
    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_REQ_CODE);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQ_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationUpdate();
            } else {
                Toast.makeText(this, "Permission required.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}