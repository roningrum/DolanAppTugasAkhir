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


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.firebasequery.FirebaseQuery;
import co.id.roningrum.dolanapptugasakhir.handler.NetworkHelper;
import co.id.roningrum.dolanapptugasakhir.model.Tourism;
import co.id.roningrum.dolanapptugasakhir.model.Users;
import co.id.roningrum.dolanapptugasakhir.ui.adapter.tourism.TourismClickCallback;
import co.id.roningrum.dolanapptugasakhir.ui.adapter.tourism.TourismPopularAdapter;
import co.id.roningrum.dolanapptugasakhir.ui.homeactivity.AllCategoryActivity;
import co.id.roningrum.dolanapptugasakhir.ui.hotel.HotelActivity;
import co.id.roningrum.dolanapptugasakhir.ui.tourism.tourismActivity.TourismAlamActivity;
import co.id.roningrum.dolanapptugasakhir.ui.tourism.tourismActivity.TourismBelanjaActivity;
import co.id.roningrum.dolanapptugasakhir.ui.tourism.tourismActivity.TourismDesaActivity;
import co.id.roningrum.dolanapptugasakhir.ui.tourism.tourismActivity.TourismRekreasiActivity;
import co.id.roningrum.dolanapptugasakhir.ui.tourism.tourismDetailActivity.TourismDetailActivity;
import co.id.roningrum.dolanapptugasakhir.ui.transportation.transportationActivity.TransportationAirportActivity;
import co.id.roningrum.dolanapptugasakhir.ui.transportation.transportationActivity.TransportationTrainActivity;
import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static co.id.roningrum.dolanapptugasakhir.firebasequery.FirebaseQuery.UserRef;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {
    private static final String SHOWCASE_ID = "4";
    private TextView tvGreetApp;
    private TextView tvProfileApp;
    private CircleImageView userPhotoHome;
    private FirebaseUser homeUser;
    private RecyclerView rvTourismPopuler;
    private TourismPopularAdapter tourismAdapter;
    private ArrayList<Tourism> tourisms = new ArrayList<>();
    private ProgressBar pbLoading;
    private AppBarLayout appBarLayout;
    private ConstraintLayout bannerView;
    private CollapsingToolbarLayout collapseToolbar;

    private LinearLayout moreMenu, natureMenu, categoryMenu;

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
        tvGreetApp = view.findViewById(R.id.tv_greeting_app);
        tvProfileApp = view.findViewById(R.id.tv_user_app);
        userPhotoHome = view.findViewById(R.id.img_user_home);

        natureMenu = view.findViewById(R.id.ln_alam_tour_home_menu);
        LinearLayout entertainMenu = view.findViewById(R.id.ln_hiburan_tour_home_menu);
        LinearLayout shoppingMenu = view.findViewById(R.id.ln_belanja_tour_home_menu);
        LinearLayout villageMenu = view.findViewById(R.id.ln_desa_tour_home_menu);
        LinearLayout airportMenu = view.findViewById(R.id.ln_bandara_public_home);
        LinearLayout hotelMenu = view.findViewById(R.id.ln_hotel_public_home);
        LinearLayout trainMenu = view.findViewById(R.id.ln_train_public_home);
        moreMenu = view.findViewById(R.id.ln_more_home);
        categoryMenu = view.findViewById(R.id.layout_category);
        rvTourismPopuler = view.findViewById(R.id.rv_tourism_popular);
        pbLoading = view.findViewById(R.id.pb_loading);
        appBarLayout = view.findViewById(R.id.appbar_home);
        bannerView = view.findViewById(R.id.contentBanner);
        collapseToolbar = view.findViewById(R.id.collapseToolbar_home);

        natureMenu.setOnClickListener(this);
        entertainMenu.setOnClickListener(this);
        shoppingMenu.setOnClickListener(this);
        villageMenu.setOnClickListener(this);
        airportMenu.setOnClickListener(this);
        hotelMenu.setOnClickListener(this);
        trainMenu.setOnClickListener(this);
        moreMenu.setOnClickListener(this);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        homeUser = firebaseAuth.getCurrentUser();

        userPhotoHome.setOnClickListener(this);
        rvTourismPopuler.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));

        loadData();
        setUpAppBarLayout();
        initialTutorShowCase();


    }

    private void initialTutorShowCase() {
        new MaterialShowcaseView.Builder(getActivity())
                .setTarget(moreMenu)
                .setTitleText("Untuk melihat menu lainnya klik ini")
                .setDismissText("Ok")
                .setDelay(500)
                .withCircleShape()
                .singleUse(SHOWCASE_ID)
                .show();
    }

    private void setUpAppBarLayout() {
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset <= -bannerView.getHeight() / 2) {
                    collapseToolbar.setTitle("Beranda");
                    collapseToolbar.setCollapsedTitleTextColor(getResources().getColor(R.color.blackTextPrimary));
                } else {
                    collapseToolbar.setTitle("");
                }

            }
        });
    }

    private void loadData() {
        if (getContext() != null)
            if (NetworkHelper.isConnectedToNetwork(getContext())) {
                pbLoading.setVisibility(View.VISIBLE);
                showProfileToHome();
                showPopularTourism();
            } else {
                pbLoading.setVisibility(View.VISIBLE);
            }

    }

    private void showPopularTourism() {
        Query popularTourism = FirebaseQuery.getTourismRekreasi();
        popularTourism.limitToFirst(5).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Tourism tourism = snapshot.getValue(Tourism.class);
                    tourisms.add(tourism);
                }
                pbLoading.setVisibility(View.GONE);
                tourismAdapter = new TourismPopularAdapter();
                tourismAdapter.setTourismList(tourisms);
                tourismAdapter.setOnItemClickCallback(new TourismClickCallback() {
                    @Override
                    public void onItemClicked(Tourism tourism) {
                        String tourismKey = tourism.getId();
                        Intent intent = new Intent(getActivity(), TourismDetailActivity.class);
                        intent.putExtra(TourismDetailActivity.EXTRA_WISATA_KEY, tourismKey);
                        Log.d("Check id", "id :" + tourismKey);
                        startActivity(intent);
                    }
                });
                greetText();
                rvTourismPopuler.setAdapter(tourismAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showProfileToHome() {
        assert homeUser != null;
        UserRef.child(homeUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users users = dataSnapshot.getValue(Users.class);
                if (users != null && getActivity() != null) {
                    pbLoading.setVisibility(View.GONE);
                    String nama = users.getNama_user();
                    tvProfileApp.setText(nama);
                    if (!users.getPhoto_user().equals("")) {
                        Glide.with(getActivity()).load(users.getPhoto_user()).into(userPhotoHome);
                    } else {
                        userPhotoHome.setImageResource(R.drawable.icon_nopic);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "" + databaseError.getMessage());
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void greetText() {
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
        if (timeOfDay < 12) {
            tvGreetApp.setText("Selamat Pagi");
        } else if (timeOfDay < 14) {
            tvGreetApp.setText("Selamat Siang");
        } else if (timeOfDay < 17) {
            tvGreetApp.setText("Selamat Sore");
        } else {
            tvGreetApp.setText("Selamat Malam");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ln_alam_tour_home_menu:
                startActivity(new Intent(getContext(), TourismAlamActivity.class));
                break;
            case R.id.ln_hiburan_tour_home_menu:
                startActivity(new Intent(getContext(), TourismRekreasiActivity.class));
                break;
            case R.id.ln_belanja_tour_home_menu:
                startActivity(new Intent(getContext(), TourismBelanjaActivity.class));
                break;
            case R.id.ln_desa_tour_home_menu:
                startActivity(new Intent(getContext(), TourismDesaActivity.class));
                break;
            case R.id.ln_hotel_public_home:
                startActivity(new Intent(getContext(), HotelActivity.class));
                break;
            case R.id.ln_bandara_public_home:
                startActivity(new Intent(getContext(), TransportationAirportActivity.class));
                break;
            case R.id.ln_more_home:
                startActivity(new Intent(getContext(), AllCategoryActivity.class));
                break;
            case R.id.ln_train_public_home:
                startActivity(new Intent(getContext(), TransportationTrainActivity.class));
                break;
        }

    }
}
