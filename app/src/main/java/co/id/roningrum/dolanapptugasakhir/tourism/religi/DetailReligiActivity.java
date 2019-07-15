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

package co.id.roningrum.dolanapptugasakhir.tourism.religi;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
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

public class DetailReligiActivity extends FragmentActivity implements OnMapReadyCallback {
    public static final String EXTRA_WISATA_KEY = "religi_key";

    private GoogleMap religiMap;
    private DatabaseReference religiDetailRef;
    private GPSHandler gpsHandler;
    private ValueEventListener valueEventListener;

    private TextView tvNameReligiDetail, tvAddressReligiDetail,
            tvDescReligiDetail, tvDistanceReligiDetail;

    private ImageView imgReligiDetail;

    private double startLat;
    private double startlng;
    private double endlat;
    private double endLng;
    private double distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_religi);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_place_religi_detail);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        tvNameReligiDetail = findViewById(R.id.name_place_religi_detail);
        tvAddressReligiDetail = findViewById(R.id.address_place_religi_detail);
        tvDescReligiDetail = findViewById(R.id.info_place_religi_detail);
        tvDistanceReligiDetail = findViewById(R.id.distance_place_religi_detail);
        imgReligiDetail = findViewById(R.id.img_religi_place_detail);

        String religiKey = getIntent().getStringExtra(EXTRA_WISATA_KEY);
        if (religiKey == null) {
            throw new IllegalArgumentException("Must pass Extra");
        }
        religiDetailRef = FirebaseDatabase.getInstance().getReference().child("Tourism").child(religiKey);
        Query religiQuery = religiDetailRef.orderByChild("category_tourism").equalTo("religi");
        gpsHandler = new GPSHandler(this);
        LoadReligiDetail();
    }

    private void LoadReligiDetail() {
        if (gpsHandler.isCanGetLocation()) {
            ValueEventListener eventListener = new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    CategoryItem categoryItem = dataSnapshot.getValue(CategoryItem.class);
                    startLat = gpsHandler.getLatitude();
                    startlng = gpsHandler.getLongitude();
                    assert categoryItem != null;
                    endlat = categoryItem.getLat_location_tourism();
                    endLng = categoryItem.getLng_location_tourism();
                    distance = calculateDistance(startLat, startlng, endlat, endLng);

                    @SuppressLint("DefaultLocale") String distanceFormat = String.format("%.2f", distance);
                    tvDistanceReligiDetail.setText("" + distanceFormat + " km");
                    tvNameReligiDetail.setText(categoryItem.getName_tourism());
                    tvAddressReligiDetail.setText(categoryItem.getLocation_tourism());
                    tvDescReligiDetail.setText(categoryItem.getInfo_tourism());
                    Glide.with(getApplicationContext()).load(categoryItem.getUrl_photo()).into(imgReligiDetail);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            religiDetailRef.addValueEventListener(eventListener);
            valueEventListener = eventListener;

        }
    }

    private double calculateDistance(double startLat, double startlng, double endlat, double endLng) {
        double earthRadius = 6371;
        double latDiff = Math.toRadians(startLat - endlat);
        double lngDiff = Math.toRadians(startlng - endLng);
        double a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2) +
                Math.cos(Math.toRadians(startLat)) * Math.cos(Math.toRadians(endlat)) *
                        Math.sin(lngDiff / 2) * Math.sin(lngDiff / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c;

        int meterConversion = 1609;

        return (distance * meterConversion / 1000);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        religiMap = googleMap;
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CategoryItem categoryItem = dataSnapshot.getValue(CategoryItem.class);
                assert categoryItem != null;
                double lattitude = categoryItem.getLat_location_tourism();
                double longitude = categoryItem.getLng_location_tourism();

                LatLng location = new LatLng(lattitude, longitude);
                religiMap.addMarker(new MarkerOptions().position(location));
                religiMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16.0f));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        religiDetailRef.addValueEventListener(eventListener);
        valueEventListener = eventListener;

    }

    @Override
    protected void onStart() {
        super.onStart();
        LoadReligiDetail();
    }

    @Override
    protected void onStop() {
        super.onStop();
        religiDetailRef.removeEventListener(valueEventListener);
    }
}
