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
import android.content.Intent;
import android.net.Uri;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.handler.GPSHandler;
import co.id.roningrum.dolanapptugasakhir.handler.HaversineHandler;
import co.id.roningrum.dolanapptugasakhir.model.TransportationItem;

public class DetailAirportActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String EXTRA_AIRPORT_KEY = "airportKey";

    private static final String MAP_VIEW_KEY = "mapViewBundle";

    private final static String TAG = "Pesan";

    private GoogleMap airportGoogleMap;
    private MapView airportMapView;

    private DatabaseReference airportDetailRef;

    private GPSHandler gpsHandler;
    private ValueEventListener valueEventListener;

    private TextView tvNameAirportDetail, tvAddressAiportDetail, tvDistanceAirport;

    private ImageView imgAirportDetail;
    private CollapsingToolbarLayout collapsingToolbarAiport;


    private double startLat;
    private double startlng;
    private double endlat;
    private double endLng;
    private double distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_airport);
        tvNameAirportDetail = findViewById(R.id.name_place_airport_detail);
        tvAddressAiportDetail = findViewById(R.id.address_place_airport_detail);
        tvDistanceAirport = findViewById(R.id.distance_place_airport_detail);
        imgAirportDetail = findViewById(R.id.img_airport_detail);
        collapsingToolbarAiport = findViewById(R.id.collapseToolbar_airport);
        airportMapView = findViewById(R.id.location_airport_map_detail);

        Toolbar toolbarAirport = findViewById(R.id.toolbar_aiport_detail);
        setSupportActionBar(toolbarAirport);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_KEY);
        }
        airportMapView.onCreate(mapViewBundle);
        airportMapView.getMapAsync(this);

        String airportKey = getIntent().getStringExtra(EXTRA_AIRPORT_KEY);
        if (airportKey == null) {
            throw new IllegalArgumentException("Must pass Extra");
        }
        airportDetailRef = FirebaseDatabase.getInstance().getReference().child("Transportation").child(airportKey);
        gpsHandler = new GPSHandler(this);

        LoadAirportDetail();
    }

    private void LoadAirportDetail() {
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

                    @SuppressLint("DefaultLocale") String distanceFormat = String.format("%.2f", distance);
                    tvDistanceAirport.setText("" + distanceFormat + " km");
                    tvNameAirportDetail.setText(transportationItem.getName_transportation());
                    tvAddressAiportDetail.setText(transportationItem.getLocation_transportation());
                    Glide.with(getApplicationContext()).load(transportationItem.getUrl_photo_transport()).into(imgAirportDetail);
                    AppBarLayout appBarLayout = findViewById(R.id.app_bar_airport);
                    appBarLayout.addOnOffsetChangedListener(new AppBarLayout.BaseOnOffsetChangedListener() {
                        boolean isShow = true;
                        int scrollRange = -1;

                        @Override
                        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                            if (scrollRange == -1) {
                                scrollRange = appBarLayout.getTotalScrollRange();
                            }
                            if (scrollRange + verticalOffset == 0) {
                                collapsingToolbarAiport.setTitle(transportationItem.getName_transportation());
                                isShow = true;
                            } else {
                                collapsingToolbarAiport.setTitle(" ");
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
            airportDetailRef.addValueEventListener(eventListener);
            valueEventListener = eventListener;
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_KEY, mapViewBundle);
            airportMapView.onSaveInstanceState(mapViewBundle);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        airportGoogleMap = googleMap;
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final TransportationItem transportationItem = dataSnapshot.getValue(TransportationItem.class);
                assert transportationItem != null;
                endlat = transportationItem.getLat_transportation();
                endLng = transportationItem.getLng_transportation();

                LatLng location = new LatLng(endlat, endLng);
                airportGoogleMap.addMarker(new MarkerOptions().position(location));
                airportGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16.0f));

                airportGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        String uri = "http://maps.google.com/maps?saddr=" + startLat + "," + startlng + "&daddr=" + endlat + "," + endLng + "&mode=driving";
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
        airportDetailRef.addValueEventListener(eventListener);
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
        airportMapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LoadAirportDetail();
        airportMapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        airportMapView.onStop();
        airportDetailRef.removeEventListener(valueEventListener);

    }

    @Override
    protected void onPause() {
        super.onPause();
        airportMapView.onPause();
    }


}
