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

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.item.CategoryItem;


//public class CategoryViewHolder extends RecyclerView.Adapter<CategoryViewHolder.CategoryViewHolder> {
//    Context context;
//    ArrayList<CategoryItem> categoryItems;
//
//    public CategoryViewHolder(Context context, ArrayList<CategoryItem> categoryItems) {
//        this.context = context;
//        this.categoryItems = categoryItems;
//    }
//
//    @NonNull
//    @Override
//    public CategoryViewHolder.CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//        return new CategoryViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_category_menu,viewGroup,false));
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull CategoryViewHolder.CategoryViewHolder categoryViewHolder, int i) {
//        categoryViewHolder.showTourismData(categoryItems.get(i));
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return categoryItems.size();
//    }
//
public class CategoryViewHolder extends RecyclerView.ViewHolder {
    private final TextView name_tourisms;
    private final TextView location_tourism;
    private final TextView distance_tourism;
    private final ImageView tourism_pic;



    public CategoryViewHolder(@NonNull View itemView) {
        super(itemView);
        name_tourisms = itemView.findViewById(R.id.tv_name_tourism);
        location_tourism = itemView.findViewById(R.id.tv_location_tourism);
        distance_tourism= itemView.findViewById(R.id.tv_distance_tourism);
        tourism_pic = itemView.findViewById(R.id.tourism_pic);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryOnClick.onItemClick(v, getAdapterPosition());
            }
        });

    }

    public void showTourismData(CategoryItem categoryItem, double longitude, double latitude) {
//
//        Location loc1 = new Location("");
//        loc1.setLatitude(latitude);
//        loc1.setLongitude(longitude);
//        Location loc2 = new Location("");
//        loc2.setLatitude(categoryItem.getLat_location_tourism());
//        loc2.setLongitude(categoryItem.getLng_location_tourism());
//
        double lattitude_a = categoryItem.getLat_location_tourism();
        double longitude_a = categoryItem.getLng_location_tourism();

        float jarakMeter = (float) calculateDistance(latitude,longitude,lattitude_a,longitude_a);
//        float jarakMeter = loc1.distanceTo(loc2);
        float jarakKM = jarakMeter/1000;
        @SuppressLint("DefaultLocale") String distanceFormat = String.format("%.2f",jarakKM);

        name_tourisms.setText(categoryItem.getName_tourism());
        location_tourism.setText(categoryItem.getLocation_tourism());
        distance_tourism.setText(distanceFormat+" KM");
        Glide.with(itemView.getContext()).load(categoryItem.getUrl_photo()).into(tourism_pic);
    }

    private double calculateDistance(double lat1, double long1, double lat2, double long2) {

        double earthRadius = 6371;
        double latDiff = Math.toRadians(lat1-lat2);
        double lngDiff = Math.toRadians(long1-long2);
        double a = Math.sin(latDiff /2) * Math.sin(latDiff /2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(lngDiff /2) * Math.sin(lngDiff /2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = earthRadius * c;

        int meterConversion = 1609;

        return (float) distance * meterConversion;
    }

    //interface
    private CategoryViewHolder.ClickListener categoryOnClick;
    public interface ClickListener{
        void onItemClick (View view, int position);
    }
    public void setOnClickListener(CategoryViewHolder.ClickListener clickListener){
        categoryOnClick = clickListener;
    }


}
//}
