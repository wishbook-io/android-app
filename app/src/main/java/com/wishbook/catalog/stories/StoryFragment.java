package com.wishbook.catalog.stories;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.datasource.BaseDataSubscriber;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.ImageUtils;
import com.wishbook.catalog.Utils.PrefDatabaseUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.analysis.WishbookTracker;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.Utils.widget.CustomCountDownTimer;
import com.wishbook.catalog.commonmodels.ResponseStoryModel;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.WishbookEvent;
import com.wishbook.catalog.commonmodels.responses.Image;
import com.wishbook.catalog.frescoZoomable.DefaultZoomableController;
import com.wishbook.catalog.frescoZoomable.ZoomableDraweeView;
import com.wishbook.catalog.home.catalog.Fragment_BrowseCatalogs;
import com.wishbook.catalog.home.catalog.Fragment_Catalogs;
import com.wishbook.catalog.home.catalog.details.Fragment_CatalogsGallery;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import at.grabner.circleprogress.CircleProgressView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StoryFragment extends Fragment implements StoryStatusView.UserInteractionListener, DefaultZoomableController.ZoomGestureListener {


    @BindView(R.id.img_story)
    ZoomableDraweeView img_story;


    @BindView(R.id.last_frame)
    FrameLayout last_frame;

    @BindView(R.id.play_next_progress)
    CircleProgressView play_next_progress;

    @BindView(R.id.circle_play_next)
    RelativeLayout circle_play_next;

    @BindView(R.id.btn_show_details)
    AppCompatButton btn_show_details;

    @BindView(R.id.btn_add_to_wishlist_bottom)
    AppCompatButton btn_add_to_wishlist_bottom;

    @BindView(R.id.btn_add_to_wishlist)
    AppCompatButton btn_add_to_wishlist;

    @BindView(R.id.relative_bottom_action)
    RelativeLayout relative_bottom_action;

    @BindView(R.id.img_brand_logo)
    SimpleDraweeView img_brand_logo;

    @BindView(R.id.txt_brand_name)
    TextView txt_brand_name;

    @BindView(R.id.txt_catalog_name)
    TextView txt_catalog_name;

    StoryStatusView storyStatusView;
    private Context mContext;

    @BindView(R.id.relative_brand)
    RelativeLayout relative_brand;

    @BindView(R.id.linear_wishlist_container)
    LinearLayout linear_wishlist_container;

    @BindView(R.id.linear_brand_name_container)
    LinearLayout linear_brand_name_container;

    @BindView(R.id.img_back)
    ImageView img_back;


    private int counter = 0;
    private static long STORY_TIMER = 3000L;

    private CustomCountDownTimer customCountDownTimer;


    public static final String STATUS_DURATION_KEY = "statusStoriesDuration";
    // TestCatalogStoryModel storyModel;
    ResponseStoryModel.Catalogs_details storyModel;

    private static String TAG = "Gesture";
    private DataSource<Void> ds;

    public StoryFragment() {

    }

    public static StoryFragment newInstance(Bundle bundle) {
        StoryFragment fragment = new StoryFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.story_item_fragment, container, false);
        mContext = getActivity();
        ButterKnife.bind(this, view);
        Log.d("StoryFragment", "Pos:" + getArguments().getInt("position") + 1 + "  onCreateView");
        initView(view);
        return view;
    }

    private void initView(View view) {
        if (UserInfo.getInstance(getActivity()).getCompanyType().equals("seller")) {
            // Hide Wishlist Button
            btn_add_to_wishlist_bottom.setVisibility(View.GONE);
            linear_wishlist_container.setVisibility(View.INVISIBLE);
        } else {
            // Show Wishlist Button
            btn_add_to_wishlist_bottom.setVisibility(View.VISIBLE);
            linear_wishlist_container.setVisibility(View.VISIBLE);
            btn_add_to_wishlist.setVisibility(View.VISIBLE);
        }
        fillupData(getArguments().getInt("position"));
        if (storyModel != null) {
            storyStatusView = view.findViewById(R.id.storiesStatus);
            storyStatusView.setStoriesCount(storyModel.getProducts().size() + 1);
            storyStatusView.setStoryDuration(STORY_TIMER);
            storyStatusView.setUserInteractionListener(this);
            img_story.setVisibility(View.VISIBLE);
            img_story.getHierarchy().setProgressBarImage(new CircleProgressBarDrawable(storyStatusView, getActivity()));
            if(!UserInfo.getInstance(getActivity()).getCompanyType().equals("seller") && storyModel.isI_am_selling_this()) {
                btn_add_to_wishlist_bottom.setVisibility(View.GONE);
                linear_wishlist_container.setVisibility(View.INVISIBLE);
            }
            counter = 0;
            if (storyModel.getBrand().getImage() != null) {
                StaticFunctions.loadFresco(mContext, storyModel.getBrand().getImage().getThumbnail_small(), img_brand_logo);
                img_brand_logo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        storyStatusView.pause();
                        Bundle bundle = new Bundle();
                        bundle.putString("filtertype", "brand");
                        bundle.putString("filtervalue", storyModel.getBrand().getId());
                        Fragment_Catalogs fragmentCatalogs = new Fragment_Catalogs();
                        fragmentCatalogs.setArguments(bundle);
                        if (storyModel.getBrand().getName() != null) {
                            Application_Singleton.CONTAINER_TITLE = storyModel.getBrand().getName();
                        } else {
                            Application_Singleton.CONTAINER_TITLE = "Catalogs";
                        }
                        Application_Singleton.CONTAINERFRAG = fragmentCatalogs;
                        Intent intent = new Intent(getActivity(), OpenContainer.class);
                        startActivity(intent);
                    }
                });

                linear_brand_name_container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        storyStatusView.pause();
                        Bundle bundle = new Bundle();
                        bundle.putString("filtertype", "brand");
                        bundle.putString("filtervalue", storyModel.getBrand().getId());
                        Fragment_Catalogs fragmentCatalogs = new Fragment_Catalogs();
                        fragmentCatalogs.setArguments(bundle);
                        if (storyModel.getBrand().getName() != null) {
                            Application_Singleton.CONTAINER_TITLE = storyModel.getBrand().getName();
                        } else {
                            Application_Singleton.CONTAINER_TITLE = "Catalogs";
                        }
                        Application_Singleton.CONTAINERFRAG = fragmentCatalogs;
                        Intent intent = new Intent(getActivity(), OpenContainer.class);
                        startActivity(intent);
                    }
                });
            }


            txt_brand_name.setText(storyModel.getBrand().getName());
            txt_catalog_name.setText(storyModel.getTitle());
            ImageUtils.loadFresco1(mContext, storyModel.getProducts().get(counter).getImage().getThumbnail_medium(), img_story, storyStatusView);
            final PrefetchSubscriber subscriber = new PrefetchSubscriber();

            if (storyModel != null && storyModel.getProducts().size() > 0) {
                for (int i = 0; i < storyModel.getProducts().size(); i++) {
                    ds =
                            Fresco.getImagePipeline().prefetchToDiskCache(ImageRequest.fromUri(Uri.parse(storyModel.getProducts().get(i).getImage().getThumbnail_medium())), getContext());

                }
            }
            initListener();
        }

    }

    private void initListener() {


        circle_play_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onComplete();
            }
        });

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((StoryActivity) getActivity()).swipeGesture();
            }
        });
    }


    @OnClick({R.id.btn_add_to_wishlist_bottom, R.id.btn_add_to_wishlist})
    public void btn_wishlist_click() {
        if (storyModel != null) {
            if (UserInfo.getInstance(getActivity()).isGuest()) {
                StaticFunctions.ShowRegisterDialog(getActivity(), "Story Page");
                return;
            }
            saveWishlist(getActivity(), storyModel.getProduct_id());
        }
    }


    @OnClick({R.id.btn_show_details_bottom, R.id.btn_show_details})
    public void btn_show_details_click() {
        if (storyModel != null) {
            storyStatusView.pause();
            Bundle bundle = new Bundle();
            bundle.putString("from",Fragment_BrowseCatalogs.class.getSimpleName());
            bundle.putString("product_id",storyModel.getProduct_id());
            Fragment_CatalogsGallery gallery = new Fragment_CatalogsGallery();
            gallery.setArguments(bundle);
            Application_Singleton.CONTAINER_TITLE = "";
            Application_Singleton.CONTAINERFRAG = gallery;
            Intent intent = new Intent(mContext, OpenContainer.class);
            intent.putExtra("toolbarCategory", OpenContainer.BROWSECATALOG);
            mContext.startActivity(intent);
            sendStoryBackAnalyticsEvent();
        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("StoryFragment", "Pos:" + getArguments().getInt("position") + "  onCreate");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("StoryFragment", "Pos:" + getArguments().getInt("position") + "  onAttach");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("StoryFragment", "Pos:" + getArguments().getInt("position") + "  onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("StoryFragment", "Pos:" + getArguments().getInt("position") + "  onResume");
        if (getUserVisibleHint()) {
            setStartStopProgress(true);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("StoryFragment", "Pos:" + getArguments().getInt("position") + "  onPause");
        if (customCountDownTimer != null) {
            customCountDownTimer.cancel();
            customCountDownTimer = null;
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            Log.d("StoryFragment", "Pos:" + getArguments().getInt("position") + "  isVisibleToUser");
        } else {


        }
    }


    public void setStartStopProgress(boolean isStart) {
        if (storyModel != null) {
            sendProductDetailAnalyticsEvent();
            if (isStart) {
                Log.d("TAG", "setStartStopProgress: " + getArguments().getInt("position"));
                if (getArguments().getInt("position") == 0) {
                    Log.d("TAG", "setStartStopProgress: Resume Called" + getArguments().getInt("position"));
                    if (counter == 0) {
                        storyStatusView.playStories();
                    } else {
                        if (counter == storyStatusView.getStoriesCount() - 1) {
                            counter = counter - 1;
                            callLastImageWithTimer();
                            storyStatusView.progressStartPosition(counter);
                        } else {
                            storyStatusView.resume();
                        }
                    }
                } else {
                    if (counter == 0) {
                        storyStatusView.playStories();
                    } else {
                        if (counter == storyStatusView.getStoriesCount() - 1) {
                            counter = counter - 1;
                            callLastImageWithTimer();
                            storyStatusView.progressStartPosition(counter);
                        } else {
                            storyStatusView.resume();
                        }

                    }
                }

                callCatalogViewCount(storyModel.getProduct_id());
                if (PrefDatabaseUtils.getPrefStoryCompletion(getActivity()) != null) {
                    HashMap<String, ArrayList<String>> responseData = new Gson().fromJson(PrefDatabaseUtils.getPrefStoryCompletion(getActivity()), new TypeToken<HashMap<String, ArrayList<String>>>() {
                    }.getType());

                    if (responseData.containsKey(getArguments().getString("story_id"))) {
                        ArrayList<String> ca = responseData.get(getArguments().getString("story_id"));
                        if (!ca.contains(storyModel.getId())) {
                            ca.add(storyModel.getId());
                            responseData.put(getArguments().getString("story_id"), ca);
                            PrefDatabaseUtils.setPrefStoryCompletion(getActivity(), new Gson().toJson(responseData));
                        }
                    } else {
                        HashMap<String, ArrayList<String>> tempHash = new HashMap<>();
                        ArrayList<String> ca = new ArrayList<>();
                        ca.add(storyModel.getId());
                        tempHash.put(getArguments().getString("story_id"), ca);
                        responseData.put(getArguments().getString("story_id"), ca);
                        PrefDatabaseUtils.setPrefStoryCompletion(getActivity(), new Gson().toJson(responseData));
                    }
                } else {
                    ArrayList<String> ca = new ArrayList<>();
                    ca.add(storyModel.getId());
                    HashMap<String, ArrayList<String>> tempHash = new HashMap<>();
                    tempHash.put(getArguments().getString("story_id"), ca);
                    PrefDatabaseUtils.setPrefStoryCompletion(getActivity(), new Gson().toJson(tempHash));
                }
            } else {
                if (storyStatusView != null) {
                    storyStatusView.pause();

                    if (customCountDownTimer != null) {
                        customCountDownTimer.cancel();
                    }
                }
            }
        } else {
            if (isStart) {
                try {
                    Toast.makeText(getActivity(), "Something went Wrong!", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public void onNext() {
        if (storyModel != null) {
            try {
                storyStatusView.pause();
                Log.d(TAG, "onNext: " + storyModel.getTitle() + "==>" + counter);
                ++counter;
                if (counter == storyModel.getProducts().size()) {
                    relative_bottom_action.setVisibility(View.GONE);
                    last_frame.setVisibility(View.VISIBLE);
                    customCountDownTimer = new CustomCountDownTimer(STORY_TIMER, 100) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            play_next_progress.setValue(STORY_TIMER - millisUntilFinished);
                        }

                        @Override
                        public void onFinish() {
                            //onComplete();
                        }
                    }.start();
                    ImageUtils.loadFresco1(mContext, storyModel.getProducts().get(0).getImage().getThumbnail_medium(), img_story, storyStatusView);
                    return;
                } else {
                    last_frame.setVisibility(View.GONE);
                    relative_bottom_action.setVisibility(View.VISIBLE);
                    Log.d(TAG, "onNext: " + storyModel.getTitle() + "==> Else");
                    ImageUtils.loadFresco1(mContext, storyModel.getProducts().get(counter).getImage().getThumbnail_medium(), img_story, storyStatusView);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onPrev() {
        if (storyModel != null) {
            Log.d("TAG", "Prev: " + storyModel.getTitle() + " Called");
            if (counter - 1 < 0) return;
            if (counter > storyModel.getProducts().size() - 1) return;
            storyStatusView.pause();
            --counter;
            if (counter != storyModel.getProducts().size()) {
                relative_bottom_action.setVisibility(View.VISIBLE);
                last_frame.setVisibility(View.GONE);
            }

            ImageUtils.loadFresco1(mContext, storyModel.getProducts().get(counter).getImage().getThumbnail_medium(), img_story, storyStatusView);
        }
    }

    @Override
    public void onComplete() {
        if (storyModel != null) {
            Log.d(TAG, "onComplete: " + storyModel.getTitle() + "==>" + counter);
            storyStatusView.pause();
            if (getActivity() != null && !getActivity().isFinishing()) {
                ((StoryActivity) getActivity()).callNextStory();
            }
        }
    }

    @Override
    public void onPauseUpdated() {
        if (customCountDownTimer != null) {
            if (counter != storyModel.getProducts().size()) {
                customCountDownTimer.pause();
            }

        }
    }

    @Override
    public void onResumeUpdated() {
        if (customCountDownTimer != null) {
            if (counter != storyModel.getProducts().size()) {
                customCountDownTimer.resume();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (storyModel != null) {
            Log.d(TAG, "onDestroyView: Called");
            if (ds != null) {
                ds.close();
                ds = null;

            }

            if (storyStatusView != null) {
                storyStatusView.destroy();
            }

            if (customCountDownTimer != null) {
                customCountDownTimer.cancel();
                customCountDownTimer = null;
            }
        }
        super.onDestroy();
    }


    public void callLastImageWithTimer() {
        storyStatusView.pause();
        Log.d(TAG, "onNext(callLastImageWithTimer): " + storyModel.getTitle() + "==>" + counter);
        ++counter;
        if (counter == storyModel.getProducts().size()) {
            relative_bottom_action.setVisibility(View.GONE);
            last_frame.setVisibility(View.VISIBLE);
            customCountDownTimer = new CustomCountDownTimer(STORY_TIMER, 100) {
                @Override
                public void onTick(long millisUntilFinished) {
                    play_next_progress.setValue(STORY_TIMER - millisUntilFinished);
                }

                @Override
                public void onFinish() {
                    onComplete();
                }
            }.start();
            ImageUtils.loadFresco1(mContext, storyModel.getProducts().get(0).getImage().getThumbnail_medium(), img_story, storyStatusView);
        }
    }


    @Override
    public void zoomReset() {
        storyStatusView.resume();
    }

    private class PrefetchSubscriber extends BaseDataSubscriber<Void> {

        private int mSuccessful = 0;
        private int mFailed = 0;

        @Override
        protected void onNewResultImpl(DataSource<Void> dataSource) {
            mSuccessful++;
            updateDisplay();
        }

        @Override
        protected void onFailureImpl(DataSource<Void> dataSource) {
            mFailed++;
            updateDisplay();
        }

        private void updateDisplay() {
            if (mSuccessful + mFailed == storyModel.getProducts().size()) {
                //  mPrefetchButton.setEnabled(true);
            }
        }
    }

    public void saveWishlist(final Activity activity, String catalogID) {
        Application_Singleton.trackEvent("Wishlist", "Story/Save Wishlist", catalogID);
        String userid = UserInfo.getInstance(getActivity()).getUserId();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(activity);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("product", catalogID);
        params.put("user", userid);
        HttpManager.getInstance(activity).methodPost(HttpManager.METHOD.POSTWITHPROGRESS, URLConstants.userUrl(activity, "wishlist-catalog", userid), params, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                if (isAdded() && !isDetached()) {
                    Toast.makeText(getActivity(), "Catalog successfully added to wishlist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                new MaterialDialog.Builder(activity)
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

    private void fillupData(int position) {
        storyModel = ((StoryActivity) getActivity()).getCatalogByPoisition(position);
        if (storyModel != null) {
            ArrayList<ResponseStoryModel.Products> products = storyModel.getProducts();
            ResponseStoryModel.Products p = new ResponseStoryModel().new Products("0", new Image(storyModel.getThumbnail().getThumbnail_medium(), storyModel.getThumbnail().getThumbnail_medium()));
            products.add(0, p);
            storyModel.setProducts(products);
        }
    }

    public void callCatalogViewCount(String catalogId) {
        if(isAdded() && !isDetached()) {
            HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("product", catalogId);
            params.put("catalog_type", "public");
            HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.POST, URLConstants.companyUrl(getActivity(), "catalog_view_count", ""), params, headers, false, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {
                    onServerResponse(response, false);
                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                }


                @Override
                public void onResponseFailed(ErrorString error) {

                }
            });
        }

    }

    public void sendProductDetailAnalyticsEvent() {
        WishbookEvent wishbookEvent = new WishbookEvent();
        wishbookEvent.setEvent_category(WishbookEvent.MARKETING_EVENTS_CATEGORY);
        wishbookEvent.setEvent_names("StoryProductDetails_View");
        HashMap<String, String> prop = new HashMap<>();
        if (storyModel != null) {
            prop.put("story_id", getArguments().getString("story_id"));
            prop.put("story_name", getArguments().getString("story_name"));
            prop.put("catalog_item_id", storyModel.getId());
            prop.put("catalog_item_name", storyModel.getTitle());
        }
        wishbookEvent.setEvent_properties(prop);
        new WishbookTracker(mContext, wishbookEvent);
    }

    public void sendStoryBackAnalyticsEvent() {
        WishbookEvent wishbookEvent = new WishbookEvent();
        wishbookEvent.setEvent_category(WishbookEvent.MARKETING_EVENTS_CATEGORY);
        wishbookEvent.setEvent_names("Story_Back");
        HashMap<String, String> prop = new HashMap<>();
        if (storyModel != null) {
            prop.put("story_id", getArguments().getString("story_id"));
            prop.put("story_name", getArguments().getString("story_name"));
            prop.put("catalog_item_id", storyModel.getId());
            prop.put("catalog_item_name", storyModel.getTitle());
        }
        wishbookEvent.setEvent_properties(prop);
        new WishbookTracker(mContext, wishbookEvent);
    }
}
