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

package co.id.roningrum.dolanapptugasakhir.ui.mapsactivity;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.clustering.ClusterManager;

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.model.HotelItem;

public class HotelMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap hotelMap;
    private DatabaseReference hotelRefMap;
    private ClusterManager<HotelItem> hotelclusterManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_hotel);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        hotelRefMap = FirebaseDatabase.getInstance().getReference().child("Hotel");
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

        hotelMap = googleMap;
        hotelclusterManager = new ClusterManager<>(getApplicationContext(), hotelMap);
        hotelRefMap.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dsHotel : dataSnapshot.getChildren()) {
                    HotelItem hotelItem = dsHotel.getValue(HotelItem.class);
                    assert hotelItem != null;
                    double latHotel = hotelItem.getLat_location_hotel();
                    double lngHotel = hotelItem.getLng_location_hotel();
                    LatLng naturePlaceLoc = new LatLng(latHotel, lngHotel);
                    hotelMap.moveCamera(CameraUpdateFactory.newLatLngZoom(naturePlaceLoc, 8.0f));
//                    hotelMap.addMarker(new MarkerOptions().position(naturePlaceLoc).title(hotelItem.getName_hotel()).snippet(hotelItem.getLocation_hotel()));
                    hotelMap.setOnCameraIdleListener(hotelclusterManager);
                    hotelMap.setOnInfoWindowClickListener(hotelclusterManager);
                    hotelclusterManager.addItem(hotelItem);
                }
                hotelclusterManager.cluster();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Pesan", "Check Database :" + databaseError.getMessage());
            }
        });
//        mMap = googleMap;
//
//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
