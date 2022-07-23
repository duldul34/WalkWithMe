package com.mobile.finalproject.ma01_20190981;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.libraries.places.api.Places;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TrackingInfoDetailActivity extends AppCompatActivity {

    private TextView tvTitleDetail;
    private TextView tvSpatialDetail;
    private TextView tvAlternativeTitleDetail;
    private TextView tvDescriptionDetail;

    private Geocoder geocoder;
    private GoogleMap mGoogleMap;
    private Marker centerMarker;

    private double latitude;
    private double longitude;

    final static int PERMISSION_REQ_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_info_detail);

        Intent intent = getIntent();
        TrackingInfo dto = (TrackingInfo) intent.getSerializableExtra("dto");

        tvTitleDetail = findViewById(R.id.tvTitleDetail);
        if (dto.getTitle() == null || dto.getTitle().length() == 0) {
            tvTitleDetail.setText("장소명 없음");
        }
        else {
            tvTitleDetail.setText(dto.getTitle());
        }

        tvSpatialDetail = findViewById(R.id.tvSpatialDetail);
        if (dto.getSpatial() == null || dto.getSpatial().length() == 0) {
            tvSpatialDetail.setText("위치 없음");
        }
        else {
            tvSpatialDetail.setText(dto.getSpatial());
        }

        tvAlternativeTitleDetail = findViewById(R.id.tvAlternativeTitleDetail);
        if (dto.getAlternativeTitle() == null || dto.getAlternativeTitle().length() == 0) {
            tvAlternativeTitleDetail.setText("부제 없음");
        }
        else {
            tvAlternativeTitleDetail.setText(dto.getAlternativeTitle());
        }

        tvDescriptionDetail = findViewById(R.id.tvDescriptionDetail);
        if (dto.getDescription() == null || dto.getDescription().length() == 0) {
            tvDescriptionDetail.setText("설명 없음");
        }
        else {
            tvDescriptionDetail.setText(dto.getDescription());
        }

        geocoder = new Geocoder(this, Locale.getDefault());


        if (dto.getSpatial() != null) {
            String location = dto.getSpatial();
            List<LatLng> latLng = getLatLng(location);
            if (latLng == null) {
                Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
            }

            latitude = latLng.get(0).latitude;
            longitude = latLng.get(0).longitude;

            Log.d("location", "location: " + location);
            Log.d("location", String.format("Result Latitude: %f\nLongitude: %f", latitude, longitude));
        }
        else {
            Toast.makeText(this, "위치 정보가 존재하지 않습니다. ", Toast.LENGTH_SHORT).show();
            latitude = 37.604094;
            longitude = 127.042463;
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(mapReadyCallBack);
    }

    OnMapReadyCallback mapReadyCallBack = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mGoogleMap = googleMap;

            LatLng currentLoc = new LatLng(latitude, longitude);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 8));
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

    //    Reverse geocoding
    private List<LatLng> getLatLng(String targetLocation) {

        List<Address> addresses = null;
        ArrayList<LatLng> addressFragments = null;

//        주소에 해당하는 위도/경도 정보를 Geocoder 에게 요청
        try {
            addresses = geocoder.getFromLocationName(targetLocation, 1);
        } catch (IOException e) { // Catch network or other I/O problems.
            e.printStackTrace();
        } catch (IllegalArgumentException e) { // Catch invalid address values.
            e.printStackTrace();
        }

        if (addresses == null || addresses.size()  == 0) {
            return null;
        } else {
            Address addressList = addresses.get(0);
            addressFragments = new ArrayList<LatLng>();

            for(int i = 0; i <= addressList.getMaxAddressLineIndex(); i++) {
                LatLng latLng = new LatLng(addressList.getLatitude(), addressList.getLongitude());
                addressFragments.add(latLng);
            }
        }

        return addressFragments;
    }
}