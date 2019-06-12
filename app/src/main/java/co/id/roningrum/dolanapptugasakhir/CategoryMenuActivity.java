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

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

import co.id.roningrum.dolanapptugasakhir.adapter.CategoryAdapter;
import co.id.roningrum.dolanapptugasakhir.item.CategoryItem;

public class CategoryMenuActivity extends AppCompatActivity {
    Toolbar mToolbar;
    DatabaseReference tourismDBRef;
    RecyclerView rvTourismList;
    ArrayList<CategoryItem> categoryItemList;
    FirebaseRecyclerAdapter<CategoryItem, CategoryAdapter> firebaseAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_menu);
        mToolbar = findViewById(R.id.toolbar_top);
        rvTourismList = findViewById(R.id.tourism_list);
        rvTourismList.setLayoutManager(new LinearLayoutManager(this));
        categoryItemList = new ArrayList<>();

        setSupportActionBar(mToolbar);

        tourismDBRef = FirebaseDatabase.getInstance().getReference();
        Query query = tourismDBRef.child("Tourism");

        FirebaseRecyclerOptions<CategoryItem> options = new FirebaseRecyclerOptions.Builder<CategoryItem>()
                .setQuery(query, CategoryItem.class)
                .build();
        firebaseAdapter = new FirebaseRecyclerAdapter<CategoryItem, CategoryAdapter>(options) {

            @NonNull
            @Override
            public CategoryAdapter onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_category_menu, viewGroup, false);
                return new CategoryAdapter(view);
            }

            @Override
            public void onBindViewHolder(@NonNull CategoryAdapter holder, int position, @NonNull CategoryItem model) {
                holder.showTourismData(model);
            }
        };
        firebaseAdapter.notifyDataSetChanged();
        rvTourismList.setAdapter(firebaseAdapter);

//        tourismDBRef.child("Tourism").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
//                    CategoryItem categoryItem = dataSnapshot1.getValue(CategoryItem.class);
//                    categoryItemList.add(categoryItem);
//                }
//                categoryAdapter = new CategoryAdapter(CategoryMenuActivity.this, categoryItemList);
//                rvTourismList.setAdapter(categoryAdapter);
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        MenuItem mSearch = menu.findItem(R.id.searchMenu);
        SearchView mSearchView = (SearchView) mSearch.getActionView();
        mSearchView.setQueryHint("Search");
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String searchText) {
                firebaseSearch(searchText);

//                tourismDBRef.child("Tourism").addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        categoryItemList.clear();
//                        rvTourismList.removeAllViews();
//
//                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
//                            CategoryItem categoryItem = snapshot.getValue(CategoryItem.class);
//                            categoryItemList.add(categoryItem);
//
//                        }
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void firebaseSearch(String searchText) {
        Query firebaseSearchquery = tourismDBRef.child("Tourism").orderByKey().startAt(searchText).endAt(searchText + "\uf8ff");
        FirebaseRecyclerOptions<CategoryItem> options = new FirebaseRecyclerOptions.Builder<CategoryItem>()
                .setQuery(firebaseSearchquery, CategoryItem.class)
                .setLifecycleOwner(this)
                .build();
        firebaseAdapter = new FirebaseRecyclerAdapter<CategoryItem, CategoryAdapter>(options) {

            @NonNull
            @Override
            public CategoryAdapter onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_category_menu, viewGroup, false);
                return new CategoryAdapter(view);
            }

            @Override
            public void onBindViewHolder(@NonNull CategoryAdapter holder, int position, @NonNull CategoryItem model) {
                holder.showTourismData(model);
            }
        };
        rvTourismList.setAdapter(firebaseAdapter);

    }
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAdapter != null) {
            firebaseAdapter.stopListening();
        }

    }
}
