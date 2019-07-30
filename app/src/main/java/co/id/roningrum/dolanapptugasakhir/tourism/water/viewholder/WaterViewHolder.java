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

package co.id.roningrum.dolanapptugasakhir.tourism.water.viewholder;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.item.TourismItem;

public class WaterViewHolder extends RecyclerView.ViewHolder {
    private final TextView name_water_tourism;
    private final TextView location_water_tourism;
    private final TextView distance_waterTourism;
    private final ImageView water_tourism_pic;

    public WaterViewHolder(@NonNull View itemView) {
        super(itemView);
        name_water_tourism = itemView.findViewById(R.id.name_water_item_tourism);
        location_water_tourism = itemView.findViewById(R.id.location_water_item_tourism);
        distance_waterTourism = itemView.findViewById(R.id.distance_water_item_tourism);
        water_tourism_pic = itemView.findViewById(R.id.tourism_water_pic);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryOnClick.onItemClick(v, getAdapterPosition());
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void showWaterTourismData(TourismItem tourismItem, double latitude, double longitude) {

        double lattitude_a = tourismItem.getLat_location_tourism();
        double longitude_a = tourismItem.getLng_location_tourism();

        float jarakMeter = (float) calculateDistance(latitude, longitude, lattitude_a, longitude_a);
//        float jarakMeter = loc1.distanceTo(loc2);
        float jarakKM = jarakMeter / 1000;
        @SuppressLint("DefaultLocale") String distanceFormat = String.format("%.2f", jarakKM);

        name_water_tourism.setText(tourismItem.getName_tourism());
        location_water_tourism.setText(tourismItem.getLocation_tourism());
        distance_waterTourism.setText(distanceFormat + " km");
        Glide.with(itemView.getContext()).load(tourismItem.getUrl_photo()).into(water_tourism_pic);
    }

    private double calculateDistance(double lat1, double long1, double lat2, double long2) {

        double earthRadius = 6371;
        double latDiff = Math.toRadians(lat1 - lat2);
        double lngDiff = Math.toRadians(long1 - long2);
        double a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(lngDiff / 2) * Math.sin(lngDiff / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c;

        int meterConversion = 1609;

        return (float) distance * meterConversion;
    }

    //interface
    private WaterViewHolder.ClickListener categoryOnClick;

    public interface ClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnClickListener(WaterViewHolder.ClickListener clickListener) {
        categoryOnClick = clickListener;
    }
}
