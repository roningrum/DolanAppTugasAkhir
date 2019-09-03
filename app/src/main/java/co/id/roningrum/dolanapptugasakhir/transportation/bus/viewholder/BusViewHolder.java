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

package co.id.roningrum.dolanapptugasakhir.transportation.bus.viewholder;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.handler.HaversineHandler;
import co.id.roningrum.dolanapptugasakhir.model.TransportationItem;

public class BusViewHolder extends RecyclerView.ViewHolder {

    private final TextView nameBus;
    private final TextView locationBus;
    private final TextView distanceBus;
    private final ImageView busPic;

    private BusViewHolder.ClickListener busClickListener;

    public BusViewHolder(@NonNull View itemView) {
        super(itemView);
        nameBus = itemView.findViewById(R.id.name_bus_item_tourism);
        locationBus = itemView.findViewById(R.id.location_bus_item_tourism);
        distanceBus = itemView.findViewById(R.id.distance_bus_item_tourism);
        busPic = itemView.findViewById(R.id.img_bus_pic);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                busClickListener.onItemClick(v, getAdapterPosition());
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void showBusData(TransportationItem transportationItem, double latitude, double longitude) {
        double lattitude_a = transportationItem.getLat_transportation();
        double longitude_a = transportationItem.getLng_transportation();

        float jarakKM = (float) HaversineHandler.calculateDistance(latitude, longitude, lattitude_a, longitude_a);

        @SuppressLint("DefaultLocale") String distanceFormat = String.format("%.2f", jarakKM);

        nameBus.setText(transportationItem.getName_transportation());
        locationBus.setText(transportationItem.getLocation_transportation());
        distanceBus.setText(distanceFormat + " km");
        Glide.with(itemView.getContext()).load(transportationItem.getUrl_photo_transport()).into(busPic);
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

    public void setOnClickListener(BusViewHolder.ClickListener clickListener) {
        busClickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(View view, int position);
    }
}
