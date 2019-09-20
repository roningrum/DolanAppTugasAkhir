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
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.handler.GPSHandler;
import co.id.roningrum.dolanapptugasakhir.handler.HaversineHandler;
import co.id.roningrum.dolanapptugasakhir.model.TourismItem;

public class DetailEducatioActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static final String EXTRA_WISATA_KEY = "edukasi_key";
    private static final String MAP_VIEW_KEY = "mapViewBundle";

    private final static String TAG = "Pesan";
    private GoogleMap educationGoogleMap;
    private MapView educationMapView;

    private DatabaseReference educationDetailRef;

    private GPSHandler gpsHandler;
    private ValueEventListener valueEventListener;

    private TextView tvNameEducationDetail, tvAddressEducationDetail,
            tvDescEducation, tvDistanceEducation;

    private ImageView imgEducation;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    private double startLat;
    private double startlng;
    private double endlat;
    private double endLng;
    private double distance;

    private boolean isFavorite = false;
    private Menu menuItem = null;
    String eduKey;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_education_category);
        tvNameEducationDetail = findViewById(R.id.name_place_education_detail);
        tvAddressEducationDetail = findViewById(R.id.address_place_education_detail);
        tvDescEducation = findViewById(R.id.info_place_education_detail);
        tvDistanceEducation = findViewById(R.id.distance_place_education_detail);
        imgEducation = findViewById(R.id.img_nature_education_detail);
        educationMapView = findViewById(R.id.loc_edu_map);
        collapsingToolbarLayout = findViewById(R.id.collapseToolbar);

        Toolbar toolbarEducation = findViewById(R.id.toolbar_education_detail);
        setSupportActionBar(toolbarEducation);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();


        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_KEY);
        }
        educationMapView.onCreate(mapViewBundle);
        educationMapView.getMapAsync(this);

        eduKey = getIntent().getStringExtra(EXTRA_WISATA_KEY);
        if (eduKey == null) {
            throw new IllegalArgumentException("Must pass Extra");
        }
        educationDetailRef = FirebaseDatabase.getInstance().getReference().child("Tourism").child(eduKey);
        Query eduQuery = educationDetailRef.orderByChild("category_tourism").equalTo("edukasi");
        gpsHandler = new GPSHandler(this);


        LoadEducationDetail();

    }

    private void LoadEducationDetail() {
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
                    tvDistanceEducation.setText("" + distanceFormat + " km");
                    tvNameEducationDetail.setText(tourismItem.getName_tourism());
                    tvAddressEducationDetail.setText(tourismItem.getLocation_tourism());
                    tvDescEducation.setText(tourismItem.getInfo_tourism());
                    Glide.with(getApplicationContext()).load(tourismItem.getUrl_photo()).into(imgEducation);
                    AppBarLayout appBarLayout = findViewById(R.id.app_bar);
                    appBarLayout.addOnOffsetChangedListener(new AppBarLayout.BaseOnOffsetChangedListener() {
                        boolean isShow = true;
                        int scrollRange = -1;

                        @Override
                        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                            if (scrollRange == -1) {
                                scrollRange = appBarLayout.getTotalScrollRange();
                            }
                            if (scrollRange + verticalOffset == 0) {
                                collapsingToolbarLayout.setTitle(tourismItem.getName_tourism());
                                isShow = true;
                            } else {
                                collapsingToolbarLayout.setTitle(" ");
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
            educationDetailRef.addValueEventListener(eventListener);
            valueEventListener = eventListener;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_KEY, mapViewBundle);
        }
        educationMapView.onSaveInstanceState(mapViewBundle);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        educationGoogleMap = googleMap;
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TourismItem tourismItem = dataSnapshot.getValue(TourismItem.class);
                assert tourismItem != null;
                double lattitude = tourismItem.getLat_location_tourism();
                double longitude = tourismItem.getLng_location_tourism();

                LatLng location = new LatLng(lattitude, longitude);
                educationGoogleMap.addMarker(new MarkerOptions().position(location));
                educationGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16.0f));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Firebase Database Error" + databaseError.getMessage());
            }
        };
        educationDetailRef.addValueEventListener(eventListener);
        valueEventListener = eventListener;

    }


    @Override
    protected void onResume() {
        super.onResume();
        educationMapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LoadEducationDetail();
        educationMapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        educationMapView.onStop();
        educationDetailRef.removeEventListener(valueEventListener);

    }

    @Override
    protected void onPause() {
        super.onPause();
        educationMapView.onPause();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.favorite_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Respond to the action bar's Up/Home button
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.add_to_favorite) {
            if (isFavorite)
                removeFromFavorite();
            else
                addToFavorite();
            isFavorite = !isFavorite;
            setFavorite();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setFavorite() {
        if (isFavorite)
            menuItem.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_bookmarkadded_24dp));
        else {
            menuItem.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_unbookmarked_24dp));
        }

    }

    private void removeFromFavorite() {
        final String name = user.getDisplayName();
        final DatabaseReference favoritedb = FirebaseDatabase.getInstance().getReference("Favorite");
        educationDetailRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TourismItem tourismItem = dataSnapshot.getValue(TourismItem.class);
                assert tourismItem != null;
                favoritedb.getRef().child(name).child(eduKey).child("name_tourism").removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addToFavorite() {
        final String name = user.getDisplayName();
        final DatabaseReference favoritedb = FirebaseDatabase.getInstance().getReference("Favorite");
        educationDetailRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TourismItem tourismItem = dataSnapshot.getValue(TourismItem.class);
                assert tourismItem != null;
                favoritedb.getRef().child(name).child(eduKey).child("name_tourism").setValue(tourismItem.getName_tourism());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}