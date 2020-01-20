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

package co.id.roningrum.dolanapptugasakhir.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.smarteist.autoimageslider.SliderViewAdapter;

import co.id.roningrum.dolanapptugasakhir.R;

public class SlideAdapterExample extends SliderViewAdapter<SlideAdapterExample.SliderAdapterViewHolder> {
    @Override
    public SlideAdapterExample.SliderAdapterViewHolder onCreateViewHolder(ViewGroup parent) {
        return new SliderAdapterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_slider, parent, false));
    }

    @Override
    public void onBindViewHolder(SlideAdapterExample.SliderAdapterViewHolder viewHolder, int position) {
        switch (position) {
            case 0:
                Glide.with(viewHolder.itemView)
                        .load(R.drawable.museummandhalabaktijpg)
                        .into(viewHolder.imgSliderHome);
                break;
            case 1:
                Glide.with(viewHolder.itemView)
                        .load(R.drawable.mangroveeduparkjpg)
                        .into(viewHolder.imgSliderHome);
                break;
            case 2:
                Glide.with(viewHolder.itemView)
                        .load(R.drawable.jatirejo)
                        .into(viewHolder.imgSliderHome);
                break;
            case 3:
                Glide.with(viewHolder.itemView)
                        .load(R.drawable.jamalsari)
                        .into(viewHolder.imgSliderHome);
                break;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    class SliderAdapterViewHolder extends SliderViewAdapter.ViewHolder {
        View itemView;
        ImageView imgSliderHome;

        SliderAdapterViewHolder(View itemView) {
            super(itemView);
            imgSliderHome = itemView.findViewById(R.id.img_slider_home_example);
            this.itemView = itemView;
        }
    }
}
