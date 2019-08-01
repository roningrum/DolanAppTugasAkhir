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

package co.id.roningrum.dolanapptugasakhir.tourism.recreation;

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
import co.id.roningrum.dolanapptugasakhir.handler.HaversineHandler;
import co.id.roningrum.dolanapptugasakhir.item.TourismItem;

public class DetailRecreationActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static final String EXTRA_WISATA_KEY = "rekreasi_key";
    private static final String MAP_VIEW_KEY = "mapViewBundle";

    private final static String TAG = "Pesan";
    private GoogleMap recreationMap;
    private MapView recreationMapView;

    private DatabaseReference recreationDetailRef;
    private GPSHandler gpsHandler;
    private ValueEventListener valueEventListener;

    private TextView tvNameRecreationDetail, tvAddressRecreationDetail,
            tvDescRecreationDetail, tvDistanceRecreationDetail;

    private ImageView imgRecreation;
    private CollapsingToolbarLayout collapsingToolbarLayout_recreation;

    private double startLat;
    private double startlng;
    private double endlat;
    private double endLng;
    private double distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_recreation);

        tvNameRecreationDetail = findViewById(R.id.name_place_recreation_detail);
        tvAddressRecreationDetail = findViewById(R.id.address_place_recreation_detail);
        tvDescRecreationDetail = findViewById(R.id.info_place_recreation_detail);
        tvDistanceRecreationDetail = findViewById(R.id.distance_place_recreation_detail);
        imgRecreation = findViewById(R.id.img_recreation_place_detail);
        recreationMapView = findViewById(R.id.loc_recreation_map);
        collapsingToolbarLayout_recreation = findViewById(R.id.collapseToolbar_recreation);


        Toolbar toolbarRekreasi = findViewById(R.id.toolbar_recreation_detail);
        setSupportActionBar(toolbarRekreasi);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_KEY);
        }
        recreationMapView.onCreate(mapViewBundle);
        recreationMapView.getMapAsync(this);


        String recreationKey = getIntent().getStringExtra(EXTRA_WISATA_KEY);
        if (recreationKey == null) {
            throw new IllegalArgumentException("Must pass Extra");
        }
        recreationDetailRef = FirebaseDatabase.getInstance().getReference().child("Tourism").child(recreationKey);
        Query recreationQuery = recreationDetailRef.orderByChild("category_tourism").equalTo("rekreasi");
        gpsHandler = new GPSHandler(this);

        LoadRecreationDetail();

    }

    private void LoadRecreationDetail() {

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
                    distance = HaversineHandler.calculateDistance(startLat, startlng, endlat, endLng);

                    @SuppressLint("DefaultLocale") String distanceFormat = String.format("%.2f", distance);
                    tvDistanceRecreationDetail.setText("" + distanceFormat + " KM");
                    tvNameRecreationDetail.setText(tourismItem.getName_tourism());
                    tvAddressRecreationDetail.setText(tourismItem.getLocation_tourism());
                    tvDescRecreationDetail.setText(tourismItem.getInfo_tourism());
                    Glide.with(getApplicationContext()).load(tourismItem.getUrl_photo()).into(imgRecreation);

                    AppBarLayout appBarLayout = findViewById(R.id.app_bar_recreation);
                    appBarLayout.addOnOffsetChangedListener(new AppBarLayout.BaseOnOffsetChangedListener() {
                        boolean isShow = true;
                        int scrollRange = -1;

                        @Override
                        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                            if (scrollRange == -1) {
                                scrollRange = appBarLayout.getTotalScrollRange();
                            }
                            if (scrollRange + verticalOffset == 0) {
                                collapsingToolbarLayout_recreation.setTitle(tourismItem.getName_tourism());
                                isShow = true;
                            } else {
                                collapsingToolbarLayout_recreation.setTitle(" ");
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
            recreationDetailRef.addValueEventListener(eventListener);
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_KEY, mapViewBundle);
        }
        recreationMapView.onSaveInstanceState(mapViewBundle);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        recreationMap = googleMap;
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TourismItem tourismItem = dataSnapshot.getValue(TourismItem.class);
                assert tourismItem != null;
                double lattitude = tourismItem.getLat_location_tourism();
                double longitude = tourismItem.getLng_location_tourism();

                LatLng location = new LatLng(lattitude, longitude);
                recreationMap.addMarker(new MarkerOptions().position(location));
                recreationMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16.0f));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Firebase Database Error" + databaseError.getMessage());
            }
        };
        recreationDetailRef.addValueEventListener(eventListener);
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
        recreationMapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LoadRecreationDetail();
        recreationMapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        recreationMapView.onStop();
        recreationDetailRef.removeEventListener(valueEventListener);

    }

    @Override
    protected void onPause() {
        super.onPause();
        recreationMapView.onPause();
    }
}
