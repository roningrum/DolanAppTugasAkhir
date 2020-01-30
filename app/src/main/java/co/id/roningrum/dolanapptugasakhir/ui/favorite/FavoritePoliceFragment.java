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
import co.id.roningrum.dolanapptugasakhir.model.Police;
import co.id.roningrum.dolanapptugasakhir.ui.adapter.police.PoliceClickCallback;
import co.id.roningrum.dolanapptugasakhir.ui.favorite.adapter.FavoritePoliceAdapter;
import co.id.roningrum.dolanapptugasakhir.ui.homeactivity.AllCategoryActivity;
import co.id.roningrum.dolanapptugasakhir.ui.police.PoliceDetail;

import static co.id.roningrum.dolanapptugasakhir.firebasequery.FirebaseQuery.favoriteRef;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavoritePoliceFragment extends Fragment {

    private ArrayList<Police> polices;
    private ArrayList<String> checkUserList;
    private RecyclerView rvFavoritPoliceList;
    private FirebaseUser user;
    private FavoritePoliceAdapter favoritAdapter;
    private LocationPermissionHandler locationPermissionHandler;
    private DatabaseReference policeReference;
    private ConstraintLayout emptyLayout;
    private Button btnGoToMenu;
    private ProgressBar pbLoading;
    public FavoritePoliceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite_police, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        rvFavoritPoliceList = view.findViewById(R.id.rv_bookmark_police);
        pbLoading = view.findViewById(R.id.pb_loading);
        emptyLayout = view.findViewById(R.id.layout_empty);
        btnGoToMenu = view.findViewById(R.id.btn_choose_menu);
        rvFavoritPoliceList.setHasFixedSize(true);
        rvFavoritPoliceList.setLayoutManager(new LinearLayoutManager(getContext()));
        policeReference = FirebaseQuery.PoliceRef;

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
                        favoriteRef.getRef().child(user.getUid()).child("Police").child(polices.get(position).getId()).removeValue();
                        favoritAdapter.removeItem(position);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchHelper.attachToRecyclerView(rvFavoritPoliceList);
    }

    private void checkConnection() {
        assert getContext() != null;
        if (NetworkHelper.isConnectedToNetwork(getContext())) {
            checkUser();
        } else {
            pbLoading.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Check your connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkUser() {
        pbLoading.setVisibility(View.VISIBLE);
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Favorite").child(user.getUid()).child("Police");
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

    private void showFavorite() {
        if (havePermission()) {
            polices = new ArrayList<>();
            policeReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    polices.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Police police = snapshot.getValue(Police.class);
                        if (police != null) {
                            final String idPolice = police.getId();
                            for (String id : checkUserList) {
                                if (idPolice.equals(id)) {
                                    polices.add(police);
                                }
                            }
                        }
                    }
                    favoritAdapter = new FavoritePoliceAdapter(polices, getContext());
                    favoritAdapter.setPoliceClickCallback(new PoliceClickCallback() {
                        @Override
                        public void onItemCallback(Police police) {
                            Intent intent = new Intent(getActivity(), Police.class);
                            intent.putExtra(PoliceDetail.EXTRA_POLICE_KEY, police.getId());
                            startActivity(intent);
                        }
                    });
                    rvFavoritPoliceList.setAdapter(favoritAdapter);
                    favoritAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
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
