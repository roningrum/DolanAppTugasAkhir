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

package co.id.roningrum.dolanapptugasakhir.hospital;

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
import co.id.roningrum.dolanapptugasakhir.item.HospitalItem;

public class HospitalDetailActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static final String EXTRA_HOSPITAL_KEY = "hospitalKey";

    private static final String MAP_VIEW_KEY = "mapViewBundle";

    private final static String TAG = "Pesan";

    private GoogleMap hospitalGoogleMap;
    private MapView hospitalMapView;

    private DatabaseReference hospitalDetailRef;

    private GPSHandler gpsHandler;
    private ValueEventListener valueEventListener;

    private TextView tvNameHospitalDetail, tvAddressHospitalDetail, tvDistanceHospitalDetail;

    private ImageView imgHospitalDetail;
    private CollapsingToolbarLayout collapsingToolbarHospital;

    private double startLat;
    private double startlng;
    private double endlat;
    private double endLng;
    private double distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_detail);
        tvNameHospitalDetail = findViewById(R.id.name_place_hospital_detail);
        tvAddressHospitalDetail = findViewById(R.id.address_place_hospital_detail);
        tvDistanceHospitalDetail = findViewById(R.id.distance_place_hospital_detail);
        collapsingToolbarHospital = findViewById(R.id.collapseToolbar_hospital);
        hospitalMapView = findViewById(R.id.location_hospital_map_detail);
        imgHospitalDetail = findViewById(R.id.img_hospital_detail);

        Toolbar toolbarHospital = findViewById(R.id.toolbar_hospital_detail);
        setSupportActionBar(toolbarHospital);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_KEY);
        }
        hospitalMapView.onCreate(mapViewBundle);
        hospitalMapView.getMapAsync(this);

        String hospitalKey = getIntent().getStringExtra(EXTRA_HOSPITAL_KEY);
        if (hospitalKey == null) {
            throw new IllegalArgumentException("Must pass Extra");
        }
        hospitalDetailRef = FirebaseDatabase.getInstance().getReference().child("Hospital").child(hospitalKey);
        gpsHandler = new GPSHandler(this);

        LoadHospitaDetail();
    }

    private void LoadHospitaDetail() {
        if (gpsHandler.isCanGetLocation()) {
            ValueEventListener eventListener = new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final HospitalItem hospitalItem = dataSnapshot.getValue(HospitalItem.class);
                    startLat = gpsHandler.getLatitude();
                    startlng = gpsHandler.getLongitude();
                    assert hospitalItem != null;
                    endlat = hospitalItem.getLat_hospital();
                    endLng = hospitalItem.getLng_hospital();
                    distance = HaversineHandler.calculateDistance(startLat, startlng, endlat, endLng);


                    @SuppressLint("DefaultLocale") String distanceFormat = String.format("%.2f", distance);
                    tvDistanceHospitalDetail.setText("" + distanceFormat + " km");
                    tvNameHospitalDetail.setText(hospitalItem.getName_hospital());
                    tvAddressHospitalDetail.setText(hospitalItem.getLocation_hospital());
                    Glide.with(getApplicationContext()).load(hospitalItem.getUrl_photo_hospital()).into(imgHospitalDetail);
                    AppBarLayout appBarLayout = findViewById(R.id.app_bar_hospital);
                    appBarLayout.addOnOffsetChangedListener(new AppBarLayout.BaseOnOffsetChangedListener() {
                        boolean isShow = true;
                        int scrollRange = -1;

                        @Override
                        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                            if (scrollRange == -1) {
                                scrollRange = appBarLayout.getTotalScrollRange();
                            }
                            if (scrollRange + verticalOffset == 0) {
                                collapsingToolbarHospital.setTitle(hospitalItem.getName_hospital());
                                isShow = true;
                            } else {
                                collapsingToolbarHospital.setTitle(" ");
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
            hospitalDetailRef.addValueEventListener(eventListener);
            valueEventListener = eventListener;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        hospitalGoogleMap = googleMap;

        if (gpsHandler.isCanGetLocation()) {
            ValueEventListener eventListener = new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final HospitalItem hospitalItem = dataSnapshot.getValue(HospitalItem.class);
                    startLat = gpsHandler.getLatitude();
                    startlng = gpsHandler.getLongitude();

                    assert hospitalItem != null;
                    endlat = hospitalItem.getLat_hospital();
                    endLng = hospitalItem.getLng_hospital();

                    LatLng location = new LatLng(endlat, endLng);
                    hospitalGoogleMap.addMarker(new MarkerOptions().position(location));
                    hospitalGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16.0f));
                    hospitalGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
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
            hospitalDetailRef.addValueEventListener(eventListener);
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
        hospitalMapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LoadHospitaDetail();
        hospitalMapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        hospitalMapView.onStop();
        hospitalDetailRef.removeEventListener(valueEventListener);

    }

    @Override
    protected void onPause() {
        super.onPause();
        hospitalMapView.onPause();
    }
}
