package com.wishbook.catalog.home.more.showcase;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;

import java.util.ArrayList;

import com.wishbook.catalog.R;
import com.wishbook.catalog.commonadapters.ImageSliderAdapter;

public class Activity_ManagingSelections extends AppCompatActivity {

    private static ViewPager mPager;
    private ArrayList<Integer> ImagesArray = new ArrayList<Integer>();
    int[] IMAGES = {
          //  R.drawable.selections_1,
        //    R.drawable.selections_2,
         //   R.drawable.selections_3,
         //   R.drawable.selections_4
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_managing_selections);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.manage_selections_label));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        for (int i = 0; i < IMAGES.length; i++)
            ImagesArray.add(IMAGES[i]);

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new ImageSliderAdapter(Activity_ManagingSelections.this, ImagesArray));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
