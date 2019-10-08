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

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.ui.gasstation.GasStationCategory;
import co.id.roningrum.dolanapptugasakhir.ui.hospital.HospitalCategory;
import co.id.roningrum.dolanapptugasakhir.ui.hotel.HotelActivity;
import co.id.roningrum.dolanapptugasakhir.ui.police.PoliceCategory;
import co.id.roningrum.dolanapptugasakhir.ui.tourism.tourismActivity.TourismEducationActivity;
import co.id.roningrum.dolanapptugasakhir.ui.tourism.tourismActivity.TourismFoodActivity;
import co.id.roningrum.dolanapptugasakhir.ui.tourism.tourismActivity.TourismHistoryActivity;
import co.id.roningrum.dolanapptugasakhir.ui.tourism.tourismActivity.TourismNatureActivity;
import co.id.roningrum.dolanapptugasakhir.ui.tourism.tourismActivity.TourismRecreationActivity;
import co.id.roningrum.dolanapptugasakhir.ui.tourism.tourismActivity.TourismReligiActivity;
import co.id.roningrum.dolanapptugasakhir.ui.tourism.tourismActivity.TourismShoppingActivity;
import co.id.roningrum.dolanapptugasakhir.ui.tourism.tourismActivity.TourismVillageActivity;
import co.id.roningrum.dolanapptugasakhir.ui.tourism.tourismActivity.TourismWaterActivity;
import co.id.roningrum.dolanapptugasakhir.ui.transportation.transportationActivity.AirportCategoryActivity;
import co.id.roningrum.dolanapptugasakhir.ui.transportation.transportationActivity.BusCategoryActivity;
import co.id.roningrum.dolanapptugasakhir.ui.transportation.transportationActivity.ShipCategoryActivity;
import co.id.roningrum.dolanapptugasakhir.ui.transportation.transportationActivity.TrainCategoryActivity;

public class AllCategoryActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_all_menu);
        LinearLayout natureMenu = findViewById(R.id.ln_nature_tour);
        LinearLayout entertaintMenu = findViewById(R.id.ln_entertain_tour);
        LinearLayout shoppingMenu = findViewById(R.id.ln_belanja_tour);
        LinearLayout villageMenu = findViewById(R.id.ln_desa_tour);
        LinearLayout foodMenu = findViewById(R.id.ln_kuliner_tour);
        LinearLayout educationMenu = findViewById(R.id.ln_edukasi_tour);
        LinearLayout historyMenu = findViewById(R.id.ln_sejarah_tour);
        LinearLayout waterMenu = findViewById(R.id.ln_air_tour);
        LinearLayout religiMenu = findViewById(R.id.ln_religi_tour);
        LinearLayout hotelMenu = findViewById(R.id.ln_hotel_public);
        LinearLayout airportMenu = findViewById(R.id.ln_bandara_public);
        LinearLayout busMenu = findViewById(R.id.ln_bus_public);
        LinearLayout trainMenu = findViewById(R.id.ln_train_public);
        LinearLayout shipMenu = findViewById(R.id.ln_harbor_tour);
        LinearLayout spbuMenu = findViewById(R.id.ln_spbu_public);
        LinearLayout policeMenu = findViewById(R.id.ln_police_public);
        LinearLayout hospitalMenu = findViewById(R.id.ln_hospital_public);

        natureMenu.setOnClickListener(this);
        entertaintMenu.setOnClickListener(this);
        educationMenu.setOnClickListener(this);
        villageMenu.setOnClickListener(this);
        foodMenu.setOnClickListener(this);
        educationMenu.setOnClickListener(this);
        historyMenu.setOnClickListener(this);
        waterMenu.setOnClickListener(this);
        religiMenu.setOnClickListener(this);
        shoppingMenu.setOnClickListener(this);
        hotelMenu.setOnClickListener(this);
        airportMenu.setOnClickListener(this);
        busMenu.setOnClickListener(this);
        trainMenu.setOnClickListener(this);
        shipMenu.setOnClickListener(this);
        spbuMenu.setOnClickListener(this);
        policeMenu.setOnClickListener(this);
        hospitalMenu.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ln_nature_tour:
                startActivity(new Intent(AllCategoryActivity.this, TourismNatureActivity.class));
                break;
            case R.id.ln_entertain_tour:
                startActivity(new Intent(AllCategoryActivity.this, TourismRecreationActivity.class));
                break;
            case R.id.ln_belanja_tour:
                startActivity(new Intent(AllCategoryActivity.this, TourismShoppingActivity.class));
                break;
            case R.id.ln_desa_tour:
                startActivity(new Intent(AllCategoryActivity.this, TourismVillageActivity.class));
                break;
            case R.id.ln_kuliner_tour:
                startActivity(new Intent(AllCategoryActivity.this, TourismFoodActivity.class));
                break;
            case R.id.ln_edukasi_tour:
                startActivity(new Intent(AllCategoryActivity.this, TourismEducationActivity.class));
                break;
            case R.id.ln_air_tour:
                startActivity(new Intent(AllCategoryActivity.this, TourismWaterActivity.class));
                break;
            case R.id.ln_religi_tour:
                startActivity(new Intent(AllCategoryActivity.this, TourismReligiActivity.class));
                break;
            case R.id.ln_sejarah_tour:
                startActivity(new Intent(AllCategoryActivity.this, TourismHistoryActivity.class));
                break;
            case R.id.ln_hotel_public:
                startActivity(new Intent(AllCategoryActivity.this, HotelActivity.class));
                break;
            case R.id.ln_bandara_public:
                startActivity(new Intent(AllCategoryActivity.this, AirportCategoryActivity.class));
                break;
            case R.id.ln_bus_public:
                startActivity(new Intent(AllCategoryActivity.this, BusCategoryActivity.class));
                break;
            case R.id.ln_train_public:
                startActivity(new Intent(AllCategoryActivity.this, TrainCategoryActivity.class));
                break;
            case R.id.ln_harbor_tour:
                startActivity(new Intent(AllCategoryActivity.this, ShipCategoryActivity.class));
                break;
            case R.id.ln_spbu_public:
                startActivity(new Intent(AllCategoryActivity.this, GasStationCategory.class));
                break;
            case R.id.ln_police_public:
                startActivity(new Intent(AllCategoryActivity.this, PoliceCategory.class));
                break;

            case R.id.ln_hospital_public:
                startActivity(new Intent(AllCategoryActivity.this, HospitalCategory.class));
                break;


        }
    }
}
