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

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import co.id.roningrum.dolanapptugasakhir.handler.GPSHandler;
import co.id.roningrum.dolanapptugasakhir.item.CategoryItem;


public class DetailCategoryActivity extends AppCompatActivity {

    public static final String EXTRA_WISATA_KEY = "tourist_key";

    private DatabaseReference touristDetailRef;

    private GPSHandler gpsHandler;

    private TextView tvNameTourismObject;
    private TextView tvAddressTourismObject;
    private TextView tvInfoTourismObject;
    private TextView tvDistanceObject;
    private ImageView imgTourismObject;

    private double startlat;
    private double startlng;
    private double endlat;
    private double endLng;
    private double distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_category);
        String touristKey = getIntent().getStringExtra(EXTRA_WISATA_KEY);
        if(touristKey == null){
            throw new IllegalArgumentException("Must pass Extra");
        }
        touristDetailRef = FirebaseDatabase.getInstance().getReference().child("Tourism").child(touristKey);
        tvNameTourismObject = findViewById(R.id.tv_name_tourism_detail);
        tvAddressTourismObject = findViewById(R.id.tv_address_tourism_detail);
        tvInfoTourismObject = findViewById(R.id.tv_info_tourism_detail);
        imgTourismObject = findViewById(R.id.img_category_tourism_pic);
        tvDistanceObject = findViewById(R.id.tv_distance_tourism_detail);

        gpsHandler = new GPSHandler(getApplicationContext());

        showdata();

    }
    private void showdata() {

        if (gpsHandler.isCanGetLocation()){}
        touristDetailRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                CategoryItem categoryItem = dataSnapshot.getValue(CategoryItem.class);


                    startlat = gpsHandler.getLatitude();
                    startlng = gpsHandler.getLongitude();
                    endlat = categoryItem.getLat_location_tourism();
                    endLng = categoryItem.getLng_location_tourism();
                    distance = calculateDistance(startlat,startlng,endlat,endLng);

                    @SuppressLint("DefaultLocale") String distanceFormat = String.format("%.2f",distance);
                    tvDistanceObject.setText(""+distanceFormat+" KM");
                    tvNameTourismObject.setText(categoryItem.getName_tourism());
                    tvAddressTourismObject.setText(categoryItem.getLocation_tourism());
                    tvInfoTourismObject.setText(categoryItem.getInfo_tourism());
                    Glide.with(getApplicationContext()).load(categoryItem.getUrl_photo()).into(imgTourismObject);



//                showGalleryPhoto();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Pesan", "Database" +databaseError.getMessage());
            }
        });
    }

    private double calculateDistance(double lat1, double long1, double lat2, double long2){

        double earthRadius = 6371;
        double latDiff = Math.toRadians(lat1-lat2);
        double lngDiff = Math.toRadians(long1-long2);
        double a = Math.sin(latDiff /2) * Math.sin(latDiff /2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(lngDiff /2) * Math.sin(lngDiff /2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = earthRadius * c;

        int meterConversion = 1609;

        return (distance*meterConversion/1000);
    }

    @Override
    protected void onStart() {
        super.onStart();
        showdata();

    }

}
