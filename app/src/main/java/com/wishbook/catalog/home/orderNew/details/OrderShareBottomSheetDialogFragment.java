package com.wishbook.catalog.home.orderNew.details;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.responses.Response_buyingorder;
import com.wishbook.catalog.home.orderNew.adapters.OrderShareParentItemAdapter;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderShareBottomSheetDialogFragment extends BottomSheetDialogFragment {


    @BindView(R.id.linear_toolbar)
    LinearLayout linear_toolbar;

    @BindView(R.id.img_close)
    ImageView img_close;

    @BindView(R.id.recyclerview_share_items)
    RecyclerView mRecyclerView;

    @BindView(R.id.relative_progress)
    RelativeLayout relativeProgress;

    @BindView(R.id.relative_empty)
    RelativeLayout relative_empty;


    public static OrderShareBottomSheetDialogFragment newInstance(String string) {
        OrderShareBottomSheetDialogFragment f = new OrderShareBottomSheetDialogFragment();
        if (string != null) {
            Bundle args = new Bundle();
            args.putString("product_id", string);
            f.setArguments(args);
        }
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_order_share, container, false);
        ButterKnife.bind(this, v);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        if (getArguments().getString("product_id") != null) {
            getCatalogData(getActivity(), getArguments().getString("product_id"));
            initView();
        }
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();

        if (dialog != null) {
            View bottomSheet = dialog.findViewById(R.id.design_bottom_sheet);
            bottomSheet.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
        }
        final View view = getView();
        view.post(new Runnable() {
            @Override
            public void run() {
                View parent = (View) view.getParent();
                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) (parent).getLayoutParams();
                CoordinatorLayout.Behavior behavior = params.getBehavior();
                BottomSheetBehavior bottomSheetBehavior = (BottomSheetBehavior) behavior;
                WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
                Display display = wm.getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                bottomSheetBehavior.setPeekHeight((int) (size.y / 2.0));
                bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                    @Override
                    public void onStateChanged(@NonNull View bottomSheet, int newState) {
                        if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                            setupToolbarBlue();
                        } else if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                            dismiss();
                        }
                    }

                    @Override
                    public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                    }
                });
                parent.setBackgroundColor(Color.TRANSPARENT);
            }
        });

    }

    public void initView() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }


    public void setupToolbarBlue() {
        linear_toolbar.setBackgroundColor(getResources().getColor(R.color.color_primary));
        img_close.setImageDrawable(getResources().getDrawable(R.drawable.ic_close_white_24dp));
        img_close.setColorFilter(ContextCompat.getColor(getActivity(), R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
    }

    public void getCatalogData(Context context, String order_id) {
        try {
            String url = "purchaseorders_catalogwise";
            HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
            showProgress();
            HttpManager.getInstance((Activity) context).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(context, url, order_id), null, headers, false, new HttpManager.customCallBack() {
                @Override
                public void onCacheResponse(String response) {
                    onServerResponse(response, false);
                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    hideProgress();
                    if (isAdded() && !isDetached()) {
                        try {
                            final Response_buyingorder selectedOrder = Application_Singleton.gson.fromJson(response, Response_buyingorder.class);
                            if (selectedOrder.getCatalogs().size() > 0) {
                                relative_empty.setVisibility(View.GONE);
                                mRecyclerView.setVisibility(View.VISIBLE);
                                OrderShareParentItemAdapter orderShareParentItemAdapter = new OrderShareParentItemAdapter(getActivity(), selectedOrder.getCatalogs());
                                mRecyclerView.setAdapter(orderShareParentItemAdapter);
                            } else {
                                relative_empty.setVisibility(View.VISIBLE);
                                mRecyclerView.setVisibility(View.GONE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onResponseFailed(ErrorString error) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showProgress() {
        if (relativeProgress != null) {
            relativeProgress.setVisibility(View.VISIBLE);
        }
    }

    public void hideProgress() {
        if (relativeProgress != null) {
            relativeProgress.setVisibility(View.GONE);
        }
    }


}