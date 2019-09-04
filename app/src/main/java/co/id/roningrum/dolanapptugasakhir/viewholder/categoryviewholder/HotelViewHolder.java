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

package co.id.roningrum.dolanapptugasakhir.viewholder.categoryviewholder;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.handler.HaversineHandler;
import co.id.roningrum.dolanapptugasakhir.model.HotelItem;

public class HotelViewHolder extends RecyclerView.ViewHolder {
    private final TextView nameHotel;
    private final TextView locationHotel;
    private final TextView distanceHotel;
    private final ImageView hotelPic;
    //interface
    private HotelViewHolder.ClickListener hotelOnClick;

    public HotelViewHolder(@NonNull View itemView) {
        super(itemView);
        nameHotel = itemView.findViewById(R.id.name_hotel_item_tourism);
        locationHotel = itemView.findViewById(R.id.location_hotel_item_tourism);
        distanceHotel = itemView.findViewById(R.id.distance_hotel_item_tourism);
        hotelPic = itemView.findViewById(R.id.hotel_pic);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hotelOnClick.onItemClick(v, getAdapterPosition());
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void showHotelData(HotelItem hotelItem, double latitude, double longitude) {

        double lattitude_a = hotelItem.getLat_location_hotel();
        double longitude_a = hotelItem.getLng_location_hotel();

        float jarakKM = (float) HaversineHandler.calculateDistance(latitude, longitude, lattitude_a, longitude_a);
//        float jarakKM = jarakMeter / 1000;
        @SuppressLint("DefaultLocale") String distanceFormat = String.format("%.2f", jarakKM);

        nameHotel.setText(hotelItem.getName_hotel());
        locationHotel.setText(hotelItem.getLocation_hotel());
        distanceHotel.setText(distanceFormat + " km");
        Glide.with(itemView.getContext()).load(hotelItem.getUrl_photo_hotel()).into(hotelPic);
    }


    public void setOnClickListener(HotelViewHolder.ClickListener clickListener) {
        hotelOnClick = clickListener;
    }

    public interface ClickListener {
        void onItemClick(View view, int position);
    }
}
