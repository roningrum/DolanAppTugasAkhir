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

package co.id.roningrum.dolanapptugasakhir.adapter.transportation;

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
import co.id.roningrum.dolanapptugasakhir.model.Transportation;
import co.id.roningrum.dolanapptugasakhir.util.Utils;

public class TransportationAdapter extends RecyclerView.Adapter<TransportationAdapter.AirportViewHolder> {
    private List<Transportation> transportations = new ArrayList<>();
    private TransportationClickCallback transportationClickCallback;

    public void setTransportations(List<Transportation> transportations) {
        this.transportations = transportations;
    }

    public void setTransportationClickCallback(TransportationClickCallback transportationClickCallback) {
        this.transportationClickCallback = transportationClickCallback;
    }

    @NonNull
    @Override
    public AirportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AirportViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transportation_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AirportViewHolder holder, int position) {
        GPSHandler gpsHandler = new GPSHandler(holder.itemView.getContext());
        double lat = gpsHandler.getLatitude();
        double lng = gpsHandler.getLongitude();

        holder.bindAirportData(transportations.get(position), lat, lng);
    }

    @Override
    public int getItemCount() {
        return transportations.size();
    }

    class AirportViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTransport;
        private TextView locationTransport;
        private TextView distanceTransport;
        private ImageView transportPic;

        AirportViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTransport = itemView.findViewById(R.id.name_transporation_item);
            locationTransport = itemView.findViewById(R.id.location_transportation_item);
            distanceTransport = itemView.findViewById(R.id.distance_transportation_item);
            transportPic = itemView.findViewById(R.id.transportation_pic);
        }

        @SuppressLint({"DefaultLocale", "SetTextI18n"})
        void bindAirportData(final Transportation transportation, double latitude, double longitude) {

            double lattitude_a = transportation.getLat_location_transport();
            double longitude_a = transportation.getLng_location_transport();

            float jarakKM = (float) Utils.calculateDistance(latitude, longitude, lattitude_a, longitude_a);
            String distanceFormat = String.format("%.2f", jarakKM);

            nameTransport.setText(transportation.getName_transport());
            locationTransport.setText(transportation.getLocation_transport());
            distanceTransport.setText(distanceFormat + " km");
            Glide.with(itemView.getContext()).load(transportation.getUrl_photo_transport())
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_loading).error(R.drawable.ic_loading))
                    .into(transportPic);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    transportationClickCallback.onItemCallback(transportations.get(getAdapterPosition()));
                }
            });
        }
    }
}
