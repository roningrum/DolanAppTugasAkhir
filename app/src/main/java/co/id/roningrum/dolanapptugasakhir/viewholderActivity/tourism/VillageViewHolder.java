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
import co.id.roningrum.dolanapptugasakhir.handler.HaversineHandler;
import co.id.roningrum.dolanapptugasakhir.model.TourismItem;

public class VillageViewHolder extends RecyclerView.ViewHolder {
    private final TextView name_village_tourism;
    private final TextView location_village_tourism;
    private final TextView distance_village_tourism;
    private final ImageView village_tourism_pic;

    public VillageViewHolder(@NonNull View itemView) {
        super(itemView);
        name_village_tourism = itemView.findViewById(R.id.name_village_item_tourism);
        location_village_tourism = itemView.findViewById(R.id.location_village_item_tourism);
        distance_village_tourism = itemView.findViewById(R.id.distance_village_item_tourism);
        village_tourism_pic = itemView.findViewById(R.id.tourism_village_pic);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryOnClick.onItemClick(v, getAdapterPosition());
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void showVillageTourismData(TourismItem tourismItem, double latitude, double longitude) {

        double lattitude_a = tourismItem.getLat_location_tourism();
        double longitude_a = tourismItem.getLng_location_tourism();
        float jarakKM = (float) HaversineHandler.calculateDistance(latitude, longitude, lattitude_a, longitude_a);
//        float jarakMeter = loc1.distanceTo(loc2);
//        float jarakKM = jarakMeter / 1000;
        @SuppressLint("DefaultLocale") String distanceFormat = String.format("%.2f", jarakKM);

        name_village_tourism.setText(tourismItem.getName_tourism());
        location_village_tourism.setText(tourismItem.getLocation_tourism());
        distance_village_tourism.setText(distanceFormat + " km");
        Glide.with(itemView.getContext()).load(tourismItem.getUrl_photo()).into(village_tourism_pic);
    }
//
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

    //interface
    private VillageViewHolder.ClickListener categoryOnClick;

    public interface ClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnClickListener(VillageViewHolder.ClickListener clickListener) {
        categoryOnClick = clickListener;
    }

}
