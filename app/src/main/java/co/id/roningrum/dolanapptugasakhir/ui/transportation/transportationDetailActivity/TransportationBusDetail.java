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

public class TransportationBusDetail extends AppCompatActivity implements OnMapReadyCallback {
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
        setContentView(R.layout.activity_detail_transportation_bus);
        tvNameBusDetail = findViewById(R.id.name_place_transport_detail);
        tvAddressBusDetail = findViewById(R.id.location_transport_detail);
        tvDistanceAirport = findViewById(R.id.distance_location_trans);
        collapsingToolbarBus = findViewById(R.id.collapseToolbar_bus);
        busMapView = findViewById(R.id.location_trans_map_detail);
        imgBusDetail = findViewById(R.id.img_bus_detail);

        Toolbar toolbarBus = findViewById(R.id.toolbar_bus_detail);
        setSupportActionBar(toolbarBus);

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
        busDetailRef = FirebaseConstant.getTransportByKey(busKey);
        gpsHandler = new GPSHandler(this);

        LoadBusDetail();


    }

    private void LoadBusDetail() {
        if (gpsHandler.isCanGetLocation()) {
            ValueEventListener eventListener = new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final Transportation transportation = dataSnapshot.getValue(Transportation.class);
                    startLat = gpsHandler.getLatitude();
                    startlng = gpsHandler.getLongitude();
                    assert transportation != null;
                    endlat = transportation.getLat_transportation();
                    endLng = transportation.getLng_transportation();
                    distance = Utils.calculateDistance(startLat, startlng, endlat, endLng);

//                    Log.i("Haversine", "The Result : " +calculateDistance(gpsHandler.getLatitude(),gpsHandler.getLongitude(),endlat,endLng));

                    @SuppressLint("DefaultLocale") String distanceFormat = String.format("%.2f", distance);
                    tvDistanceAirport.setText("" + distanceFormat + " km");
                    tvNameBusDetail.setText(transportation.getName_transportation());
                    tvAddressBusDetail.setText(transportation.getLocation_transportation());
                    Glide.with(getApplicationContext()).load(transportation.getUrl_photo_transport()).into(imgBusDetail);
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
                                collapsingToolbarBus.setTitle(transportation.getName_transportation());
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

    @Override
    protected void onSaveInstanceState(@NotNull Bundle outState) {
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
                    Transportation transportation = dataSnapshot.getValue(Transportation.class);
                    assert transportation != null;
                    endlat = transportation.getLat_transportation();
                    endLng = transportation.getLng_transportation();

                    LatLng location = new LatLng(endlat, endLng);
                    busGoogleMap.addMarker(new MarkerOptions().position(location));
                    busGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16.0f));
//                    busGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//                        @Override
//                        public void onMapClick(LatLng latLng) {
//                            String uri = "http://maps.google.com/maps?saddr=" + startLat + "," + startlng + "&daddr=" + endlat + "," + endLng + "&mode=driving";
//                            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
//                            startActivity(Intent.createChooser(intent, "Select an application"));
//                        }
//                    });
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
