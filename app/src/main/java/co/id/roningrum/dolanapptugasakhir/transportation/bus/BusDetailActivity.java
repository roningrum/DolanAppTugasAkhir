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

package co.id.roningrum.dolanapptugasakhir.transportation.bus;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
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
import co.id.roningrum.dolanapptugasakhir.handler.HaversineHandler;
import co.id.roningrum.dolanapptugasakhir.model.TransportationItem;

public class BusDetailActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static final String EXTRA_BUS_KEY = "busKey";

    private static final String MAP_VIEW_KEY = "mapViewBundle";

    private final static String TAG = "Pesan";

    private GoogleMap busGoogleMap;
    private MapView busMapView;

    private DatabaseReference busDetailRef;

    private GPSHandler gpsHandler;
    private ValueEventListener valueEventListener;

    private TextView tvNameBusDetail, tvAddressBusDetail, tvDistanceAirport;

    private ImageView imgBusDetail;
    private CollapsingToolbarLayout collapsingToolbarBus;

    private double startLat;
    private double startlng;
    private double endlat;
    private double endLng;
    private double distance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_detail);
        tvNameBusDetail = findViewById(R.id.name_place_bus_detail);
        tvAddressBusDetail = findViewById(R.id.address_place_bus_detail);
        tvDistanceAirport = findViewById(R.id.distance_place_bus_detail);
        collapsingToolbarBus = findViewById(R.id.collapseToolbar_bus);
        busMapView = findViewById(R.id.location_bus_map_detail);
        imgBusDetail = findViewById(R.id.img_bus_detail);

        Toolbar toolbarBus = findViewById(R.id.toolbar_bus_detail);
        setSupportActionBar(toolbarBus);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp);


        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_KEY);
        }
        busMapView.onCreate(mapViewBundle);
        busMapView.getMapAsync(this);

        String busKey = getIntent().getStringExtra(EXTRA_BUS_KEY);
        if (busKey == null) {
            throw new IllegalArgumentException("Must pass Extra");
        }
        busDetailRef = FirebaseDatabase.getInstance().getReference().child("Transportation").child(busKey);
        gpsHandler = new GPSHandler(this);

        LoadBusDetail();


    }

    private void LoadBusDetail() {
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
                    distance = HaversineHandler.calculateDistance(startLat, startlng, endlat, endLng);

//                    Log.i("Haversine", "The Result : " +calculateDistance(gpsHandler.getLatitude(),gpsHandler.getLongitude(),endlat,endLng));

                    @SuppressLint("DefaultLocale") String distanceFormat = String.format("%.2f", distance);
                    tvDistanceAirport.setText("" + distanceFormat + " km");
                    tvNameBusDetail.setText(transportationItem.getName_transportation());
                    tvAddressBusDetail.setText(transportationItem.getLocation_transportation());
                    Glide.with(getApplicationContext()).load(transportationItem.getUrl_photo_transport()).into(imgBusDetail);
                    AppBarLayout appBarLayout = findViewById(R.id.app_bar_bus);
                    appBarLayout.addOnOffsetChangedListener(new AppBarLayout.BaseOnOffsetChangedListener() {
                        boolean isShow = true;
                        int scrollRange = -1;

                        @Override
                        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                            if (scrollRange == -1) {
                                scrollRange = appBarLayout.getTotalScrollRange();
                            }
                            if (scrollRange + verticalOffset == 0) {
                                collapsingToolbarBus.setTitle(transportationItem.getName_transportation());
                                isShow = true;
                            } else {
                                collapsingToolbarBus.setTitle(" ");
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
            busDetailRef.addValueEventListener(eventListener);
            valueEventListener = eventListener;
        }
    }

//    private double calculateDistance(double startLat, double startlng, double endlat, double endLng) {
//
//        double earthRadius = 6372.8;
//        double latDiff = Math.toRadians((endlat - startLat));
//        double lngDiff = Math.toRadians((endLng - startlng));
//
//        startLat = Math.toRadians(startLat);
//        endlat   = Math.toRadians(endlat);
//
//        double a    = Math.sin(latDiff / 2) * Math.sin(latDiff / 2);
//        double c    = Math.sin(lngDiff / 2) * Math.sin(lngDiff / 2) * Math.cos(startLat) * Math.cos(endlat);
//        return earthRadius * 2 * Math.asin(Math.sqrt(a + c));
//
//////        int meterConversion = 1609;
////
////        return earthRadius * c;
//    }
//
//    public static double haversin(double val) {
//        return Math.pow(Math.sin(val / 2), 2);
//    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_KEY, mapViewBundle);
            busMapView.onSaveInstanceState(mapViewBundle);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        busGoogleMap = googleMap;
        if (gpsHandler.isCanGetLocation()) {
            ValueEventListener eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    TransportationItem transportationItem = dataSnapshot.getValue(TransportationItem.class);
                    assert transportationItem != null;
                    endlat = transportationItem.getLat_transportation();
                    endLng = transportationItem.getLng_transportation();

                    LatLng location = new LatLng(endlat, endLng);
                    busGoogleMap.addMarker(new MarkerOptions().position(location));
                    busGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16.0f));
                    busGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng latLng) {
                            String uri = "http://maps.google.com/maps?saddr=" + gpsHandler.getLatitude() + "," + gpsHandler.getLongitude() + "&daddr=" + endlat + "," + endLng + "&mode=driving";
                            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                            startActivity(Intent.createChooser(intent, "Select an application"));
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "Firebase Database Error" + databaseError.getMessage());
                }
            };
            busDetailRef.addValueEventListener(eventListener);
            valueEventListener = eventListener;

        }


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
        busMapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LoadBusDetail();
        busMapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        busMapView.onStop();
        busDetailRef.removeEventListener(valueEventListener);

    }

    @Override
    protected void onPause() {
        super.onPause();
        busMapView.onPause();
    }
}
