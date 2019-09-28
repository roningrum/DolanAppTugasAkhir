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
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import co.id.roningrum.dolanapptugasakhir.handler.HaversineHandler;
import co.id.roningrum.dolanapptugasakhir.model.TourismItem;

public class FavoritViewHolder extends RecyclerView.ViewHolder {
    private final TextView nameFavTourism;
    private final TextView locationFavTourism;
    private final TextView distanceFavTourism;
    private final ImageView favTourismPic;

    public FavoritViewHolder(@NonNull View itemView) {
        super(itemView);
        nameFavTourism = itemView.findViewById(R.id.name_fav_item_tourism);
        locationFavTourism = itemView.findViewById(R.id.location_fav_item_tourism);
        distanceFavTourism = itemView.findViewById(R.id.distance_fav_item_tourism);
        favTourismPic = itemView.findViewById(R.id.tourism_fav_pic);
    }

    @SuppressLint("SetTextI18n")
    public void showFavTourism(TourismItem tourismItem, double latitude, double longitude) {
        double lattitude_a = tourismItem.getLat_location_tourism();
        double longitude_a = tourismItem.getLng_location_tourism();

        float jarakKM = (float) HaversineHandler.calculateDistance(latitude, longitude, lattitude_a, longitude_a);
        @SuppressLint("DefaultLocale") String distanceFormat = String.format("%.2f", jarakKM);
        nameFavTourism.setText(tourismItem.getName_tourism());
        locationFavTourism.setText(tourismItem.getLocation_tourism());
        distanceFavTourism.setText(distanceFormat + " km");
        Glide.with(itemView.getContext()).load(tourismItem.getUrl_photo()).into(favTourismPic);
    }
}
