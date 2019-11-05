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

package co.id.roningrum.dolanapptugasakhir.ui.tourism.tourismActivity;

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
import androidx.constraintlayout.widget.ConstraintLayout;
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
import co.id.roningrum.dolanapptugasakhir.adapter.TourismAdapter;
import co.id.roningrum.dolanapptugasakhir.controller.FirebaseConstant;
import co.id.roningrum.dolanapptugasakhir.handler.LocationPermissionHandler;
import co.id.roningrum.dolanapptugasakhir.handler.NetworkHelper;
import co.id.roningrum.dolanapptugasakhir.model.Tourism;
import co.id.roningrum.dolanapptugasakhir.ui.tourism.tourismDetailActivity.TourismNatureDetail;
import co.id.roningrum.dolanapptugasakhir.ui.tourism.tourismMapActivity.TourismNatureMaps;

public class TourismNatureActivity extends AppCompatActivity {
    private RecyclerView rvNatureList;
    private ShimmerFrameLayout shimmerFrameLayout;
    private TourismAdapter tourismAdapter;
    private ArrayList<Tourism> tourisms = new ArrayList<>();

    private LocationPermissionHandler locationPermissionHandler;
    protected ConstraintLayout layoutUnavailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_nature);
        rvNatureList = findViewById(R.id.tourism_nature_list);
        Toolbar toolbarNature = findViewById(R.id.toolbar_top_nature);
        layoutUnavailable = findViewById(R.id.layout_connect);
        shimmerFrameLayout = findViewById(R.id.shimmer_view_container);
        rvNatureList.setLayoutManager(new LinearLayoutManager(this));
        setSupportActionBar(toolbarNature);
        checkConnection();
    }

    private void checkConnection() {
        if (NetworkHelper.isConnectedToNetwork(getApplicationContext())) {
            showLoading(false);
            showData();
        } else {
            showLoading(true);
            Toast.makeText(this, "Check your connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void showData() {
        if (havePermission()) {
            Query natureQuery = FirebaseConstant.getTourismAlam();
            natureQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        Tourism tourism = dataSnapshot1.getValue(Tourism.class);
                        tourisms.add(tourism);
                    }
                    tourismAdapter = new TourismAdapter();
                    tourismAdapter.setTourismList(tourisms);
                    tourismAdapter.setOnItemClickCallback(new TourismAdapter.OnItemClickCallback() {
                        @Override
                        public void onItemClicked(Tourism tourism) {
                            String tourismKey = tourism.getId();
                            Intent intent = new Intent(TourismNatureActivity.this, TourismNatureDetail.class);
                            intent.putExtra(TourismNatureDetail.EXTRA_WISATA_KEY, tourismKey);
                            Log.d("Check id", "id :" + tourismKey);
                            startActivity(intent);
                        }
                    });
                    rvNatureList.setAdapter(tourismAdapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("Error DatabaseError", " " + databaseError.getMessage());

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
        int id = item.getItemId();
        if (id == R.id.petaMenu) {
            startActivity(new Intent(TourismNatureActivity.this, TourismNatureMaps.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showLoading(Boolean state) {
        if (state) {
            shimmerFrameLayout.startShimmer();
        } else {
            shimmerFrameLayout.stopShimmer();
        }
    }

}
