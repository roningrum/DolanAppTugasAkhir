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


package co.id.roningrum.dolanapptugasakhir.adapter;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.item.CategoryItem;


public class RecreationViewHolder extends RecyclerView.ViewHolder {
    private final TextView name_recreation_tourisms;
    private final TextView location_recreation_tourisms;
    private final TextView distance_recreation_tourisms;
    private final ImageView tourism_recreation_pic;
    //interface
    private RecreationViewHolder.ClickListener categoryOnClick;

    public RecreationViewHolder(@NonNull View itemView) {
        super(itemView);
        name_recreation_tourisms = itemView.findViewById(R.id.name_recreation_item_tourism);
        location_recreation_tourisms = itemView.findViewById(R.id.location_recreation_item_tourism);
        distance_recreation_tourisms = itemView.findViewById(R.id.distance_recreation_item_tourism);
        tourism_recreation_pic = itemView.findViewById(R.id.tourism_recreation_pic);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryOnClick.onItemClick(v, getAdapterPosition());
            }
        });

    }

    @SuppressLint("SetTextI18n")
    public void showRecreationTourismData(CategoryItem categoryItem, double longitude, double latitude) {

        double lattitude_a = categoryItem.getLat_location_tourism();
        double longitude_a = categoryItem.getLng_location_tourism();

        float jarakMeter = (float) calculateDistance(latitude, longitude, lattitude_a, longitude_a);
//        float jarakMeter = loc1.distanceTo(loc2);
        float jarakKM = jarakMeter / 1000;
        @SuppressLint("DefaultLocale") String distanceFormat = String.format("%.2f", jarakKM);

        name_recreation_tourisms.setText(categoryItem.getName_tourism());
        location_recreation_tourisms.setText(categoryItem.getLocation_tourism());
        distance_recreation_tourisms.setText(distanceFormat + " KM");
        Glide.with(itemView.getContext()).load(categoryItem.getUrl_photo()).into(tourism_recreation_pic);
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

    public void setOnClickListener(RecreationViewHolder.ClickListener clickListener) {
        categoryOnClick = clickListener;
    }

    public interface ClickListener {
        void onItemClick(View view, int position);
    }


}
//}
