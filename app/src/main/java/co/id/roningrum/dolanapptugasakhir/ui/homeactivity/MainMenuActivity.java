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

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.ui.homefragment.BookmarkFragment;
import co.id.roningrum.dolanapptugasakhir.ui.homefragment.HomeFragment;
import co.id.roningrum.dolanapptugasakhir.ui.homefragment.ProfileFragment;

public class MainMenuActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        loadFragment(new HomeFragment());

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
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
//            case R.id.nearbyMenu:
//                fragment = new NearbyFragment();
//                break;
            case R.id.bookmarkMenu:
                fragment = new BookmarkFragment();
                break;
            case R.id.profileMenu:
                fragment = new ProfileFragment();
                break;
        }
        return loadFragment(fragment);
    }
}
