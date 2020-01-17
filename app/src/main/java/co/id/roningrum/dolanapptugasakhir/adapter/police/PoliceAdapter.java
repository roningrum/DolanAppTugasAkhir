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

package co.id.roningrum.dolanapptugasakhir.adapter.police;

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
import co.id.roningrum.dolanapptugasakhir.model.Police;
import co.id.roningrum.dolanapptugasakhir.util.Utils;

public class PoliceAdapter extends RecyclerView.Adapter<PoliceAdapter.PoliceViewHolder> {
    private List<Police> policeList = new ArrayList<>();
    private PoliceClickCallback policeClickCallback;

    public void setPoliceList(List<Police> policeList) {
        this.policeList = policeList;
    }

    public void setPoliceClickCallback(PoliceClickCallback policeClickCallback) {
        this.policeClickCallback = policeClickCallback;
    }

    @NonNull
    @Override
    public PoliceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PoliceViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_police_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PoliceViewHolder holder, int position) {
        GPSHandler gpsHandler = new GPSHandler(holder.itemView.getContext());
        double lat = gpsHandler.getLatitude();
        double lng = gpsHandler.getLongitude();
        holder.showPoliceData(policeList.get(position), lat, lng);
    }

    @Override
    public int getItemCount() {
        return policeList.size();
    }

    class PoliceViewHolder extends RecyclerView.ViewHolder {
        private TextView namePolice;
        private TextView addressPolice;
        private TextView distancePolice;
        private ImageView policePic;

        PoliceViewHolder(@NonNull View itemView) {
            super(itemView);
            namePolice = itemView.findViewById(R.id.name_police_item);
            addressPolice = itemView.findViewById(R.id.location_police_item);
            distancePolice = itemView.findViewById(R.id.distance_police_item);
            policePic = itemView.findViewById(R.id.police_pic);
        }

        @SuppressLint({"DefaultLocale", "SetTextI18n"})
        void showPoliceData(Police police, double latitude, double longitude) {
            double lattitude_a = police.getLat_location_police();
            double longitude_a = police.getLng_location_police();

            float jarakKM = (float) Utils.calculateDistance(latitude, longitude, lattitude_a, longitude_a);

            String distanceFormat = String.format("%.2f", jarakKM);
            namePolice.setText(police.getName_police());
            addressPolice.setText(police.getLocation_police());
            distancePolice.setText(distanceFormat + " km");
            Glide.with(itemView.getContext()).load(police.getUrl_photo_police())
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_loading).error(R.drawable.ic_loading))
                    .into(policePic);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    policeClickCallback.onItemCallback(policeList.get(getAdapterPosition()));
                }
            });

        }
    }
}
