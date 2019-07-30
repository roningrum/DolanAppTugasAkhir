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

package co.id.roningrum.dolanapptugasakhir.tourism.food;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.handler.GPSHandler;
import co.id.roningrum.dolanapptugasakhir.item.TourismItem;

public class DetailFoodActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static final String EXTRA_WISATA_KEY = "kuliner_key";
    public static final String MAP_VIEW_KEY = "mapViewBundle";
    private final static String TAG = "Pesan";

    private GoogleMap foodMap;
    private MapView foodMapView;
    private DatabaseReference foodDetailRef;
    private GPSHandler gpsHandler;
    private ValueEventListener valueEventListener;

    private TextView tvNameFoodDetail, tvAddressFoodDetail,
            tvDescFoodDetail, tvDistanceFoodDetail;

    private ImageView imgFoodDetail;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    private double startLat;
    private double startlng;
    private double endlat;
    private double endLng;
    private double distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_food);

        tvNameFoodDetail = findViewById(R.id.name_place_food_detail);
        tvDescFoodDetail = findViewById(R.id.info_place_food_detail);
        tvAddressFoodDetail = findViewById(R.id.address_place_food_detail);
        tvDistanceFoodDetail = findViewById(R.id.distance_place_food_detail);
        imgFoodDetail = findViewById(R.id.img_food_place_detail);
        collapsingToolbarLayout = findViewById(R.id.collapseToolbar_food);
        foodMapView = findViewById(R.id.map_place_food_detail);

        Toolbar toolbarFood = findViewById(R.id.toolbar_food_detail);
        setSupportActionBar(toolbarFood);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_KEY);
        }
        foodMapView.onCreate(mapViewBundle);
        foodMapView.getMapAsync(this);


        String foodKey = getIntent().getStringExtra(EXTRA_WISATA_KEY);
        if (foodKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA");
        }
        foodDetailRef = FirebaseDatabase.getInstance().getReference().child("Tourism").child(foodKey);
        Query foodQuery = foodDetailRef.orderByChild("category_tourism").equalTo("kuliner");
        gpsHandler = new GPSHandler(this);
        LoadFoodDetail();
    }

    private void LoadFoodDetail() {
        if (gpsHandler.isCanGetLocation()) {
            ValueEventListener eventListener = new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final TourismItem tourismItem = dataSnapshot.getValue(TourismItem.class);
                    startLat = gpsHandler.getLatitude();
                    startlng = gpsHandler.getLongitude();
                    assert tourismItem != null;
                    endlat = tourismItem.getLat_location_tourism();
                    endLng = tourismItem.getLng_location_tourism();
                    distance = calculateDistance(startLat, startlng, endlat, endLng);

                    @SuppressLint("DefaultLocale") String distanceFormat = String.format("%.2f", distance);
                    tvDistanceFoodDetail.setText("" + distanceFormat + " KM");
                    tvNameFoodDetail.setText(tourismItem.getName_tourism());
                    tvAddressFoodDetail.setText(tourismItem.getLocation_tourism());
                    tvDescFoodDetail.setText(tourismItem.getInfo_tourism());
                    Glide.with(getApplicationContext()).load(tourismItem.getUrl_photo()).into(imgFoodDetail);
                    AppBarLayout appBarLayout = findViewById(R.id.app_bar_food);
                    appBarLayout.addOnOffsetChangedListener(new AppBarLayout.BaseOnOffsetChangedListener() {
                        boolean isShow = true;
                        int scrollRange = -1;

                        @Override
                        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                            if (scrollRange == -1) {
                                scrollRange = appBarLayout.getTotalScrollRange();
                            }
                            if (scrollRange + verticalOffset == 0) {
                                collapsingToolbarLayout.setTitle(tourismItem.getName_tourism());
                                isShow = true;
                            } else {
                                collapsingToolbarLayout.setTitle(" ");
                                isShow = false;
                            }

                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "Firebase Database Error" + databaseError.getMessage());
                }
            };
            foodDetailRef.addValueEventListener(eventListener);
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_KEY, mapViewBundle);
        }
        foodMapView.onSaveInstanceState(mapViewBundle);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        foodMap = googleMap;
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TourismItem tourismItem = dataSnapshot.getValue(TourismItem.class);
//                assert tourismItem != null;
                assert tourismItem != null;
                double lattitude = tourismItem.getLat_location_tourism();
                double longitude = tourismItem.getLng_location_tourism();

                LatLng location = new LatLng(lattitude, longitude);
                foodMap.addMarker(new MarkerOptions().position(location));
                foodMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15.0f));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        foodDetailRef.addValueEventListener(eventListener);
        valueEventListener = eventListener;

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
    protected void onStart() {
        super.onStart();
        LoadFoodDetail();
        foodMapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        foodMapView.onStop();
        foodDetailRef.removeEventListener(valueEventListener);

    }

    @Override
    protected void onResume() {
        super.onResume();
        foodMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        foodMapView.onPause();
    }
}
