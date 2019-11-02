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

package co.id.roningrum.dolanapptugasakhir.viewholderActivity.gasstation;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.model.GasStation;
import co.id.roningrum.dolanapptugasakhir.util.HaversineHandler;

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
    public void showGasData(GasStation gasStation, double latitude, double longitude) {
        double lattitude_a = gasStation.getLat_gasstation();
        double longitude_a = gasStation.getLng_gasstation();

        float jarakKM = (float) HaversineHandler.calculateDistance(latitude, longitude, lattitude_a, longitude_a);

        @SuppressLint("DefaultLocale") String distanceFormat = String.format("%.2f", jarakKM);

        nameSPBU.setText(gasStation.getName_gasstation());
        locationSPBU.setText(gasStation.getLocation_gasstation());
        distanceSPBU.setText(distanceFormat + " km");
        Glide.with(itemView.getContext()).load(gasStation.getUrl_photo_gasstation()).into(spbuPic);

    }

    public void setOnClickListener(GasViewHolder.ClickListener clickListener) {
        gasClickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(View view, int position);
    }
}
