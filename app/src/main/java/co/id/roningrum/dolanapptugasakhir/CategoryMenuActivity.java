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

package co.id.roningrum.dolanapptugasakhir;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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

import co.id.roningrum.dolanapptugasakhir.adapter.CategoryViewHolder;
import co.id.roningrum.dolanapptugasakhir.handler.GPSHandler;
import co.id.roningrum.dolanapptugasakhir.handler.NetworkHelper;
import co.id.roningrum.dolanapptugasakhir.handler.PermissionHandler;
import co.id.roningrum.dolanapptugasakhir.model.TourismItem;

public class CategoryMenuActivity extends AppCompatActivity {

    private static final String REQUESTING_LOCATION_UPDATES_KEY = "12";

    private DatabaseReference tourismDBRef;
    private RecyclerView rvTourismList;
    private ShimmerFrameLayout shimmerFrameLayout;
    private FirebaseRecyclerAdapter<TourismItem, CategoryViewHolder> firebaseAdapter;

    private GPSHandler gpsHandler;
    private PermissionHandler permissionHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_category_menu);
        Toolbar mToolbar = findViewById(R.id.toolbar_top);
//        progressBar = findViewById(R.id.progressbar);
        rvTourismList = findViewById(R.id.tourism_list);
        shimmerFrameLayout = findViewById(R.id.shimmer_view_container);

        rvTourismList.setLayoutManager(new LinearLayoutManager(this));
        //    ProgressBar progressBar;
        ArrayList<TourismItem> tourismItemList = new ArrayList<>();
        setSupportActionBar(mToolbar);
//        updateValuesFromBundle(savedInstanceState);
        checkConnection();


    }

    private void checkConnection() {
        if (NetworkHelper.isConnectedToNetwork(getApplicationContext())) {
            showData();
        } else {
//            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            Toast.makeText(this, "Check your connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void showData() {
        if (havePermission()) {
            tourismDBRef = FirebaseDatabase.getInstance().getReference();
            Query query = tourismDBRef.child("Tourism");

            FirebaseRecyclerOptions<TourismItem> options = new FirebaseRecyclerOptions.Builder<TourismItem>()
                    .setQuery(query, TourismItem.class)
                    .build();
            firebaseAdapter = new FirebaseRecyclerAdapter<TourismItem, CategoryViewHolder>(options) {

                @NonNull
                @Override
                public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                    View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_category_menu, viewGroup, false);
                    return new CategoryViewHolder(view);
                }

                @Override
                public void onBindViewHolder(@NonNull final CategoryViewHolder holder, int position, @NonNull final TourismItem model) {


                    final DatabaseReference touristRef = getRef(position);
                    final String wiskey = touristRef.getKey();

                    gpsHandler = new GPSHandler(getApplicationContext());
                    if (gpsHandler.isCanGetLocation()) {
                        double latitude = gpsHandler.getLatitude();
                        double longitude = gpsHandler.getLongitude();

                        Log.i("Message", "CurLoc :" + latitude + "," + longitude);

                        holder.showTourismData(model, longitude, latitude);
                        holder.setOnClickListener(new CategoryViewHolder.ClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Intent intent = new Intent(getApplicationContext(), DetailCategoryActivity.class);
                                intent.putExtra(DetailCategoryActivity.EXTRA_WISATA_KEY, wiskey);
                                startActivity(intent);
                            }
                        });

                    } else {
                        gpsHandler.showSettingsAlert();
                    }

                }
            };
            firebaseAdapter.notifyDataSetChanged();
            rvTourismList.setAdapter(firebaseAdapter);
        }


    }

//    private void updateValuesFromBundle(Bundle savedInstanceState) {
//        if (savedInstanceState == null) {
//            return;
//        }
//
//        // Update the value of requestingLocationUpdates from the Bundle.
//        if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
//            requestingLocationUpdates = savedInstanceState.getBoolean(
//                    REQUESTING_LOCATION_UPDATES_KEY);
//        }
//
//    }

    @Override
    protected void onResume() {
        super.onResume();
        shimmerFrameLayout.startShimmer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (firebaseAdapter != null) {
            shimmerFrameLayout.stopShimmer();
            firebaseAdapter.stopListening();
        }
    }

    private boolean havePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            permissionHandler = PermissionHandler.getInstance(this);
            if (permissionHandler.isAllPermissionAvailable()) {

            } else {
                permissionHandler.setActivity(this);
                permissionHandler.deniedPermission();
            }
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

//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        //stop location update
////        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
//    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
//        MenuItem mSearch = menu.findItem(R.id.searchMenu);
//        SearchView mSearchView = (SearchView) mSearch.getActionView();
//        mSearchView.setQueryHint("Search");
//        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String searchText) {
//                firebaseSearch(searchText);
//                shimmerFrameLayout.clearAnimation();
//                return false;
//            }
//        });
//        return super.onCreateOptionsMenu(menu);
//    }

    private void firebaseSearch(String searchText) {
        Query firebaseSearchquery = tourismDBRef.child("Tourism").orderByKey().startAt(searchText).endAt(searchText + "\uf8ff");
        FirebaseRecyclerOptions<TourismItem> options = new FirebaseRecyclerOptions.Builder<TourismItem>()
                .setQuery(firebaseSearchquery, TourismItem.class)
                .setLifecycleOwner(this)
                .build();
        firebaseAdapter = new FirebaseRecyclerAdapter<TourismItem, CategoryViewHolder>(options) {

            @NonNull
            @Override
            public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_category_menu, viewGroup, false);
                return new CategoryViewHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull final CategoryViewHolder holder, int position, @NonNull final TourismItem model) {
                final DatabaseReference touristRef = getRef(position);
                final String wiskey = touristRef.getKey();
                gpsHandler = new GPSHandler(getApplicationContext());
                if (gpsHandler.isCanGetLocation()) {
                    double lattitude = gpsHandler.getLatitude();
                    double longitude = gpsHandler.getLongitude();

                    Log.i("Message", "CurLoc :" + lattitude + "," + longitude);

                    holder.showTourismData(model, longitude, lattitude);
                    holder.setOnClickListener(new CategoryViewHolder.ClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Intent intent = new Intent(getApplicationContext(), DetailCategoryActivity.class);
                            intent.putExtra(DetailCategoryActivity.EXTRA_WISATA_KEY, wiskey);
                            startActivity(intent);
                        }
                    });

                }


            }

        };
        rvTourismList.setAdapter(firebaseAdapter);
//        shimmerFrameLayout.stopShimmerAnimation();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.petaMenu) {
            startActivity(new Intent(CategoryMenuActivity.this, CategoryMapActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        boolean requestingLocationUpdates = true;
        outState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY,
                requestingLocationUpdates);
        // ...
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        progressBar.setVisibility(View.VISIBLE);
        if (firebaseAdapter != null) {
            firebaseAdapter.startListening();
//            Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    shimmerFrameLayout.startShimmerAnimation();
//                    firebaseAdapter.startListening();
//                }
//            }, 4000);

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
//        progressBar.setVisibility(View.GONE);
        if (firebaseAdapter != null) {
//            shimmerFrameLayout.stopShimmerAnimation();
            firebaseAdapter.stopListening();
        }

    }
}
