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

package co.id.roningrum.dolanapptugasakhir.adapter.hospital;

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
import co.id.roningrum.dolanapptugasakhir.model.Hospital;
import co.id.roningrum.dolanapptugasakhir.util.Util;

public class HospitalAdapter extends RecyclerView.Adapter<HospitalAdapter.HospitalViewHolder> {
    private List<Hospital> hospitalList = new ArrayList<>();
    private HospitalClickCallback hospitalClickCallback;

    public void setHospitalList(List<Hospital> hospitalList) {
        this.hospitalList = hospitalList;
    }

    public void setHospitalClickCallback(HospitalClickCallback hospitalClickCallback) {
        this.hospitalClickCallback = hospitalClickCallback;
    }

    @NonNull
    @Override
    public HospitalAdapter.HospitalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HospitalViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hospital_facility_menu, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HospitalAdapter.HospitalViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return hospitalList.size();
    }

    class HospitalViewHolder extends RecyclerView.ViewHolder {
        private TextView nameHospital;
        private TextView locationHospital;
        private TextView distaceHospital;
        private ImageView hospitalPic;

        HospitalViewHolder(@NonNull View itemView) {
            super(itemView);
            nameHospital = itemView.findViewById(R.id.name_hospital_item);
            locationHospital = itemView.findViewById(R.id.location_hospital_item);
            distaceHospital = itemView.findViewById(R.id.distance_hospital_item);
            hospitalPic = itemView.findViewById(R.id.hospital_pic);
        }

        @SuppressLint({"DefaultLocale", "SetTextI18n"})
        void bindToHospital(Hospital hospital, double latitude, double longitude) {
            double latitude_a = hospital.getLat_hospital();
            double longitude_a = hospital.getLng_hospital();

            float jarakKM = (float) Util.calculateDistance(latitude, longitude, latitude_a, longitude_a);
            nameHospital.setText(hospital.getName_hospital());
            locationHospital.setText(hospital.getLocation_hospital());

            String distanceFormat = String.format("%.2f", jarakKM);
            distaceHospital.setText(distanceFormat + "km");
            Glide.with(itemView.getContext()).load(hospital.getUrl_photo_hospital()).into(hospitalPic);
        }
    }
}
