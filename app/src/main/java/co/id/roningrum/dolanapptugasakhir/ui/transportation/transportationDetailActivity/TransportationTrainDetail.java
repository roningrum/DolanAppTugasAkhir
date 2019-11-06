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

package co.id.roningrum.dolanapptugasakhir.ui.transportation.transportationDetailActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
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
import co.id.roningrum.dolanapptugasakhir.model.Transportation;
import co.id.roningrum.dolanapptugasakhir.util.HaversineHandler;

public class TransportationTrainDetail extends AppCompatActivity implements OnMapReadyCallback {

    public static final String EXTRA_TRAIN_KEY = "busKey";

    private static final String MAP_VIEW_KEY = "mapViewBundle";

    private final static String TAG = "Pesan";

    private GoogleMap trainGoogleMap;
    private MapView trainMapView;

    private DatabaseReference trainDetailRef;

    private GPSHandler gpsHandler;
    private ValueEventListener valueEventListener;

    private TextView tvNameTrainDetail, tvAddressTrainDetail, tvDistanceTrainport;

    private ImageView imgTrainDetail;
    private CollapsingToolbarLayout collapsingToolbarTrain;

    private double startLat;
    private double startlng;
    private double endlat;
    private double endLng;
    private double distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_transportation_train);
        tvNameTrainDetail = findViewById(R.id.name_place_transport_detail);
        tvAddressTrainDetail = findViewById(R.id.location_transport_detail);
        tvDistanceTrainport = findViewById(R.id.distance_location_trans);
        imgTrainDetail = findViewById(R.id.img_train_detail);
        collapsingToolbarTrain = findViewById(R.id.collapseToolbar_train);
        trainMapView = findViewById(R.id.location_trans_map_detail);

        Toolbar toolbarTrain = findViewById(R.id.toolbar_train_detail);
        setSupportActionBar(toolbarTrain);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp);


        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_KEY);
        }
        trainMapView.onCreate(mapViewBundle);
        trainMapView.getMapAsync(this);

        String trainKey = getIntent().getStringExtra(EXTRA_TRAIN_KEY);
        if (trainKey == null) {
            throw new IllegalArgumentException("Must pass Extra");
        }
        trainDetailRef = FirebaseConstant.getTransportByKey(trainKey);
        gpsHandler = new GPSHandler(this);

        LoadTrainDetail();


    }

    private void LoadTrainDetail() {
        if (gpsHandler.isCanGetLocation()) {
            ValueEventListener eventListener = new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final Transportation transportation = dataSnapshot.getValue(Transportation.class);
                    startLat = gpsHandler.getLatitude();
                    startlng = gpsHandler.getLongitude();
                    assert transportation != null;
                    endlat = transportation.getLat_transportation();
                    endLng = transportation.getLng_transportation();
                    distance = HaversineHandler.calculateDistance(startLat, startlng, endlat, endLng);

                    @SuppressLint("DefaultLocale") String distanceFormat = String.format("%.2f", distance);
                    tvDistanceTrainport.setText("" + distanceFormat + " km");
                    tvNameTrainDetail.setText(transportation.getName_transportation());
                    tvAddressTrainDetail.setText(transportation.getLocation_transportation());
                    Glide.with(getApplicationContext()).load(transportation.getUrl_photo_transport()).into(imgTrainDetail);
                    AppBarLayout appBarLayout = findViewById(R.id.app_bar_train);
                    appBarLayout.addOnOffsetChangedListener(new AppBarLayout.BaseOnOffsetChangedListener() {
                        boolean isShow = true;
                        int scrollRange = -1;

                        @Override
                        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                            if (scrollRange == -1) {
                                scrollRange = appBarLayout.getTotalScrollRange();
                            }
                            if (scrollRange + verticalOffset == 0) {
                                collapsingToolbarTrain.setTitle(transportation.getName_transportation());
                                isShow = true;
                            } else {
                                collapsingToolbarTrain.setTitle(" ");
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
            trainDetailRef.addValueEventListener(eventListener);
            valueEventListener = eventListener;
        }
    }

//    private double calculateDistance(double startLat, double startlng, double endlat, double endLng) {
//        double earthRadius = 6371;
//        double latDiff = Math.toRadians(startLat - endlat);
//        double lngDiff = Math.toRadians(startlng - endLng);
//        double a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2) +
//                Math.cos(Math.toRadians(startLat)) * Math.cos(Math.toRadians(endlat)) *
//                        Math.sin(lngDiff / 2) * Math.sin(lngDiff / 2);
//        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
//        double distance = earthRadius * c;
//
//        int meterConversion = 1609;
//
//        return (distance * meterConversion / 1000);
//    }


    @Override
    protected void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_KEY, mapViewBundle);
            trainMapView.onSaveInstanceState(mapViewBundle);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        trainGoogleMap = googleMap;
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Transportation transportation = dataSnapshot.getValue(Transportation.class);
                assert transportation != null;
                endlat = transportation.getLat_transportation();
                endLng = transportation.getLng_transportation();

                LatLng location = new LatLng(endlat, endLng);
                trainGoogleMap.addMarker(new MarkerOptions().position(location));
                trainGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16.0f));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Firebase Database Error" + databaseError.getMessage());
            }
        };
        trainDetailRef.addValueEventListener(eventListener);
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
        trainMapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LoadTrainDetail();
        trainMapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        trainMapView.onStop();
        trainDetailRef.removeEventListener(valueEventListener);

    }

    @Override
    protected void onPause() {
        super.onPause();
        trainMapView.onPause();
    }
}
