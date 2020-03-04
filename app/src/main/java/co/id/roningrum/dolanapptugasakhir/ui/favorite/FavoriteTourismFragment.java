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

package co.id.roningrum.dolanapptugasakhir.ui.favorite;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.firebasequery.FirebaseQuery;
import co.id.roningrum.dolanapptugasakhir.handler.LocationPermissionHandler;
import co.id.roningrum.dolanapptugasakhir.handler.NetworkHelper;
import co.id.roningrum.dolanapptugasakhir.model.Tourism;
import co.id.roningrum.dolanapptugasakhir.ui.adapter.tourism.TourismClickCallback;
import co.id.roningrum.dolanapptugasakhir.ui.favorite.adapter.FavoriteTourismAdapter;
import co.id.roningrum.dolanapptugasakhir.ui.homeactivity.AllCategoryActivity;
import co.id.roningrum.dolanapptugasakhir.ui.tourism.tourismDetailActivity.TourismDetailActivity;

import static co.id.roningrum.dolanapptugasakhir.firebasequery.FirebaseQuery.favoriteRef;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteTourismFragment extends Fragment {
    private ArrayList<Tourism> tourismList;
    private ArrayList<String> checkUserList;
    private RecyclerView rvFavoritList;
    private FirebaseUser user;
    private FavoriteTourismAdapter favoriteTourismAdapter;
    private LocationPermissionHandler locationPermissionHandler;
    private DatabaseReference databaseReference;
    private ProgressBar pbLoading;
    private ConstraintLayout emptyLayout;
    private Button btnGoToMenu;


    public FavoriteTourismFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite_tourism, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        rvFavoritList = view.findViewById(R.id.rv_bookmark);
        pbLoading = view.findViewById(R.id.pb_loading);
        emptyLayout = view.findViewById(R.id.layout_empty);
        btnGoToMenu = view.findViewById(R.id.btn_choose_menu);
        rvFavoritList.setHasFixedSize(true);
        rvFavoritList.setLayoutManager(new LinearLayoutManager(getContext()));

        databaseReference = FirebaseQuery.TourismRef;

        checkConnection();
        enableSwipeToDelete();

    }

    private void enableSwipeToDelete() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(getContext()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                favoriteRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                        favoriteRef.getRef().child(user.getUid()).child("Tourism").child(tourismList.get(position).getId()).removeValue();
                        favoriteTourismAdapter.removeItem(position);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchHelper.attachToRecyclerView(rvFavoritList);
    }

    private void checkConnection() {
        assert getContext() != null;
        if (NetworkHelper.isConnectedToNetwork(getContext())) {
            checkUser();
        } else {
            pbLoading.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "Periksa Koneksi", Toast.LENGTH_SHORT).show();
        }
    }

    private void showFavorite() {
        if (havePermission()) {
            tourismList = new ArrayList<>();
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    tourismList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Tourism tourism = snapshot.getValue(Tourism.class);
                        if (tourism != null) {
                            final String idTourism = tourism.getId();
                            for (String id : checkUserList) {
                                if (idTourism.equals(id)) {
                                    tourismList.add(tourism);
                                }
                            }
                        }

                    }

                    favoriteTourismAdapter = new FavoriteTourismAdapter(tourismList, getContext());
                    favoriteTourismAdapter.setTourismClickCallback(new TourismClickCallback() {
                        @Override
                        public void onItemClicked(Tourism tourism) {
                            Intent intent = new Intent(getActivity(), TourismDetailActivity.class);
                            intent.putExtra(TourismDetailActivity.EXTRA_WISATA_KEY, tourism.getId());
                            startActivity(intent);
                        }
                    });
                    rvFavoritList.setAdapter(favoriteTourismAdapter);
                    favoriteTourismAdapter.notifyDataSetChanged();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

    private void checkUser() {
        pbLoading.setVisibility(View.VISIBLE);
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Favorite").child(user.getUid()).child("Tourism");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                checkUserList = new ArrayList<>();
                checkUserList.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        checkUserList.add(snapshot.getKey());
                        Log.d("check id user", "" + checkUserList);
                    }
                    showFavorite();
                    pbLoading.setVisibility(View.GONE);
                } else {
                    emptyLayout.setVisibility(View.VISIBLE);
                    btnGoToMenu.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(getActivity(), AllCategoryActivity.class));
                        }
                    });
                    pbLoading.setVisibility(View.GONE);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private boolean havePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            locationPermissionHandler = LocationPermissionHandler.getInstance(getActivity());
            if (locationPermissionHandler.isAllPermissionAvailable()) {
                Log.d("Pesan", "Permissions have done");
            } else {
                locationPermissionHandler.setActivity(getActivity());
                locationPermissionHandler.deniedPermission();
            }
        } else {
            Toast.makeText(getActivity(), "Check your permission", Toast.LENGTH_SHORT).show();
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
                locationPermissionHandler.deniedPermission(Manifest.permission.ACCESS_FINE_LOCATION);
                locationPermissionHandler.deniedPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
        }
    }
}