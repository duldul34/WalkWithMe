package com.mobile.finalproject.ma01_20190981;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;

public class StartWalkActivity extends AppCompatActivity {

    final static String TAG = "StartWalkActivity";
    final static int PERMISSION_REQ_CODE = 100;

    private TextView tvLv;
    private TextView tvKm;
    private TextView tvKcal;
    private TextView tvHour;
    private TextView tvMin;
    private TextView tvSec;

    private Button btnStop;

    WalkInfo walkInfo;

    long startTime;
    long currTime;

    private Location location1;
    private Location location2;

    private GoogleMap mGoogleMap;
    private LocationManager locationManager;

    private Marker centerMarker;
    private PolylineOptions pOptions;

    WalkInfoDBHelper helper;
    WalkInfoDBManager manager;

    private float nowKm = 0;
    private float nowKcal = 0;
    int time = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_walk);

        tvLv = findViewById(R.id.tvLv);
        tvKm = findViewById(R.id.tvKm);
        tvKcal = findViewById(R.id.tvKcal);
        tvHour = findViewById(R.id.tvHour);
        tvMin = findViewById(R.id.tvMin);
        tvSec = findViewById(R.id.tvSec);

        helper = new WalkInfoDBHelper(this);
        manager = new WalkInfoDBManager(this);

        Intent intent = getIntent();

        walkInfo = (WalkInfo) intent.getSerializableExtra("walkInfo");

        tvLv.setText(Integer.toString(walkInfo.getLevel()));
        tvKm.setText(Float.toString(nowKm));
        tvKcal.setText(Float.toString(nowKm * 40));
        tvHour.setText(Integer.toString(walkInfo.getTime() / 3600));
        tvMin.setText(Integer.toString(walkInfo.getTime() / 60 % 60));
        tvSec.setText(Integer.toString(walkInfo.getTime() % 60));

        btnStop = findViewById(R.id.btnStop);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(mapReadyCallBack);

        Places.initialize(getApplicationContext(), getResources().getString(R.string.api_key)); // getResources는 생략 가능

        pOptions = new PolylineOptions();
        pOptions.color(Color.RED);
        pOptions.width(5);

        startTime = System.currentTimeMillis();
        locationUpdate();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStop:
                int recentTime = walkInfo.getTime();
                walkInfo.setTime(recentTime + time);
                manager.modifyWalkInfo(walkInfo);
                finish();
                break;
        }
    }

    private void locationUpdate() {
        if (checkPermission()) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
        }
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            LatLng currentLoc = new LatLng(location.getLatitude(), location.getLongitude());
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 17));
            centerMarker.setPosition(currentLoc);
            if(location1 == null) {
                location1 = location;
            }
            location2 = location;
            pOptions.add(currentLoc);
            mGoogleMap.addPolyline(pOptions);

            currTime = System.currentTimeMillis();
            time = (int) (currTime - startTime) / 1000;
            // 시
            tvHour.setText(Integer.toString(time / 3600));
            // 분
            tvMin.setText(Integer.toString(time / 60 % 60));
            // 초
            tvSec.setText(Integer.toString(time % 60));

            float distanceMeters = location1.distanceTo(location2);
            float distanceKm = distanceMeters / 1000f;
            nowKm += distanceKm;
            tvKm.setText(Float.toString(nowKm));
            walkInfo.setMoveKm(walkInfo.getMoveKm() + distanceKm);
            nowKcal = nowKcal + distanceKm * 40;
            tvKcal.setText(Float.toString(nowKcal));
            walkInfo.setKcal(walkInfo.getKcal() + distanceKm * 40);

            location1 = location2;
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