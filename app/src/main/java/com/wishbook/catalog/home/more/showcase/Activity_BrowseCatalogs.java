package com.wishbook.catalog.home.more.showcase;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;

import java.util.ArrayList;

import com.wishbook.catalog.R;
import com.wishbook.catalog.commonadapters.ImageSliderAdapter;

public class Activity_BrowseCatalogs extends AppCompatActivity {

    private static ViewPager mPager;
    private ArrayList<Integer> ImagesArray = new ArrayList<Integer>();
    int[] IMAGES = {
     /*       R.drawable.browse_1,
            R.drawable.browse_2,
            R.drawable.browse_3,
            R.drawable.browse_4,
            R.drawable.browse_5,
            R.drawable.browse_6,
            R.drawable.browse_7,
            R.drawable.browse_8*/
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_catalogs);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.browse_catalogs_label));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        for(int i=0;i<IMAGES.length;i++)
            ImagesArray.add(IMAGES[i]);

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new ImageSliderAdapter(Activity_BrowseCatalogs.this,ImagesArray));

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
