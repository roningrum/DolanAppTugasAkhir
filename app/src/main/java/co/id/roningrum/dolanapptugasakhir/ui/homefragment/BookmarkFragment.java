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


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

import co.id.roningrum.dolanapptugasakhir.FavoritAdapter;
import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.model.FavoriteItem;


/**
 * A simple {@link Fragment} subclass.
 */
public class BookmarkFragment extends Fragment {
    private ArrayList<FavoriteItem> favoriteItems = new ArrayList<>();
    private DatabaseReference dbFavoritRef;
    private FavoritAdapter favoritAdapter;
    private RecyclerView rvFavoritList;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;


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
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        rvFavoritList = view.findViewById(R.id.rv_bookmark);
        rvFavoritList.setHasFixedSize(true);
        rvFavoritList.setLayoutManager(new LinearLayoutManager(getContext()));

        //untuk menampilkan data favorite
        dbFavoritRef = FirebaseDatabase.getInstance().getReference();
        dbFavoritRef.child("Favorite").child("Tourism").child(user.getDisplayName()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String userId = dataSnapshot.getKey();
                Log.d("cek id tourism", "idTourism : " + userId + "");

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    FavoriteItem favoriteItem = dataSnapshot1.getValue(FavoriteItem.class);
                    favoriteItems.add(favoriteItem);
                }

                favoritAdapter = new FavoritAdapter();
                favoritAdapter.setFavoriteItems(favoriteItems);
                favoritAdapter.notifyDataSetChanged();
                rvFavoritList.setAdapter(favoritAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("GAGAL MUAT", "Pesan : " + databaseError.getMessage());
            }
        });

    }
}