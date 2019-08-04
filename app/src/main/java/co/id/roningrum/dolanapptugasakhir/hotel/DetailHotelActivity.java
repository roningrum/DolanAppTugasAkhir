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

package co.id.roningrum.dolanapptugasakhir.hotel;

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
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import co.id.roningrum.dolanapptugasakhir.model.HotelItem;

public class DetailHotelActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {
    public static final String EXTRA_HOTEL_KEY = "hotel_key";
    private static final String MAP_VIEW_KEY = "mapViewBundle";

    private final static String TAG = "Pesan";

    private GoogleMap hotelGoogleMap;
    private MapView hotelMapView;

    private DatabaseReference hotelDetailRef;

    private GPSHandler gpsHandler;
    private ValueEventListener valueEventListener;

    private TextView tvNameHotelDetail, tvAddressHotelDetail, tvDistanceHotelEducation;

    private ImageView imgHotelDetail;
    private CollapsingToolbarLayout collapsingToolbarHotel;

    private double startLat;
    private double startlng;
    private double endlat;
    private double endLng;
    private double distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_hotel);
        tvNameHotelDetail = findViewById(R.id.name_place_hotel_detail);
        tvAddressHotelDetail = findViewById(R.id.address_place_hotel_detail);
        tvDistanceHotelEducation = findViewById(R.id.distance_place_hotel_detail);
        imgHotelDetail = findViewById(R.id.img_hotel_detail);
        hotelMapView = findViewById(R.id.loc_hotel_map_detail);
        collapsingToolbarHotel = findViewById(R.id.collapseToolbar_hotel);
        LinearLayout btnOrderHotel = findViewById(R.id.btn_order_link_hotel);
        LinearLayout btnOrderHotel1 = findViewById(R.id.btn_order_link_hotel1);

        Toolbar toolbarHotel = findViewById(R.id.toolbar_hotel_detail);
        setSupportActionBar(toolbarHotel);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_KEY);
        }
        hotelMapView.onCreate(mapViewBundle);
        hotelMapView.getMapAsync(this);

        String hotelKey = getIntent().getStringExtra(EXTRA_HOTEL_KEY);
        if (hotelKey == null) {
            throw new IllegalArgumentException("Must pass Extra");
        }
        hotelDetailRef = FirebaseDatabase.getInstance().getReference().child("Hotel").child(hotelKey);
        gpsHandler = new GPSHandler(this);

        btnOrderHotel1.setOnClickListener(this);
        btnOrderHotel.setOnClickListener(this);

        LoadHotelDetail();

    }

    private void LoadHotelDetail() {
        if (gpsHandler.isCanGetLocation()) {
            ValueEventListener eventListener = new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final HotelItem hotelItem = dataSnapshot.getValue(HotelItem.class);
                    startLat = gpsHandler.getLatitude();
                    startlng = gpsHandler.getLongitude();
                    assert hotelItem != null;
                    endlat = hotelItem.getLat_location_hotel();
                    endLng = hotelItem.getLng_location_hotel();
                    distance = HaversineHandler.calculateDistance(startLat, startlng, endlat, endLng);

                    @SuppressLint("DefaultLocale") String distanceFormat = String.format("%.2f", distance);
                    tvDistanceHotelEducation.setText("" + distanceFormat + " km");
                    tvNameHotelDetail.setText(hotelItem.getName_hotel());
                    tvAddressHotelDetail.setText(hotelItem.getLocation_hotel());
                    Glide.with(getApplicationContext()).load(hotelItem.getUrl_photo_hotel()).into(imgHotelDetail);
                    AppBarLayout appBarLayout = findViewById(R.id.app_bar_hotel);
                    appBarLayout.addOnOffsetChangedListener(new AppBarLayout.BaseOnOffsetChangedListener() {
                        boolean isShow = true;
                        int scrollRange = -1;

                        @Override
                        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                            if (scrollRange == -1) {
                                scrollRange = appBarLayout.getTotalScrollRange();
                            }
                            if (scrollRange + verticalOffset == 0) {
                                collapsingToolbarHotel.setTitle(hotelItem.getName_hotel());
                                isShow = true;
                            } else {
                                collapsingToolbarHotel.setTitle(" ");
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
            hotelDetailRef.addValueEventListener(eventListener);
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
            hotelMapView.onSaveInstanceState(mapViewBundle);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        hotelGoogleMap = googleMap;
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HotelItem hotelItem = dataSnapshot.getValue(HotelItem.class);
                assert hotelItem != null;
                endlat = hotelItem.getLat_location_hotel();
                endLng = hotelItem.getLng_location_hotel();

                LatLng location = new LatLng(endlat, endLng);
                hotelGoogleMap.addMarker(new MarkerOptions().position(location));
                hotelGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16.0f));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Firebase Database Error" + databaseError.getMessage());
            }
        };
        hotelDetailRef.addValueEventListener(eventListener);
        valueEventListener = eventListener;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_order_link_hotel:
                getOrderKamarHotel();
                break;
            case R.id.btn_order_link_hotel1:
                getOrderKamarHotel1();
                break;
        }

    }

    private void getOrderKamarHotel1() {
        final Query orderHotelLinkQuery = hotelDetailRef.orderByChild("order_link_hotel1");
        orderHotelLinkQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HotelItem hotelItem = dataSnapshot.getValue(HotelItem.class);
                assert hotelItem != null;
                String orderLink = hotelItem.getOrder_link_hotel1();

                if (orderLink.equals("tidak tersedia")) {
                    Toast.makeText(DetailHotelActivity.this, "Maaf Link Tidak tersedia", Toast.LENGTH_SHORT).show();
                } else {
                    Intent linkOrderNow = new Intent(Intent.ACTION_VIEW, Uri.parse(orderLink));
                    startActivity(linkOrderNow);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "" + databaseError.getMessage());
            }
        });

    }

    private void getOrderKamarHotel() {
        final Query orderHotelLinkQuery = hotelDetailRef.orderByChild("order_link_hotel");
        orderHotelLinkQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HotelItem hotelItem = dataSnapshot.getValue(HotelItem.class);
                assert hotelItem != null;
                String orderLink = hotelItem.getOrder_link_hotel();

                if (orderLink.equals("tidak tersedia")) {
                    Toast.makeText(DetailHotelActivity.this, "Maaf Link Tidak tersedia", Toast.LENGTH_SHORT).show();
                } else {
                    Intent linkOrderNow = new Intent(Intent.ACTION_VIEW, Uri.parse(orderLink));
                    startActivity(linkOrderNow);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "" + databaseError.getMessage());
            }
        });
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
        hotelMapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LoadHotelDetail();
        hotelMapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        hotelMapView.onStop();
        hotelDetailRef.removeEventListener(valueEventListener);

    }

    @Override
    protected void onPause() {
        super.onPause();
        hotelMapView.onPause();
    }
}
