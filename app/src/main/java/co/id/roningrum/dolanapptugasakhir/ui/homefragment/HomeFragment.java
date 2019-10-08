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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.model.Herritage;
import co.id.roningrum.dolanapptugasakhir.model.Tourism;
import co.id.roningrum.dolanapptugasakhir.ui.homeactivity.AllCategoryActivity;
import co.id.roningrum.dolanapptugasakhir.ui.hotel.HotelActivity;
import co.id.roningrum.dolanapptugasakhir.ui.tourism.tourismActivity.TourismNatureActivity;
import co.id.roningrum.dolanapptugasakhir.ui.tourism.tourismActivity.TourismRecreationActivity;
import co.id.roningrum.dolanapptugasakhir.ui.tourism.tourismActivity.TourismShoppingActivity;
import co.id.roningrum.dolanapptugasakhir.ui.tourism.tourismActivity.TourismVillageActivity;
import co.id.roningrum.dolanapptugasakhir.ui.transportation.transportationActivity.TransportationTrainActivity;
import co.id.roningrum.dolanapptugasakhir.ui.transportation.transportationActivity.TrasportationAirportActivity;
import co.id.roningrum.dolanapptugasakhir.viewholderActivity.ViewHolderHome.FoodRecommendationViewHolder;
import co.id.roningrum.dolanapptugasakhir.viewholderActivity.ViewHolderHome.HerritageHomeViewHolder;
import co.id.roningrum.dolanapptugasakhir.viewholderActivity.ViewHolderHome.TouristRecommendationViewHolder;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {
    private RecyclerView rvTouristRecommendation, rvFoodRecommendation, rvHeriitageHome;

    private FirebaseRecyclerAdapter<Tourism, FoodRecommendationViewHolder> foodRecommendAdapter;
    private FirebaseRecyclerAdapter<Tourism, TouristRecommendationViewHolder> touristRecommendAdapter;
    private FirebaseRecyclerAdapter<Herritage, HerritageHomeViewHolder> herritageItemAdapter;
    private DatabaseReference recommendationDB;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        TextView tvAllCategory = view.findViewById(R.id.tv_subtitle_category_main_menu);
        LinearLayout natureMenu = view.findViewById(R.id.ln_alam_tour_home_menu);
        LinearLayout entertainMenu = view.findViewById(R.id.ln_hiburan_tour_home_menu);
        LinearLayout shoppingMenu = view.findViewById(R.id.ln_belanja_tour_home_menu);
        LinearLayout villageMenu = view.findViewById(R.id.ln_desa_tour_home_menu);
        LinearLayout airportMenu = view.findViewById(R.id.ln_bandara_public_home);
        LinearLayout hotelMenu = view.findViewById(R.id.ln_hotel_public_home);
        LinearLayout trainMenu = view.findViewById(R.id.ln_train_public_home);
//        LinearLayout busMenu = view.findViewById(R.id.ln_bus_public_home);

        tvAllCategory.setOnClickListener(this);
        natureMenu.setOnClickListener(this);
        entertainMenu.setOnClickListener(this);
        shoppingMenu.setOnClickListener(this);
        villageMenu.setOnClickListener(this);
        airportMenu.setOnClickListener(this);
        hotelMenu.setOnClickListener(this);
        trainMenu.setOnClickListener(this);
//        busMenu.setOnClickListener(this);

        //rv
        rvTouristRecommendation = view.findViewById(R.id.rv_tourism_recommendation);
        rvFoodRecommendation = view.findViewById(R.id.rv_food_recommendation);
        rvHeriitageHome = view.findViewById(R.id.rv_herritage);

        rvFoodRecommendation.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvTouristRecommendation.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvHeriitageHome.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        recommendationDB = FirebaseDatabase.getInstance().getReference();
        showFoodReccomendation();
        showTourismRecommendation();
        showHerritageHome();
        return view;
    }

    private void showHerritageHome() {
        Query herritageQuery = recommendationDB.child("HerritageSemarang");
        FirebaseRecyclerOptions<Herritage> herritageOption = new FirebaseRecyclerOptions.Builder<Herritage>()
                .setQuery(herritageQuery, Herritage.class)
                .build();
        herritageItemAdapter = new FirebaseRecyclerAdapter<Herritage, HerritageHomeViewHolder>(herritageOption) {
            @Override
            protected void onBindViewHolder(@NonNull HerritageHomeViewHolder holder, int position, @NonNull Herritage model) {
                holder.showHerritageData(model);
            }

            @NonNull
            @Override
            public HerritageHomeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return new HerritageHomeViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_herritage_home, viewGroup, false));
            }
        };
        herritageItemAdapter.notifyDataSetChanged();
        rvHeriitageHome.setAdapter(herritageItemAdapter);
    }

    private void showTourismRecommendation() {
        Query tourismRecommendQuery = recommendationDB.child("BestPlaceTourism");
        FirebaseRecyclerOptions<Tourism> tourismOption = new FirebaseRecyclerOptions.Builder<Tourism>()
                .setQuery(tourismRecommendQuery, Tourism.class)
                .build();
        touristRecommendAdapter = new FirebaseRecyclerAdapter<Tourism, TouristRecommendationViewHolder>(tourismOption) {
            @Override
            protected void onBindViewHolder(@NonNull TouristRecommendationViewHolder holder, int position, @NonNull Tourism model) {
                holder.showRecommendationTourismData(model);
            }

            @NonNull
            @Override
            public TouristRecommendationViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return new TouristRecommendationViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_content_tourism_recommend_home, viewGroup, false));
            }
        };
        touristRecommendAdapter.notifyDataSetChanged();
        rvTouristRecommendation.setAdapter(touristRecommendAdapter);
    }

    private void showFoodReccomendation() {
        Query foodRecommendedQuery = recommendationDB.child("Tourism").orderByChild("category_tourism").equalTo("kuliner").limitToFirst(5);
        FirebaseRecyclerOptions<Tourism> foodOption = new FirebaseRecyclerOptions.Builder<Tourism>()
                .setQuery(foodRecommendedQuery, Tourism.class)
                .build();
        foodRecommendAdapter = new FirebaseRecyclerAdapter<Tourism, FoodRecommendationViewHolder>(foodOption) {
            @Override
            protected void onBindViewHolder(@NonNull FoodRecommendationViewHolder holder, int position, @NonNull Tourism model) {
                holder.showFoodRecommendationTourismData(model);
            }

            @NonNull
            @Override
            public FoodRecommendationViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return new FoodRecommendationViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_food_recommendation_home, viewGroup, false));
            }
        };
        foodRecommendAdapter.notifyDataSetChanged();
        rvFoodRecommendation.setAdapter(foodRecommendAdapter);
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
                startActivity(new Intent(getContext(), TrasportationAirportActivity.class));
                break;
//            case R.id.ln_bus_public_home:
//                startActivity(new Intent(getContext(), TransportationBusActivity.class));
//                break;
            case R.id.ln_train_public_home:
                startActivity(new Intent(getContext(), TransportationTrainActivity.class));
                break;
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        if (touristRecommendAdapter != null) {
            touristRecommendAdapter.startListening();
        }
        if (foodRecommendAdapter != null) {
            foodRecommendAdapter.startListening();
        }
        if (herritageItemAdapter != null) {
            herritageItemAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (touristRecommendAdapter != null) {
            touristRecommendAdapter.stopListening();
        }
        if (foodRecommendAdapter != null) {
            foodRecommendAdapter.stopListening();
        }
        if (herritageItemAdapter != null) {
            herritageItemAdapter.stopListening();
        }
    }
}
