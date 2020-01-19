/*
 * Copyright 2020 RONINGRUM. All rights reserved.
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

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.firebasequery.FirebaseConstant;
import co.id.roningrum.dolanapptugasakhir.handler.GPSHandler;
import co.id.roningrum.dolanapptugasakhir.model.Transportation;
import co.id.roningrum.dolanapptugasakhir.util.Utils;

public class TransportationDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String EXTRA_TRANSPORT_KEY = "trasnsportKey";

    private static final String MAP_VIEW_KEY = "mapViewBundle";

    private final static String TAG = "Pesan";

    private GoogleMap transportGoogleMap;
    private MapView transportMapView;

    private DatabaseReference transportDetailRef;

    private GPSHandler gpsHandler;

    private TextView tvNameTransDetail, tvAddressTransDetail, tvDistanceTrans;

    private ImageView imgTransportDetail;
    private CollapsingToolbarLayout collapsingToolbarTransport;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_transportation);

        tvNameTransDetail = findViewById(R.id.name_place_transport_detail);
        tvAddressTransDetail = findViewById(R.id.location_transport_detail);
        tvDistanceTrans = findViewById(R.id.distance_location_trans);
        imgTransportDetail = findViewById(R.id.img_transport_detail);
        collapsingToolbarTransport = findViewById(R.id.collapseToolbar_transport);
        transportMapView = findViewById(R.id.location_trans_map_detail);

        Toolbar toolbarTransportation = findViewById(R.id.toolbar_transport_detail);
        setSupportActionBar(toolbarTransportation);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_KEY);
        }

        transportMapView.onCreate(mapViewBundle);
        transportMapView.getMapAsync(this);

        String transportKey = getIntent().getStringExtra(EXTRA_TRANSPORT_KEY);
        if (transportKey == null) {
            throw new IllegalArgumentException("Must pass Extra");
        }

        transportDetailRef = FirebaseConstant.getTransportByKey(transportKey);
        gpsHandler = new GPSHandler(this);

        LoadTransportDetail();
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void LoadTransportDetail() {
        if (gpsHandler.isCanGetLocation()) {
            transportDetailRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final Transportation transportation = dataSnapshot.getValue(Transportation.class);
                    double startLat = gpsHandler.getLatitude();
                    double startlng = gpsHandler.getLongitude();
                    assert transportation != null;
                    double endlat = transportation.getLat_location_transport();
                    double endLng = transportation.getLng_location_transport();
                    double distance = Utils.calculateDistance(startLat, startlng, endlat, endLng);

                    String distanceFormat = String.format("%.2f", distance);
                    tvDistanceTrans.setText("" + distanceFormat + " km");
                    tvNameTransDetail.setText(transportation.getName_transport());
                    tvAddressTransDetail.setText(transportation.getLocation_transport());
                    Glide.with(getApplicationContext()).load(transportation.getUrl_photo_transport()).into(imgTransportDetail);
                    AppBarLayout appBarLayout = findViewById(R.id.app_bar_transport);
                    appBarLayout.addOnOffsetChangedListener(new AppBarLayout.BaseOnOffsetChangedListener() {
                        boolean isShow = true;
                        int scrollRange = -1;

                        @Override
                        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                            if (scrollRange == -1) {
                                scrollRange = appBarLayout.getTotalScrollRange();
                            }
                            if (scrollRange + verticalOffset == 0) {
                                collapsingToolbarTransport.setTitle(transportation.getName_transport());
                                isShow = true;
                            } else {
                                collapsingToolbarTransport.setTitle(" ");
                                isShow = false;
                            }

                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    protected void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_KEY, mapViewBundle);
            transportMapView.onSaveInstanceState(mapViewBundle);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        transportGoogleMap = googleMap;
        transportDetailRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final Transportation transportation = dataSnapshot.getValue(Transportation.class);
                assert transportation != null;
                final double endlat = transportation.getLat_location_transport();
                final double endLng = transportation.getLng_location_transport();

                LatLng location = new LatLng(endlat, endLng);
                transportGoogleMap.addMarker(new MarkerOptions().position(location));
                transportGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15.0f));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Firebase Database Error" + databaseError.getMessage());
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
        transportMapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LoadTransportDetail();
        transportMapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        transportMapView.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        transportMapView.onPause();
    }
}
