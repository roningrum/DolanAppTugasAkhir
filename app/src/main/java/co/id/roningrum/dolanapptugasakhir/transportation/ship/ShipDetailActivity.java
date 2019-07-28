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

package co.id.roningrum.dolanapptugasakhir.transportation.ship;

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
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.handler.GPSHandler;
import co.id.roningrum.dolanapptugasakhir.item.TransportationItem;

public class ShipDetailActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static final String EXTRA_SHIP_KEY = "shipKey";

    public static final String MAP_VIEW_KEY = "mapViewBundle";

    private final static String TAG = "Pesan";

    private GoogleMap shipGoogleMap;
    private MapView shipMapView;

    private DatabaseReference shipDetailRef;

    private GPSHandler gpsHandler;
    private ValueEventListener valueEventListener;

    private TextView tvNameShipDetail, tvAddressShipDetail, tvDistanceShip;

    private ImageView imgShipDetail;
    private CollapsingToolbarLayout collapsingToolbarShip;

    private double startLat;
    private double startlng;
    private double endlat;
    private double endLng;
    private double distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ship_detail);
        tvNameShipDetail = findViewById(R.id.name_place_ship_detail);
        tvAddressShipDetail = findViewById(R.id.address_place_ship_detail);
        tvDistanceShip = findViewById(R.id.distance_place_ship_detail);
        shipMapView = findViewById(R.id.location_ship_map_detail);
        imgShipDetail = findViewById(R.id.img_ship_detail);
        collapsingToolbarShip = findViewById(R.id.collapseToolbar_ship);

        Toolbar toolbarShip = findViewById(R.id.toolbar_ship_detail);
        setSupportActionBar(toolbarShip);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_KEY);
        }
        shipMapView.onCreate(mapViewBundle);
        shipMapView.getMapAsync(this);

        String shipKey = getIntent().getStringExtra(EXTRA_SHIP_KEY);
        if (shipKey == null) {
            throw new IllegalArgumentException("Must pass Extra");
        }
        shipDetailRef = FirebaseDatabase.getInstance().getReference().child("Transportation").child(shipKey);
        gpsHandler = new GPSHandler(this);

        LoadShipDetail();
    }

    private void LoadShipDetail() {
        if (gpsHandler.isCanGetLocation()) {
            ValueEventListener eventListener = new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final TransportationItem transportationItem = dataSnapshot.getValue(TransportationItem.class);
                    startLat = gpsHandler.getLatitude();
                    startlng = gpsHandler.getLongitude();
                    assert transportationItem != null;
                    endlat = transportationItem.getLat_transportation();
                    endLng = transportationItem.getLng_transportation();
                    distance = calculateDistance(startLat, startlng, endlat, endLng);

                    @SuppressLint("DefaultLocale") String distanceFormat = String.format("%.2f", distance);
                    tvDistanceShip.setText("" + distanceFormat + " km");
                    tvNameShipDetail.setText(transportationItem.getName_transportation());
                    tvAddressShipDetail.setText(transportationItem.getLocation_transportation());
                    Glide.with(getApplicationContext()).load(transportationItem.getUrl_photo_transport()).into(imgShipDetail);
                    AppBarLayout appBarLayout = findViewById(R.id.app_bar_ship);
                    appBarLayout.addOnOffsetChangedListener(new AppBarLayout.BaseOnOffsetChangedListener() {
                        boolean isShow = true;
                        int scrollRange = -1;

                        @Override
                        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                            if (scrollRange == -1) {
                                scrollRange = appBarLayout.getTotalScrollRange();
                            }
                            if (scrollRange + verticalOffset == 0) {
                                collapsingToolbarShip.setTitle(transportationItem.getName_transportation());
                                isShow = true;
                            } else {
                                collapsingToolbarShip.setTitle(" ");
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
            shipDetailRef.addValueEventListener(eventListener);
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
            shipMapView.onSaveInstanceState(mapViewBundle);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        shipGoogleMap = googleMap;
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TransportationItem transportationItem = dataSnapshot.getValue(TransportationItem.class);
                assert transportationItem != null;
                endlat = transportationItem.getLat_transportation();
                endLng = transportationItem.getLng_transportation();

                LatLng location = new LatLng(endlat, endLng);
                shipGoogleMap.addMarker(new MarkerOptions().position(location));
                shipGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16.0f));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Firebase Database Error" + databaseError.getMessage());
            }
        };
        shipDetailRef.addValueEventListener(eventListener);
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
        shipMapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LoadShipDetail();
        shipMapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        shipMapView.onStop();
        shipDetailRef.removeEventListener(valueEventListener);

    }

    @Override
    protected void onPause() {
        super.onPause();
        shipMapView.onPause();
    }
}
