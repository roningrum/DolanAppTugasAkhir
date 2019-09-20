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

package co.id.roningrum.dolanapptugasakhir;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import co.id.roningrum.dolanapptugasakhir.model.FavoriteItem;

public class FavoritAdapter extends RecyclerView.Adapter<FavoritAdapter.FavoritViewHolder> {
    private ArrayList<FavoriteItem> favoriteItems = new ArrayList<>();

    public void setFavoriteItems(ArrayList<FavoriteItem> favoriteItems) {
        this.favoriteItems = favoriteItems;
    }

    @NonNull
    @Override
    public FavoritViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FavoritViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite_fragment, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritViewHolder holder, int position) {
        holder.bindName(favoriteItems.get(position));
    }

    @Override
    public int getItemCount() {
        return favoriteItems.size();
    }

    class FavoritViewHolder extends RecyclerView.ViewHolder {
        private TextView tvFavoritTest;

        FavoritViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFavoritTest = itemView.findViewById(R.id.tv_bookmark_item);
        }

        void bindName(FavoriteItem favoriteItem) {
            tvFavoritTest.setText(favoriteItem.getName_tourism());
        }
    }
}
