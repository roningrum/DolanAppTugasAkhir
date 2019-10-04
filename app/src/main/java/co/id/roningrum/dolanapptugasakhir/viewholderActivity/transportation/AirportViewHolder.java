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

package co.id.roningrum.dolanapptugasakhir.viewholderActivity.transportation;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.handler.HaversineHandler;
import co.id.roningrum.dolanapptugasakhir.model.Transportation;

public class AirportViewHolder extends RecyclerView.ViewHolder {

    private final TextView nameAirport;
    private final TextView locationAirport;
    private final TextView distanceAirport;
    private final ImageView airportPic;

    private AirportViewHolder.ClickListener airportClickListener;


    public AirportViewHolder(@NonNull View itemView) {
        super(itemView);
        nameAirport = itemView.findViewById(R.id.name_airport_item_tourism);
        locationAirport = itemView.findViewById(R.id.location_airport_item_tourism);
        distanceAirport = itemView.findViewById(R.id.distance_airport_item_tourism);
        airportPic = itemView.findViewById(R.id.airport_pic);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                airportClickListener.onItemClick(v, getAdapterPosition());
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void showAirportData(Transportation transportation, double latitude, double longitude) {

        double lattitude_a = transportation.getLat_transportation();
        double longitude_a = transportation.getLng_transportation();

        float jarakKM = (float) HaversineHandler.calculateDistance(latitude, longitude, lattitude_a, longitude_a);
//        float jarakMeter = loc1.distanceTo(loc2);
//        float jarakKM = jarakMeter / 1000;
        @SuppressLint("DefaultLocale") String distanceFormat = String.format("%.2f", jarakKM);

        nameAirport.setText(transportation.getName_transportation());
        locationAirport.setText(transportation.getLocation_transportation());
        distanceAirport.setText(distanceFormat + " km");
        Glide.with(itemView.getContext()).load(transportation.getUrl_photo_transport()).into(airportPic);
    }

//    private double calculateDistance(double lat1, double long1, double lat2, double long2) {
//
//        double earthRadius = 6371;
//        double latDiff = Math.toRadians(lat1 - lat2);
//        double lngDiff = Math.toRadians(long1 - long2);
//        double a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2) +
//                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
//                        Math.sin(lngDiff / 2) * Math.sin(lngDiff / 2);
//        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
//        double distance = earthRadius * c;
//
//        int meterConversion = 1609;
//
//        return (float) distance * meterConversion;
//    }

    public void setOnClickListener(AirportViewHolder.ClickListener clickListener) {
        airportClickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(View view, int position);
    }
}
