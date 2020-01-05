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

package co.id.roningrum.dolanapptugasakhir.ui.gasstation;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.firebasequery.FirebaseConstant;
import co.id.roningrum.dolanapptugasakhir.handler.GPSHandler;
import co.id.roningrum.dolanapptugasakhir.model.GasStation;

public class GasStationMap extends FragmentActivity implements OnMapReadyCallback {

    private DatabaseReference gasMapRef;
    private GoogleMap gasGoogleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_gas_station);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.gas_map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        gasMapRef = FirebaseConstant.GasRef;

        Toolbar toolbarGas = findViewById(R.id.toolbar_gas_map);
//        setSupportActionBar(toolbarGas);
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setHomeButtonEnabled(true);
//            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp);
//        }



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

        showGasMap(googleMap);

    }

    private void showGasMap(final GoogleMap googleMap) {
        gasGoogleMap = googleMap;
        gasMapRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dsGasStation : dataSnapshot.getChildren()) {
                    GasStation gasStation = dsGasStation.getValue(GasStation.class);
                    assert gasStation != null;
                    double latGas = gasStation.getLat_gasstation();
                    double lngGas = gasStation.getLng_gasstation();

                    //getUserKoordinat
                    GPSHandler gpsHandler = new GPSHandler(getApplicationContext());
                    double lat = gpsHandler.getLatitude();
                    double lng = gpsHandler.getLongitude();

                    LatLng busPlaceLoc = new LatLng(latGas, lngGas);
                    LatLng userLoc = new LatLng(lat, lng);

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(userLoc)
                            .zoom(14.07f)
                            .build();
                    gasGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    gasGoogleMap.setMyLocationEnabled(true);
                    gasGoogleMap.addMarker(new MarkerOptions().position(busPlaceLoc).title(gasStation.getName_gasstation()).snippet(gasStation.getLocation_gasstation()));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Pesan", "Check Database :" + databaseError.getMessage());

            }
        });
    }
}
