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

package co.id.roningrum.dolanapptugasakhir.tourism.shopping;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

public class DetailShoppingActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String EXTRA_WISATA_KEY = "shopping_key";
    public static final String MAP_VIEW_KEY = "mapViewBundle";

    private final static String TAG = "Pesan";

    private GoogleMap shoppingLocationMap;
    private MapView shoppingMapView;
    private DatabaseReference shoppingDetailRef;

    private GPSHandler gpsHandler;
    private ValueEventListener valueEventListener;

    private TextView tvNameShoppingDetail, tvAddressShoppingDetail, tvDescShoppingDetail,
            tvDistanceShoppingDetail;

    private ImageView imgShoppingObject;
    private CollapsingToolbarLayout collapsingToolbarLayout_shopping;

    private double startLat;
    private double startLng;
    private double endLat;
    private double endLng;
    private double distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_shopping_category);
        tvNameShoppingDetail = findViewById(R.id.name_place_shopping_detail);
        tvAddressShoppingDetail = findViewById(R.id.address_place_shopping_detail);
        tvDescShoppingDetail = findViewById(R.id.info_place_shopping_detail);
        tvDistanceShoppingDetail = findViewById(R.id.distance_place_shopping_detail);
        imgShoppingObject = findViewById(R.id.img_shopping_place_detail);
        shoppingMapView = findViewById(R.id.loc_map_shopping);
        collapsingToolbarLayout_shopping = findViewById(R.id.collapseToolbar_shopping);

        Toolbar toolbarShopping = findViewById(R.id.toolbar_shopping_detail_top);
        setSupportActionBar(toolbarShopping);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_KEY);
        }
        shoppingMapView.onCreate(mapViewBundle);
        shoppingMapView.getMapAsync(this);

        String shoppingKey = getIntent().getStringExtra(EXTRA_WISATA_KEY);
        if(shoppingKey == null){
            throw new IllegalArgumentException("Must pass Extra");
        }
        shoppingDetailRef = FirebaseDatabase.getInstance().getReference().child("Tourism").child(shoppingKey);
        Query shoppingQuery = shoppingDetailRef.orderByChild("category_tourism").equalTo("belanja");
        gpsHandler = new GPSHandler(this);
        
        LoadShoppingDetail();
    }

    private void LoadShoppingDetail() {
        if(gpsHandler.isCanGetLocation()){
            ValueEventListener eventListener = new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final CategoryItem categoryItem = dataSnapshot.getValue(CategoryItem.class);
                    startLat = gpsHandler.getLatitude();
                    startLng = gpsHandler.getLongitude();
                    assert categoryItem != null;
                    endLat = categoryItem.getLat_location_tourism();
                    endLng = categoryItem.getLng_location_tourism();
                    distance = calculateDistance(startLat,startLng,endLat,endLng);

                    @SuppressLint("DefaultLocale") String distanceFormat = String.format("%.2f",distance);
                    tvDistanceShoppingDetail.setText(""+distanceFormat+" KM");
                    tvNameShoppingDetail.setText(categoryItem.getName_tourism());
                    tvAddressShoppingDetail.setText(categoryItem.getLocation_tourism());
                    tvDescShoppingDetail.setText(categoryItem.getInfo_tourism());
                    Glide.with(getApplicationContext()).load(categoryItem.getUrl_photo()).into(imgShoppingObject);
                    AppBarLayout appBarLayout = findViewById(R.id.app_bar_shopping);
                    appBarLayout.addOnOffsetChangedListener(new AppBarLayout.BaseOnOffsetChangedListener() {
                        boolean isShow = true;
                        int scrollRange = -1;

                        @Override
                        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                            if (scrollRange == -1) {
                                scrollRange = appBarLayout.getTotalScrollRange();
                            }
                            if (scrollRange + verticalOffset == 0) {
                                collapsingToolbarLayout_shopping.setTitle(categoryItem.getName_tourism());
                                isShow = true;
                            } else {
                                collapsingToolbarLayout_shopping.setTitle(" ");
                                isShow = false;
                            }

                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            shoppingDetailRef.addValueEventListener(eventListener);
            valueEventListener = eventListener;
        }
    }

    private double calculateDistance(double startLat, double startLng, double endLat, double endLng) {
        double earthRadius = 6371;
        double latDiff = Math.toRadians(startLat-endLat);
        double lngDiff = Math.toRadians(startLng-endLng);
        double a = Math.sin(latDiff /2) * Math.sin(latDiff /2) +
                Math.cos(Math.toRadians(startLat)) * Math.cos(Math.toRadians(endLat)) *
                        Math.sin(lngDiff /2) * Math.sin(lngDiff /2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = earthRadius * c;

        int meterConversion = 1609;

        return (distance*meterConversion/1000);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_KEY, mapViewBundle);
        }
        shoppingMapView.onSaveInstanceState(mapViewBundle);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        shoppingLocationMap = googleMap;

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CategoryItem categoryItem = dataSnapshot.getValue(CategoryItem.class);
                assert categoryItem != null;
                double lattitude = categoryItem.getLat_location_tourism();
                double longitude = categoryItem.getLng_location_tourism();

                LatLng location = new LatLng(lattitude, longitude);
                shoppingLocationMap.addMarker(new MarkerOptions().position(location));
                shoppingLocationMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,16.0f));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        shoppingDetailRef.addValueEventListener(eventListener);
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
        shoppingMapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LoadShoppingDetail();
        shoppingMapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        shoppingMapView.onStop();
        shoppingDetailRef.removeEventListener(valueEventListener);

    }

    @Override
    protected void onPause() {
        super.onPause();
        shoppingMapView.onPause();
    }

}
