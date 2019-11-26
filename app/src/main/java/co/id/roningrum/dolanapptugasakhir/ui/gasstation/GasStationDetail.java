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

package co.id.roningrum.dolanapptugasakhir.ui.gasstation;

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
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.firebasequery.FirebaseConstant;
import co.id.roningrum.dolanapptugasakhir.handler.GPSHandler;
import co.id.roningrum.dolanapptugasakhir.model.GasStation;
import co.id.roningrum.dolanapptugasakhir.util.HaversineHandler;

public class GasStationDetail extends AppCompatActivity implements OnMapReadyCallback {

    public static final String EXTRA_GAS_KEY = "gasKey";

    private static final String MAP_VIEW_KEY = "mapViewBundle";

    private final static String TAG = "Pesan";

    private MapView gasMapView;

    private DatabaseReference gasDetailRef;

    private GPSHandler gpsHandler;
    private ValueEventListener valueEventListener;

    private TextView tvNameGasDetail, tvAddressGasDetail, tvDistanceGasDetail;

    private ImageView imgGasDetail;
    private CollapsingToolbarLayout collapsingToolbarGas;

    private double startLat;
    private double startlng;
    private double endlat;
    private double endLng;
    private double distance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_gas_station);
        tvNameGasDetail = findViewById(R.id.name_place_gas_detail);
        tvAddressGasDetail = findViewById(R.id.location_gas_detail);
        tvDistanceGasDetail = findViewById(R.id.distance_location_gas);
        collapsingToolbarGas = findViewById(R.id.collapseToolbar_spbu);
        gasMapView = findViewById(R.id.location_gas_map_detail);
        imgGasDetail = findViewById(R.id.img_spbu_detail);

        Toolbar toolbarSPBU = findViewById(R.id.toolbar_spbu_detail);
        setSupportActionBar(toolbarSPBU);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_KEY);
        }
        gasMapView.onCreate(mapViewBundle);
        gasMapView.getMapAsync(this);

        String gasKey = getIntent().getStringExtra(EXTRA_GAS_KEY);
        if (gasKey == null) {
            throw new IllegalArgumentException("Must pass Extra");
        }
        gasDetailRef = FirebaseConstant.getGasByKey(gasKey);
        gpsHandler = new GPSHandler(this);

        LoadGasDetail();
    }

    private void LoadGasDetail() {
        if (gpsHandler.isCanGetLocation()) {
            ValueEventListener eventListener = new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final GasStation gasStation = dataSnapshot.getValue(GasStation.class);
                    startLat = gpsHandler.getLatitude();
                    startlng = gpsHandler.getLongitude();
                    assert gasStation != null;
                    endlat = gasStation.getLat_gasstation();
                    endLng = gasStation.getLng_gasstation();
                    distance = HaversineHandler.calculateDistance(startLat, startlng, endlat, endLng);


                    @SuppressLint("DefaultLocale") String distanceFormat = String.format("%.2f", distance);
                    tvDistanceGasDetail.setText("" + distanceFormat + " km");
                    tvNameGasDetail.setText(gasStation.getName_gasstation());
                    tvAddressGasDetail.setText(gasStation.getLocation_gasstation());
                    Glide.with(getApplicationContext()).load(gasStation.getUrl_photo_gasstation()).into(imgGasDetail);
                    AppBarLayout appBarLayout = findViewById(R.id.app_bar_spbu);
                    appBarLayout.addOnOffsetChangedListener(new AppBarLayout.BaseOnOffsetChangedListener() {
                        boolean isShow = true;
                        int scrollRange = -1;

                        @Override
                        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                            if (scrollRange == -1) {
                                scrollRange = appBarLayout.getTotalScrollRange();
                            }
                            if (scrollRange + verticalOffset == 0) {
                                collapsingToolbarGas.setTitle(gasStation.getName_gasstation());
                                isShow = true;
                            } else {
                                collapsingToolbarGas.setTitle(" ");
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
            gasDetailRef.addValueEventListener(eventListener);
            valueEventListener = eventListener;
        }
    }

    @Override
    protected void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_KEY, mapViewBundle);
            gasMapView.onSaveInstanceState(mapViewBundle);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        showGasMapDetail(googleMap);

    }

    private void showGasMapDetail(final GoogleMap gasGoogleMap) {
        if (gpsHandler.isCanGetLocation()) {
            ValueEventListener eventListener = new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final GasStation gasStation = dataSnapshot.getValue(GasStation.class);
                    startLat = gpsHandler.getLatitude();
                    startlng = gpsHandler.getLongitude();

                    assert gasStation != null;
                    endlat = gasStation.getLat_gasstation();
                    endLng = gasStation.getLng_gasstation();

                    LatLng location = new LatLng(endlat, endLng);
                    gasGoogleMap.addMarker(new MarkerOptions().position(location));
                    gasGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16.0f));
                    gasGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
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
            gasDetailRef.addValueEventListener(eventListener);
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
        gasMapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LoadGasDetail();
        gasMapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        gasMapView.onStop();
        gasDetailRef.removeEventListener(valueEventListener);

    }

    @Override
    protected void onPause() {
        super.onPause();
        gasMapView.onPause();
    }
}
