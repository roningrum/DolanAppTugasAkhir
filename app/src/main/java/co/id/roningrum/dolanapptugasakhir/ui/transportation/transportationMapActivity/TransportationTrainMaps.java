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

package co.id.roningrum.dolanapptugasakhir.ui.transportation.transportationMapActivity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.firebasequery.FirebaseConstant;
import co.id.roningrum.dolanapptugasakhir.handler.GPSHandler;
import co.id.roningrum.dolanapptugasakhir.model.Transportation;
import co.id.roningrum.dolanapptugasakhir.util.Utils;

public class TransportationTrainMaps extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap trainMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_train);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.train_map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        Toolbar toolbarTrain = findViewById(R.id.toolbar_train_map);
        setSupportActionBar(toolbarTrain);
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
        showTrainMap(googleMap);
    }

    private void showTrainMap(GoogleMap googleMap) {
        trainMap = googleMap;
        Query trainMapQuery = FirebaseConstant.getTransportKereta();
        trainMapQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dsAirport : dataSnapshot.getChildren()) {
                    Transportation transportation = dsAirport.getValue(Transportation.class);
                    assert transportation != null;
                    double latTrain = transportation.getLat_transportation();
                    double lngTrain = transportation.getLng_transportation();

                    GPSHandler gpsHandler = new GPSHandler(getApplicationContext());
                    double lat = gpsHandler.getLatitude();
                    double lng = gpsHandler.getLongitude();
                    LatLng userLoc = new LatLng(lat, lng);

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(userLoc)
                            .zoom(12.27f)
                            .build();

                    trainMap.setMyLocationEnabled(true);
                    trainMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    LatLng trainPlaceLoc = new LatLng(latTrain, lngTrain);
                    trainMap.addMarker(new MarkerOptions().position(trainPlaceLoc).icon(Utils.getBitmapDescriptor(getApplicationContext())).
                            title(transportation.getName_transportation()).snippet(transportation.getLocation_transportation()));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Pesan", "Check Database :" + databaseError.getMessage());

            }
        });
    }

    private BitmapDescriptor getBitmapDescriptor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            VectorDrawable vectorDrawable = (VectorDrawable) getDrawable(R.drawable.ic_marker);

            assert vectorDrawable != null;
            int h = vectorDrawable.getIntrinsicHeight();
            int w = vectorDrawable.getIntrinsicWidth();

            vectorDrawable.setBounds(0, 0, w, h);

            Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bm);
            vectorDrawable.draw(canvas);

            return BitmapDescriptorFactory.fromBitmap(bm);

        } else {
            return BitmapDescriptorFactory.fromResource(R.drawable.ic_marker);
        }
    }
}
