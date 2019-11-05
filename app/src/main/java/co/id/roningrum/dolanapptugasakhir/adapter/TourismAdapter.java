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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.handler.GPSHandler;
import co.id.roningrum.dolanapptugasakhir.model.Tourism;
import co.id.roningrum.dolanapptugasakhir.util.Util;

public class TourismAdapter extends RecyclerView.Adapter<TourismAdapter.TourismViewHolder> {
    private List<Tourism> tourismList = new ArrayList<>();
    private TourismAdapter.OnItemClickCallback onItemClickCallback;

    public void setOnItemClickCallback(OnItemClickCallback onItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback;
    }

    public void setTourismList(List<Tourism> tourismList) {
        this.tourismList = tourismList;
    }

    @NonNull
    @Override
    public TourismViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TourismViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu_category_tourism, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TourismViewHolder holder, int position) {
        GPSHandler gpsHandler = new GPSHandler(holder.itemView.getContext());
        double lat = gpsHandler.getLatitude();
        double lng = gpsHandler.getLongitude();
        holder.showTourismData(tourismList.get(position), lat, lng);
    }

    @Override
    public int getItemCount() {
        return tourismList.size();
    }


    public interface OnItemClickCallback {
        void onItemClicked(Tourism tourism);
    }

    class TourismViewHolder extends RecyclerView.ViewHolder {
        ImageView tourismPic;
        TextView tourismName;
        TextView tourismLocation;
        TextView tourismDistance;

        TourismViewHolder(@NonNull View itemView) {
            super(itemView);
            tourismPic = itemView.findViewById(R.id.tourism_pic);
            tourismName = itemView.findViewById(R.id.name_tourism);
            tourismLocation = itemView.findViewById(R.id.location_tourism);
            tourismDistance = itemView.findViewById(R.id.distance_tourism);
        }

        @SuppressLint({"DefaultLocale", "SetTextI18n"})
        void showTourismData(final Tourism tourism, double latitude, double longitude) {
            double latitude_a = tourism.getLat_location_tourism();
            double longitude_a = tourism.getLng_location_tourism();

            float jarakKM = (float) Util.calculateDistance(latitude, longitude, latitude_a, longitude_a);
            String distanceFormat = String.format("%.2f", jarakKM);
            tourismName.setText(tourism.getName_tourism());
            tourismLocation.setText(tourism.getLocation_tourism());
            tourismDistance.setText(distanceFormat + " " + itemView.getContext().getString(R.string.km));
            Glide.with(itemView.getContext()).load(tourism.getUrl_photo()).into(tourismPic);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickCallback.onItemClicked(tourismList.get(getAdapterPosition()));
                }
            });
        }
    }
}
