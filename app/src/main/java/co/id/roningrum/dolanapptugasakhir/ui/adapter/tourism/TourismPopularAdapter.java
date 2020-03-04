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

package co.id.roningrum.dolanapptugasakhir.ui.adapter.tourism;

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
import co.id.roningrum.dolanapptugasakhir.model.Tourism;

public class TourismPopularAdapter extends RecyclerView.Adapter<TourismPopularAdapter.TourismViewHolder> {
    private List<Tourism> tourismList = new ArrayList<>();
    private TourismClickCallback onItemClickCallback;

    public void setTourismList(List<Tourism> tourismList) {
        this.tourismList = tourismList;
    }

    public void setOnItemClickCallback(TourismClickCallback onItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback;
    }

    @NonNull
    @Override
    public TourismViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TourismViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tourism_popular_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TourismViewHolder holder, int position) {
        holder.showTourismData(tourismList.get(position));
    }

    @Override
    public int getItemCount() {
        return tourismList.size();
    }


    class TourismViewHolder extends RecyclerView.ViewHolder {
        ImageView tourismPic;
        TextView tourismName;

        TourismViewHolder(@NonNull View itemView) {
            super(itemView);
            tourismPic = itemView.findViewById(R.id.tourism_pic);
            tourismName = itemView.findViewById(R.id.name_tourism);
        }

        @SuppressLint({"DefaultLocale", "SetTextI18n"})
        void showTourismData(final Tourism tourism) {
            tourismName.setText(tourism.getName_tourism());
            Glide.with(itemView.getContext()).load(tourism.getUrl_photo_tourism())
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_loading).error(R.drawable.ic_loading))
                    .into(tourismPic);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickCallback.onItemClicked(tourismList.get(getAdapterPosition()));
                }
            });
        }
    }
}
