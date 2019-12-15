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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.firebasequery.FirebaseConstant;
import co.id.roningrum.dolanapptugasakhir.handler.GPSHandler;
import co.id.roningrum.dolanapptugasakhir.model.Tourism;
import co.id.roningrum.dolanapptugasakhir.util.Util;

public class TourismKulinerDetail extends AppCompatActivity implements OnMapReadyCallback {
    public static final String EXTRA_WISATA_KEY = "kuliner_key";
    private static final String MAP_VIEW_KEY = "mapViewBundle";
    private final static String TAG = "Pesan";

    private GoogleMap foodMap;
    private MapView foodMapView;
    private DatabaseReference foodDetailRef;
    private GPSHandler gpsHandler;
    private ValueEventListener valueEventListener;

    private TextView tvNameFoodDetail, tvAddressFoodDetail,
            tvDescFoodDetail, tvDistanceFoodDetail;

    private ImageView imgFoodDetail;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    private double startLat;
    private double startlng;
    private double endlat;
    private double endLng;
    private double distance;

    boolean isFavorite = false;
    Menu menuItem;
    Tourism tourism = new Tourism();

    String foodKey;
    private FirebaseUser user;
    private DatabaseReference favoritedb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_tourism_food);

        tvNameFoodDetail = findViewById(R.id.name_place_tourism_detail);
        tvDescFoodDetail = findViewById(R.id.info_place_tourism_detail);
        tvAddressFoodDetail = findViewById(R.id.location_tourism_detail);
        tvDistanceFoodDetail = findViewById(R.id.distance_location_tourism);
        imgFoodDetail = findViewById(R.id.img_food_place_detail);
        collapsingToolbarLayout = findViewById(R.id.collapseToolbar_food);
        foodMapView = findViewById(R.id.location_tourism_map_detail);

        Toolbar toolbarFood = findViewById(R.id.toolbar_food_detail);
        setSupportActionBar(toolbarFood);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        favoritedb = FirebaseDatabase.getInstance().getReference("Favorite");

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_KEY);
        }
        foodMapView.onCreate(mapViewBundle);
        foodMapView.getMapAsync(this);


        foodKey = getIntent().getStringExtra(EXTRA_WISATA_KEY);
        if (foodKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA");
        }
        Log.d("Check Id", "id Detail : " + foodKey);
        foodDetailRef = FirebaseConstant.getTourismRef(foodKey);
        gpsHandler = new GPSHandler(this);
        LoadFoodDetail();
        favoriteState();
    }

    private void favoriteState() {
        final String uid = user.getUid();
        favoritedb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(uid).child(foodKey).exists()) {
                    isFavorite = true;
                    menuItem.getItem(0).setIcon(R.drawable.ic_bookmarkadded_24dp);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void LoadFoodDetail() {
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
                    distance = Util.calculateDistance(startLat, startlng, endlat, endLng);

                    @SuppressLint("DefaultLocale") String distanceFormat = String.format("%.2f", distance);
                    tvDistanceFoodDetail.setText("" + distanceFormat + " KM");
                    tvNameFoodDetail.setText(tourism.getName_tourism());
                    tvAddressFoodDetail.setText(tourism.getLocation_tourism());
                    tvDescFoodDetail.setText(tourism.getInfo_tourism());
                    Glide.with(getApplicationContext()).load(tourism.getUrl_photo()).into(imgFoodDetail);
                    AppBarLayout appBarLayout = findViewById(R.id.app_bar_food);
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
            foodDetailRef.addValueEventListener(eventListener);
            valueEventListener = eventListener;

        }
    }
//

    @Override
    protected void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_KEY, mapViewBundle);
        }
        foodMapView.onSaveInstanceState(mapViewBundle);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        foodMap = googleMap;
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Tourism tourism = dataSnapshot.getValue(Tourism.class);
//                assert tourism != null;
                assert tourism != null;
                double lattitude = tourism.getLat_location_tourism();
                double longitude = tourism.getLng_location_tourism();

                LatLng location = new LatLng(lattitude, longitude);
                foodMap.addMarker(new MarkerOptions().position(location));
                foodMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15.0f));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        foodDetailRef.addValueEventListener(eventListener);
        valueEventListener = eventListener;

    }

    private void setFavorite() {
        if (isFavorite) {
            menuItem.getItem(0).setIcon(R.drawable.ic_bookmarkadded_24dp);
        } else {
            menuItem.getItem(0).setIcon(R.drawable.ic_unbookmarked_24dp);
        }
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

    private void removeFavorite() {
        final String uid = user.getUid();
        favoritedb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                favoritedb.getRef().child(uid).child(foodKey).removeValue();
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
                favoritedb.getRef().child(uid).child(foodKey).setValue(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        LoadFoodDetail();
        foodMapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        foodMapView.onStop();
        foodDetailRef.removeEventListener(valueEventListener);

    }

    @Override
    protected void onResume() {
        super.onResume();
        foodMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        foodMapView.onPause();
    }


}
