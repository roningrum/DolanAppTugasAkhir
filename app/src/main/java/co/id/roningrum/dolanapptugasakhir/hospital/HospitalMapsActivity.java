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

package co.id.roningrum.dolanapptugasakhir.hospital;

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
import com.google.firebase.database.ValueEventListener;

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.model.HospitalItem;

public class HospitalMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap hospitalGoogleMap;
    private DatabaseReference hospitalMapRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.hospital_map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        hospitalMapRef = FirebaseDatabase.getInstance().getReference().child("Hospital");
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
        hospitalGoogleMap = googleMap;
        hospitalMapRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dsHospital : dataSnapshot.getChildren()) {
                    HospitalItem hospitalItem = dsHospital.getValue(HospitalItem.class);
                    assert hospitalItem != null;
                    double latHospital = hospitalItem.getLat_hospital();
                    double lngHospital = hospitalItem.getLng_hospital();
                    LatLng hospitalPlaceLoc = new LatLng(latHospital, lngHospital);
                    hospitalGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hospitalPlaceLoc, 10.2f));
                    hospitalGoogleMap.addMarker(new MarkerOptions().position(hospitalPlaceLoc).title(hospitalItem.getName_hospital()).snippet(hospitalItem.getLocation_hospital()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Pesan", "Check Database :" + databaseError.getMessage());

            }
        });
    }
}
