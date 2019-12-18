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

package co.id.roningrum.dolanapptugasakhir.ui.homeactivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Arrays;

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.favorite.FavoriteFragment;
import co.id.roningrum.dolanapptugasakhir.handler.LocationPermissionHandler;
import co.id.roningrum.dolanapptugasakhir.ui.homefragment.HomeFragment;
import co.id.roningrum.dolanapptugasakhir.ui.homefragment.ProfileFragment;

public class MainMenuActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private LocationPermissionHandler locationPermissionHandler;
    private static final String SELECTED_MENU = "selected_menu";
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        loadFragment(new HomeFragment());
        havePermission();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        if (savedInstanceState != null) {
            savedInstanceState.getInt(SELECTED_MENU);
        } else {
            bottomNavigationView.setSelectedItemId(R.id.homeMenu);
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

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.frame_layout_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;
        switch (menuItem.getItemId()) {
            case R.id.homeMenu:
                fragment = new HomeFragment();
                break;
            case R.id.bookmarkMenu:
                fragment = new FavoriteFragment();
                break;
            case R.id.profileMenu:
                fragment = new ProfileFragment();
                break;
        }
        return loadFragment(fragment);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i : grantResults) {
            if (i == PackageManager.PERMISSION_GRANTED) {
                Log.d("test", "Permission" + Arrays.toString(permissions) + "Success");
            } else {
                locationPermissionHandler.deniedPermission(Manifest.permission.ACCESS_FINE_LOCATION);
                locationPermissionHandler.deniedPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_MENU, bottomNavigationView.getSelectedItemId());
    }
}
