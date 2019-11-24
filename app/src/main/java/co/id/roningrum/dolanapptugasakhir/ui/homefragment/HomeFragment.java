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

package co.id.roningrum.dolanapptugasakhir.ui.homefragment;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.adapter.SlideAdapterExample;
import co.id.roningrum.dolanapptugasakhir.ui.homeactivity.AllCategoryActivity;
import co.id.roningrum.dolanapptugasakhir.ui.hotel.HotelActivity;
import co.id.roningrum.dolanapptugasakhir.ui.tourism.tourismActivity.TourismNatureActivity;
import co.id.roningrum.dolanapptugasakhir.ui.tourism.tourismActivity.TourismRecreationActivity;
import co.id.roningrum.dolanapptugasakhir.ui.tourism.tourismActivity.TourismShoppingActivity;
import co.id.roningrum.dolanapptugasakhir.ui.tourism.tourismActivity.TourismVillageActivity;
import co.id.roningrum.dolanapptugasakhir.ui.transportation.transportationActivity.TransportationAirportActivity;
import co.id.roningrum.dolanapptugasakhir.ui.transportation.transportationActivity.TransportationBusActivity;
import co.id.roningrum.dolanapptugasakhir.ui.transportation.transportationActivity.TransportationTrainActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView tvAllCategory = view.findViewById(R.id.tv_subtitle_category_main_menu);
        LinearLayout natureMenu = view.findViewById(R.id.ln_alam_tour_home_menu);
        LinearLayout entertainMenu = view.findViewById(R.id.ln_hiburan_tour_home_menu);
        LinearLayout shoppingMenu = view.findViewById(R.id.ln_belanja_tour_home_menu);
        LinearLayout villageMenu = view.findViewById(R.id.ln_desa_tour_home_menu);
        LinearLayout airportMenu = view.findViewById(R.id.ln_bandara_public_home);
        LinearLayout hotelMenu = view.findViewById(R.id.ln_hotel_public_home);
        LinearLayout trainMenu = view.findViewById(R.id.ln_train_public_home);
        LinearLayout busMenu = view.findViewById(R.id.ln_bus_public_home);

        SliderView sliderView = view.findViewById(R.id.sliderView);
        SlideAdapterExample adapterExample = new SlideAdapterExample();

        sliderView.setSliderAdapter(adapterExample);
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setIndicatorAnimation(IndicatorAnimations.WORM);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(5);
        sliderView.startAutoCycle();

        tvAllCategory.setOnClickListener(this);
        natureMenu.setOnClickListener(this);
        entertainMenu.setOnClickListener(this);
        shoppingMenu.setOnClickListener(this);
        villageMenu.setOnClickListener(this);
        airportMenu.setOnClickListener(this);
        hotelMenu.setOnClickListener(this);
        trainMenu.setOnClickListener(this);
        busMenu.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_subtitle_category_main_menu:
                startActivity(new Intent(getContext(), AllCategoryActivity.class));
                break;
            case R.id.ln_alam_tour_home_menu:
                startActivity(new Intent(getContext(), TourismNatureActivity.class));
                break;
            case R.id.ln_hiburan_tour_home_menu:
                startActivity(new Intent(getContext(), TourismRecreationActivity.class));
                break;
            case R.id.ln_belanja_tour_home_menu:
                startActivity(new Intent(getContext(), TourismShoppingActivity.class));
                break;
            case R.id.ln_desa_tour_home_menu:
                startActivity(new Intent(getContext(), TourismVillageActivity.class));
                break;
            case R.id.ln_hotel_public_home:
                startActivity(new Intent(getContext(), HotelActivity.class));
                break;
            case R.id.ln_bandara_public_home:
                startActivity(new Intent(getContext(), TransportationAirportActivity.class));
                break;
            case R.id.ln_bus_public_home:
                startActivity(new Intent(getContext(), TransportationBusActivity.class));
                break;
            case R.id.ln_train_public_home:
                startActivity(new Intent(getContext(), TransportationTrainActivity.class));
                break;
        }

    }

}
