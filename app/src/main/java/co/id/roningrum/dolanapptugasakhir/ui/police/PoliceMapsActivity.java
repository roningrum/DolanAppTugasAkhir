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

package co.id.roningrum.dolanapptugasakhir.ui.police;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.firebasequery.FirebaseQuery;
import co.id.roningrum.dolanapptugasakhir.handler.GPSHandler;
import co.id.roningrum.dolanapptugasakhir.model.Police;
import co.id.roningrum.dolanapptugasakhir.util.Utils;

public class PoliceMapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap policeMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_police);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.police_map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        Toolbar toolbarPoliceMap = findViewById(R.id.toolbar_police_map);
        setSupportActionBar(toolbarPoliceMap);

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

        showPoliceMap(googleMap);
    }

    private void showPoliceMap(GoogleMap googleMap) {
        policeMap = googleMap;
        Query policeRefQuery = FirebaseQuery.getPolice();
        policeRefQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dsPolice : dataSnapshot.getChildren()) {
                    Police police = dsPolice.getValue(Police.class);
                    assert police != null;
                    double latPolice = police.getLat_location_police();
                    double lngPolice = police.getLng_location_police();
                    LatLng policePlaceLoc = new LatLng(latPolice, lngPolice);

                    GPSHandler gpsHandler = new GPSHandler(getApplicationContext());
                    double lat = gpsHandler.getLatitude();
                    double lng = gpsHandler.getLongitude();
                    LatLng userLoc = new LatLng(lat, lng);

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(userLoc)
                            .zoom(12.27f)
                            .build();
                    policeMap.setMyLocationEnabled(true);
                    policeMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    policeMap.addMarker(new MarkerOptions().position(policePlaceLoc).icon(Utils.getBitmapDescriptor(getApplicationContext())).title(police.getName_police()).snippet(police.getLocation_police()));

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
            boolean success = policeMap.setMapStyle(
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