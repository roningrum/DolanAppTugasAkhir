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

package co.id.roningrum.dolanapptugasakhir.tourism.history;

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

import java.util.ArrayList;
import java.util.Arrays;

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.handler.GPSHandler;
import co.id.roningrum.dolanapptugasakhir.handler.NetworkHelper;
import co.id.roningrum.dolanapptugasakhir.handler.PermissionHandler;
import co.id.roningrum.dolanapptugasakhir.item.CategoryItem;
import co.id.roningrum.dolanapptugasakhir.tourism.history.viewholder.HistoryViewHolder;

public class HistoryCategoryActivity extends AppCompatActivity {

    private RecyclerView rvHistoryList;
    private ShimmerFrameLayout shimmerFrameLayout;
    private FirebaseRecyclerAdapter<CategoryItem, HistoryViewHolder> historyFirebaseAdapter;

    private GPSHandler gpsHandler;
    private PermissionHandler permissionHandler;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_catgeory);
        rvHistoryList = findViewById(R.id.tourism_history_list);
        Toolbar toolbarHistory = findViewById(R.id.toolbar_top_history);
        shimmerFrameLayout = findViewById(R.id.shimmer_view_container);
        rvHistoryList.setLayoutManager(new LinearLayoutManager(this));
        rvHistoryList.setHasFixedSize(true);
        ArrayList<CategoryItem> categoryItems = new ArrayList<>();
        checkConnection();
        setSupportActionBar(toolbarHistory);

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
        if (havePermission()) {
            DatabaseReference historyCategoryDB = FirebaseDatabase.getInstance().getReference();
            Query historyQuery = historyCategoryDB.child("Tourism").orderByChild("category_tourism").equalTo("sejarah");
            FirebaseRecyclerOptions<CategoryItem> historyOptions = new FirebaseRecyclerOptions.Builder<CategoryItem>()
                    .setQuery(historyQuery, CategoryItem.class)
                    .build();
            historyFirebaseAdapter = new FirebaseRecyclerAdapter<CategoryItem, HistoryViewHolder>(historyOptions) {

                @NonNull
                @Override
                public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                    return new HistoryViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_history_category_menu, viewGroup, false));
                }

                @Override
                protected void onBindViewHolder(@NonNull HistoryViewHolder holder, int position, @NonNull CategoryItem model) {
                    final DatabaseReference historyCategoryRef = getRef(position);
                    final String historyKey = historyCategoryRef.getKey();

                    gpsHandler = new GPSHandler(getApplicationContext());
                    if (gpsHandler.isCanGetLocation()) {
                        double latitude = gpsHandler.getLatitude();
                        double longitude = gpsHandler.getLongitude();

                        Log.i("Message", "CurLoc :" + latitude + "," + longitude);

                        shimmerFrameLayout.stopShimmerAnimation();
                        shimmerFrameLayout.setVisibility(View.GONE);

                        holder.showHistoryTourismData(model, latitude, longitude);
                        holder.setOnClickListener(new HistoryViewHolder.ClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Intent intent = new Intent(getApplicationContext(), DetailHistoryActivity.class);
                                intent.putExtra(DetailHistoryActivity.EXTRA_WISATA_KEY, historyKey);
                                startActivity(intent);
                            }
                        });

                    } else {
                        gpsHandler.showSettingsAlert();
                    }
                }
            };
            historyFirebaseAdapter.notifyDataSetChanged();
            rvHistoryList.setAdapter(historyFirebaseAdapter);
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
            startActivity(new Intent(HistoryCategoryActivity.this, HistoryCategoryMap.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        shimmerFrameLayout.startShimmerAnimation();
        historyFirebaseAdapter.startListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
        shimmerFrameLayout.stopShimmerAnimation();
        historyFirebaseAdapter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (historyFirebaseAdapter != null) {
            historyFirebaseAdapter.startListening();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (historyFirebaseAdapter != null) {
            historyFirebaseAdapter.stopListening();
        }

    }
}
