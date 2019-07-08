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

package co.id.roningrum.dolanapptugasakhir.tourism.village;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class DetailVillageActivity extends FragmentActivity implements OnMapReadyCallback {
    public static final String EXTRA_WISATA_KEY = "wisata_key";

    private GoogleMap villageLocationMap;
    private DatabaseReference villageDetailRef;

    private GPSHandler gpsHandler;
    private Query villageQuery;
    
    private ValueEventListener valueEventListener;

    private TextView tvNameVillageDetail, tvAddressVillageDetail, tvDescVillageDetail,
            tvDistanceVillageDetail;

    private ImageView imgVillageObject;

    private double startLat;
    private double startLng;
    private double endLat;
    private double endLng;
    private double distance;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_village);
        
        tvNameVillageDetail = findViewById(R.id.name_place_village_detail);
        tvAddressVillageDetail = findViewById(R.id.address_place_village_detail);
        tvDescVillageDetail = findViewById(R.id.info_place_village_detail);
        tvDistanceVillageDetail = findViewById(R.id.distance_place_village_detail);
        imgVillageObject = findViewById(R.id.img_village_place_detail);
        
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_place_village_detail);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        String villageKey = getIntent().getStringExtra(EXTRA_WISATA_KEY);
        if(villageKey == null){
            throw new IllegalArgumentException("Must pass Extra");
        }
        
        villageDetailRef = FirebaseDatabase.getInstance().getReference().child("Tourism");
        villageQuery = villageDetailRef.orderByChild("category_tourism").equalTo("desa");
        gpsHandler = new GPSHandler(this);
        
        LoadDetailDesa();

    }

    private void LoadDetailDesa() {
        if (gpsHandler.isCanGetLocation()) {
            ValueEventListener eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    CategoryItem categoryItem = dataSnapshot.getValue(CategoryItem.class);
                    startLat = gpsHandler.getLatitude();
                    startLng = gpsHandler.getLongitude();
                    endLat = categoryItem.getLat_location_tourism();
                    endLng = categoryItem.getLng_location_tourism();
                    distance = calculateDistance(startLat, startLng, endLat, endLng);

                    @SuppressLint("DefaultLocale") String distanceFormat = String.format("%.2f", distance);
                    tvDistanceVillageDetail.setText("" + distanceFormat + " KM");
                    tvNameVillageDetail.setText(categoryItem.getName_tourism());
                    tvAddressVillageDetail.setText(categoryItem.getLocation_tourism());
                    tvDescVillageDetail.setText(categoryItem.getInfo_tourism());
                    Glide.with(getApplicationContext()).load(categoryItem.getUrl_photo()).into(imgVillageObject);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            villageDetailRef.addValueEventListener(eventListener);
            valueEventListener = eventListener;
        }
    }

        @Override
        public void onMapReady (GoogleMap googleMap){
            villageLocationMap = googleMap;

            ValueEventListener eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    CategoryItem categoryItem = dataSnapshot.getValue(CategoryItem.class);
                    double lattitude = categoryItem.getLat_location_tourism();
                    double longitude = categoryItem.getLng_location_tourism();

                    LatLng location = new LatLng(lattitude, longitude);
                    villageLocationMap.addMarker(new MarkerOptions().position(location));
                    villageLocationMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,16.0f));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            villageDetailRef.addValueEventListener(eventListener);
            valueEventListener = eventListener;

        }

        private double calculateDistance ( double startLat, double startLng, double endLat,
        double endLng){

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

    }
