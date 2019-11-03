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

package co.id.roningrum.dolanapptugasakhir.ui.tourism.tourismDetailActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.controller.FirebaseConstant;
import co.id.roningrum.dolanapptugasakhir.handler.GPSHandler;
import co.id.roningrum.dolanapptugasakhir.model.Tourism;
import co.id.roningrum.dolanapptugasakhir.util.HaversineHandler;

public class TourismVillageDetail extends AppCompatActivity implements OnMapReadyCallback {
    public static final String EXTRA_WISATA_KEY = "wisata_key";
    private static final String MAP_VIEW_KEY = "mapViewBundle";

    private final static String TAG = "Pesan";

    private GoogleMap villageLocationMap;
    private MapView villageMapView;
    private DatabaseReference villageDetailRef;

    private GPSHandler gpsHandler;

    private ValueEventListener valueEventListener;

    private TextView tvNameVillageDetail, tvAddressVillageDetail, tvDescVillageDetail,
            tvDistanceVillageDetail;

    private ImageView imgVillageObject;
    private CollapsingToolbarLayout collapsingToolbarLayout_village;

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
        villageMapView = findViewById(R.id.loc_village_map);
        collapsingToolbarLayout_village = findViewById(R.id.collapseToolbar_village);

        Toolbar toolbarVillage = findViewById(R.id.toolbar_village_detail_top);
        setSupportActionBar(toolbarVillage);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_KEY);
        }
        villageMapView.onCreate(mapViewBundle);
        villageMapView.getMapAsync(this);

        String villageKey = getIntent().getStringExtra(EXTRA_WISATA_KEY);
        if(villageKey == null){
            throw new IllegalArgumentException("Must pass Extra");
        }

        villageDetailRef = FirebaseConstant.getTourismRef(villageKey);
        gpsHandler = new GPSHandler(this);
        
        LoadDetailDesa();

    }

    private void LoadDetailDesa() {
        if (gpsHandler.isCanGetLocation()) {
            ValueEventListener eventListener = new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final Tourism tourism = dataSnapshot.getValue(Tourism.class);
                    startLat = gpsHandler.getLatitude();
                    startLng = gpsHandler.getLongitude();
                    assert tourism != null;
                    endLat = tourism.getLat_location_tourism();
                    endLng = tourism.getLng_location_tourism();
                    distance = HaversineHandler.calculateDistance(startLat, startLng, endLat, endLng);

                    @SuppressLint("DefaultLocale") String distanceFormat = String.format("%.2f", distance);
                    tvDistanceVillageDetail.setText("" + distanceFormat + " KM");
                    tvNameVillageDetail.setText(tourism.getName_tourism());
                    tvAddressVillageDetail.setText(tourism.getLocation_tourism());
                    tvDescVillageDetail.setText(tourism.getInfo_tourism());
                    Glide.with(getApplicationContext()).load(tourism.getUrl_photo()).into(imgVillageObject);
                    AppBarLayout appBarLayout = findViewById(R.id.app_bar_village);
                    appBarLayout.addOnOffsetChangedListener(new AppBarLayout.BaseOnOffsetChangedListener() {
                        boolean isShow = true;
                        int scrollRange = -1;

                        @Override
                        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                            if (scrollRange == -1) {
                                scrollRange = appBarLayout.getTotalScrollRange();
                            }
                            if (scrollRange + verticalOffset == 0) {
                                collapsingToolbarLayout_village.setTitle(tourism.getName_tourism());
                                isShow = true;
                            } else {
                                collapsingToolbarLayout_village.setTitle(" ");
                                isShow = false;
                            }

                        }
                    });
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
                    Tourism tourism = dataSnapshot.getValue(Tourism.class);
                    assert tourism != null;
                    double lattitude = tourism.getLat_location_tourism();
                    double longitude = tourism.getLng_location_tourism();

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

//        private double calculateDistance ( double startLat, double startLng, double endLat,
//        double endLng){
//
//            double earthRadius = 6371;
//            double latDiff = Math.toRadians(startLat-endLat);
//            double lngDiff = Math.toRadians(startLng-endLng);
//            double a = Math.sin(latDiff /2) * Math.sin(latDiff /2) +
//                    Math.cos(Math.toRadians(startLat)) * Math.cos(Math.toRadians(endLat)) *
//                            Math.sin(lngDiff /2) * Math.sin(lngDiff /2);
//            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
//            double distance = earthRadius * c;
//
//            int meterConversion = 1609;
//
//            return (distance*meterConversion/1000);
//        }

    @Override
    protected void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_KEY, mapViewBundle);
        }
        villageMapView.onSaveInstanceState(mapViewBundle);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Respond to the action bar's Up/Home button
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        villageMapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LoadDetailDesa();
        villageMapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        villageMapView.onStop();
        villageDetailRef.removeEventListener(valueEventListener);

    }

    @Override
    protected void onPause() {
        super.onPause();
        villageMapView.onPause();
    }


}
