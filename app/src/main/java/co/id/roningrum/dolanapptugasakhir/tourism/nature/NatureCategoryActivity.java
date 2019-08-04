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

package co.id.roningrum.dolanapptugasakhir.tourism.nature;

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
import co.id.roningrum.dolanapptugasakhir.model.TourismItem;
import co.id.roningrum.dolanapptugasakhir.tourism.nature.viewholder.NatureViewHolder;

public class NatureCategoryActivity extends AppCompatActivity {
    private RecyclerView rvNatureList;
    private ShimmerFrameLayout shimmerFrameLayout;
    private FirebaseRecyclerAdapter<TourismItem, NatureViewHolder> natureFirebaseAdapter;

    private GPSHandler gpsHandler;
    private PermissionHandler permissionHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_nature);
        rvNatureList = findViewById(R.id.tourism_nature_list);
        Toolbar toolbarNature = findViewById(R.id.toolbar_top_nature);
        shimmerFrameLayout = findViewById(R.id.shimmer_view_container);
        rvNatureList.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<TourismItem> tourismItems = new ArrayList<>();
        checkConnection();
        setSupportActionBar(toolbarNature);
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
        if (havePermission()) {
            DatabaseReference natureCategoryDB = FirebaseDatabase.getInstance().getReference();
            Query query = natureCategoryDB.child("Tourism").orderByChild("category_tourism").equalTo("alam");
            FirebaseRecyclerOptions<TourismItem> options = new FirebaseRecyclerOptions.Builder<TourismItem>()
                    .setQuery(query, TourismItem.class)
                    .build();
            natureFirebaseAdapter = new FirebaseRecyclerAdapter<TourismItem, NatureViewHolder>(options) {

                @Override
                protected void onBindViewHolder(@NonNull NatureViewHolder holder, int position, @NonNull TourismItem model) {
                    final DatabaseReference natureCategoryRef = getRef(position);
                    final String natureKey = natureCategoryRef.getKey();

                    gpsHandler = new GPSHandler(getApplicationContext());
                    if (gpsHandler.isCanGetLocation()) {
                        double latitude = gpsHandler.getLatitude();
                        double longitude = gpsHandler.getLongitude();

                        Log.i("Message", "CurLoc :" + latitude + "," + longitude);

                        shimmerFrameLayout.stopShimmerAnimation();
                        shimmerFrameLayout.setVisibility(View.GONE);

                        holder.showTourismData(model, longitude, latitude);
                        holder.setOnClickListener(new NatureViewHolder.ClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Intent intent = new Intent(getApplicationContext(), DetailNatureActivity.class);
                                intent.putExtra(DetailNatureActivity.EXTRA_WISATA_KEY, natureKey);
                                startActivity(intent);
                            }
                        });

                    } else {
                        gpsHandler.showSettingsAlert();
                    }
                }

                @NonNull
                @Override
                public NatureViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                    View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_nature_category_menu, viewGroup, false);
                    return new NatureViewHolder(view);
                }

            };
            natureFirebaseAdapter.notifyDataSetChanged();
            rvNatureList.setAdapter(natureFirebaseAdapter);
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
            startActivity(new Intent(NatureCategoryActivity.this, NatureMapsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        shimmerFrameLayout.startShimmerAnimation();
        natureFirebaseAdapter.startListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
        shimmerFrameLayout.stopShimmerAnimation();
        natureFirebaseAdapter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (natureFirebaseAdapter != null) {
            natureFirebaseAdapter.startListening();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (natureFirebaseAdapter != null) {
            natureFirebaseAdapter.stopListening();
        }

    }
}
