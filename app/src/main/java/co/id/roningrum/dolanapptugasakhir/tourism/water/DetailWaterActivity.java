/*
 * Copyright 2019 RONINGRUM. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package co.id.roningrum.dolanapptugasakhir.tourism.water;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.handler.GPSHandler;
import co.id.roningrum.dolanapptugasakhir.item.CategoryItem;

public class DetailWaterActivity extends FragmentActivity implements OnMapReadyCallback {
    public static final String EXTRA_WISATA_KEY = "wisata_key";

    private GoogleMap waterLocationMap;
    private DatabaseReference waterDetailRef;

    private GPSHandler gpsHandler;
    private Query waterQuery;
    private ValueEventListener valueEventListener;

    private TextView tvNameWaterDetail, tvAddressWaterDetail, tvDescWaterDetail,
    tvDistanceWaterDetail;

    private ImageView imgWaterObject;

    private double startLat;
    private double startLng;
    private double endLat;
    private double endLng;
    private double distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_water);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_place_water_detail);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        tvNameWaterDetail = findViewById(R.id.name_place_water_detail);
        tvAddressWaterDetail = findViewById(R.id.address_place_water_detail);
        tvDescWaterDetail = findViewById(R.id.info_place_water_detail);
        tvDistanceWaterDetail = findViewById(R.id.distance_place_water_detail);
        imgWaterObject = findViewById(R.id.img_water_place_detail);

        String waterKey = getIntent().getStringExtra(EXTRA_WISATA_KEY);
        if(waterKey == null){
            throw new IllegalArgumentException("Must pass Extra");
        }

        waterDetailRef = FirebaseDatabase.getInstance().getReference().child("Tourism").child(waterKey);
        waterQuery = waterDetailRef.orderByChild("category_tourism").equalTo("air");
        gpsHandler = new GPSHandler( this);

        LoadDetail();
    }

    private void LoadDetail() {
        if(gpsHandler.isCanGetLocation()){
            ValueEventListener eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    CategoryItem categoryItem = dataSnapshot.getValue(CategoryItem.class);
                    startLat = gpsHandler.getLatitude();
                    startLng = gpsHandler.getLongitude();
                    endLat = categoryItem.getLat_location_tourism();
                    endLng = categoryItem.getLng_location_tourism();
                    distance = calculateDistance(startLat,startLng,endLat,endLng);

                    @SuppressLint("DefaultLocale") String distanceFormat = String.format("%.2f",distance);
                    tvDistanceWaterDetail.setText(""+distanceFormat+" KM");
                    tvNameWaterDetail.setText(categoryItem.getName_tourism());
                    tvAddressWaterDetail.setText(categoryItem.getLocation_tourism());
                    tvDescWaterDetail.setText(categoryItem.getInfo_tourism());
                    Glide.with(getApplicationContext()).load(categoryItem.getUrl_photo()).into(imgWaterObject);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            waterDetailRef.addValueEventListener(eventListener);
            valueEventListener = eventListener;
        }
    }

    private double calculateDistance(double startLat, double startLng, double endLat, double endLng) {
        double earthRadius = 6371;
        double latDiff = Math.toRadians(startLat-endLat);
        double lngDiff = Math.toRadians(startLng-endLng);
        double a = Math.sin(latDiff /2) * Math.sin(latDiff /2) +
                Math.cos(Math.toRadians(startLat)) * Math.cos(Math.toRadians(endLat)) *
                        Math.sin(lngDiff /2) * Math.sin(lngDiff /2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = earthRadius * c;

        int meterConversion = 1609;

        return (distance*meterConversion/1000);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        waterLocationMap = googleMap;

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CategoryItem categoryItem = dataSnapshot.getValue(CategoryItem.class);
                double lattitude = categoryItem.getLat_location_tourism();
                double longitude = categoryItem.getLng_location_tourism();

                LatLng location = new LatLng(lattitude, longitude);
                waterLocationMap.addMarker(new MarkerOptions().position(location));
                waterLocationMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,16.0f));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        waterDetailRef.addValueEventListener(eventListener);
        valueEventListener = eventListener;

//        waterQuery.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                CategoryItem categoryItem = dataSnapshot.getValue(CategoryItem.class);
//                double lattitude = categoryItem.getLat_location_tourism();
//                double longitude = categoryItem.getLng_location_tourism();
//
//                LatLng location = new LatLng(lattitude, longitude);
//                waterLocationMap.addMarker(new MarkerOptions().position(location));
//                waterLocationMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,10.0f));
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.e("Pesan", "Database Error" +databaseError.getMessage());
//            }
//        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        LoadDetail();
    }

    @Override
    protected void onStop() {
        super.onStop();
        waterDetailRef.removeEventListener(valueEventListener);
    }
}
