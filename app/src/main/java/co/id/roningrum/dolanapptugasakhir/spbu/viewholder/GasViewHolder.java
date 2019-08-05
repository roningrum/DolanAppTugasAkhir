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

package co.id.roningrum.dolanapptugasakhir.spbu.viewholder;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.handler.HaversineHandler;
import co.id.roningrum.dolanapptugasakhir.model.GasStationItem;

public class GasViewHolder extends RecyclerView.ViewHolder {
    private final TextView nameSPBU;
    private final TextView locationSPBU;
    private final TextView distanceSPBU;
    private final ImageView spbuPic;

    private GasViewHolder.ClickListener gasClickListener;

    public GasViewHolder(@NonNull View itemView) {
        super(itemView);
        nameSPBU = itemView.findViewById(R.id.name_spbu_item);
        locationSPBU = itemView.findViewById(R.id.location_spbu_item);
        distanceSPBU = itemView.findViewById(R.id.distance_spbu_item);
        spbuPic = itemView.findViewById(R.id.spbu_pic);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gasClickListener.onItemClick(v, getAdapterPosition());
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void showGasData(GasStationItem gasStationItem, double latitude, double longitude) {
        double lattitude_a = gasStationItem.getLat_gasstation();
        double longitude_a = gasStationItem.getLng_gasstation();

        float jarakKM = (float) HaversineHandler.calculateDistance(latitude, longitude, lattitude_a, longitude_a);

        @SuppressLint("DefaultLocale") String distanceFormat = String.format("%.2f", jarakKM);

        nameSPBU.setText(gasStationItem.getName_gasstation());
        locationSPBU.setText(gasStationItem.getLocation_gasstation());
        distanceSPBU.setText(distanceFormat + " km");
        Glide.with(itemView.getContext()).load(gasStationItem.getUrl_photo_gasstation()).into(spbuPic);

    }

    public void setOnClickListener(GasViewHolder.ClickListener clickListener) {
        gasClickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(View view, int position);
    }
}
