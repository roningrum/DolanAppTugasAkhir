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

package co.id.roningrum.dolanapptugasakhir.ui.categoryactivity;

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

import java.util.ArrayList;
import java.util.Arrays;

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.handler.GPSHandler;
import co.id.roningrum.dolanapptugasakhir.handler.NetworkHelper;
import co.id.roningrum.dolanapptugasakhir.handler.PermissionHandler;
import co.id.roningrum.dolanapptugasakhir.model.TourismItem;
import co.id.roningrum.dolanapptugasakhir.ui.detailactivity.DetailWaterActivity;
import co.id.roningrum.dolanapptugasakhir.ui.mapsactivity.WaterMapsActivity;
import co.id.roningrum.dolanapptugasakhir.viewholder.categoryviewholder.WaterViewHolder;

public class WaterCategoryActivity extends AppCompatActivity {
    private RecyclerView rvWaterList;
    private ShimmerFrameLayout shimmerFrameLayout;
    private FirebaseRecyclerAdapter<TourismItem, WaterViewHolder> waterFirebaseAdapter;

    private GPSHandler gpsHandler;
    private PermissionHandler permissionHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_water);
        rvWaterList = findViewById(R.id.tourism_water_list);
        Toolbar toolbarWater = findViewById(R.id.toolbar_top_water);
        shimmerFrameLayout = findViewById(R.id.shimmer_view_container);

        rvWaterList.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<TourismItem> tourismItems = new ArrayList<>();
        checkConnection();
        setSupportActionBar(toolbarWater);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void checkConnection() {
        if (NetworkHelper.isConnectedToNetwork(getApplicationContext())) {
            showData();
        } else {
            Toast.makeText(this, "Check your connection", Toast.LENGTH_SHORT).show();
            gpsHandler.stopUsingGPS();
        }
    }

    private void showData() {
        if(havePermission()){
            DatabaseReference waterCategoryDB = FirebaseDatabase.getInstance().getReference();
            Query waterItemListQuery = waterCategoryDB.child("Tourism").orderByChild("category_tourism").equalTo("air");
            FirebaseRecyclerOptions<TourismItem> options = new FirebaseRecyclerOptions.Builder<TourismItem>()
                    .setQuery(waterItemListQuery, TourismItem.class)
                    .build();
            waterFirebaseAdapter = new FirebaseRecyclerAdapter<TourismItem, WaterViewHolder>(options) {

                @Override
                protected void onBindViewHolder(@NonNull WaterViewHolder holder, int position, @NonNull TourismItem model) {
                    final DatabaseReference waterCategoryRef = getRef(position);
                    final String waterKey = waterCategoryRef.getKey();

                    gpsHandler = new GPSHandler(getApplicationContext());
                    if (gpsHandler.isCanGetLocation()) {
                        double latitude = gpsHandler.getLatitude();
                        double longitude = gpsHandler.getLongitude();

                        Log.i("Message", "CurLoc :" + latitude + "," + longitude);

                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);

                        holder.showWaterTourismData(model, latitude, longitude);
                        holder.setOnClickListener(new WaterViewHolder.ClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Intent intent = new Intent(getApplicationContext(), DetailWaterActivity.class);
                                intent.putExtra(DetailWaterActivity.EXTRA_WISATA_KEY, waterKey);
                                startActivity(intent);
                            }
                        });


                    } else {
                        gpsHandler.showSettingsAlert();
                    }
                }

                @NonNull
                @Override
                public WaterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                    View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_water_category_menu, viewGroup, false);
                    return new WaterViewHolder(view);
                }
            };
            waterFirebaseAdapter.notifyDataSetChanged();
            rvWaterList.setAdapter(waterFirebaseAdapter);
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
            startActivity(new Intent(WaterCategoryActivity.this, WaterMapsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        shimmerFrameLayout.startShimmer();
        waterFirebaseAdapter.startListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
        shimmerFrameLayout.stopShimmer();
        waterFirebaseAdapter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (waterFirebaseAdapter != null) {
            waterFirebaseAdapter.startListening();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (waterFirebaseAdapter != null) {
            waterFirebaseAdapter.stopListening();
        }

    }
}
