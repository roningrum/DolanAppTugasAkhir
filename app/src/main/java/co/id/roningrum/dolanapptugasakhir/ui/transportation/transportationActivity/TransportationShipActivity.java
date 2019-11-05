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

package co.id.roningrum.dolanapptugasakhir.ui.transportation.transportationActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.Arrays;

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.adapter.transportation.ShipViewHolder;
import co.id.roningrum.dolanapptugasakhir.controller.FirebaseConstant;
import co.id.roningrum.dolanapptugasakhir.handler.GPSHandler;
import co.id.roningrum.dolanapptugasakhir.handler.LocationPermissionHandler;
import co.id.roningrum.dolanapptugasakhir.handler.NetworkHelper;
import co.id.roningrum.dolanapptugasakhir.model.Transportation;
import co.id.roningrum.dolanapptugasakhir.ui.transportation.transportationDetailActivity.TransportationShipDetail;
import co.id.roningrum.dolanapptugasakhir.ui.transportation.transportationMapActivity.TransportationShipMaps;

public class TransportationShipActivity extends AppCompatActivity {
    private RecyclerView rvShipList;
    private ShimmerFrameLayout shimmerFrameLayout;
    private FirebaseRecyclerAdapter<Transportation, ShipViewHolder> shipFirebaseadapter;

    private GPSHandler gpsHandler;
    private LocationPermissionHandler locationPermissionHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_ship);
        rvShipList = findViewById(R.id.rv_ship_list);
        Toolbar toolbarShip = findViewById(R.id.toolbar_top_ship);
        shimmerFrameLayout = findViewById(R.id.shimmer_view_container);
        rvShipList.setLayoutManager(new LinearLayoutManager(this));
        setSupportActionBar(toolbarShip);
        checkConnection();
    }

    private void checkConnection() {
        if (NetworkHelper.isConnectedToNetwork(getApplicationContext())) {
            showShipData();
        } else {
            Toast.makeText(this, "Check your connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void showShipData() {
        if (havePermission()) {
            Query shipQuery = FirebaseConstant.getTransportShip();
            final FirebaseRecyclerOptions<Transportation> shipOptions = new FirebaseRecyclerOptions.Builder<Transportation>()
                    .setQuery(shipQuery, Transportation.class)
                    .build();
            shipFirebaseadapter = new FirebaseRecyclerAdapter<Transportation, ShipViewHolder>(shipOptions) {
                @Override
                protected void onBindViewHolder(@NonNull ShipViewHolder holder, int position, @NonNull Transportation model) {
                    final DatabaseReference shipRef = getRef(position);
                    final String shipKey = shipRef.getKey();
                    gpsHandler = new GPSHandler(getApplicationContext());
                    if (gpsHandler.isCanGetLocation()) {
                        double latitude = gpsHandler.getLatitude();
                        double longitude = gpsHandler.getLongitude();

                        Log.i("Message", "CurLoc :" + latitude + "," + longitude);

                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);

                        holder.showHarborData(model, latitude, longitude);
                        holder.setOnClickListener(new ShipViewHolder.ClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Intent intent = new Intent(getApplicationContext(), TransportationShipDetail.class);
                                intent.putExtra(TransportationShipDetail.EXTRA_SHIP_KEY, shipKey);
                                startActivity(intent);
                            }
                        });

                    } else {
                        gpsHandler.stopUsingGPS();
                        gpsHandler.showSettingsAlert();
                    }

                }

                @NonNull
                @Override
                public ShipViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                    return new ShipViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_menu_ship_category_transport, viewGroup, false));
                }
            };
            shipFirebaseadapter.notifyDataSetChanged();
            rvShipList.setAdapter(shipFirebaseadapter);
        }
    }

    private boolean havePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            locationPermissionHandler = LocationPermissionHandler.getInstance(this);
            if (locationPermissionHandler.isAllPermissionAvailable()) {
                Log.d("Pesan", "Permissions have done");
            } else {
                locationPermissionHandler.setActivity(this);
                locationPermissionHandler.deniedPermission();
            }
        } else {
            Toast.makeText(this, "Check your permission", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i : grantResults) {
            if (i == PackageManager.PERMISSION_GRANTED) {
                Log.d("test", "Permission" + Arrays.toString(permissions) + "Success");
            } else {
                //denied
                locationPermissionHandler.deniedPermission(Manifest.permission.ACCESS_FINE_LOCATION);
                locationPermissionHandler.deniedPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.petaMenu) {
            startActivity(new Intent(TransportationShipActivity.this, TransportationShipMaps.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        shimmerFrameLayout.startShimmer();
        if (shipFirebaseadapter != null) {
            shipFirebaseadapter.startListening();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        shimmerFrameLayout.stopShimmer();
        if (shipFirebaseadapter != null) {
            shipFirebaseadapter.stopListening();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (shipFirebaseadapter != null) {
            shipFirebaseadapter.startListening();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (shipFirebaseadapter != null) {
            shipFirebaseadapter.stopListening();
        }
    }
}
