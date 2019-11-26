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

package co.id.roningrum.dolanapptugasakhir.ui.tourism.tourismMapActivity;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.firebasequery.FirebaseConstant;
import co.id.roningrum.dolanapptugasakhir.model.Tourism;
import co.id.roningrum.dolanapptugasakhir.util.BitmapDescriptorHandler;

public class TourismVillageMaps extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap villageMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_village);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.tourist_village_map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
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
        showVillageMap(googleMap);
    }

    private void showVillageMap(GoogleMap googleMap) {
        villageMap = googleMap;
        Query villageMapQuery = FirebaseConstant.getTourismDesa();
        villageMapQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dsNature : dataSnapshot.getChildren()) {
                    Tourism tourism = dsNature.getValue(Tourism.class);
                    assert tourism != null;
                    double latNature = tourism.getLat_location_tourism();
                    double lngNature = tourism.getLng_location_tourism();
                    LatLng villagePlaceLoc = new LatLng(latNature, lngNature);
                    villageMap.moveCamera(CameraUpdateFactory.newLatLngZoom(villagePlaceLoc, 10.0f));
                    villageMap.addMarker(new MarkerOptions().position(villagePlaceLoc).icon(BitmapDescriptorHandler.getBitmapDescriptor(getApplicationContext())).title(tourism.getName_tourism()).snippet(tourism.getLocation_tourism()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Pesan", "Check Database :" + databaseError.getMessage());
            }
        });
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = villageMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.google_map_style));

            if (!success) {
                Log.e("MapsActivityRaw", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MapsActivityRaw", "Can't find style.", e);
        }
    }

}
