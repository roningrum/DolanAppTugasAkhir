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

package co.id.roningrum.dolanapptugasakhir.police;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import co.id.roningrum.dolanapptugasakhir.item.PoliceItem;
import co.id.roningrum.dolanapptugasakhir.police.viewholder.PoliceViewHolder;

public class PoliceActivity extends AppCompatActivity {

    private RecyclerView rvPoliceList;
    private ShimmerFrameLayout shimmerFrameLayout;
    private FirebaseRecyclerAdapter<PoliceItem, PoliceViewHolder> policeFirebaseAdapter;

    private GPSHandler gpsHandler;
    private PermissionHandler permissionHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_police);
        rvPoliceList = findViewById(R.id.rv_police_list);
        shimmerFrameLayout = findViewById(R.id.shimmer_view_container);
        Toolbar toolbarPolice = findViewById(R.id.toolbar_top_police);
        rvPoliceList.setLayoutManager(new LinearLayoutManager(this));
        setSupportActionBar(toolbarPolice);
        checkConnection();
    }

    private void checkConnection() {
        if (NetworkHelper.isConnectedToNetwork(getApplicationContext())) {
            showPoliceData();
        } else {
            Toast.makeText(this, "Check your connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void showPoliceData() {
        if (havePermission()) {
            DatabaseReference policeRef = FirebaseDatabase.getInstance().getReference();
            Query policeQuery = policeRef.child("Police");
            FirebaseRecyclerOptions<PoliceItem> policeOptions = new FirebaseRecyclerOptions.Builder<PoliceItem>()
                    .setQuery(policeQuery, PoliceItem.class)
                    .build();

            policeFirebaseAdapter = new FirebaseRecyclerAdapter<PoliceItem, PoliceViewHolder>(policeOptions) {
                @Override
                protected void onBindViewHolder(@NonNull PoliceViewHolder holder, int position, @NonNull PoliceItem model) {
                    final DatabaseReference policeRef = getRef(position);
                    final String policeKey = policeRef.getKey();

                    gpsHandler = new GPSHandler(getApplicationContext());
                    if (gpsHandler.isCanGetLocation()) {
                        double latitude = gpsHandler.getLatitude();
                        double longitude = gpsHandler.getLongitude();

                        Log.i("Message", "CurLoc :" + latitude + "," + longitude);

                        shimmerFrameLayout.stopShimmerAnimation();
                        shimmerFrameLayout.setVisibility(View.GONE);

                        holder.showPoliceData(model, latitude, longitude);
                        holder.setOnClickListener(new PoliceViewHolder.ClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Intent intent = new Intent(getApplicationContext(), PoliceDetailActivity.class);
                                intent.putExtra(PoliceDetailActivity.EXTRA_POLICE_KEY, policeKey);
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
                public PoliceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                    return new PoliceViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_police_facility_menu, viewGroup, false));
                }
            };
            policeFirebaseAdapter.notifyDataSetChanged();
            rvPoliceList.setAdapter(policeFirebaseAdapter);
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
            startActivity(new Intent(PoliceActivity.this, PoliceMapsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        shimmerFrameLayout.startShimmerAnimation();
        if (policeFirebaseAdapter != null) {
            policeFirebaseAdapter.startListening();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        shimmerFrameLayout.stopShimmerAnimation();
        if (policeFirebaseAdapter != null) {
            policeFirebaseAdapter.stopListening();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (policeFirebaseAdapter != null) {
            policeFirebaseAdapter.startListening();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (policeFirebaseAdapter != null) {
            policeFirebaseAdapter.stopListening();
        }
    }

}
