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
import co.id.roningrum.dolanapptugasakhir.ui.detailactivity.DetailEducatioActivity;
import co.id.roningrum.dolanapptugasakhir.ui.mapsactivity.EducationCategoryMaps;
import co.id.roningrum.dolanapptugasakhir.viewholder.categoryviewholder.EducationViewHolder;

public class EducationCategoryActivity extends AppCompatActivity {

    private RecyclerView rvEducationList;
    private ShimmerFrameLayout shimmerFrameLayout;
    private FirebaseRecyclerAdapter<TourismItem, EducationViewHolder> educationFirebaseAdapter;

    private GPSHandler gpsHandler;
    private PermissionHandler permissionHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_education);
        rvEducationList = findViewById(R.id.tourism_education_list);
        Toolbar toolbarEducation = findViewById(R.id.toolbar_top_education);
        shimmerFrameLayout = findViewById(R.id.shimmer_view_container);
        rvEducationList.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<TourismItem> tourismItems = new ArrayList<>();
        setSupportActionBar(toolbarEducation);
        checkConnection();
    }

    private void checkConnection() {
        if (NetworkHelper.isConnectedToNetwork(getApplicationContext())) {
            showData();
        } else {
            Toast.makeText(this, "Check your connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void showData() {
        if (havePermission()) {
            DatabaseReference educationCategoryDB = FirebaseDatabase.getInstance().getReference();
            Query educationQuery = educationCategoryDB.child("Tourism").orderByChild("category_tourism").equalTo("edukasi");
            FirebaseRecyclerOptions<TourismItem> educationOptions = new FirebaseRecyclerOptions.Builder<TourismItem>()
                    .setQuery(educationQuery, TourismItem.class)
                    .build();
            educationFirebaseAdapter = new FirebaseRecyclerAdapter<TourismItem, EducationViewHolder>(educationOptions) {
                @NonNull
                @Override
                public EducationViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                    return new EducationViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_education_category_menu, viewGroup, false));
                }

                @Override
                protected void onBindViewHolder(@NonNull EducationViewHolder holder, int position, @NonNull TourismItem model) {
                    final DatabaseReference educationCategoryRef = getRef(position);
                    final String eductaionKey = educationCategoryRef.getKey();

                    gpsHandler = new GPSHandler(getApplicationContext());
                    if (gpsHandler.isCanGetLocation()) {
                        double latitude = gpsHandler.getLatitude();
                        double longitude = gpsHandler.getLongitude();

                        Log.i("Message", "CurLoc :" + latitude + "," + longitude);

                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);

                        holder.showEducationTourismData(model, latitude, longitude);
                        holder.setOnClickListener(new EducationViewHolder.ClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Intent intent = new Intent(getApplicationContext(), DetailEducatioActivity.class);
                                intent.putExtra(DetailEducatioActivity.EXTRA_WISATA_KEY, eductaionKey);
                                startActivity(intent);
                            }
                        });

                    } else {
                        gpsHandler.stopUsingGPS();
                        gpsHandler.showSettingsAlert();
                    }
                }

//                @NonNull
//                @Override
//                public ShoppingViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//                    View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_shopping_category_menu, viewGroup, false);
//                    return new ShoppingViewHolder(view);
//                }
            };
            educationFirebaseAdapter.notifyDataSetChanged();
            rvEducationList.setAdapter(educationFirebaseAdapter);
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
            startActivity(new Intent(EducationCategoryActivity.this, EducationCategoryMaps.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        shimmerFrameLayout.startShimmer();
        if (educationFirebaseAdapter != null) {
            educationFirebaseAdapter.startListening();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        shimmerFrameLayout.stopShimmer();
        if (educationFirebaseAdapter != null) {
            educationFirebaseAdapter.stopListening();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (educationFirebaseAdapter != null) {
            educationFirebaseAdapter.startListening();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (educationFirebaseAdapter != null) {
            educationFirebaseAdapter.stopListening();
        }
    }
}
