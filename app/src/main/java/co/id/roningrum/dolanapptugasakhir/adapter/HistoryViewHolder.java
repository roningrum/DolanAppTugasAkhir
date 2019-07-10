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

package co.id.roningrum.dolanapptugasakhir.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import co.id.roningrum.dolanapptugasakhir.R;

public class HistoryViewHolder extends RecyclerView.ViewHolder {
    private final TextView nameHisToryTourismItem;
    private final TextView locationHisToryTourismItem;
    private final TextView distanceHisToryTourismItem;
    private final ImageView hisToryTourismpic;

    public HistoryViewHolder(@NonNull View itemView) {
        super(itemView);
        nameHisToryTourismItem = itemView.findViewById(R.id.name_history_item_tourism);
        locationHisToryTourismItem = itemView.findViewById(R.id.location_history_item_tourism);
        distanceHisToryTourismItem = itemView.findViewById(R.id.distance_history_item_tourism);
        hisToryTourismpic = itemView.findViewById(R.id.tourism_history_pic);
    }

}
