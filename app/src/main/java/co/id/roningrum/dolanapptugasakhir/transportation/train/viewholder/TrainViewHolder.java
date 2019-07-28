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

package co.id.roningrum.dolanapptugasakhir.transportation.train.viewholder;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.item.TransportationItem;

public class TrainViewHolder extends RecyclerView.ViewHolder {

    private final TextView nameTrain;
    private final TextView locationTrain;
    private final TextView distanceTrain;
    private final ImageView trainPic;

    private TrainViewHolder.ClickListener trainClickListener;

    public TrainViewHolder(@NonNull View itemView) {
        super(itemView);
        nameTrain = itemView.findViewById(R.id.name_train_item_tourism);
        locationTrain = itemView.findViewById(R.id.location_train_item_tourism);
        distanceTrain = itemView.findViewById(R.id.distance_train_item_tourism);
        trainPic = itemView.findViewById(R.id.train_pic);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trainClickListener.onItemClick(v, getAdapterPosition());
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void showTrainData(TransportationItem transportationItem, double latitude, double longitude) {
        double lattitude_a = transportationItem.getLat_transportation();
        double longitude_a = transportationItem.getLng_transportation();

        float jarakMeter = (float) calculateDistance(latitude, longitude, lattitude_a, longitude_a);
        float jarakKM = jarakMeter / 1000;

        @SuppressLint("DefaultLocale") String distanceFormat = String.format("%.2f", jarakKM);

        nameTrain.setText(transportationItem.getName_transportation());
        locationTrain.setText(transportationItem.getLocation_transportation());
        distanceTrain.setText(distanceFormat + " km");
        Glide.with(itemView.getContext()).load(transportationItem.getUrl_photo_transport()).into(trainPic);
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

    public void setOnClickListener(TrainViewHolder.ClickListener clickListener) {
        trainClickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(View view, int position);
    }

}
