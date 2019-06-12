package co.id.roningrum.dolanapptugasakhir;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class DetailCategoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_category);

        Toolbar toolbarDetail = findViewById(R.id.toolbar_detail);
        setSupportActionBar(toolbarDetail);

        CollapsingToolbarLayout collapsingToolbarLayoutDetail = findViewById(R.id.collapsing_layout_detail);
        collapsingToolbarLayoutDetail.setCollapsedTitleTextColor(ContextCompat.getColor(this, R.color.txtWhitePrimary));
        collapsingToolbarLayoutDetail.setExpandedTitleColor(ContextCompat.getColor(this, R.color.orangePrimary));
    }
}
