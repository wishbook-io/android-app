package com.wishbook.catalog.stories;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.request.ImageRequest;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.analysis.WishbookTracker;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.ResponseStoryModel;
import com.wishbook.catalog.commonmodels.WishbookEvent;
import com.wishbook.catalog.frescoZoomable.DoubleTapGestureListener;
import com.wishbook.catalog.stories.adapter.StoriesPagerAdapter;

import java.util.HashMap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;

public class StoryActivity extends AppCompatActivity implements DoubleTapGestureListener.UpdateActivityListener {


    private Context mContext;

    ViewPager viewPager;
    StoriesPagerAdapter pagerAdapter;
    ResponseStoryModel responseStoryModel;
    static int SCREEN_OFFSET_LIMIT = 2;

    @BindView(R.id.frame_story)
    FrameLayout frame_story;

    @BindView(R.id.relative_progress)
    RelativeLayout relative_progress;

    private long story_start_time;
    private int startPos;
    private Runnable finalRunnable;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = StoryActivity.this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_story);
        ButterKnife.bind(this);

        startPos = getIntent().getIntExtra("story_start_position", 0);

        story_start_time = System.currentTimeMillis();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            postponeEnterTransition();
        }

        initView();


    }

    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new StoriesPagerAdapter(getSupportFragmentManager());
        if (getIntent().getStringExtra("story_id") != null) {
            getStoriesById(getIntent().getStringExtra("story_id"));
        }

    }

    public void callNextStory() {
        if (viewPager.getCurrentItem() + 1 < viewPager.getAdapter().getCount()) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
        } else {
            setResult(Activity.RESULT_OK);
            finish();
        }

    }

    private void addingFragmentsTOpagerAdapter() {
        try {
            for (int i = 0; i < responseStoryModel.getCatalogs_details().size(); i++) {
                Bundle bundle = new Bundle();
                bundle.putInt("position", i);
                bundle.putString("story_name",responseStoryModel.getName());
                bundle.putString("story_id", responseStoryModel.getId());
                pagerAdapter.addFragments(StoryFragment.newInstance(bundle));
            }
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public ResponseStoryModel.Catalogs_details getCatalogByPoisition(int position) {
        if (responseStoryModel != null && responseStoryModel.getCatalogs_details().size() > 0) {
            return responseStoryModel.getCatalogs_details().get(position);
        }
        return null;
    }


    private void getStoriesById(final String id) {
        showProgress();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(this);
        HttpManager.getInstance(this).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.STORIES + id + "/?expand=true", null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override

            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgress();
                if (!isFinishing()) {
                    try {
                        responseStoryModel = Application_Singleton.gson.fromJson(response, ResponseStoryModel.class);
                        sendOpenAnalyticsEvent();
                        for (int i = 0; i < responseStoryModel.getCatalogs_details().size(); i++) {
                            final DataSource<Void> ds =
                                    Fresco.getImagePipeline().prefetchToDiskCache(ImageRequest.fromUri(Uri.parse(responseStoryModel.getCatalogs_details().get(i).getThumbnail().getThumbnail_medium())), this);
                        }
                        addingFragmentsTOpagerAdapter();
                        viewPager.setAdapter(pagerAdapter);
                        viewPager.setOffscreenPageLimit(SCREEN_OFFSET_LIMIT);
                        if(Application_Singleton.SCREEN_WIDTH > 999) {
                            CubeOutRotationTransformation cubeOutRotationTransformation = new CubeOutRotationTransformation();
                            viewPager.setPageTransformer(true, cubeOutRotationTransformation);
                        }
                        Log.d("StoryActivity", "initView: ==>" + startPos);

                        ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
                                boolean first = true;

                                @Override
                                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                                    if (first && positionOffset == 0 && positionOffsetPixels == 0) {
                                        onPageSelected(0);
                                        first = false;
                                    }
                                }

                                @Override
                                public void onPageSelected(int position) {
                                    for (int i = 0; i < responseStoryModel.getCatalogs_details().size(); i++) {
                                        if (position == i) {
                                            Log.d("Gesture", "onPageSelected Start: I Postion==>"+i);
                                            ((StoryFragment) viewPager.getAdapter().instantiateItem(viewPager, i)).setStartStopProgress(true);
                                        } else {
                                            Log.d("Gesture", "onPageSelected Stop: I Postion==>"+i);
                                            ((StoryFragment) viewPager.getAdapter().instantiateItem(viewPager, i)).setStartStopProgress(false);
                                        }
                                    }
                                }

                                @Override
                                public void onPageScrollStateChanged(int state) {

                                }
                            };
                        viewPager.addOnPageChangeListener(pageChangeListener);
                        final Handler handler = new Handler();
                        final Runnable r = new Runnable() {
                            public void run() {
                                Log.d("StoryActivity", "setCurrentItem");
                                viewPager.setCurrentItem(SCREEN_OFFSET_LIMIT, true);
                                handler.postDelayed(finalRunnable, 100);


                            }
                        };

                        finalRunnable = new Runnable() {
                            public void run() {
                                Log.d("StoryActivity", "setCurrentItem");
                                viewPager.setCurrentItem(startPos, true);


                            }
                        };

                        if(startPos > SCREEN_OFFSET_LIMIT){
                            handler.postDelayed(r, 100);

                        }
                        else {
                            handler.postDelayed(finalRunnable, 100);
                        }





                        //pageChangeListener.onPageSelected(getIntent().getIntExtra("story_start_position", 0));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            }

            @Override
            public void onResponseFailed(ErrorString error) {
                new MaterialDialog.Builder(mContext)
                        .title(StaticFunctions.formatErrorTitle(error.getErrorkey()))
                        .content(error.getErrormessage())
                        .positiveText("OK")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        swipeGesture();
    }



    @Override
    public void swipeGesture() {
        try {
            Application_Singleton.trackEvent("Story","View Duration" , String.valueOf((System.currentTimeMillis() - story_start_time) /1000));
        } catch (Exception e){
            e.printStackTrace();
        }
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.slide_down);
    }


    public void showProgress() {
        relative_progress.setVisibility(View.VISIBLE);
    }

    public void hideProgress() {
        relative_progress.setVisibility(View.GONE);
    }

    public void sendOpenAnalyticsEvent() {
        WishbookEvent wishbookEvent = new WishbookEvent();
        wishbookEvent.setEvent_category(WishbookEvent.MARKETING_EVENTS_CATEGORY);
        wishbookEvent.setEvent_names("Story_Open");
        HashMap<String,String> prop = new HashMap<>();
        if(responseStoryModel!=null) {
            prop.put("story_id",responseStoryModel.getId());
            prop.put("story_name",responseStoryModel.getName());
        }
        wishbookEvent.setEvent_properties(prop);
       new WishbookTracker(mContext,wishbookEvent);
    }
}
