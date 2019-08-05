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
import android.support.v4.app.FragmentActivity;

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

import co.id.roningrum.dolanapptugasakhir.model.TourismItem;

public class CategoryMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    //    private GPSHandler gpsHandler;
    private DatabaseReference tourismMapObjectDB;
    private ClusterManager<TourismItem> clusterManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        tourismMapObjectDB = FirebaseDatabase.getInstance().getReference().child("Tourism");
//        gpsHandler = new GPSHandler(getApplicationContext());
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
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        clusterManager = new ClusterManager<>(getApplicationContext(), mMap);
        tourismMapObjectDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot s : dataSnapshot.getChildren()) {
                    TourismItem tourismItems = s.getValue(TourismItem.class);
                    assert tourismItems != null;
                    double latitude = tourismItems.getLat_location_tourism();
                    double longitude = tourismItems.getLng_location_tourism();

                    LatLng location = new LatLng(latitude, longitude);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 8.0f));
//                    mMap.addMarker(new MarkerOptions().position(location).title("" + tourismItems.getName_tourism()));
                    mMap.setOnCameraIdleListener(clusterManager);
                    mMap.setOnInfoWindowClickListener(clusterManager);
                    clusterManager.addItem(tourismItems);

//                    mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
//                    mMap.animateCamera(CameraUpdateFactory.zoomIn());
////                    mMap.animateCamera(CameraUpdateFactory.zoomTo(6.0f);
//                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//                        @Override
//                        public boolean onMarkerClick(Marker marker) {
//                            return false;
//                        }
//                    });


//                    mMap.animateCamera(CameraUpdateFactory.zoomIn(8.0f));
                }
                clusterManager.cluster();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        // Add a marker in Sydney and move the camera


    }
}
