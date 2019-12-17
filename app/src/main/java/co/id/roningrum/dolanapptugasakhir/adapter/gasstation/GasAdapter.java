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

package co.id.roningrum.dolanapptugasakhir.adapter.gasstation;

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
import co.id.roningrum.dolanapptugasakhir.model.GasStation;
import co.id.roningrum.dolanapptugasakhir.util.Util;

public class GasAdapter extends RecyclerView.Adapter<GasAdapter.GasViewHolder> {
    private List<GasStation> gasStations = new ArrayList<>();
    private GasClickCallback gasClickCallback;

    public void setGasStations(List<GasStation> gasStations) {
        this.gasStations = gasStations;
    }

    public void setGasClickCallback(GasClickCallback gasClickCallback) {
        this.gasClickCallback = gasClickCallback;
    }

    @NonNull
    @Override
    public GasAdapter.GasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GasViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gasstation_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GasAdapter.GasViewHolder holder, int position) {
        GPSHandler gpsHandler = new GPSHandler(holder.itemView.getContext());
        double lat = gpsHandler.getLatitude();
        double lng = gpsHandler.getLongitude();
        holder.showGasData(gasStations.get(position), lat, lng);

    }

    @Override
    public int getItemCount() {
        return gasStations.size();
    }

    class GasViewHolder extends RecyclerView.ViewHolder {
        private TextView nameSPBU;
        private TextView locationSPBU;
        private TextView distanceSPBU;
        private ImageView spbuPic;

        GasViewHolder(@NonNull View itemView) {
            super(itemView);
            nameSPBU = itemView.findViewById(R.id.name_spbu_item);
            locationSPBU = itemView.findViewById(R.id.location_spbu_item);
            distanceSPBU = itemView.findViewById(R.id.distance_spbu_item);
            spbuPic = itemView.findViewById(R.id.spbu_pic);
        }

        @SuppressLint({"DefaultLocale", "SetTextI18n"})
        void showGasData(GasStation gasStation, double latitude, double longitude) {
            double lattitude_a = gasStation.getLat_gasstation();
            double longitude_a = gasStation.getLng_gasstation();

            float jarakKM = (float) Util.calculateDistance(latitude, longitude, lattitude_a, longitude_a);

            String distanceFormat = String.format("%.2f", jarakKM);

            nameSPBU.setText(gasStation.getName_gasstation());
            locationSPBU.setText(gasStation.getLocation_gasstation());
            distanceSPBU.setText(distanceFormat + " km");
            Glide.with(itemView.getContext()).
                    load(gasStation.getUrl_photo_gasstation())
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_loading).error(R.drawable.ic_loading))
                    .into(spbuPic);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gasClickCallback.onItemClicked(gasStations.get(getAdapterPosition()));
                }
            });

        }
    }
}
