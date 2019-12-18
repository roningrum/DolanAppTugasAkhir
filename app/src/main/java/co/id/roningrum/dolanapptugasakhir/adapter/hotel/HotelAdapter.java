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

package co.id.roningrum.dolanapptugasakhir.adapter.hotel;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.handler.GPSHandler;
import co.id.roningrum.dolanapptugasakhir.model.Hotel;
import co.id.roningrum.dolanapptugasakhir.util.Utils;

public class HotelAdapter extends RecyclerView.Adapter<HotelAdapter.HotelViewHolder> {
    private List<Hotel> hotelList = new ArrayList<>();
    private HotelClickCallback hotelClickCallback;

    public void setHotelList(List<Hotel> hotelList) {
        this.hotelList = hotelList;
    }

    public void setHotelClickCallback(HotelClickCallback hotelClickCallback) {
        this.hotelClickCallback = hotelClickCallback;
    }

    @NonNull
    @Override
    public HotelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HotelViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hotel_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HotelViewHolder holder, int position) {
        GPSHandler gpsHandler = new GPSHandler(holder.itemView.getContext());
        double lat = gpsHandler.getLatitude();
        double lng = gpsHandler.getLongitude();
        holder.showHotelData(hotelList.get(position), lat, lng);
    }

    @Override
    public int getItemCount() {
        return hotelList.size();
    }

    class HotelViewHolder extends RecyclerView.ViewHolder {
        private TextView nameHotel;
        private TextView locationHotel;
        private TextView distanceHotel;
        private ImageView hotelPic;

        HotelViewHolder(@NonNull View itemView) {
            super(itemView);
            nameHotel = itemView.findViewById(R.id.name_hotel_item_tourism);
            locationHotel = itemView.findViewById(R.id.location_hotel_item_tourism);
            distanceHotel = itemView.findViewById(R.id.distance_hotel_item_tourism);
            hotelPic = itemView.findViewById(R.id.hotel_pic);
        }

        @SuppressLint({"DefaultLocale", "SetTextI18n"})
        void showHotelData(Hotel hotel, double latitude, double longitude) {

            double lattitude_hotel = hotel.getLat_location_hotel();
            double longitude_hotel = hotel.getLng_location_hotel();

            float jarakKM = (float) Utils.calculateDistance(latitude, longitude, lattitude_hotel, longitude_hotel);
            String distanceFormat = String.format("%.2f", jarakKM);

            nameHotel.setText(hotel.getName_hotel());
            locationHotel.setText(hotel.getLocation_hotel());
            distanceHotel.setText(distanceFormat + " km");
            Glide.with(itemView.getContext()).load(hotel.getUrl_photo_hotel())
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_loading).error(R.drawable.ic_loading))
                    .into(hotelPic);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hotelClickCallback.onItemClicked(hotelList.get(getAdapterPosition()));
                }
            });
        }
    }
}
