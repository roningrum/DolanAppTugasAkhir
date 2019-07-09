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

package co.id.roningrum.dolanapptugasakhir.tourism.nature;

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

public class DetailNatureActivity extends FragmentActivity implements OnMapReadyCallback {
    public static final String EXTRA_WISATA_KEY = "alam_key";

    private GoogleMap gMap;
    private DatabaseReference natureDetailRef;
    private GPSHandler gpsHandler;
    private ValueEventListener valueEventListener;

    private TextView tvNameNatureDetail, tvAddressNature,
    tvDescNature, tvDistanceNature;

    private ImageView imgNature;

    private double startLat;
    private double startlng;
    private double endlat;
    private double endLng;
    private double distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_nature);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_place_nature_detail);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        tvNameNatureDetail = findViewById(R.id.name_place_nature_detail);
        tvAddressNature = findViewById(R.id.address_place_nature_detail);
        tvDescNature = findViewById(R.id.info_place_nature_detail);
        tvDistanceNature = findViewById(R.id.distance_place_nature_detail);
        imgNature = findViewById(R.id.img_nature_place_detail);

        String alamKey = getIntent().getStringExtra(EXTRA_WISATA_KEY);
        if(alamKey == null){
            throw new IllegalArgumentException("Must pass Extra");
        }
        natureDetailRef = FirebaseDatabase.getInstance().getReference().child("Tourism").child(alamKey);
        Query natureQuery = natureDetailRef.orderByChild("category_tourism").equalTo("alam");
        gpsHandler = new GPSHandler(this);


        LoadDetail();
    }

    private void LoadDetail() {
        if(gpsHandler.isCanGetLocation()){
            ValueEventListener eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    CategoryItem categoryItem = dataSnapshot.getValue(CategoryItem.class);
                    startLat = gpsHandler.getLatitude();
                    startlng = gpsHandler.getLongitude();
                    endlat = categoryItem.getLat_location_tourism();
                    endLng = categoryItem.getLng_location_tourism();
                    distance = calculateDistance(startLat,startlng,endlat,endLng);

                    @SuppressLint("DefaultLocale") String distanceFormat = String.format("%.2f",distance);
                    tvDistanceNature.setText(""+distanceFormat+" KM");
                    tvNameNatureDetail.setText(categoryItem.getName_tourism());
                    tvAddressNature.setText(categoryItem.getLocation_tourism());
                    tvDescNature.setText(categoryItem.getInfo_tourism());
                    Glide.with(getApplicationContext()).load(categoryItem.getUrl_photo()).into(imgNature);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            natureDetailRef.addValueEventListener(eventListener);
            valueEventListener = eventListener;

        }
    }

    private double calculateDistance(double startLat, double startlng, double endlat, double endLng) {
        double earthRadius = 6371;
        double latDiff = Math.toRadians(startLat-endlat);
        double lngDiff = Math.toRadians(startlng-endLng);
        double a = Math.sin(latDiff /2) * Math.sin(latDiff /2) +
                Math.cos(Math.toRadians(startLat)) * Math.cos(Math.toRadians(endlat)) *
                        Math.sin(lngDiff /2) * Math.sin(lngDiff /2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = earthRadius * c;

        int meterConversion = 1609;

        return (distance*meterConversion/1000);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CategoryItem categoryItem = dataSnapshot.getValue(CategoryItem.class);
                double lattitude = categoryItem.getLat_location_tourism();
                double longitude = categoryItem.getLng_location_tourism();

                LatLng location = new LatLng(lattitude, longitude);
                gMap.addMarker(new MarkerOptions().position(location));
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,16.0f));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        natureDetailRef.addValueEventListener(eventListener);
        valueEventListener = eventListener;

    }

    @Override
    protected void onStart() {
        super.onStart();
        LoadDetail();
    }

    @Override
    protected void onStop() {
        super.onStop();
        natureDetailRef.removeEventListener(valueEventListener);
    }
}
