/*
 * Copyright 2020 RONINGRUM. All rights reserved.
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

package co.id.roningrum.dolanapptugasakhir.ui.favorite.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
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

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.handler.GPSHandler;
import co.id.roningrum.dolanapptugasakhir.model.Transportation;
import co.id.roningrum.dolanapptugasakhir.ui.adapter.transportation.TransportationClickCallback;
import co.id.roningrum.dolanapptugasakhir.util.Utils;

public class FavoriteTransAdapter extends RecyclerView.Adapter<FavoriteTransAdapter.FavoritViewHolder> {
    private ArrayList<Transportation> transportations;
    private Context context;
    private TransportationClickCallback transportationClickCallback;

    public FavoriteTransAdapter(ArrayList<Transportation> transports, Context context) {
        this.transportations = transports;
        this.context = context;
    }


    @NonNull
    @Override
    public FavoritViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FavoritViewHolder(LayoutInflater.from(context).inflate(R.layout.item_favorite_fragment, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final FavoritViewHolder holder, final int position) {
        GPSHandler gpsHandler = new GPSHandler(context);
        if (gpsHandler.isCanGetLocation()) {
            double latitude = gpsHandler.getLatitude();
            double longitude = gpsHandler.getLongitude();

            holder.bindTransData(transportations.get(position), latitude, longitude);
        } else {
            gpsHandler.stopUsingGPS();
            gpsHandler.showSettingsAlert();
        }

    }

    @Override
    public int getItemCount() {
        return transportations.size();
    }

    public void removeItem(int position) {
        transportations.remove(position);
        notifyItemRemoved(position);
    }

    public void setTransportationClickCallback(TransportationClickCallback transportationClickCallback) {
        this.transportationClickCallback = transportationClickCallback;
    }

    class FavoritViewHolder extends RecyclerView.ViewHolder {
        private TextView nameFavTourism;
        private TextView locationFavTourism;
        private TextView distanceFavTourism;
        private ImageView favTourismPic;

        FavoritViewHolder(@NonNull View itemView) {
            super(itemView);
            nameFavTourism = itemView.findViewById(R.id.name_fav_item_tourism);
            locationFavTourism = itemView.findViewById(R.id.location_fav_item_tourism);
            distanceFavTourism = itemView.findViewById(R.id.distance_fav_item_tourism);
            favTourismPic = itemView.findViewById(R.id.tourism_fav_pic);
        }

        @SuppressLint("SetTextI18n")
        void bindTransData(final Transportation favoriteItem, double lat, double lng) {

            double lattitude_a = favoriteItem.getLat_location_transport();
            double longitude_a = favoriteItem.getLng_location_transport();

            float jarakKM = (float) Utils.calculateDistance(lat, lng, lattitude_a, longitude_a);
            nameFavTourism.setText(favoriteItem.getName_transport());
            locationFavTourism.setText(favoriteItem.getLocation_transport());
            @SuppressLint("DefaultLocale") String distanceFormat = String.format("%.2f", jarakKM);
            distanceFavTourism.setText(distanceFormat + " km");
            Glide.with(itemView.getContext()).load(favoriteItem.getUrl_photo_transport())
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_loading).error(R.drawable.ic_loading))
                    .into(favTourismPic);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    transportationClickCallback.onItemCallback(transportations.get(getAdapterPosition()));
                }
            });
        }
    }

}