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

package co.id.roningrum.dolanapptugasakhir.ui.hotel;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

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
import co.id.roningrum.dolanapptugasakhir.model.Hotel;
import co.id.roningrum.dolanapptugasakhir.util.Utils;

import static co.id.roningrum.dolanapptugasakhir.firebasequery.FirebaseConstant.favoriteRef;

public class HotelDetail extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {
    public static final String EXTRA_HOTEL_KEY = "hotel_key";

    private static final String MAP_VIEW_KEY = "mapViewBundle";

    private final static String TAG = "Pesan";

    private GoogleMap hotelGoogleMap;
    private MapView hotelMapView;

    private DatabaseReference hotelDetailRef;

    private GPSHandler gpsHandler;
    Hotel hotel = new Hotel();
    private ImageButton btnRouteToMap, btnCall;
    private TextView tvNameHotelDetail;
    private TextView tvAddressHotelDetail;
    private TextView tvDistanceHotelEducation;
    private TextView tvDetailHotel;

    private ImageView imgHotelDetail;
    private CollapsingToolbarLayout collapsingToolbarHotel;

    private double startLat;
    private double startlng;
    private double endlat;
    private double endLng;
    private double distance;
    private TextView tvHotelMoreDesc;

    boolean isFavorite = false;
    Menu menuItem;
    String hotelKey;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_hotel);
        tvNameHotelDetail = findViewById(R.id.name_place_hotel_detail);
        tvAddressHotelDetail = findViewById(R.id.location_hotel_detail);
        tvDistanceHotelEducation = findViewById(R.id.distance_location_hotel);
        imgHotelDetail = findViewById(R.id.img_hotel_detail);
        hotelMapView = findViewById(R.id.location_hotel_map_detail);
        collapsingToolbarHotel = findViewById(R.id.collapseToolbar_hotel);
        tvDetailHotel = findViewById(R.id.info_place_hotel_detail);
        btnCall = findViewById(R.id.btn_call);
        btnRouteToMap = findViewById(R.id.btn_route_map);
        tvHotelMoreDesc = findViewById(R.id.info_place_hotel_more);

        Button btnOrdeHotel = findViewById(R.id.btn_order_hotel);

        Toolbar toolbarHotel = findViewById(R.id.toolbar_hotel_detail);
        setSupportActionBar(toolbarHotel);


        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_KEY);
        }
        hotelMapView.onCreate(mapViewBundle);
        hotelMapView.getMapAsync(this);

        hotelKey = getIntent().getStringExtra(EXTRA_HOTEL_KEY);
        if (hotelKey == null) {
            throw new IllegalArgumentException("Must pass Extra");
        }
        hotelDetailRef = FirebaseConstant.getHotelKey(hotelKey);
        gpsHandler = new GPSHandler(this);

        user = FirebaseAuth.getInstance().getCurrentUser();

        btnOrdeHotel.setOnClickListener(this);
        tvHotelMoreDesc.setOnClickListener(this);
        LoadHotelDetail();
        favoriteState();

    }

    private void favoriteState() {
        final String uid = user.getUid();
        favoriteRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(uid).child("Hotel").child(hotelKey).exists()) {
                    isFavorite = true;
                    menuItem.getItem(0).setIcon(R.drawable.ic_bookmarkadded_24dp);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void LoadHotelDetail() {
        if (gpsHandler.isCanGetLocation()) {
            hotelDetailRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    hotel = dataSnapshot.getValue(Hotel.class);
                    startLat = gpsHandler.getLatitude();
                    startlng = gpsHandler.getLongitude();

                    assert hotel != null;
                    endlat = hotel.getLat_location_hotel();
                    endLng = hotel.getLng_location_hotel();
                    distance = Utils.calculateDistance(startLat, startlng, endlat, endLng);

                    String distanceFormat = String.format("%.2f", distance);
                    tvDistanceHotelEducation.setText("" + distanceFormat + " km");
                    tvNameHotelDetail.setText(hotel.getName_hotel());
                    tvAddressHotelDetail.setText(hotel.getLocation_hotel());

                    tvDetailHotel.setText(hotel.getInfo_hotel());
                    tvDetailHotel.setMaxLines(4);

                    Glide.with(getApplicationContext()).load(hotel.getUrl_photo_hotel()).into(imgHotelDetail);
                    AppBarLayout appBarLayout = findViewById(R.id.app_bar_hotel);
                    appBarLayout.addOnOffsetChangedListener(new AppBarLayout.BaseOnOffsetChangedListener() {
                        boolean isShow = true;
                        int scrollRange = -1;

                        @Override
                        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                            if (scrollRange == -1) {
                                scrollRange = appBarLayout.getTotalScrollRange();
                            }
                            if (scrollRange + verticalOffset == 0) {
                                collapsingToolbarHotel.setTitle(hotel.getName_hotel());
                                collapsingToolbarHotel.setCollapsedTitleTextColor(getResources().getColor(android.R.color.white));
                                collapsingToolbarHotel.setContentScrim(getResources().getDrawable(R.drawable.bg_image_blur));
                                tvDistanceHotelEducation.setVisibility(View.INVISIBLE);
                                findViewById(R.id.location_icon_pic).setVisibility(View.INVISIBLE);
                                isShow = true;
                            } else {
                                collapsingToolbarHotel.setTitle(" ");
                                tvDistanceHotelEducation.setVisibility(View.VISIBLE);
                                findViewById(R.id.location_icon_pic).setVisibility(View.VISIBLE);
                                isShow = false;
                            }

                        }
                    });
                    btnCall.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", hotel.getTelepon(), null));
                            if (!hotel.getTelepon().equals("Tidak tersedia")) {
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
            });
        }
    }

    @Override
    protected void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_KEY, mapViewBundle);
            hotelMapView.onSaveInstanceState(mapViewBundle);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        hotelGoogleMap = googleMap;
        showHotelMapDetail();
    }

    private void showHotelMapDetail() {
        hotelDetailRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hotel = dataSnapshot.getValue(Hotel.class);
                assert hotel != null;
                endlat = hotel.getLat_location_hotel();
                endLng = hotel.getLng_location_hotel();

                LatLng location = new LatLng(endlat, endLng);
                hotelGoogleMap.addMarker(new MarkerOptions().position(location).title(hotel.getName_hotel()));
                hotelGoogleMap.getUiSettings().setMapToolbarEnabled(false);
                hotelGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15.10f));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Firebase Database Error" + databaseError.getMessage());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_order_hotel:
                OptionalOrderHotelFragment optionalOrderHotelFragment = new OptionalOrderHotelFragment();
                FragmentManager fm = getSupportFragmentManager();
                Bundle data = new Bundle();
                data.putString(EXTRA_HOTEL_KEY, hotelKey);
                optionalOrderHotelFragment.setArguments(data);
                optionalOrderHotelFragment.show(fm, OptionalOrderHotelFragment.class.getSimpleName());
                break;
            case R.id.info_place_hotel_more:
                if (tvHotelMoreDesc.getText().toString().equalsIgnoreCase("Lebih Lengkap")) {
                    tvDetailHotel.setMaxLines(Integer.MAX_VALUE);
                    tvHotelMoreDesc.setText(getResources().getString(R.string.showless_text));
                } else {
                    tvDetailHotel.setMaxLines(4);
                    tvHotelMoreDesc.setText(getResources().getString(R.string.showmore_text));
                }
        }

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
                favoriteRef.getRef().child(uid).child("Hotel").child(hotelKey).setValue(true);
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
                favoriteRef.getRef().child(uid).child("Hotel").child(hotelKey).removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        hotelMapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LoadHotelDetail();
        hotelMapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        hotelMapView.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        hotelMapView.onPause();
    }
}
