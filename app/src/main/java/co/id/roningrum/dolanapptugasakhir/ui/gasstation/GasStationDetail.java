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
import android.view.Menu;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.firebasequery.FirebaseConstant;
import co.id.roningrum.dolanapptugasakhir.handler.GPSHandler;
import co.id.roningrum.dolanapptugasakhir.model.GasStation;
import co.id.roningrum.dolanapptugasakhir.util.Utils;

import static co.id.roningrum.dolanapptugasakhir.firebasequery.FirebaseConstant.favoriteRef;

public class GasStationDetail extends AppCompatActivity implements OnMapReadyCallback {

    public static final String EXTRA_GAS_KEY = "gasKey";

    private static final String MAP_VIEW_KEY = "mapViewBundle";

    private final static String TAG = "Pesan";

    private MapView gasMapView;

    private DatabaseReference gasDetailRef;

    private GPSHandler gpsHandler;
    private ValueEventListener valueEventListener;

    private TextView tvNameGasDetail, tvAddressGasDetail, tvDistanceGasDetail, tvDetailGas;

    private ImageView imgGasDetail;
    private CollapsingToolbarLayout collapsingToolbarGas;

    private double startLat;
    private double startlng;
    private double endlat;
    private double endLng;
    private double distance;

    boolean isFavorite = false;
    Menu menuItem;
    private String gasKey;
    private FirebaseUser user;

    private GasStation gasStation;
    private ImageButton btnCall, btnRouteToMap;

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
        btnCall = findViewById(R.id.btn_call);
        btnRouteToMap = findViewById(R.id.btn_route_map);
        tvDetailGas = findViewById(R.id.info_place_detail);

        Toolbar toolbarSPBU = findViewById(R.id.toolbar_spbu_detail);
        setSupportActionBar(toolbarSPBU);
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_KEY);
        }
        gasMapView.onCreate(mapViewBundle);
        gasMapView.getMapAsync(this);

        gasKey = getIntent().getStringExtra(EXTRA_GAS_KEY);
        if (gasKey == null) {
            throw new IllegalArgumentException("Must pass Extra");
        }
        gasDetailRef = FirebaseConstant.getGasByKey(gasKey);
        gpsHandler = new GPSHandler(this);

        gasStation = new GasStation();
        user = FirebaseAuth.getInstance().getCurrentUser();

        LoadGasDetail();
        favoriteState();
    }

    private void LoadGasDetail() {
        if (gpsHandler.isCanGetLocation()) {
            ValueEventListener eventListener = new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    gasStation = dataSnapshot.getValue(GasStation.class);
                    startLat = gpsHandler.getLatitude();
                    startlng = gpsHandler.getLongitude();
                    assert gasStation != null;
                    endlat = gasStation.getLat_location_gasstation();
                    endLng = gasStation.getLng_location_gasstation();
                    distance = Utils.calculateDistance(startLat, startlng, endlat, endLng);


                    @SuppressLint("DefaultLocale") String distanceFormat = String.format("%.2f", distance);
                    tvDistanceGasDetail.setText("" + distanceFormat + " km");
                    tvNameGasDetail.setText(gasStation.getName_gasstation());
                    tvAddressGasDetail.setText(gasStation.getLocation_gasstation());
                    Glide.with(getApplicationContext()).load(gasStation.getUrl_photo_gasstation()).into(imgGasDetail);
                    tvDetailGas.setText("Tidak Tersedia untuk saat ini");

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
                                collapsingToolbarGas.setCollapsedTitleTextColor(getResources().getColor(android.R.color.white));
                                collapsingToolbarGas.setContentScrim(getResources().getDrawable(R.drawable.bg_image_blur));
                                tvDistanceGasDetail.setVisibility(View.INVISIBLE);
                                findViewById(R.id.location_icon_pic).setVisibility(View.INVISIBLE);
                                isShow = true;
                            } else {
                                collapsingToolbarGas.setTitle(" ");
                                tvDistanceGasDetail.setVisibility(View.VISIBLE);
                                findViewById(R.id.location_icon_pic).setVisibility(View.VISIBLE);
                                isShow = false;
                            }

                        }
                    });
                    btnCall.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", gasStation.getTelepon(), null));
                            if (!gasStation.getTelepon().equals("Tidak tersedia")) {
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
                                    Uri.parse("http://maps.google.com/maps?saddr=" + startLat + "," + startlng + "&daddr=" + endlat + "," + endLng));
                            startActivity(intent);
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
                    gasStation = dataSnapshot.getValue(GasStation.class);
                    startLat = gpsHandler.getLatitude();
                    startlng = gpsHandler.getLongitude();

                    assert gasStation != null;
                    endlat = gasStation.getLat_location_gasstation();
                    endLng = gasStation.getLng_location_gasstation();

                    LatLng location = new LatLng(endlat, endLng);
                    gasGoogleMap.addMarker(new MarkerOptions().position(location).title(gasStation.getName_gasstation()));
                    gasGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16.0f));
                    gasGoogleMap.getUiSettings().setMapToolbarEnabled(false);
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

    private void favoriteState() {
        final String uid = user.getUid();
        favoriteRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(uid).child("GasStation").child(gasKey).exists()) {
                    isFavorite = true;
                    menuItem.getItem(0).setIcon(R.drawable.ic_bookmarkadded_24dp);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.favorite_menu, menu);
        menuItem = menu;
        setFavoriteHotel();
        return true;
    }

    private void setFavoriteHotel() {
        if (isFavorite) {
            menuItem.getItem(0).setIcon(R.drawable.ic_bookmarkadded_24dp);
        } else {
            menuItem.getItem(0).setIcon(R.drawable.ic_unbookmarked_24dp);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Respond to the action bar's Up/Home button
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.add_to_favorite) {
            if (isFavorite) {
                removeFavorite();
            } else {
                addToFavorite();
            }
            isFavorite = !isFavorite;
            setFavoriteHotel();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }

    }

    private void addToFavorite() {
        final String uid = user.getUid();
        favoriteRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                favoriteRef.getRef().child(uid).child("GasStation").child(gasKey).setValue(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void removeFavorite() {
        final String uid = user.getUid();
        favoriteRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                favoriteRef.getRef().child(uid).child("GasStation").child(gasKey).removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
