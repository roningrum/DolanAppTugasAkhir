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

package co.id.roningrum.dolanapptugasakhir;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import co.id.roningrum.dolanapptugasakhir.handler.GPSHandler;
import co.id.roningrum.dolanapptugasakhir.model.TourismItem;

public class FavTourismDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String EXTRA_TOURISM_FAV = "favorit_key";
    private static final String MAP_VIEW_KEY = "mapViewBundle";
    private final static String TAG = "Pesan";
    boolean isFavorite;
    TourismItem tourismItem = new TourismItem();
    String tourismKey;
    DatabaseReference favoritedb;
    private GoogleMap tourismGoogleMap;
    private MapView tourismMapView;
    private DatabaseReference tourismDetailRef;
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
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav_tourism_detail);
        tvNameTourismDetail = findViewById(R.id.name_tourism_detail);
        tvAddressTourismDetail = findViewById(R.id.address_tourism_detail);
        tvDescTourism = findViewById(R.id.info_fav_tourism_detail);
        tvDistanceTourism = findViewById(R.id.distance_fav_item_tourism);

        imgTourism = findViewById(R.id.img_fav_tourism_detail);
        tourismMapView = findViewById(R.id.loc_tourism_map);
        collapsingToolbarLayout = findViewById(R.id.collapseToolbar);

        Toolbar toolbarTourism = findViewById(R.id.toolbar_tourism_detail);
        setSupportActionBar(toolbarTourism);
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
        tourismMapView.onCreate(mapViewBundle);
        tourismMapView.getMapAsync(this);

        tourismKey = getIntent().getStringExtra(EXTRA_TOURISM_FAV);
        if (tourismKey == null) {
            throw new IllegalArgumentException("Must pass Extra");
        }

        tourismDetailRef = FirebaseDatabase.getInstance().getReference().child("Tourism").child(tourismKey);
//        gpsHandler
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}
