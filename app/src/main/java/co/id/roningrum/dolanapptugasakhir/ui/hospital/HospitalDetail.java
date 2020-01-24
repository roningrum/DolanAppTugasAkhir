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

package co.id.roningrum.dolanapptugasakhir.ui.hospital;

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

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.firebasequery.FirebaseConstant;
import co.id.roningrum.dolanapptugasakhir.handler.GPSHandler;
import co.id.roningrum.dolanapptugasakhir.model.Hospital;
import co.id.roningrum.dolanapptugasakhir.util.Utils;

import static co.id.roningrum.dolanapptugasakhir.firebasequery.FirebaseConstant.favoriteRef;

public class HospitalDetail extends AppCompatActivity implements OnMapReadyCallback {
    public static final String EXTRA_HOSPITAL_KEY = "hospitalKey";

    private static final String MAP_VIEW_KEY = "mapViewBundle";

    private final static String TAG = "Pesan";

    private MapView hospitalMapView;

    private DatabaseReference hospitalDetailRef;

    private GPSHandler gpsHandler;
    private ValueEventListener valueEventListener;

    private TextView tvNameHospitalDetail,
            tvAddressHospitalDetail, tvDistanceHospitalDetail, tvDetailHospital;

    private ImageView imgHospitalDetail;
    private CollapsingToolbarLayout collapsingToolbarHospital;

    private double startLat;
    private double startlng;
    private double endlat;
    private double endLng;
    private double distance;


    boolean isFavorite = false;
    Menu menuItem;
    String hospitalKey;
    private FirebaseUser user;

    private ImageButton btnCall, btnRouteToMap;
    private Hospital hospital;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_hospital);
        tvNameHospitalDetail = findViewById(R.id.name_place_hospital_detail);
        tvAddressHospitalDetail = findViewById(R.id.location_hospital_detail);
        tvDistanceHospitalDetail = findViewById(R.id.distance_location_hospital);
        collapsingToolbarHospital = findViewById(R.id.collapseToolbar_hospital);
        hospitalMapView = findViewById(R.id.location_hospital_map_detail);
        imgHospitalDetail = findViewById(R.id.img_hospital_detail);
        btnCall = findViewById(R.id.btn_call);
        btnRouteToMap = findViewById(R.id.btn_route_map);
        tvDetailHospital = findViewById(R.id.info_place_detail);

        Toolbar toolbarHospital = findViewById(R.id.toolbar_hospital_detail);
        setSupportActionBar(toolbarHospital);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_KEY);
        }
        hospitalMapView.onCreate(mapViewBundle);
        hospitalMapView.getMapAsync(this);

        hospitalKey = getIntent().getStringExtra(EXTRA_HOSPITAL_KEY);
        if (hospitalKey == null) {
            throw new IllegalArgumentException("Must pass Extra");
        }
        hospitalDetailRef = FirebaseConstant.getHospitalByKey(hospitalKey);
        gpsHandler = new GPSHandler(this);
        hospital = new Hospital();
        user = FirebaseAuth.getInstance().getCurrentUser();

        LoadHospitaDetail();
        favoriteState();
    }

    private void LoadHospitaDetail() {
        if (gpsHandler.isCanGetLocation()) {
            ValueEventListener eventListener = new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    hospital = dataSnapshot.getValue(Hospital.class);
                    startLat = gpsHandler.getLatitude();
                    startlng = gpsHandler.getLongitude();
                    assert hospital != null;
                    endlat = hospital.getLat_location_hospital();
                    endLng = hospital.getLng_location_hospital();
                    distance = Utils.calculateDistance(startLat, startlng, endlat, endLng);


                    @SuppressLint("DefaultLocale") String distanceFormat = String.format("%.2f", distance);
                    tvDistanceHospitalDetail.setText("" + distanceFormat + " km");
                    tvNameHospitalDetail.setText(hospital.getName_hospital());
                    tvAddressHospitalDetail.setText(hospital.getLocation_hospital());
                    Glide.with(getApplicationContext()).load(hospital.getUrl_photo_hospital()).into(imgHospitalDetail);
                    tvDetailHospital.setText("Tidak Tersedia untuk saat ini");
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
                                collapsingToolbarHospital.setTitle(hospital.getName_hospital());
                                collapsingToolbarHospital.setCollapsedTitleTextColor(getResources().getColor(android.R.color.white));
                                collapsingToolbarHospital.setContentScrim(getResources().getDrawable(R.drawable.bg_image_blur));
                                tvDistanceHospitalDetail.setVisibility(View.INVISIBLE);
                                tvNameHospitalDetail.setVisibility(View.INVISIBLE);
                                findViewById(R.id.location_icon_pic).setVisibility(View.INVISIBLE);
                                isShow = true;
                            } else {
                                collapsingToolbarHospital.setTitle(" ");
                                tvDistanceHospitalDetail.setVisibility(View.VISIBLE);
                                tvNameHospitalDetail.setVisibility(View.VISIBLE);
                                findViewById(R.id.location_icon_pic).setVisibility(View.VISIBLE);
                                isShow = false;
                            }

                        }
                    });

                    btnCall.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", hospital.getTelepon(), null));
                            if (!hospital.getTelepon().equals("Tidak tersedia")) {
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
            hospitalDetailRef.addValueEventListener(eventListener);
            valueEventListener = eventListener;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        showMapDetail(googleMap);
    }

    private void showMapDetail(final GoogleMap hospitalGoogleMap) {
        if (gpsHandler.isCanGetLocation()) {
            ValueEventListener eventListener = new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    hospital = dataSnapshot.getValue(Hospital.class);
                    startLat = gpsHandler.getLatitude();
                    startlng = gpsHandler.getLongitude();

                    assert hospital != null;
                    endlat = hospital.getLat_location_hospital();
                    endLng = hospital.getLng_location_hospital();

                    LatLng location = new LatLng(endlat, endLng);
                    hospitalGoogleMap.addMarker(new MarkerOptions().position(location).title(hospital.getName_hospital()));
                    hospitalGoogleMap.getUiSettings().setMapToolbarEnabled(false);
                    hospitalGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16.0f));
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

    private void favoriteState() {
        final String uid = user.getUid();
        favoriteRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(uid).child("Hospital").child(hospitalKey).exists()) {
                    isFavorite = true;
                    menuItem.getItem(0).setIcon(R.drawable.ic_bookmarkadded_24dp);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void addToFavorite() {
        final String uid = user.getUid();
        favoriteRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                favoriteRef.getRef().child(uid).child("Hospital").child(hospitalKey).setValue(true);
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
                favoriteRef.getRef().child(uid).child("Hospital").child(hospitalKey).removeValue();
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
