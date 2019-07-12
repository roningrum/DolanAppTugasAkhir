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

package co.id.roningrum.dolanapptugasakhir.tourism.nature;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.item.CategoryItem;

public class NatureMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap natureMap;
    private DatabaseReference natureRefMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nature_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nature_map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        natureRefMap = FirebaseDatabase.getInstance().getReference().child("Tourism");
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        natureMap = googleMap;
        Query natureMapQuery = natureRefMap.orderByChild("category_tourism").equalTo("alam");
        natureMapQuery.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dsNature : dataSnapshot.getChildren()){
                    CategoryItem categoryItem = dsNature.getValue(CategoryItem.class);
                    double latNature = categoryItem.getLat_location_tourism();
                    double lngNature = categoryItem.getLng_location_tourism();
                    LatLng naturePlaceLoc = new LatLng(latNature, lngNature);
                    natureMap.moveCamera(CameraUpdateFactory.newLatLngZoom(naturePlaceLoc, 10.0f));
                    natureMap.addMarker(new MarkerOptions().position(naturePlaceLoc).title(categoryItem.getName_tourism()).snippet(categoryItem.getLocation_tourism()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Pesan", "Check Database :" +databaseError.getMessage());
            }
        });
//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        natureMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        natureMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}