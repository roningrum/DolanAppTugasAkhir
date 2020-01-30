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

package co.id.roningrum.dolanapptugasakhir.ui.hospital;

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
import co.id.roningrum.dolanapptugasakhir.model.Hospital;
import co.id.roningrum.dolanapptugasakhir.ui.adapter.hospital.HospitalAdapter;
import co.id.roningrum.dolanapptugasakhir.ui.adapter.hospital.HospitalClickCallback;
import co.id.roningrum.dolanapptugasakhir.util.Utils;

public class HospitalCategory extends AppCompatActivity {

    private RecyclerView rvHospitalList;
    private ShimmerFrameLayout shimmerFrameLayout;
    private HospitalAdapter hospitalAdapter;
    private ArrayList<Hospital> hospitals = new ArrayList<>();

    private LocationPermissionHandler locationPermissionHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_hospital);
        rvHospitalList = findViewById(R.id.rv_hospital_list);
        shimmerFrameLayout = findViewById(R.id.shimmer_view_container);
        Toolbar toolbarHospital = findViewById(R.id.toolbar_top_hospital);
        rvHospitalList.setLayoutManager(new LinearLayoutManager(this));
        setSupportActionBar(toolbarHospital);
        checkConnection();
    }

    private void checkConnection() {
        if (Utils.isConnectedToNetwork(getApplicationContext())) {
            showLoading(false);
            showHospitalData();
        } else {
            showLoading(true);
            Toast.makeText(this, "Check your connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void showHospitalData() {
        if (havePermission()) {
            Query hospitalQuery = FirebaseQuery.getHospital();
            hospitalQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        Hospital hospital = dataSnapshot1.getValue(Hospital.class);
                        hospitals.add(hospital);
                    }
                    hospitalAdapter = new HospitalAdapter();
                    rvHospitalList.setAdapter(hospitalAdapter);
                    hospitalAdapter.setHospitalList(hospitals);
                    hospitalAdapter.setHospitalClickCallback(new HospitalClickCallback() {
                        @Override
                        public void onItemCallback(Hospital hospital) {
                            String hospitalKey = hospital.getId();
                            Intent intent = new Intent(HospitalCategory.this, HospitalDetail.class);
                            intent.putExtra(HospitalDetail.EXTRA_HOSPITAL_KEY, hospitalKey);
                            startActivity(intent);
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("Database Error", "" + databaseError.getMessage());
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
            startActivity(new Intent(HospitalCategory.this, HospitalMap.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showLoading(boolean state) {
        if (state) {
            shimmerFrameLayout.startShimmer();
        } else {
            shimmerFrameLayout.stopShimmer();
        }
    }
}
