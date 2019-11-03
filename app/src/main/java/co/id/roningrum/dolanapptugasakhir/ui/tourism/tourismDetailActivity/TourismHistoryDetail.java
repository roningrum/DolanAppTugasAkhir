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
import co.id.roningrum.dolanapptugasakhir.controller.FirebaseConstant;
import co.id.roningrum.dolanapptugasakhir.handler.GPSHandler;
import co.id.roningrum.dolanapptugasakhir.model.Tourism;
import co.id.roningrum.dolanapptugasakhir.util.HaversineHandler;

public class TourismHistoryDetail extends AppCompatActivity implements OnMapReadyCallback {
    public static final String EXTRA_WISATA_KEY = "history_key";
    private static final String MAP_VIEW_KEY = "maViewBundle";
    private final static String TAG = "Pesan";


    private GoogleMap historyMap;
    private MapView historyMapView;

    private DatabaseReference historyDetailRef;
    private GPSHandler gpsHandler;
    private ValueEventListener valueEventListener;

    private TextView tvNameHistoryDetail, tvAddressHistoryDetail,
            tvDescHistoryDetail, tvDistanceHistoryDetail;

    private ImageView imgHistory;
    private CollapsingToolbarLayout collapsingToolbarLayout_history;

    private double startLat;
    private double startlng;
    private double endlat;
    private double endLng;
    private double distance;

    String historyKey;
    boolean isFavorite;
    private FirebaseUser user;
    private DatabaseReference favoritedb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_history);


        tvNameHistoryDetail = findViewById(R.id.name_place_history_detail);
        tvAddressHistoryDetail = findViewById(R.id.address_place_history_detail);
        tvDescHistoryDetail = findViewById(R.id.info_place_histroy_detail);
        tvDistanceHistoryDetail = findViewById(R.id.distance_place_history_detail);
        imgHistory = findViewById(R.id.img_history_place_detail);
        historyMapView = findViewById(R.id.loc_history_map_detail);
        collapsingToolbarLayout_history = findViewById(R.id.collapseToolbar_history);


        Toolbar toolbarHistory = findViewById(R.id.toolbar_history_detail);
        setSupportActionBar(toolbarHistory);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_KEY);
        }
        historyMapView.onCreate(mapViewBundle);
        historyMapView.getMapAsync(this);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        favoritedb = FirebaseDatabase.getInstance().getReference("Favorite");

        historyKey = getIntent().getStringExtra(EXTRA_WISATA_KEY);
        if (historyKey == null) {
            throw new IllegalArgumentException("Must pass Extra");
        }
        historyDetailRef = FirebaseConstant.getTourismRef(historyKey);
//        Query historyQuery = historyDetailRef.orderByChild("category_tourism").equalTo("sejarah");
        gpsHandler = new GPSHandler(this);

        LoadHistoryDetail();
    }

    private void LoadHistoryDetail() {
        if (gpsHandler.isCanGetLocation()) {
            ValueEventListener eventListener = new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final Tourism tourism = dataSnapshot.getValue(Tourism.class);
                    startLat = gpsHandler.getLatitude();
                    startlng = gpsHandler.getLongitude();
                    assert tourism != null;
                    endlat = tourism.getLat_location_tourism();
                    endLng = tourism.getLng_location_tourism();
                    distance = HaversineHandler.calculateDistance(startLat, startlng, endlat, endLng);

                    @SuppressLint("DefaultLocale") String distanceFormat = String.format("%.2f", distance);
                    tvDistanceHistoryDetail.setText("" + distanceFormat + " KM");
                    tvNameHistoryDetail.setText(tourism.getName_tourism());
                    tvAddressHistoryDetail.setText(tourism.getLocation_tourism());
                    tvDescHistoryDetail.setText(tourism.getInfo_tourism());
                    Glide.with(getApplicationContext()).load(tourism.getUrl_photo()).into(imgHistory);

                    AppBarLayout appBarLayout = findViewById(R.id.app_bar_history);
                    appBarLayout.addOnOffsetChangedListener(new AppBarLayout.BaseOnOffsetChangedListener() {
                        boolean isShow = true;
                        int scrollRange = -1;

                        @Override
                        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                            if (scrollRange == -1) {
                                scrollRange = appBarLayout.getTotalScrollRange();
                            }
                            if (scrollRange + verticalOffset == 0) {
                                collapsingToolbarLayout_history.setTitle(tourism.getName_tourism());
                                isShow = true;
                            } else {
                                collapsingToolbarLayout_history.setTitle(" ");
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
            historyDetailRef.addValueEventListener(eventListener);
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
        historyMapView.onSaveInstanceState(mapViewBundle);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        historyMap = googleMap;
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Tourism tourism = dataSnapshot.getValue(Tourism.class);
                assert tourism != null;
                double lattitude = tourism.getLat_location_tourism();
                double longitude = tourism.getLng_location_tourism();

                LatLng location = new LatLng(lattitude, longitude);
                historyMap.addMarker(new MarkerOptions().position(location));
                historyMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16.0f));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Firebase Database Error" + databaseError.getMessage());
            }
        };
        historyDetailRef.addValueEventListener(eventListener);
        valueEventListener = eventListener;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.favorite_menu, menu);

        final MenuItem item = menu.findItem(R.id.add_to_favorite);
        final String uid = user.getUid();
        favoritedb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(uid).child(historyKey).exists()) {
                    item.setIcon(R.drawable.ic_bookmarkadded_24dp);
                } else {
                    item.setIcon(R.drawable.ic_unbookmarked_24dp);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Respond to the action bar's Up/Home button
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        if (item.getItemId() == R.id.add_to_favorite) {

            final String uid = user.getUid();
            favoritedb.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    DataSnapshot favorit = dataSnapshot.child(uid);
                    if (isFavorite) {
                        item.setIcon(R.drawable.ic_unbookmarked_24dp);
                        favoritedb.getRef().child(uid).child(historyKey).removeValue();
                        isFavorite = false;

                    } else {
                        item.setIcon(R.drawable.ic_bookmarkadded_24dp);
                        favoritedb.getRef().child(uid).child(historyKey).setValue(true);
                        isFavorite = true;
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        historyMapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LoadHistoryDetail();
    }

    @Override
    protected void onStop() {
        super.onStop();
        historyMapView.onStop();
        historyDetailRef.removeEventListener(valueEventListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        historyMapView.onPause();
    }
}
