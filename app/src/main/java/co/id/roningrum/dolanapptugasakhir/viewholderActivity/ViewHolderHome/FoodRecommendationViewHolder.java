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

package co.id.roningrum.dolanapptugasakhir.viewholderActivity.ViewHolderHome;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.model.Tourism;

public class FoodRecommendationViewHolder extends RecyclerView.ViewHolder {
    private final TextView nameFoodRecommendation;
    private final ImageView foodRecommendationTourismPic;
    //interface
    private FoodRecommendationViewHolder.ClickListener categoryOnClick;

    public FoodRecommendationViewHolder(@NonNull View itemView) {
        super(itemView);
        nameFoodRecommendation = itemView.findViewById(R.id.recommenadation_food_item_text);
        foodRecommendationTourismPic = itemView.findViewById(R.id.recommendation_food_item_pic);
    }

    @SuppressLint("SetTextI18n")
    public void showFoodRecommendationTourismData(Tourism tourism) {
        nameFoodRecommendation.setText(tourism.getName_tourism());
        Glide.with(itemView.getContext()).load(tourism.getUrl_photo()).into(foodRecommendationTourismPic);
    }

    public void setOnClickListener(FoodRecommendationViewHolder.ClickListener clickListener) {
        categoryOnClick = clickListener;
    }

    public interface ClickListener {
        void onItemClick(View view, int position);
    }
}
