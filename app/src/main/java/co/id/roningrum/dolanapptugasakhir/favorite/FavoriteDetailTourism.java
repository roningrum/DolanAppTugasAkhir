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

package co.id.roningrum.dolanapptugasakhir.favorite;

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
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.firebasequery.FirebaseConstant;
import co.id.roningrum.dolanapptugasakhir.handler.GPSHandler;
import co.id.roningrum.dolanapptugasakhir.model.Tourism;
import co.id.roningrum.dolanapptugasakhir.util.Utils;

public class FavoriteDetailTourism extends AppCompatActivity implements OnMapReadyCallback {
    public static final String EXTRA_TOURISM = "Tourism";

    private static final String MAP_VIEW_KEY = "mapViewBundle";

    private final static String TAG = "Pesan";
    boolean isFavorite = false;
    Menu menuItem;
    Tourism tourism = new Tourism();
    private GoogleMap educationGoogleMap;
    private MapView educationMapView;
    private DatabaseReference educationDetailRef;
    private GPSHandler gpsHandler;
    private ValueEventListener valueEventListener;
    private TextView tvNameTourismDetail, tvAddressTourismDetail,
            tvDescTourism, tvDistanceTourism;
    private ImageView imgTourism;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private double startLat;
    private double startlng;
    private double endlat;
    private double endLng;
    private double distance;
    private String eduKey;
    private FirebaseUser user;
    private DatabaseReference favoritedb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_fav_tourism);

        tvNameTourismDetail = findViewById(R.id.name_place_tourism_detail);
        tvAddressTourismDetail = findViewById(R.id.location_tourism_detail);
        tvDescTourism = findViewById(R.id.info_place_tourism_detail);
        tvDistanceTourism = findViewById(R.id.distance_location_tourism);
        imgTourism = findViewById(R.id.img_nature_education_detail);
        educationMapView = findViewById(R.id.location_tourism_map_detail);
        collapsingToolbarLayout = findViewById(R.id.collapseToolbar);


        Toolbar toolbarEducation = findViewById(R.id.toolbar_education_detail);
        setSupportActionBar(toolbarEducation);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_KEY);
        }
        educationMapView.onCreate(mapViewBundle);
        educationMapView.getMapAsync(this);

        eduKey = getIntent().getStringExtra(EXTRA_TOURISM);
        if (eduKey == null) {
            throw new IllegalArgumentException("Must pass Extra");
        }
        educationDetailRef = FirebaseConstant.getTourismRef(eduKey);
        gpsHandler = new GPSHandler(this);
        favoritedb = FirebaseDatabase.getInstance().getReference("Favorite");

        favoriteState();
        LoadEducationDetail();

    }

    private void favoriteState() {
        final String uid = user.getUid();
        favoritedb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(uid).child(eduKey).exists()) {
                    isFavorite = true;
                    menuItem.getItem(0).setIcon(R.drawable.ic_bookmarkadded_24dp);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void LoadEducationDetail() {
        if (gpsHandler.isCanGetLocation()) {
            ValueEventListener eventListener = new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    tourism = dataSnapshot.getValue(Tourism.class);
                    startLat = gpsHandler.getLatitude();
                    startlng = gpsHandler.getLongitude();
                    assert tourism != null;
                    endlat = tourism.getLat_location_tourism();
                    endLng = tourism.getLng_location_tourism();
                    distance = Utils.calculateDistance(startLat, startlng, endlat, endLng);

                    @SuppressLint("DefaultLocale") String distanceFormat = String.format("%.2f", distance);
                    tvDistanceTourism.setText("" + distanceFormat + " km");
                    tvNameTourismDetail.setText(tourism.getName_tourism());
                    tvAddressTourismDetail.setText(tourism.getLocation_tourism());
                    tvDescTourism.setText(tourism.getInfo_tourism());
                    Glide.with(getApplicationContext()).load(tourism.getUrl_photo()).into(imgTourism);
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
                                collapsingToolbarLayout.setTitle(tourism.getName_tourism());
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
    protected void onSaveInstanceState(@NotNull Bundle outState) {
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
        showTourismMapDetail();

    }

    private void showTourismMapDetail() {
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tourism = dataSnapshot.getValue(Tourism.class);
                assert tourism != null;
                double lattitude = tourism.getLat_location_tourism();
                double longitude = tourism.getLng_location_tourism();

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
        menuItem = menu;
        setFavorite();
        return true;
    }

    private void setFavorite() {
        if (isFavorite) {
            menuItem.getItem(0).setIcon(R.drawable.ic_bookmarkadded_24dp);
        } else {
            menuItem.getItem(0).setIcon(R.drawable.ic_unbookmarked_24dp);
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Respond to the action bar's Up/Home button
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.add_to_favorite) {

            if (isFavorite) {
                removeFavorite();
            } else {
                addToFavorite();
            }
            isFavorite = !isFavorite;
            setFavorite();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }

    }

    private void removeFavorite() {
        final String uid = user.getUid();
        favoritedb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                favoritedb.getRef().child(uid).child(eduKey).removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void addToFavorite() {
        final String uid = user.getUid();
        favoritedb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                favoritedb.getRef().child(uid).child(eduKey).setValue(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
