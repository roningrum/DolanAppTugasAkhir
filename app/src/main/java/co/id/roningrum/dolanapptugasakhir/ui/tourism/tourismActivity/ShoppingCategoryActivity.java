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
import co.id.roningrum.dolanapptugasakhir.model.Tourism;
import co.id.roningrum.dolanapptugasakhir.ui.tourism.tourismDetailActivity.DetailShoppingActivity;
import co.id.roningrum.dolanapptugasakhir.ui.tourism.tourismMapActivity.ShoppingCategoryMap;
import co.id.roningrum.dolanapptugasakhir.viewholderActivity.tourism.ShoppingViewHolder;

public class ShoppingCategoryActivity extends AppCompatActivity {

    private RecyclerView rvShoppingList;
    private ShimmerFrameLayout shimmerFrameLayout;
    private FirebaseRecyclerAdapter<Tourism, ShoppingViewHolder> shoppingFirebaseAdapter;

    private GPSHandler gpsHandler;
    private PermissionHandler permissionHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_shopping);
        rvShoppingList = findViewById(R.id.tourism_shopping_list);
        Toolbar toolbarShopping = findViewById(R.id.toolbar_top_shopping);
        shimmerFrameLayout = findViewById(R.id.shimmer_view_container);
        rvShoppingList.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<Tourism> tourisms = new ArrayList<>();
        checkConnection();
        setSupportActionBar(toolbarShopping);
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
            DatabaseReference shoppingCategoryDB = FirebaseDatabase.getInstance().getReference();
            Query shoppingQuery = shoppingCategoryDB.child("Tourism").orderByChild("category_tourism").equalTo("belanja");
            FirebaseRecyclerOptions<Tourism> shoppingOptions = new FirebaseRecyclerOptions.Builder<Tourism>()
                    .setQuery(shoppingQuery, Tourism.class)
                    .build();
            shoppingFirebaseAdapter = new FirebaseRecyclerAdapter<Tourism, ShoppingViewHolder>(shoppingOptions) {
                @Override
                protected void onBindViewHolder(@NonNull ShoppingViewHolder holder, int position, @NonNull Tourism model) {
                    final DatabaseReference shoppingCategoryRef = getRef(position);
                    final String shoppingKey = shoppingCategoryRef.getKey();

                    gpsHandler = new GPSHandler(getApplicationContext());
                    if (gpsHandler.isCanGetLocation()) {
                        double latitude = gpsHandler.getLatitude();
                        double longitude = gpsHandler.getLongitude();

                        Log.i("Message", "CurLoc :" + latitude + "," + longitude);

                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);

                        holder.showShoppingTourismData(model, latitude, longitude);
                        holder.setOnClickListener(new ShoppingViewHolder.ClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Intent intent = new Intent(getApplicationContext(), DetailShoppingActivity.class);
                                intent.putExtra(DetailShoppingActivity.EXTRA_WISATA_KEY, shoppingKey);
                                startActivity(intent);
                            }
                        });

                    } else {
                        gpsHandler.showSettingsAlert();
                    }
                }

                @NonNull
                @Override
                public ShoppingViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                    View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_menu_shopping_category_tourism, viewGroup, false);
                    return new ShoppingViewHolder(view);
                }
            };
            shoppingFirebaseAdapter.notifyDataSetChanged();
            rvShoppingList.setAdapter(shoppingFirebaseAdapter);

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
            startActivity(new Intent(ShoppingCategoryActivity.this, ShoppingCategoryMap.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        shimmerFrameLayout.startShimmer();
        shoppingFirebaseAdapter.startListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
        shimmerFrameLayout.stopShimmer();
        shoppingFirebaseAdapter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (shoppingFirebaseAdapter != null) {
            shoppingFirebaseAdapter.startListening();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (shoppingFirebaseAdapter != null) {
            shoppingFirebaseAdapter.stopListening();
        }
    }

}
