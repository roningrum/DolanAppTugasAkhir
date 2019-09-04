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

package co.id.roningrum.dolanapptugasakhir.ui.detailactivity;

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
import co.id.roningrum.dolanapptugasakhir.model.PoliceItem;

public class DetailPoliceActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static final String EXTRA_POLICE_KEY = "policeKey";

    private static final String MAP_VIEW_KEY = "mapViewBundle";

    private final static String TAG = "Pesan";

    private GoogleMap policeGoogleMap;
    private MapView policeMapView;

    private DatabaseReference policeDetailRef;

    private GPSHandler gpsHandler;
    private ValueEventListener valueEventListener;

    private TextView tvNamePoliceDetail, tvAddressPoliceDetail, tvDistancePoliceDetail;

    private ImageView imgPoliceDetail;
    private CollapsingToolbarLayout collapsingToolbarPolice;

    private double startLat;
    private double startlng;
    private double endlat;
    private double endLng;
    private double distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_police);
        tvNamePoliceDetail = findViewById(R.id.name_place_police_detail);
        tvAddressPoliceDetail = findViewById(R.id.address_place_police_detail);
        tvDistancePoliceDetail = findViewById(R.id.distance_place_police_detail);
        collapsingToolbarPolice = findViewById(R.id.collapseToolbar_police);
        policeMapView = findViewById(R.id.location_police_map_detail);
        imgPoliceDetail = findViewById(R.id.img_police_detail);

        Toolbar toolbarPolice = findViewById(R.id.toolbar_police_detail);
        setSupportActionBar(toolbarPolice);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp);


        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_KEY);
        }
        policeMapView.onCreate(mapViewBundle);
        policeMapView.getMapAsync(this);

        String policeKey = getIntent().getStringExtra(EXTRA_POLICE_KEY);
        if (policeKey == null) {
            throw new IllegalArgumentException("Must pass Extra");
        }
        policeDetailRef = FirebaseDatabase.getInstance().getReference().child("Police").child(policeKey);
        gpsHandler = new GPSHandler(this);

        LoadPoliceDetail();


    }

    private void LoadPoliceDetail() {
        if (gpsHandler.isCanGetLocation()) {
            ValueEventListener eventListener = new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final PoliceItem policeItem = dataSnapshot.getValue(PoliceItem.class);
                    startLat = gpsHandler.getLatitude();
                    startlng = gpsHandler.getLongitude();
                    assert policeItem != null;
                    endlat = policeItem.getLat_police();
                    endLng = policeItem.getLng_police();
                    distance = HaversineHandler.calculateDistance(startLat, startlng, endlat, endLng);


                    @SuppressLint("DefaultLocale") String distanceFormat = String.format("%.2f", distance);
                    tvDistancePoliceDetail.setText("" + distanceFormat + " km");
                    tvNamePoliceDetail.setText(policeItem.getName_police());
                    tvAddressPoliceDetail.setText(policeItem.getLocation_police());
                    Glide.with(getApplicationContext()).load(policeItem.getUrl_photo_police()).into(imgPoliceDetail);
                    AppBarLayout appBarLayout = findViewById(R.id.app_bar_police);
                    appBarLayout.addOnOffsetChangedListener(new AppBarLayout.BaseOnOffsetChangedListener() {
                        boolean isShow = true;
                        int scrollRange = -1;

                        @Override
                        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                            if (scrollRange == -1) {
                                scrollRange = appBarLayout.getTotalScrollRange();
                            }
                            if (scrollRange + verticalOffset == 0) {
                                collapsingToolbarPolice.setTitle(policeItem.getName_police());
                                isShow = true;
                            } else {
                                collapsingToolbarPolice.setTitle(" ");
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
            policeDetailRef.addValueEventListener(eventListener);
            valueEventListener = eventListener;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        policeGoogleMap = googleMap;

        if (gpsHandler.isCanGetLocation()) {
            ValueEventListener eventListener = new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final PoliceItem policeItem = dataSnapshot.getValue(PoliceItem.class);
                    startLat = gpsHandler.getLatitude();
                    startlng = gpsHandler.getLongitude();

                    assert policeItem != null;
                    endlat = policeItem.getLat_police();
                    endLng = policeItem.getLng_police();

                    LatLng location = new LatLng(endlat, endLng);
                    policeGoogleMap.addMarker(new MarkerOptions().position(location));
                    policeGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16.0f));
                    policeGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
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
            policeDetailRef.addValueEventListener(eventListener);
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
        policeMapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LoadPoliceDetail();
        policeMapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        policeMapView.onStop();
        policeDetailRef.removeEventListener(valueEventListener);

    }

    @Override
    protected void onPause() {
        super.onPause();
        policeMapView.onPause();
    }
}
