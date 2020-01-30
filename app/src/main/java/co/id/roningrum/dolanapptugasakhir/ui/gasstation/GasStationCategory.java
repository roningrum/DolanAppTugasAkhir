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

package co.id.roningrum.dolanapptugasakhir.ui.gasstation;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.firebasequery.FirebaseQuery;
import co.id.roningrum.dolanapptugasakhir.handler.LocationPermissionHandler;
import co.id.roningrum.dolanapptugasakhir.handler.NetworkHelper;
import co.id.roningrum.dolanapptugasakhir.model.GasStation;
import co.id.roningrum.dolanapptugasakhir.ui.adapter.gasstation.GasAdapter;
import co.id.roningrum.dolanapptugasakhir.ui.adapter.gasstation.GasClickCallback;

public class GasStationCategory extends AppCompatActivity {
    private RecyclerView rvSpbuList;
    private ShimmerFrameLayout shimmerFrameLayout;
    private GasAdapter gasAdapter;
    private ArrayList<GasStation> gasStations = new ArrayList<>();
    private LocationPermissionHandler locationPermissionHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_gas_station);
        rvSpbuList = findViewById(R.id.rv_spbu_list);
        Toolbar toolbarGas = findViewById(R.id.toolbar_top_gas);
        setSupportActionBar(toolbarGas);

        shimmerFrameLayout = findViewById(R.id.shimmer_view_container);
        rvSpbuList.setLayoutManager(new LinearLayoutManager(this));
        checkConnection();
    }

    private void checkConnection() {
        if (NetworkHelper.isConnectedToNetwork(getApplicationContext())) {
            showLoading(false);
            showGasData();
        } else {
            showLoading(true);
            Toast.makeText(this, "Check your connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void showGasData() {
        if (havePermission()) {
            Query spbuQuery = FirebaseQuery.getGas();
            spbuQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        GasStation gasStation = dataSnapshot1.getValue(GasStation.class);
                        gasStations.add(gasStation);
                    }
                    gasAdapter = new GasAdapter();
                    rvSpbuList.setAdapter(gasAdapter);
                    gasAdapter.setGasStations(gasStations);
                    gasAdapter.setGasClickCallback(new GasClickCallback() {
                        @Override
                        public void onItemClicked(GasStation gasStation) {
                            String gasKey = gasStation.getId();
                            Intent intent = new Intent(GasStationCategory.this, GasStationDetail.class);
                            intent.putExtra(GasStationDetail.EXTRA_GAS_KEY, gasKey);
                            startActivity(intent);
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

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
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.petaMenu:
                startActivity(new Intent(GasStationCategory.this, GasStationMap.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }

    private void showLoading(boolean state) {
        if (state) {
            shimmerFrameLayout.startShimmer();
        } else {
            shimmerFrameLayout.stopShimmer();
        }
    }

}
