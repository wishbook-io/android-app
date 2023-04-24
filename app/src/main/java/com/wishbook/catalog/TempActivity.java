package com.wishbook.catalog;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

public class TempActivity extends AppCompatActivity {


    SimpleDraweeView noncatalog_banner_img;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);


        noncatalog_banner_img = findViewById(R.id.noncatalog_banner_img);
        DraweeController controller =
                Fresco.newDraweeControllerBuilder()
                        .setUri("http://b2b.wishbook.io/media/promotion_image/VID_20181121_170422.gif")
                        .setAutoPlayAnimations(true)
                        .build();

        noncatalog_banner_img.setController(controller);
    }
}
