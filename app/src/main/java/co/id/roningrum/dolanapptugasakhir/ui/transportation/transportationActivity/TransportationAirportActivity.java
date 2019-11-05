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
import co.id.roningrum.dolanapptugasakhir.adapter.transportation.AirportViewHolder;
import co.id.roningrum.dolanapptugasakhir.controller.FirebaseConstant;
import co.id.roningrum.dolanapptugasakhir.handler.GPSHandler;
import co.id.roningrum.dolanapptugasakhir.handler.LocationPermissionHandler;
import co.id.roningrum.dolanapptugasakhir.handler.NetworkHelper;
import co.id.roningrum.dolanapptugasakhir.model.Transportation;
import co.id.roningrum.dolanapptugasakhir.ui.transportation.transportationDetailActivity.TransportationAirportDetail;
import co.id.roningrum.dolanapptugasakhir.ui.transportation.transportationMapActivity.TransportationAirportMaps;

public class TransportationAirportActivity extends AppCompatActivity {
    private RecyclerView rvAirportList;
    private ShimmerFrameLayout shimmerFrameLayout;
    private FirebaseRecyclerAdapter<Transportation, AirportViewHolder> airportFirebaseadapter;

    private GPSHandler gpsHandler;
    private LocationPermissionHandler locationPermissionHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_airport);
        rvAirportList = findViewById(R.id.rv_airport_list);
        Toolbar toolbarAirport = findViewById(R.id.toolbar_top_airport);
        shimmerFrameLayout = findViewById(R.id.shimmer_view_container);
        rvAirportList.setLayoutManager(new LinearLayoutManager(this));
        setSupportActionBar(toolbarAirport);
        checkConnection();

    }

    private void checkConnection() {
        if (NetworkHelper.isConnectedToNetwork(getApplicationContext())) {
            showAirportData();
        } else {
            Toast.makeText(this, "Check your connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAirportData() {
        if (havePermission()) {
            Query airportQuery = FirebaseConstant.getTransportPesawat();
            FirebaseRecyclerOptions<Transportation> airportOptions = new FirebaseRecyclerOptions.Builder<Transportation>()
                    .setQuery(airportQuery, Transportation.class)
                    .build();
            airportFirebaseadapter = new FirebaseRecyclerAdapter<Transportation, AirportViewHolder>(airportOptions) {
                @Override
                protected void onBindViewHolder(@NonNull AirportViewHolder holder, int position, @NonNull Transportation model) {
                    final DatabaseReference airportRef = getRef(position);
                    final String airportKey = airportRef.getKey();

                    gpsHandler = new GPSHandler(getApplicationContext());
                    if (gpsHandler.isCanGetLocation()) {
                        double latitude = gpsHandler.getLatitude();
                        double longitude = gpsHandler.getLongitude();

                        Log.i("Message", "CurLoc :" + latitude + "," + longitude);

                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);

                        holder.showAirportData(model, latitude, longitude);
                        holder.setOnClickListener(new AirportViewHolder.ClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Intent intent = new Intent(getApplicationContext(), TransportationAirportDetail.class);
                                intent.putExtra(TransportationAirportDetail.EXTRA_AIRPORT_KEY, airportKey);
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
                public AirportViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                    return new AirportViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_menu_airport_category_transport, viewGroup, false));
                }
            };
            airportFirebaseadapter.notifyDataSetChanged();
            rvAirportList.setAdapter(airportFirebaseadapter);

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
            startActivity(new Intent(TransportationAirportActivity.this, TransportationAirportMaps.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        shimmerFrameLayout.startShimmer();
        if (airportFirebaseadapter != null) {
            airportFirebaseadapter.startListening();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        shimmerFrameLayout.stopShimmer();
        if (airportFirebaseadapter != null) {
            airportFirebaseadapter.stopListening();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (airportFirebaseadapter != null) {
            airportFirebaseadapter.startListening();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (airportFirebaseadapter != null) {
            airportFirebaseadapter.stopListening();
        }
    }
}
