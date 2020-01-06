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

package co.id.roningrum.dolanapptugasakhir.ui.tourism.tourismDetailActivity;

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
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.firebasequery.FirebaseConstant;
import co.id.roningrum.dolanapptugasakhir.handler.GPSHandler;
import co.id.roningrum.dolanapptugasakhir.model.Tourism;
import co.id.roningrum.dolanapptugasakhir.util.Utils;

import static co.id.roningrum.dolanapptugasakhir.firebasequery.FirebaseConstant.favoriteRef;

public class TourismAirDetail extends AppCompatActivity implements OnMapReadyCallback {
    public static final String EXTRA_WISATA_KEY = "air_key";

    private static final String MAP_VIEW_KEY = "mapViewBundle";

    private final static String TAG = "Pesan";
    private GoogleMap waterLocationMap;
    private MapView waterMapView;
    private DatabaseReference waterDetailRef;

    private GPSHandler gpsHandler;
    private ValueEventListener valueEventListener;

    private TextView tvNameWaterDetail, tvAddressWaterDetail, tvDescWaterDetail,
    tvDistanceWaterDetail;

    private ImageView imgWaterObject;
    private CollapsingToolbarLayout collapsingToolbarLayout_water;

    private double startLat;
    private double startLng;
    private double endLat;
    private double endLng;
    private double distance;

    boolean isFavorite = false;
    Menu menuItem;
    Tourism tourism = new Tourism();
    private String waterKey;
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_tourism_water);
        tvNameWaterDetail = findViewById(R.id.name_place_tourism_detail);
        tvAddressWaterDetail = findViewById(R.id.location_tourism_detail);
        tvDescWaterDetail = findViewById(R.id.info_place_tourism_detail);
        tvDistanceWaterDetail = findViewById(R.id.distance_location_tourism);
        imgWaterObject = findViewById(R.id.img_water_place_detail);
        waterMapView = findViewById(R.id.location_tourism_map_detail);
        collapsingToolbarLayout_water = findViewById(R.id.collapseToolbar_water_detail);


        Toolbar toolbarWater = findViewById(R.id.toolbar_water_detail);
        setSupportActionBar(toolbarWater);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_KEY);
        }
        waterMapView.onCreate(mapViewBundle);
        waterMapView.getMapAsync(this);

        waterKey = getIntent().getStringExtra(EXTRA_WISATA_KEY);

        assert waterKey != null;
        waterDetailRef = FirebaseConstant.getTourismRef(waterKey);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        Log.d("Check id", " idDetail : " + waterKey);
        gpsHandler = new GPSHandler( this);

        favoriteState();
        LoadDetail();
    }

    private void LoadDetail() {
        if(gpsHandler.isCanGetLocation()){
            ValueEventListener eventListener = new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    tourism = dataSnapshot.getValue(Tourism.class);
                    startLat = gpsHandler.getLatitude();
                    startLng = gpsHandler.getLongitude();
                    assert tourism != null;
                    endLat = tourism.getLat_location_tourism();
                    endLng = tourism.getLng_location_tourism();
                    distance = Utils.calculateDistance(startLat, startLng, endLat, endLng);

                    @SuppressLint("DefaultLocale") String distanceFormat = String.format("%.2f",distance);
                    tvDistanceWaterDetail.setText(""+distanceFormat+" KM");
                    tvNameWaterDetail.setText(tourism.getName_tourism());
                    tvAddressWaterDetail.setText(tourism.getLocation_tourism());
                    tvDescWaterDetail.setText(tourism.getInfo_tourism());
                    Glide.with(getApplicationContext()).load(tourism.getUrl_photo()).into(imgWaterObject);
                    AppBarLayout appBarLayout = findViewById(R.id.app_bar_water);
                    appBarLayout.addOnOffsetChangedListener(new AppBarLayout.BaseOnOffsetChangedListener() {
                        boolean isShow = true;
                        int scrollRange = -1;

                        @Override
                        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                            if (scrollRange == -1) {
                                scrollRange = appBarLayout.getTotalScrollRange();
                            }
                            if (scrollRange + verticalOffset == 0) {
                                collapsingToolbarLayout_water.setTitle(tourism.getName_tourism());
                                isShow = true;
                            } else {
                                collapsingToolbarLayout_water.setTitle(" ");
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
            waterDetailRef.addValueEventListener(eventListener);
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
        waterMapView.onSaveInstanceState(mapViewBundle);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        waterLocationMap = googleMap;

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tourism = dataSnapshot.getValue(Tourism.class);
                assert tourism != null;
                double lattitude = tourism.getLat_location_tourism();
                double longitude = tourism.getLng_location_tourism();

                LatLng location = new LatLng(lattitude, longitude);
                waterLocationMap.addMarker(new MarkerOptions().position(location));
                waterLocationMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,16.0f));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        waterDetailRef.addValueEventListener(eventListener);
        valueEventListener = eventListener;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.favorite_menu, menu);
        menuItem = menu;
        setFavorite();
        return true;
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

    private void addToFavorite() {
        final String uid = user.getUid();
        favoriteRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                favoriteRef.getRef().child(uid).child(waterKey).setValue(true);
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
                favoriteRef.getRef().child(uid).child(waterKey).removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setFavorite() {
        if (isFavorite) {
            menuItem.getItem(0).setIcon(R.drawable.ic_bookmarkadded_24dp);
        } else {
            menuItem.getItem(0).setIcon(R.drawable.ic_unbookmarked_24dp);
        }
    }

    private void favoriteState() {
        final String uid = user.getUid();
        favoriteRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(uid).child(waterKey).exists()) {
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
    protected void onResume() {
        super.onResume();
        waterMapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LoadDetail();
        waterMapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        waterMapView.onStop();
        waterDetailRef.removeEventListener(valueEventListener);

    }

    @Override
    protected void onPause() {
        super.onPause();
        waterMapView.onPause();
    }
}
