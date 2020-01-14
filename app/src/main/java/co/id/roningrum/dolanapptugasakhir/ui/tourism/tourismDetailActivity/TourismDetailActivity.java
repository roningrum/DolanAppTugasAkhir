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

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.firebasequery.FirebaseConstant;
import co.id.roningrum.dolanapptugasakhir.handler.GPSHandler;
import co.id.roningrum.dolanapptugasakhir.model.Tourism;
import co.id.roningrum.dolanapptugasakhir.util.Utils;

import static co.id.roningrum.dolanapptugasakhir.firebasequery.FirebaseConstant.favoriteRef;

public class TourismDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String EXTRA_WISATA_KEY = "tourism_key";

    private static final String MAP_VIEW_KEY = "mapViewBundle";

    private final static String TAG = "Pesan";
    boolean isFavorite = false;
    Menu menuItem;
    Tourism tourism = new Tourism();
    private GoogleMap tourismLocationMap;
    //    private ValueEventListener valueEventListener;
    private MapView tourismMapView;
    private DatabaseReference tourismDetailRef;
    private GPSHandler gpsHandler;
    private TextView tvNameTourismDetail, tvAddressTourismDetail, tvDescTourismDetail,
            tvDistanceTourismDetail;
    private ImageView imgTourismObject;
    private CollapsingToolbarLayout collapsingToolbarLayoutTourism;
    private double startLat;
    private double startLng;
    private double endLat;
    private double endLng;
    private double distance;
    private String tourismKey;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_tourism);
        tvNameTourismDetail = findViewById(R.id.name_place_tourism_detail);
        tvAddressTourismDetail = findViewById(R.id.location_tourism_detail);
        tvDescTourismDetail = findViewById(R.id.info_place_tourism_detail);
        tvDistanceTourismDetail = findViewById(R.id.distance_location_tourism);
        imgTourismObject = findViewById(R.id.img_tourism_detail);
        tourismMapView = findViewById(R.id.location_tourism_map_detail);

        collapsingToolbarLayoutTourism = findViewById(R.id.collapseToolbar_tourism_detail);
        Toolbar toolbarTourismDetail = findViewById(R.id.toolbar_tourism_detail);
        setSupportActionBar(toolbarTourismDetail);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_KEY);
        }

        tourismMapView.onCreate(mapViewBundle);
        tourismMapView.getMapAsync(this);

        tourismKey = getIntent().getStringExtra(EXTRA_WISATA_KEY);

        assert tourismKey != null;
        tourismDetailRef = FirebaseConstant.getTourismRef(tourismKey);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        Log.d(TAG, "idDetail : " + tourismKey);
        gpsHandler = new GPSHandler(this);

        loadTourismDetail();
        favoriteState();
    }

    private void favoriteState() {
        final String uid = user.getUid();
        favoriteRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(uid).child(tourismKey).exists()) {
                    isFavorite = true;
                    menuItem.getItem(0).setIcon(R.drawable.ic_bookmarkadded_24dp);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void loadTourismDetail() {
        if (gpsHandler.isCanGetLocation()) {
            tourismDetailRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    tourism = dataSnapshot.getValue(Tourism.class);
                    startLat = gpsHandler.getLatitude();
                    startLng = gpsHandler.getLongitude();
                    assert tourism != null;
                    endLat = tourism.getLat_location_tourism();
                    endLng = tourism.getLng_location_tourism();
                    distance = Utils.calculateDistance(startLat, startLng, endLat, endLng);

                    String distanceFormat = String.format("%.2f", distance);
                    tvNameTourismDetail.setText(tourism.getName_tourism());
                    tvDistanceTourismDetail.setText("" + distanceFormat + " KM");
                    tvAddressTourismDetail.setText(tourism.getLocation_tourism());
                    tvDescTourismDetail.setText(tourism.getInfo_tourism());
                    Glide.with(getApplicationContext()).load(tourism.getUrl_photo_tourism()).into(imgTourismObject);
                    Log.d(TAG, "Url_Photo_Tourism " + tourism.getUrl_photo_tourism());
                    AppBarLayout appBarLayout = findViewById(R.id.app_bar_tourism);
                    appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {

                        boolean isShow = true;
                        int scrollRange = -1;

                        @Override
                        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                            if (scrollRange == -1) {
                                scrollRange = appBarLayout.getTotalScrollRange();
                            }
                            if (scrollRange + verticalOffset == 0) {
                                collapsingToolbarLayoutTourism.setTitle(tourism.getName_tourism());
                                isShow = true;
                            } else {
                                collapsingToolbarLayoutTourism.setTitle(" ");
                                isShow = false;
                            }
                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "Firebase Database Error" + databaseError.getMessage());

                }
            });
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_KEY, mapViewBundle);
        }
        tourismMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        tourismLocationMap = googleMap;

        tourismDetailRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tourism = dataSnapshot.getValue(Tourism.class);
                assert tourism != null;

                double lattitude = tourism.getLat_location_tourism();
                double longitude = tourism.getLng_location_tourism();

                LatLng location = new LatLng(lattitude, longitude);
                tourismLocationMap.addMarker(new MarkerOptions().position(location));
                tourismLocationMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15.10f));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, " " + databaseError.getMessage());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.favorite_menu, menu);
        menuItem = menu;
        setFavorite();
        return super.onCreateOptionsMenu(menu);
    }

    private void setFavorite() {
        if (isFavorite) {
            menuItem.getItem(0).setIcon(R.drawable.ic_bookmarkadded_24dp);
        } else {
            menuItem.getItem(0).setIcon(R.drawable.ic_unbookmarked_24dp);
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.add_to_favorite) {
            if (isFavorite) {
                removeFavorite();
            } else {
                addtoFavorite();
            }
            isFavorite = !isFavorite;
            setFavorite();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }

    }

    private void addtoFavorite() {
        final String uid = user.getUid();
        favoriteRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                favoriteRef.getRef().child(uid).child(tourismKey).setValue(true);
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
                favoriteRef.getRef().child(uid).child(tourismKey).removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        tourismMapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        tourismMapView.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        tourismMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        tourismMapView.onPause();
    }
}
