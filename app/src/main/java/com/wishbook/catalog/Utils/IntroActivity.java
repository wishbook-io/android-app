package com.wishbook.catalog.Utils;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;

import com.github.paolorotolo.appintro.AppIntroFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.util.AppIntro3;
import com.wishbook.catalog.commonmodels.AppIntroModel;

import java.util.ArrayList;

/**
 * Created by root on 9/8/17.
 */

public class IntroActivity extends AppIntro3 {

    ArrayList<AppIntroModel> models = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view;
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.activity_intro, null);
        setBackgroundView(view);
        //setContentView(R.layout.activity_intro);

        // Note here that we DO NOT use setContentView();

        // Add your slide fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.
        // addSlide(firstFragment);
        // addSlide(secondFragment);
        // addSlide(thirdFragment);
        //  addSlide(fourthFragment);

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.

        models = getIntent().getParcelableArrayListExtra("list");

        if(models!=null) {
            if (models.size() > 0) {
                for (int i = 0; i < models.size(); i++) {
                    addSlide(AppIntroFragment.newInstance(models.get(i).getTitle(),null, models.get(i).getDescription(),null, models.get(i).getImage(), getResources().getColor(R.color.transparent),getResources().getColor(R.color.intro_text_color),getResources().getColor(R.color.intro_text_color)));
                }
            }
        }


        // OPTIONAL METHODS
        // Override bar/separator color.
       // setBarColor( getResources().getColor(R.color.intro_colour));
       // setSeparatorColor( getResources().getColor(R.color.intro_colour));

        // Hide Skip/Done button.
        setImageSkipButton(getResources().getDrawable(R.drawable.ic_skip_button));
        setImageDoneButton(getResources().getDrawable(R.drawable.ic_done_button));
        setImageNextButton(getResources().getDrawable(R.drawable.ic_next_button));
        setFadeAnimation();
        setIndicatorColor(getResources().getColor(R.color.purchase_light_gray),getResources().getColor(R.color.intro_text_color));
        //setImageSkipButton();
        showSkipButton(true);

        setProgressButtonEnabled(true);

        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permission in Manifest.
        setVibrate(true);
        setVibrateIntensity(30);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}