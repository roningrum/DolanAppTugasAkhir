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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Arrays;

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.handler.GPSHandler;
import co.id.roningrum.dolanapptugasakhir.handler.NetworkHelper;
import co.id.roningrum.dolanapptugasakhir.handler.PermissionHandler;
import co.id.roningrum.dolanapptugasakhir.model.TransportationItem;
import co.id.roningrum.dolanapptugasakhir.ui.transportation.transportationDetailActivity.DetailBusActivity;
import co.id.roningrum.dolanapptugasakhir.ui.transportation.transportationMapActivity.BusMapsActivity;
import co.id.roningrum.dolanapptugasakhir.viewholderActivity.transportation.BusViewHolder;

public class BusCategoryActivity extends AppCompatActivity {
    private RecyclerView rvBusList;
    private ShimmerFrameLayout shimmerFrameLayout;
    private FirebaseRecyclerAdapter<TransportationItem, BusViewHolder> busFirebaseadapter;

    private GPSHandler gpsHandler;
    private PermissionHandler permissionHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_bus);
        rvBusList = findViewById(R.id.rv_bus_list);
        Toolbar toolbarBus = findViewById(R.id.toolbar_top_bus);
        shimmerFrameLayout = findViewById(R.id.shimmer_view_container);
        rvBusList.setLayoutManager(new LinearLayoutManager(this));
        setSupportActionBar(toolbarBus);
        checkConnection();
    }

    private void checkConnection() {
        if (NetworkHelper.isConnectedToNetwork(getApplicationContext())) {
            showBusData();
        } else {
            Toast.makeText(this, "Check your connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void showBusData() {
        if (havePermission()) {
            DatabaseReference busRef = FirebaseDatabase.getInstance().getReference();
            Query busQuery = busRef.child("Transportation").orderByChild("category_transportation").equalTo("bus");
            FirebaseRecyclerOptions<TransportationItem> busOptions = new FirebaseRecyclerOptions.Builder<TransportationItem>()
                    .setQuery(busQuery, TransportationItem.class)
                    .build();

            busFirebaseadapter = new FirebaseRecyclerAdapter<TransportationItem, BusViewHolder>(busOptions) {
                @Override
                protected void onBindViewHolder(@NonNull BusViewHolder holder, int position, @NonNull TransportationItem model) {
                    final DatabaseReference busRef = getRef(position);
                    final String busKey = busRef.getKey();

                    gpsHandler = new GPSHandler(getApplicationContext());
                    if (gpsHandler.isCanGetLocation()) {
                        double latitude = gpsHandler.getLatitude();
                        double longitude = gpsHandler.getLongitude();

                        Log.i("Message", "CurLoc :" + latitude + "," + longitude);

                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);

                        holder.showBusData(model, latitude, longitude);
                        holder.setOnClickListener(new BusViewHolder.ClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Intent intent = new Intent(getApplicationContext(), DetailBusActivity.class);
                                intent.putExtra(DetailBusActivity.EXTRA_BUS_KEY, busKey);
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
                public BusViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                    return new BusViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_menu_bus_category_transport, viewGroup, false));
                }
            };
            busFirebaseadapter.notifyDataSetChanged();
            rvBusList.setAdapter(busFirebaseadapter);

        }
    }

    private boolean havePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            permissionHandler = PermissionHandler.getInstance(this);
            if (permissionHandler.isAllPermissionAvailable()) {
                Log.d("Pesan", "Permissions have done");
            } else {
                permissionHandler.setActivity(this);
                permissionHandler.deniedPermission();
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
                permissionHandler.deniedPermission(Manifest.permission.ACCESS_FINE_LOCATION);
                permissionHandler.deniedPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
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
            startActivity(new Intent(BusCategoryActivity.this, BusMapsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        shimmerFrameLayout.startShimmer();
        if (busFirebaseadapter != null) {
            busFirebaseadapter.startListening();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        shimmerFrameLayout.stopShimmer();
        if (busFirebaseadapter != null) {
            busFirebaseadapter.stopListening();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (busFirebaseadapter != null) {
            busFirebaseadapter.startListening();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (busFirebaseadapter != null) {
            busFirebaseadapter.stopListening();
        }
    }

}
