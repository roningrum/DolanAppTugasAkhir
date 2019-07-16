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

package co.id.roningrum.dolanapptugasakhir.tourism.history;

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
import co.id.roningrum.dolanapptugasakhir.item.CategoryItem;

public class DetailHistoryActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static final String EXTRA_WISATA_KEY = "history_key";
    public static final String MAP_VIEW_KEY = "maViewBundle";
    private final static String TAG = "Pesan";


    private GoogleMap historyMap;
    private MapView historyMapView;

    private DatabaseReference historyDetailRef;
    private GPSHandler gpsHandler;
    private ValueEventListener valueEventListener;

    private TextView tvNameHistoryDetail, tvAddressHistoryDetail,
            tvDescHistoryDetail, tvDistanceHistoryDetail;

    private ImageView imgHistory;
    private CollapsingToolbarLayout collapsingToolbarLayout_history;

    private double startLat;
    private double startlng;
    private double endlat;
    private double endLng;
    private double distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_history);


        tvNameHistoryDetail = findViewById(R.id.name_place_history_detail);
        tvAddressHistoryDetail = findViewById(R.id.address_place_history_detail);
        tvDescHistoryDetail = findViewById(R.id.info_place_histroy_detail);
        tvDistanceHistoryDetail = findViewById(R.id.distance_place_history_detail);
        imgHistory = findViewById(R.id.img_history_place_detail);
        historyMapView = findViewById(R.id.loc_history_map_detail);
        collapsingToolbarLayout_history = findViewById(R.id.collapseToolbar_history);


        Toolbar toolbarHistory = findViewById(R.id.toolbar_history_detail);
        setSupportActionBar(toolbarHistory);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_KEY);
        }
        historyMapView.onCreate(mapViewBundle);
        historyMapView.getMapAsync(this);


        String historyKey = getIntent().getStringExtra(EXTRA_WISATA_KEY);
        if (historyKey == null) {
            throw new IllegalArgumentException("Must pass Extra");
        }
        historyDetailRef = FirebaseDatabase.getInstance().getReference().child("Tourism").child(historyKey);
        Query natureQuery = historyDetailRef.orderByChild("category_tourism").equalTo("sejarah");
        gpsHandler = new GPSHandler(this);

        LoadHistoryDetail();
    }

    private void LoadHistoryDetail() {
        if (gpsHandler.isCanGetLocation()) {
            ValueEventListener eventListener = new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final CategoryItem categoryItem = dataSnapshot.getValue(CategoryItem.class);
                    startLat = gpsHandler.getLatitude();
                    startlng = gpsHandler.getLongitude();
                    assert categoryItem != null;
                    endlat = categoryItem.getLat_location_tourism();
                    endLng = categoryItem.getLng_location_tourism();
                    distance = calculateDistance(startLat, startlng, endlat, endLng);

                    @SuppressLint("DefaultLocale") String distanceFormat = String.format("%.2f", distance);
                    tvDistanceHistoryDetail.setText("" + distanceFormat + " KM");
                    tvNameHistoryDetail.setText(categoryItem.getName_tourism());
                    tvAddressHistoryDetail.setText(categoryItem.getLocation_tourism());
                    tvDescHistoryDetail.setText(categoryItem.getInfo_tourism());
                    Glide.with(getApplicationContext()).load(categoryItem.getUrl_photo()).into(imgHistory);

                    AppBarLayout appBarLayout = findViewById(R.id.app_bar_history);
                    appBarLayout.addOnOffsetChangedListener(new AppBarLayout.BaseOnOffsetChangedListener() {
                        boolean isShow = true;
                        int scrollRange = -1;

                        @Override
                        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                            if (scrollRange == -1) {
                                scrollRange = appBarLayout.getTotalScrollRange();
                            }
                            if (scrollRange + verticalOffset == 0) {
                                collapsingToolbarLayout_history.setTitle(categoryItem.getName_tourism());
                                isShow = true;
                            } else {
                                collapsingToolbarLayout_history.setTitle(" ");
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
            historyDetailRef.addValueEventListener(eventListener);
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
        historyMapView.onSaveInstanceState(mapViewBundle);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        historyMap = googleMap;
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CategoryItem categoryItem = dataSnapshot.getValue(CategoryItem.class);
                assert categoryItem != null;
                double lattitude = categoryItem.getLat_location_tourism();
                double longitude = categoryItem.getLng_location_tourism();

                LatLng location = new LatLng(lattitude, longitude);
                historyMap.addMarker(new MarkerOptions().position(location));
                historyMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16.0f));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Firebase Database Error" + databaseError.getMessage());
            }
        };
        historyDetailRef.addValueEventListener(eventListener);
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
    protected void onResume() {
        super.onResume();
        historyMapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LoadHistoryDetail();
    }

    @Override
    protected void onStop() {
        super.onStop();
        historyMapView.onStop();
        historyDetailRef.removeEventListener(valueEventListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        historyMapView.onPause();
    }
}
