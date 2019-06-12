package co.id.roningrum.dolanapptugasakhir.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.item.CategoryItem;


//public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
//    Context context;
//    ArrayList<CategoryItem> categoryItems;
//
//    public CategoryAdapter(Context context, ArrayList<CategoryItem> categoryItems) {
//        this.context = context;
//        this.categoryItems = categoryItems;
//    }
//
//    @NonNull
//    @Override
//    public CategoryAdapter.CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//        return new CategoryViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_category_menu,viewGroup,false));
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull CategoryAdapter.CategoryViewHolder categoryViewHolder, int i) {
//        categoryViewHolder.showTourismData(categoryItems.get(i));
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return categoryItems.size();
//    }
//
public class CategoryAdapter extends RecyclerView.ViewHolder {
    TextView name_tourisms, location_tourism;
    ImageView tourism_pic;

    public CategoryAdapter(@NonNull View itemView) {
        super(itemView);
        name_tourisms = itemView.findViewById(R.id.tv_name_tourism);
        location_tourism = itemView.findViewById(R.id.tv_location_tourism);
        tourism_pic = itemView.findViewById(R.id.tourism_pic);
    }

    public void showTourismData(CategoryItem categoryItem) {
        name_tourisms.setText(categoryItem.getName_tourism());
        location_tourism.setText(categoryItem.getLocation_tourism());
        Glide.with(itemView.getContext()).load(categoryItem.getUrl_photo()).into(tourism_pic);
    }
}
//}
