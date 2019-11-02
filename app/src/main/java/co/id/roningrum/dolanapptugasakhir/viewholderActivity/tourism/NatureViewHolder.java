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


package co.id.roningrum.dolanapptugasakhir.viewholderActivity.tourism;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.model.Tourism;
import co.id.roningrum.dolanapptugasakhir.util.HaversineHandler;


public class NatureViewHolder extends RecyclerView.ViewHolder {
    private final TextView name_tourisms;
    private final TextView location_tourism;
    private final TextView distance_tourism;
    private final ImageView tourism_pic;

    public NatureViewHolder(@NonNull View itemView) {
        super(itemView);
        name_tourisms = itemView.findViewById(R.id.name_nature_item_tourism);
        location_tourism = itemView.findViewById(R.id.location_nature_item_tourism);
        distance_tourism= itemView.findViewById(R.id.distance_nature_item_tourism);
        tourism_pic = itemView.findViewById(R.id.tourism_nature_pic);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryOnClick.onItemClick(v, getAdapterPosition());
            }
        });

    }

    @SuppressLint("SetTextI18n")
    public void showTourismData(Tourism tourism, double longitude, double latitude) {

        double lattitude_a = tourism.getLat_location_tourism();
        double longitude_a = tourism.getLng_location_tourism();

        float jarakKM = (float) HaversineHandler.calculateDistance(latitude, longitude, lattitude_a, longitude_a);
//        float jarakMeter = loc1.distanceTo(loc2);
//        float jarakKM = jarakMeter/1000;
        @SuppressLint("DefaultLocale") String distanceFormat = String.format("%.2f",jarakKM);

        name_tourisms.setText(tourism.getName_tourism());
        location_tourism.setText(tourism.getLocation_tourism());
        distance_tourism.setText(distanceFormat + " km");
        Glide.with(itemView.getContext()).load(tourism.getUrl_photo()).into(tourism_pic);
    }

//    private double calculateDistance(double lat1, double long1, double lat2, double long2){
//
//        double earthRadius = 6371;
//        double latDiff = Math.toRadians(lat1-lat2);
//        double lngDiff = Math.toRadians(long1-long2);
//        double a = Math.sin(latDiff /2) * Math.sin(latDiff /2) +
//                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
//                        Math.sin(lngDiff /2) * Math.sin(lngDiff /2);
//        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
//        double distance = earthRadius * c;
//
//        int meterConversion = 1609;
//
//        return (float) distance * meterConversion;
//    }

    //interface
    private NatureViewHolder.ClickListener categoryOnClick;
    public interface ClickListener{
        void onItemClick(View view, int position);
    }
    public void setOnClickListener(NatureViewHolder.ClickListener clickListener){
        categoryOnClick = clickListener;
    }


}
//}
