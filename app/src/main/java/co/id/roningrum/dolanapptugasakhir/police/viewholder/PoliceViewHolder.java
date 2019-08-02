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

package co.id.roningrum.dolanapptugasakhir.police.viewholder;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.handler.HaversineHandler;
import co.id.roningrum.dolanapptugasakhir.item.PoliceItem;

public class PoliceViewHolder extends RecyclerView.ViewHolder {
    private final TextView namePolice;
    private final TextView addressPolice;
    private final TextView distancePolice;
    private final ImageView policePic;

    private PoliceViewHolder.ClickListener policeClickListener;

    public PoliceViewHolder(@NonNull View itemView) {
        super(itemView);
        namePolice = itemView.findViewById(R.id.name_police_item);
        addressPolice = itemView.findViewById(R.id.location_police_item);
        distancePolice = itemView.findViewById(R.id.distance_police_item);
        policePic = itemView.findViewById(R.id.police_pic);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                policeClickListener.onItemClick(v, getAdapterPosition());
            }
        });

    }

    @SuppressLint("SetTextI18n")
    public void showPoliceData(PoliceItem policeItem, double latitude, double longitude) {
        double lattitude_a = policeItem.getLat_police();
        double longitude_a = policeItem.getLng_police();

        float jarakKM = (float) HaversineHandler.calculateDistance(latitude, longitude, lattitude_a, longitude_a);

        @SuppressLint("DefaultLocale") String distanceFormat = String.format("%.2f", jarakKM);

        namePolice.setText(policeItem.getName_police());
        addressPolice.setText(policeItem.getLocation_police());
        distancePolice.setText(distanceFormat + " km");
        Glide.with(itemView.getContext()).load(policeItem.getUrl_photo_police()).into(policePic);

    }


    public void setOnClickListener(PoliceViewHolder.ClickListener clickListener) {
        policeClickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(View view, int position);
    }
}