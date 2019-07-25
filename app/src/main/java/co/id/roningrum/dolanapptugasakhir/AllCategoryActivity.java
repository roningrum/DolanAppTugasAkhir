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

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import co.id.roningrum.dolanapptugasakhir.hotel.HotelActivity;
import co.id.roningrum.dolanapptugasakhir.tourism.education.EducationCategoryActivity;
import co.id.roningrum.dolanapptugasakhir.tourism.food.FoodCategoryActivity;
import co.id.roningrum.dolanapptugasakhir.tourism.history.HistoryCategoryActivity;
import co.id.roningrum.dolanapptugasakhir.tourism.nature.NatureCategoryActivity;
import co.id.roningrum.dolanapptugasakhir.tourism.recreation.RecreationCategoryActivity;
import co.id.roningrum.dolanapptugasakhir.tourism.religi.ReligiCategoryActivity;
import co.id.roningrum.dolanapptugasakhir.tourism.shopping.ShoppingCategoryActivity;
import co.id.roningrum.dolanapptugasakhir.tourism.village.VillageCategoryActivity;
import co.id.roningrum.dolanapptugasakhir.tourism.water.WaterCategoryActivity;
import co.id.roningrum.dolanapptugasakhir.transportation.airport.AirportActivity;
import co.id.roningrum.dolanapptugasakhir.transportation.bus.BusActivity;
import co.id.roningrum.dolanapptugasakhir.transportation.train.TrainActivity;

public class AllCategoryActivity extends AppCompatActivity implements View.OnClickListener {
    LinearLayout natureMenu, entertaintMenu, shoppingMenu, villageMenu,
            foodMenu, educationMenu, historyMenu, waterMenu,
            religiMenu, hotelMenu, airportMenu, busMenu,
            trainMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_category);
        natureMenu = findViewById(R.id.ln_nature_tour);
        entertaintMenu = findViewById(R.id.ln_entertain_tour);
        shoppingMenu = findViewById(R.id.ln_belanja_tour);
        villageMenu = findViewById(R.id.ln_desa_tour);
        foodMenu = findViewById(R.id.ln_kuliner_tour);
        educationMenu = findViewById(R.id.ln_edukasi_tour);
        historyMenu = findViewById(R.id.ln_sejarah_tour);
        waterMenu = findViewById(R.id.ln_air_tour);
        religiMenu = findViewById(R.id.ln_religi_tour);
        hotelMenu = findViewById(R.id.ln_hotel_public);
        airportMenu = findViewById(R.id.ln_bandara_public);
        busMenu = findViewById(R.id.ln_bus_public);
        trainMenu = findViewById(R.id.ln_train_public);

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

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ln_nature_tour:
                startActivity(new Intent(AllCategoryActivity.this, NatureCategoryActivity.class));
                break;
            case R.id.ln_entertain_tour:
                startActivity(new Intent(AllCategoryActivity.this, RecreationCategoryActivity.class));
                break;
            case R.id.ln_belanja_tour:
                startActivity(new Intent(AllCategoryActivity.this, ShoppingCategoryActivity.class));
                break;
            case R.id.ln_desa_tour:
                startActivity(new Intent(AllCategoryActivity.this, VillageCategoryActivity.class));
                break;
            case R.id.ln_kuliner_tour:
                startActivity(new Intent(AllCategoryActivity.this, FoodCategoryActivity.class));
                break;
            case R.id.ln_edukasi_tour:
                startActivity(new Intent(AllCategoryActivity.this, EducationCategoryActivity.class));
                break;
            case R.id.ln_air_tour:
                startActivity(new Intent(AllCategoryActivity.this, WaterCategoryActivity.class));
                break;
            case R.id.ln_religi_tour:
                startActivity(new Intent(AllCategoryActivity.this, ReligiCategoryActivity.class));
                break;
            case R.id.ln_sejarah_tour:
                startActivity(new Intent(AllCategoryActivity.this, HistoryCategoryActivity.class));
                break;
            case R.id.ln_hotel_public:
                startActivity(new Intent(AllCategoryActivity.this, HotelActivity.class));
                break;
            case R.id.ln_bandara_public:
                startActivity(new Intent(AllCategoryActivity.this, AirportActivity.class));
                break;
            case R.id.ln_bus_public:
                startActivity(new Intent(AllCategoryActivity.this, BusActivity.class));
                break;
            case R.id.ln_train_public:
                startActivity(new Intent(AllCategoryActivity.this, TrainActivity.class));
                break;



        }
    }
}
