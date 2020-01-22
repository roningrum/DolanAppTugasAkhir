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
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

    private TextView tvNameTransDetail, tvAddressTransDetail, tvDistanceTrans, tvDetailTransport;

    private ImageView imgTransportDetail;
    private CollapsingToolbarLayout collapsingToolbarTransport;
    private Transportation transportation;
    private ImageButton btnCall, btnRouteToMap;
    private double startLat;
    private double startLng;
    private double endLat;
    private double endLng;


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
        btnCall = findViewById(R.id.btn_call);
        btnRouteToMap = findViewById(R.id.btn_route_map);
        tvDetailTransport = findViewById(R.id.info_place_detail);

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
        transportation = new Transportation();

        LoadTransportDetail();
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void LoadTransportDetail() {
        if (gpsHandler.isCanGetLocation()) {
            transportDetailRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    transportation = dataSnapshot.getValue(Transportation.class);
                    startLat = gpsHandler.getLatitude();
                    startLng = gpsHandler.getLongitude();
                    assert transportation != null;
                    endLat = transportation.getLat_location_transport();
                    endLng = transportation.getLng_location_transport();
                    double distance = Utils.calculateDistance(startLat, startLng, endLat, endLng);

                    String distanceFormat = String.format("%.2f", distance);
                    tvDistanceTrans.setText("" + distanceFormat + " km");
                    tvNameTransDetail.setText(transportation.getName_transport());
                    tvAddressTransDetail.setText(transportation.getLocation_transport());
                    tvDetailTransport.setText("Tidak tersedia untuk saat ini");
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
                                collapsingToolbarTransport.setCollapsedTitleTextColor(getResources().getColor(android.R.color.white));
                                collapsingToolbarTransport.setContentScrim(getResources().getDrawable(R.drawable.bg_image_blur));
                                tvDistanceTrans.setVisibility(View.INVISIBLE);
                                tvNameTransDetail.setVisibility(View.INVISIBLE);
                                findViewById(R.id.location_icon_trans_pic).setVisibility(View.INVISIBLE);
                                isShow = true;
                            } else {
                                collapsingToolbarTransport.setTitle(" ");
                                tvDistanceTrans.setVisibility(View.VISIBLE);
                                tvNameTransDetail.setVisibility(View.VISIBLE);
                                findViewById(R.id.location_icon_trans_pic).setVisibility(View.VISIBLE);
                                isShow = false;
                            }

                        }
                    });

                    btnCall.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", transportation.getTelepon(), null));
                            if (!transportation.getTelepon().equals("Tidak tersedia")) {
                                startActivity(callIntent);
                            } else {
                                Toast.makeText(getApplicationContext(), "Mohon Maaf belum tersedia untuk saat ini", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                    btnRouteToMap.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                    Uri.parse("http://maps.google.com/maps?saddr=" + startLat + "," + startLng + "&daddr=" + endLat + "," + endLng));
                            startActivity(intent);
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
                endLat = transportation.getLat_location_transport();
                endLng = transportation.getLng_location_transport();

                LatLng location = new LatLng(endLat, endLng);
                transportGoogleMap.addMarker(new MarkerOptions().position(location).title(transportation.getName_transport()));
                transportGoogleMap.getUiSettings().setMapToolbarEnabled(false);
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
