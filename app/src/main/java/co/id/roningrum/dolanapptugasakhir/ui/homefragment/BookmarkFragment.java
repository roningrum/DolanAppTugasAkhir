/*
 * Copyright 2020 RONINGRUM. All rights reserved.
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

package co.id.roningrum.dolanapptugasakhir.ui.homefragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.ui.adapter.ViewPagerAdapter;
import co.id.roningrum.dolanapptugasakhir.ui.favorite.FavoriteHotelFragment;
import co.id.roningrum.dolanapptugasakhir.ui.favorite.FavoriteTouristFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookmarkFragment extends Fragment {


    public BookmarkFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bookmark, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TabLayout tabBookmark = view.findViewById(R.id.tab_layout);
        ViewPager viewPager = view.findViewById(R.id.view_pager);

        ViewPagerAdapter favPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        favPagerAdapter.addFragment(new FavoriteTouristFragment(), "Wisata");
        favPagerAdapter.addFragment(new FavoriteHotelFragment(), "Hotel");

        viewPager.setAdapter(favPagerAdapter);
        tabBookmark.setupWithViewPager(viewPager);

    }
}
