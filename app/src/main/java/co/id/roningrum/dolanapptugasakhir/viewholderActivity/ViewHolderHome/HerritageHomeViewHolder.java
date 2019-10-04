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

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.model.Herritage;

public class HerritageHomeViewHolder extends RecyclerView.ViewHolder {
    private final TextView nameHerritageItem;
    private final ImageView imgHerritageItem;

    public HerritageHomeViewHolder(@NonNull View itemView) {
        super(itemView);
        nameHerritageItem = itemView.findViewById(R.id.herritage_item_text);
        imgHerritageItem = itemView.findViewById(R.id.herritage_item_pic);
    }

    public void showHerritageData(Herritage herritage) {
        nameHerritageItem.setText(herritage.getName_herritage());
        Glide.with(itemView.getContext()).load(herritage.getUrl_photo_herritage()).into(imgHerritageItem);
    }
}
