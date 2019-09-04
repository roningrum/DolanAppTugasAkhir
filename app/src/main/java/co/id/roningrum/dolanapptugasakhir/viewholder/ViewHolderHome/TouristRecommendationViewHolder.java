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

package co.id.roningrum.dolanapptugasakhir.viewholder.ViewHolderHome;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.model.TourismItem;

public class TouristRecommendationViewHolder extends RecyclerView.ViewHolder {
    private final TextView nameRecommendationTourism;
    private final ImageView imageTourismPic;
    //interface
    private TouristRecommendationViewHolder.ClickListener categoryOnClick;

    public TouristRecommendationViewHolder(@NonNull View itemView) {
        super(itemView);
        nameRecommendationTourism = itemView.findViewById(R.id.recommenadation_item_text);
        imageTourismPic = itemView.findViewById(R.id.recommendation_item_pic);
//        itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                categoryOnClick.onItemClick(v, getAdapterPosition());
//            }
//        });
    }

    public void showRecommendationTourismData(TourismItem tourismItem) {
        nameRecommendationTourism.setText(tourismItem.getName_tourism());
        Glide.with(itemView.getContext()).load(tourismItem.getUrl_photo()).into(imageTourismPic);
    }

    public void setOnClickListener(TouristRecommendationViewHolder.ClickListener clickListener) {
        categoryOnClick = clickListener;
    }

    public interface ClickListener {
        void onItemClick(View view, int position);
    }
}
