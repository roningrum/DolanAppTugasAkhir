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

package co.id.roningrum.dolanapptugasakhir.hospital;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.handler.HaversineHandler;
import co.id.roningrum.dolanapptugasakhir.item.HospitalItem;

public class HospitalViewHolder extends RecyclerView.ViewHolder {
    private final TextView nameHospital;
    private final TextView locationHospital;
    private final TextView distaceHospital;
    private final ImageView hospitalPic;

    private HospitalViewHolder.ClickListener hospitalClickListener;

    public HospitalViewHolder(@NonNull View itemView) {
        super(itemView);
        nameHospital = itemView.findViewById(R.id.name_hospital_item);
        locationHospital = itemView.findViewById(R.id.location_hospital_item);
        distaceHospital = itemView.findViewById(R.id.distance_hospital_item);
        hospitalPic = itemView.findViewById(R.id.hospital_pic);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hospitalClickListener.onItemClick(v, getAdapterPosition());
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void showHospitalData(HospitalItem hospitalItem, double latitude, double longitude) {

        double latitude_a = hospitalItem.getLat_hospital();
        double longitude_a = hospitalItem.getLng_hospital();

        float jarakKM = (float) HaversineHandler.calculateDistance(latitude, longitude, latitude_a, longitude_a);
        nameHospital.setText(hospitalItem.getName_hospital());
        locationHospital.setText(hospitalItem.getLocation_hospital());
        @SuppressLint("DefaultLocale") String distanceFormat = String.format("%.2f", jarakKM);
        distaceHospital.setText(distanceFormat + "km");
        Glide.with(itemView.getContext()).load(hospitalItem.getUrl_photo_hospital()).into(hospitalPic);
    }

    public void setOnClickListener(HospitalViewHolder.ClickListener clickListener) {
        hospitalClickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(View view, int position);
    }
}
