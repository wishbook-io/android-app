package com.wishbook.catalog.home.catalog;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.DateUtils;
import com.wishbook.catalog.Utils.PrefDatabaseUtils;
import com.wishbook.catalog.Utils.ShareImageDownloadUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.analysis.WishbookTracker;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.Utils.widget.SimpleTextWatcher;
import com.wishbook.catalog.commonmodels.Advancedcompanyprofile;
import com.wishbook.catalog.commonmodels.CompanyProfile;
import com.wishbook.catalog.commonmodels.ProductMyDetail;
import com.wishbook.catalog.commonmodels.RequestProductBatchUpdate;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.WishbookEvent;
import com.wishbook.catalog.commonmodels.responses.ConfigResponse;
import com.wishbook.catalog.commonmodels.responses.ResponseProductShare;
import com.wishbook.catalog.commonmodels.responses.Response_catalog;
import com.wishbook.catalog.home.catalog.adapter.ResellerShareAdapter;
import com.wishbook.catalog.home.catalog.fbshare.Fragment_FBPostText;
import com.wishbook.catalog.home.models.ProductObj;
import com.wishbook.catalog.home.models.ThumbnailObj;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.util.ContentMetadata;
import io.branch.referral.util.LinkProperties;

public class ResellerCatalogShareBottomSheet extends BottomSheetDialogFragment {


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

    @BindView(R.id.txt_btn_whatsapp_share)
    TextView txt_btn_whatsapp_share;

    @BindView(R.id.linear_whatsapp_share)
    LinearLayout linear_whatsapp_share;

    @BindView(R.id.txt_btn_other_share)
    TextView txt_btn_other_share;

    @BindView(R.id.txt_total_resale_price)
    EditText edit_resale_price;


    @BindView(R.id.frame_bottom)
    LinearLayout frame_bottom;

    @BindView(R.id.linear_bottom_other)
    LinearLayout linear_bottom_other;

    @BindView(R.id.txt_title)
    TextView txt_title;

    @BindView(R.id.txt_catalog_price)
    TextView txt_catalog_price;

    @BindView(R.id.txt_availability_value)
    TextView txt_availability_value;

    @BindView(R.id.btn_share_facebook)
    RelativeLayout btn_share_facebook;

    @BindView(R.id.btn_share_fbpage)
    RelativeLayout btn_share_fbpage;

    @BindView(R.id.btn_share_whatsapp_business)
    RelativeLayout btn_share_whatsapp_business;

    @BindView(R.id.btn_share_other)
    RelativeLayout btn_share_other;

    @BindView(R.id.btn_save_gallery)
    RelativeLayout btn_save_gallery;

    @BindView(R.id.btn_share_link)
    RelativeLayout btn_share_link;


    @BindView(R.id.txt_price_label)
    TextView txt_price_label;

    @BindView(R.id.txt_resale_price_label)
    TextView txt_resale_price_label;

    @BindView(R.id.linear_last_shared_date)
    LinearLayout linear_last_shared_date;

    @BindView(R.id.txt_last_shared_value)
    TextView txt_last_shared_value;

    @BindView(R.id.txt_copy_details)
    TextView txt_copy_details;

    @BindView(R.id.txt_final_price)
    TextView txt_final_price;


    SHARETYPE share_type;

    Response_catalog response_catalog;
    ResellerShareAdapter resellerShareAdapter;
    double price;
    boolean isSinglePcAvailable = false;

    String b2c_product_url;

    ArrayList<String> product_b2c_urls;

    String from = "Product Detail";

    public enum SHARETYPE {
        WHATSAPP, FACEBOOK, LINK, OTHER, GALLERY
    }

    public static String TAG = ResellerCatalogShareBottomSheet.class.getSimpleName();


    public static ResellerCatalogShareBottomSheet newInstance(String string) {
        ResellerCatalogShareBottomSheet f = new ResellerCatalogShareBottomSheet();
        if (string != null) {
            Bundle args = new Bundle();
            args.putString("product_id", string);
            f.setArguments(args);
        }

        return f;
    }

    public static ResellerCatalogShareBottomSheet newInstance(String string,String from) {
        ResellerCatalogShareBottomSheet f = new ResellerCatalogShareBottomSheet();
        if (string != null) {
            Bundle args = new Bundle();
            args.putString("product_id", string);
            args.putString("from",from);
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
        View v = inflater.inflate(R.layout.reseller_catalog_share_bottom_sheet, container, false);
        ButterKnife.bind(this, v);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        if (getArguments().getString("product_id") != null) {
            getCatalogData(getActivity(), getArguments().getString("product_id"));
            getMyDetails(getActivity(), getArguments().getString("product_id"));
            initView();
            initListner();
        }

        if(getArguments()!=null && getArguments().getString("from")!=null) {
            from = getArguments().getString("from");

        }
        Application_Singleton.getInstance().trackScreenView("ResellerCatalogShareBottomSheet",getActivity());
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
        if (getActivity() != null) {
            view.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        View parent = (View) view.getParent();
                        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) (parent).getLayoutParams();
                        CoordinatorLayout.Behavior behavior = params.getBehavior();
                        BottomSheetBehavior bottomSheetBehavior = (BottomSheetBehavior) behavior;
                        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
                        Display display = wm.getDefaultDisplay();
                        final Point size = new Point();
                        display.getSize(size);
                        bottomSheetBehavior.setPeekHeight((int) (size.y));
                        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                            @Override
                            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                                //Log.e(TAG, "onState: " + newState);
                            }

                            @Override
                            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                                //Log.e(TAG, "onSlide: " + slideOffset);
                            }
                        });
                        parent.setBackgroundColor(Color.TRANSPARENT);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void initView() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        btn_share_fbpage.setVisibility(View.GONE);

        boolean wb_installed = StaticFunctions.appInstalledOrNot("com.whatsapp.w4b", getContext());
        if(wb_installed) {
            btn_share_whatsapp_business.setVisibility(View.VISIBLE);
        } else {
            btn_share_whatsapp_business.setVisibility(View.GONE);
        }

        if (PrefDatabaseUtils.getConfig(getActivity()) != null) {
            ArrayList<ConfigResponse> data = new Gson().fromJson(PrefDatabaseUtils.getConfig(getActivity()), new TypeToken<ArrayList<ConfigResponse>>() {
            }.getType());
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).getKey().equals("SHARE_FB_PAGE_FEATURE_IN_APP")
                        && data.get(i).getValue()!=null
                        && data.get(i).getValue().equalsIgnoreCase("1")) {
                    btn_share_fbpage.setVisibility(View.VISIBLE);
                    break;
                }
            }
        }
    }

    public void initListner() {
        txt_copy_details.setTextColor(ContextCompat.getColor(getActivity(), R.color.purchase_light_gray));
        txt_copy_details.setCompoundDrawables(getResources().getDrawable(R.drawable.ic_copy_content_grey), null, null, null);
        txt_btn_other_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (linear_bottom_other.getVisibility() == View.GONE) {
                    if (validateData()) {
                        linear_bottom_other.setVisibility(View.VISIBLE);
                    }
                } else {
                    linear_bottom_other.setVisibility(View.GONE);
                }
            }
        });
        txt_copy_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateData()) {
                    if (response_catalog != null && resellerShareAdapter != null && resellerShareAdapter.getSelectedItems().length > 0) {
                        copyProductDetail(true, resellerShareAdapter.getSelectedItems().length, edit_resale_price.getText().toString());
                    }
                }

            }
        });

        linear_whatsapp_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateData()) {
                    shareData(StaticFunctions.SHARETYPE.WHATSAPP);
                }
            }
        });


        edit_resale_price.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edit_resale_price.getText().toString().isEmpty()) {
                    txt_copy_details.setTextColor(ContextCompat.getColor(getActivity(), R.color.purchase_light_gray));
                    txt_copy_details.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_copy_content_grey), null, null, null);
                } else {
                    try {
                        if (Double.parseDouble(edit_resale_price.getText().toString()) <= price) {
                            txt_copy_details.setTextColor(ContextCompat.getColor(getActivity(), R.color.purchase_light_gray));
                            txt_copy_details.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_copy_content_grey), null, null, null);
                        } else {
                            txt_copy_details.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_primary));
                            txt_copy_details.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_copy_content), null, null, null);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        btn_share_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linear_bottom_other.setVisibility(View.GONE);
                shareData(StaticFunctions.SHARETYPE.LINKSHARE);


            }
        });

        btn_share_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linear_bottom_other.setVisibility(View.GONE);
                shareData(StaticFunctions.SHARETYPE.FACEBOOK);
            }
        });

        btn_save_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linear_bottom_other.setVisibility(View.GONE);
                if (response_catalog != null && resellerShareAdapter != null && resellerShareAdapter.getSelectedItems().length > 0) {
                    checkStorePermission(getActivity(), resellerShareAdapter.getSelectedItems(), response_catalog.getTitle());
                }
            }
        });

        btn_share_other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linear_bottom_other.setVisibility(View.GONE);
                shareData(StaticFunctions.SHARETYPE.OTHER);
            }
        });

        btn_share_fbpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linear_bottom_other.setVisibility(View.GONE);
                shareData(StaticFunctions.SHARETYPE.FBPAGE);
            }
        });

        btn_share_whatsapp_business.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linear_bottom_other.setVisibility(View.GONE);
                shareData(StaticFunctions.SHARETYPE.WHATSAPP_BUSINESS);
            }
        });
    }

    public void shareData(StaticFunctions.SHARETYPE sharetype) {
        if (validateData()) {
            sendProductShareAnalysis(getActivity(), response_catalog, edit_resale_price.getText().toString(), sharetype.name(), !isSinglePcAvailable);
            if (response_catalog != null && resellerShareAdapter != null && resellerShareAdapter.getSelectedItems().length > 0) {
                if (isSinglePcAvailable) {
                    postShare(getActivity(), resellerShareAdapter.getSelectedItems(), sharetype);
                } else {
                    postScreenShare(getActivity(), response_catalog, sharetype);
                }

            }
        }
    }

    public boolean validateData() {
        if (response_catalog != null && resellerShareAdapter != null && resellerShareAdapter.getSelectedItems().length == 0) {
            Toast.makeText(getActivity(), "Please Select any one product", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (edit_resale_price.getText().toString().isEmpty()) {
            edit_resale_price.setError("Please enter resale price");
            edit_resale_price.requestFocus();
            return false;
        } else {
            try {
                if (Double.parseDouble(edit_resale_price.getText().toString()) <= price) {
                    edit_resale_price.setError("Resale price should be greater than " + price);
                    edit_resale_price.requestFocus();
                    return false;
                }

                if (Double.parseDouble(edit_resale_price.getText().toString()) > (price * 3)) {
                    edit_resale_price.setError(getContext().getResources().getString(R.string.resell_amount_error_3x_item));
                    edit_resale_price.requestFocus();
                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.resell_amount_error_3x_item), Toast.LENGTH_SHORT).show();
                    return false;
                }
            } catch (Exception e) {
                edit_resale_price.setError("Enter valid resale price ");
                edit_resale_price.requestFocus();
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult: Request Code====>" + requestCode + "\n Result Code==>" + resultCode);
        try {
            if (requestCode == 5001) {
                if (ShareImageDownloadUtils.last_share_type == StaticFunctions.SHARETYPE.WHATSAPP_BUSINESS ||
                        ShareImageDownloadUtils.last_share_type == StaticFunctions.SHARETYPE.WHATSAPP) {
                    ShareImageDownloadUtils.setStepTwoCounter(getActivity(), copyProductDetail(false, resellerShareAdapter.getSelectedItems().length, edit_resale_price.getText().toString()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void setupToolbarBlue() {
        linear_toolbar.setBackgroundColor(getResources().getColor(R.color.color_primary));
        img_close.setImageDrawable(getResources().getDrawable(R.drawable.ic_close_white_24dp));
        img_close.setColorFilter(ContextCompat.getColor(getActivity(), R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
        if (response_catalog != null) {
            txt_title.setText("Share " + response_catalog.getTitle());
        }
    }


    public void getCatalogData(Context context, String responseCatalogID) {
        try {
            HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
            HttpManager.getInstance((Activity) context).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(context, "catalogs_expand_true_id", responseCatalogID), null, headers, true, new HttpManager.customCallBack() {

                @Override
                public void onCacheResponse(String response) {
                    onServerResponse(response, false);
                }

                @Override

                public void onServerResponse(String response, boolean dataUpdated) {
                    hideProgress();
                    try {
                        if (isAdded() && !isDetached()) {
                            try {
                                response_catalog = Application_Singleton.gson.fromJson(response, Response_catalog.class);
                                if (response_catalog != null) {
                                    setupToolbarBlue();
                                    if (response_catalog.full_catalog_orders_only != null && response_catalog.full_catalog_orders_only.equals("true")) {
                                        isSinglePcAvailable = false;
                                        txt_availability_value.setText("Full Catalog only");
                                        txt_availability_value.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
                                    } else {
                                        isSinglePcAvailable = true;
                                        txt_availability_value.setText("Single Pcs.");
                                        txt_availability_value.setTextColor(ContextCompat.getColor(getActivity(), R.color.green));
                                    }
                                    if (response_catalog.getProduct() != null && response_catalog.getProduct().length == 1 && !response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN)) {
                                        txt_availability_value.setText("Single Pcs.");
                                        txt_availability_value.setTextColor(ContextCompat.getColor(getActivity(), R.color.green));
                                    }

                                    relative_empty.setVisibility(View.GONE);
                                    mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
                                    mRecyclerView.setVisibility(View.VISIBLE);

                                    if (response_catalog.getProduct_type() != null && !response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN) && !response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SINGLE)) {
                                        // For Catalog and Non-Catalog
                                        // Default state checked true
                                        ArrayList<ProductObj> productObjArrayList = new ArrayList<>();
                                        for (int i = 0; i < response_catalog.getProduct().length; i++) {
                                            ProductObj productObj = response_catalog.getProduct()[i];
                                            productObj.setChecked(true);
                                            productObjArrayList.add(productObj);
                                        }
                                        getPrice(response_catalog);
                                        resellerShareAdapter = new ResellerShareAdapter(getActivity(), productObjArrayList, isSinglePcAvailable);
                                        resellerShareAdapter.setProductSelectionListener(new ResellerShareAdapter.ProductSelectionListener() {
                                            @Override
                                            public void selectionChanged() {
                                                getPrice(response_catalog);
                                            }
                                        });
                                        mRecyclerView.setAdapter(resellerShareAdapter);
                                    } else if (response_catalog.getProduct_type() != null && response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SINGLE)) {
                                        txt_availability_value.setText("Single Pcs.");
                                        txt_availability_value.setTextColor(ContextCompat.getColor(getActivity(), R.color.green));
                                        ArrayList<ProductObj> list = new ArrayList<>();
                                        ThumbnailObj temp = new ThumbnailObj(response_catalog.getImage().getFull_size(), response_catalog.getImage().getThumbnail_medium(), response_catalog.getImage().getThumbnail_small());
                                        ProductObj productObj = new ProductObj(response_catalog.getId(),
                                                response_catalog.getTitle(),
                                                response_catalog.getTitle(),
                                                null,
                                                null, null, temp, response_catalog.getPrice_range(), response_catalog.getPrice_range(), response_catalog.getPrice_range(), null, null, null);
                                        productObj.setChecked(true);
                                        list.add(productObj);
                                        getPrice(response_catalog);
                                        resellerShareAdapter = new ResellerShareAdapter(getActivity(), list, false);
                                        mRecyclerView.setAdapter(resellerShareAdapter);
                                    } else {
                                        // For Set-matching
                                        ArrayList<ProductObj> list = new ArrayList<>();
                                        if (response_catalog.getPhotos() != null && response_catalog.getPhotos().size() > 0) {
                                            for (int i = 0; i < response_catalog.getPhotos().size(); i++) {
                                                ThumbnailObj temp = new ThumbnailObj(response_catalog.getPhotos().get(i).getImage().getFull_size(), response_catalog.getPhotos().get(i).getImage().getThumbnail_medium(), response_catalog.getPhotos().get(i).getImage().getThumbnail_small());
                                                ProductObj productObj = new ProductObj(response_catalog.getId(),
                                                        response_catalog.getTitle(),
                                                        response_catalog.getTitle(),
                                                        null,
                                                        null, null, temp, response_catalog.getPrice_range(), response_catalog.getPrice_range(), response_catalog.getPrice_range(), null, null, null);
                                                productObj.setChecked(true);
                                                list.add(productObj);
                                            }
                                            getPrice(response_catalog);
                                            resellerShareAdapter = new ResellerShareAdapter(getActivity(), list, isSinglePcAvailable);
                                            mRecyclerView.setAdapter(resellerShareAdapter);
                                        }
                                    }
                                } else {
                                    relative_empty.setVisibility(View.VISIBLE);
                                    mRecyclerView.setVisibility(View.GONE);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    hideProgress();
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

    private void patchDefaultMargin(final String margin) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) getActivity());
        JsonObject jsonObject;
        CompanyProfile companyProfile = new CompanyProfile();
        Advancedcompanyprofile advancedcompanyprofile = new Advancedcompanyprofile();
        advancedcompanyprofile.setResale_default_margin(margin);
        companyProfile.setAdvancedcompanyprofile(advancedcompanyprofile);
        jsonObject = new Gson().fromJson(new Gson().toJson(companyProfile), JsonObject.class);
        HttpManager.getInstance((Activity) getActivity()).requestPatch(HttpManager.METHOD.PATCH, URLConstants.companyUrl(getActivity(), "", ""), jsonObject, headers, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                UserInfo.getInstance(getActivity()).setResaleDefaultMargin(margin);
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.logger(error.getErrormessage());
            }
        });

    }


    public void linkShare(final Context context, Response_catalog response_catalog) {
        LinkProperties lp = new LinkProperties()
                .setChannel("All")
                .setCampaign("Refer Catalog")
                .setFeature("Branch Link")
                .addTag("company_" + UserInfo.getInstance(getActivity()).getCompany_id());

        String link = "https://app.wishbook.io/?type=product&id=" + response_catalog.getId();
        BranchUniversalObject buo = new BranchUniversalObject()
                .setTitle("Earn From Home - Start Fashion Business")
                .setContentDescription("Sell Sarees, Kurtis, Dress Materials, to your friends, relatives & customers. Place your orders on Wishbook and we will have products delivered to your customer. COD Available,Install Now!")
                .setContentMetadata(new ContentMetadata().addCustomMetadata("deep_link", link))
                .setContentImageUrl(response_catalog.getImage().getThumbnail_medium());
        final String finalShare_msg = "";
        buo.generateShortUrl(getActivity(), lp, new Branch.BranchLinkCreateListener() {
            @Override
            public void onLinkCreate(String url, BranchError error) {
                if (error == null) {
                    Log.d("Reseller_Share", "branch share link created: " + url);
                    Intent intent = new Intent();
                    intent.putExtra(Intent.EXTRA_SUBJECT, "View Catalog");
                    intent.setAction(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_TEXT,
                            finalShare_msg + "\n" + Uri.parse(url));
                    intent.setType("text/plain");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
                    context.startActivity(intent);
                }
            }
        });
    }

    public void getMyDetails(Context context, String catalogID) {
        try {
            HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
            HttpManager.getInstance((Activity) context).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(context, "mydetails", catalogID), null, headers, true, new HttpManager.customCallBack() {

                @Override
                public void onCacheResponse(String response) {
                    onServerResponse(response, false);
                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    hideProgress();
                    try {
                        if (isAdded() && !isDetached()) {
                            ProductMyDetail productMyDetail = Application_Singleton.gson.fromJson(response, ProductMyDetail.class);
                            if (productMyDetail.getB2c_product_url() != null && !productMyDetail.getB2c_product_url().isEmpty()) {
                                b2c_product_url = productMyDetail.getB2c_product_url();
                            }
                            if (productMyDetail.getLast_shared_date() != null) {
                                linear_last_shared_date.setVisibility(View.VISIBLE);
                                String t1 = DateUtils.changeDateFormat(StaticFunctions.SERVER_POST_FORMAT1, StaticFunctions.CLIENT_DISPLAY_FORMAT3, productMyDetail.getLast_shared_date());
                                txt_last_shared_value.setText(t1);
                            } else {
                                linear_last_shared_date.setVisibility(View.GONE);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    hideProgress();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hideShareItem() {
        mRecyclerView.setVisibility(View.GONE);
    }

    public void showShareItem() {
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void checkStorePermission(Context context, ProductObj[] productObjs, String catalogname) {
        String[] permissions = {
                "android.permission.WRITE_EXTERNAL_STORAGE"
        };
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(permissions, 1599);
        } else {
            if (isSinglePcAvailable) {
                postShare(getActivity(), resellerShareAdapter.getSelectedItems(), StaticFunctions.SHARETYPE.GALLERY);
            } else {
                postScreenShare(getActivity(), response_catalog, StaticFunctions.SHARETYPE.GALLERY);
            }
        }
    }

    void downloadAndCopyDetails(Context context, ProductObj[] productObjs, StaticFunctions.SHARETYPE sharetype, String catalogname) {
        try {
            if (productObjs != null && productObjs.length > 0) {
                try {
                    ShareImageDownloadUtils.shareProducts((AppCompatActivity) context, ResellerCatalogShareBottomSheet.this,
                            productObjs, sharetype, "", catalogname, false, true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (sharetype == StaticFunctions.SHARETYPE.WHATSAPP || sharetype == StaticFunctions.SHARETYPE.WHATSAPP_BUSINESS) {
                    copyProductDetail(false, productObjs.length, edit_resale_price.getText().toString());
                } else {
                    copyProductDetail(true, productObjs.length, edit_resale_price.getText().toString());
                }

            } else {
                Toast.makeText(context, "No product to share", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String copyProductDetail(boolean isCopy, int shareProductLength, String resaleprice) {
        StringBuffer copy_details = new StringBuffer();
        int products = 0;
        if (response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN)) {
            products = Integer.parseInt(response_catalog.getNo_of_pcs_per_design());
        } else {
            products = response_catalog.getProduct().length;
        }
        if (products > 1) {
            if (response_catalog.getBrand() != null && response_catalog.getBrand().getName() != null) {
                if (!response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN)) {
                    if (response_catalog.getCatalog_type().equalsIgnoreCase("catalog")) {
                        copy_details.append("\u26A1" + response_catalog.getBrand().getName() + " - " + response_catalog.getTitle() + " " + response_catalog.getCategory_name() + " Collection" + "\u26A1" + "\n\n");
                        copy_details.append("*No of Designs*: " + shareProductLength + "\n");

                    } else if (response_catalog.getCatalog_type().equalsIgnoreCase("noncatalog")) {
                        copy_details.append("*" + response_catalog.getTitle() + " Collection of " + shareProductLength + " " + response_catalog.getCategory_name() + "*" + "\n\n");
                    }
                }

            } else {
                if (response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN)) {
                    copy_details.append("*" + response_catalog.getTitle() + " Collection of " + shareProductLength + " " + response_catalog.getCategory_name() + "*" + "\n\n");
                }
            }
        } else {
            String categoryname = response_catalog.getCategory_name();
            if (response_catalog.getCategory_name().equalsIgnoreCase("Sarees")
                    || response_catalog.getCategory_name().equalsIgnoreCase("Kurtis")
                    || response_catalog.getCategory_name().equalsIgnoreCase("Dress Materials")) {
                categoryname = categoryname.substring(0, response_catalog.getCategory_name().length() - 1);
            }
            copy_details.append(getResources().getString(R.string.unicode_fire) + " *" + "Hot selling " + categoryname + "* " + getResources().getString(R.string.unicode_fire) + "\n\n");
        }

        ////////
        String id = "";
        if (response_catalog.getProduct_type() != null && response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN)) {
            id = "*Set ID*: " + (response_catalog.getId()) + "\n";
        } else if (response_catalog.getProduct_type() != null && response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SINGLE)) {
            id = "*Product ID*: " + (response_catalog.getId()) + "\n";
        } else {
            id = "*Catalog ID*: " + (response_catalog.getId()) + "\n";
        }
        copy_details.append(id + "\n");

        String price = "";
        price = "*Price*: " + resaleprice + "/Pc.";
        if (!price.isEmpty()) {
            copy_details.append(price + "\n");
        }

        /////////////
        String size = "";
        if (response_catalog.full_catalog_orders_only.equals("false")) {
            if (!StaticFunctions.ArrayListToString(getSelectedSizesDistinctEav(response_catalog), StaticFunctions.COMMASEPRATED).isEmpty()) {
                size = "*Available Sizes*: " + (StaticFunctions.ArrayListToString(getSelectedSizesDistinctEav(response_catalog), StaticFunctions.COMMASEPRATED)) + "\n";
            }
        } else {
            if (response_catalog.getAvailable_sizes() != null && !response_catalog.getAvailable_sizes().isEmpty()) {
                size = "*Available Sizes*: " + response_catalog.getAvailable_sizes() + "\n";
            }
        }


        ////////////
        String striching = "";
        if (response_catalog.getEavdata() != null && response_catalog.getEavdata().getStitching_type() != null) {
            striching = "*Stitching Details*: " + response_catalog.getEavdata().getStitching_type();
        }
        if (!size.isEmpty()) {
            copy_details.append(size + "\n");
        }

        if (!striching.isEmpty()) {
            copy_details.append(striching + "\n");
        }

        String fabric = (StaticFunctions.ArrayListToString(response_catalog.getEavdata().getFabric(), StaticFunctions.COMMASEPRATEDSPACE));
        String work = (StaticFunctions.ArrayListToString(response_catalog.getEavdata().getWork(), StaticFunctions.COMMASEPRATEDSPACE));
        copy_details.append("*Fabric*: " + fabric + "\n");
        copy_details.append("*Work*: " + work + "\n");

        ArrayList<String> length = new ArrayList<>();
        if (response_catalog.getEavdata() != null && response_catalog.getEavdata().getTop() != null) {
            length.add("Top: " + response_catalog.getEavdata().getTop());
        }
        if (response_catalog.getEavdata() != null && response_catalog.getEavdata().getBottom() != null) {
            length.add("Bottom: " + response_catalog.getEavdata().getBottom());
        }
        if (response_catalog.getEavdata() != null && response_catalog.getEavdata().getDupatta() != null) {
            length.add("Dupatta: " + response_catalog.getEavdata().getDupatta());
        }
        if (response_catalog.getEavdata() != null && response_catalog.getEavdata().getDupatta_length() != null) {
            length.add("Dupatta Length: " + response_catalog.getEavdata().getDupatta_length());
        }
        if (response_catalog.getEavdata() != null && response_catalog.getEavdata().getDupatta_width() != null) {
            length.add("Dupatta Width: " + response_catalog.getEavdata().getDupatta_width());
        }
        if (length != null && length.size() > 0) {
            copy_details.append("*Lengths*: " + StaticFunctions.ArrayListToString(length, StaticFunctions.COMMASEPRATEDSPACE) + "\n");
        }


        String other_details = "";
        if (response_catalog.getEavdata() != null && response_catalog.getEavdata().getOther() != null && response_catalog.getEavdata().getOther().length() > 0) {
            other_details = "*Other Details*: " + response_catalog.getEavdata().getOther();
        }
        if (!other_details.isEmpty()) {
            copy_details.append(other_details + "\n");
        }

        copy_details.append("\n\n");
        copy_details.append("*COD Available*" + "\n");
        if (response_catalog.full_catalog_orders_only.equals("false")) {
            copy_details.append("*Single Piece Available*" + "\n");
        }

        if (product_b2c_urls != null && product_b2c_urls.size() > 0) {
            try {
                if (!checkB2CALLURLISSame(product_b2c_urls)) {
                    copy_details.append("\n" + "More information visit:\n" + StaticFunctions.ArrayListToString(product_b2c_urls, "\n\n"));
                } else {
                    copy_details.append("\n" + "More information visit:\n" + product_b2c_urls.get(0));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (isCopy) {
            int sdk = android.os.Build.VERSION.SDK_INT;
            if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setText(copy_details.toString());
                Toast.makeText(getActivity(), "Products description copied to clipboard", Toast.LENGTH_LONG).show();
            } else {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("Product Details", copy_details.toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getActivity(), "Products description copied to clipboard", Toast.LENGTH_LONG).show();
            }
        }

        return copy_details.toString();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1599) {
            if (grantResults.length > 0) {
                // Fill with results
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    btn_save_gallery.performClick();
                } else {
                    // Permission Denied
                    Toast.makeText(getActivity(), "Write External Storage Permission Denied", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }
    }


    public void getPrice(Response_catalog response_catalog) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        if (response_catalog.getProduct_type() != null && !response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN) && !response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SINGLE)) {
            if (response_catalog.getFull_catalog_orders_only() != null && response_catalog.getFull_catalog_orders_only().equalsIgnoreCase("true")) {
                /**
                 * Show Avg price WB-4404
                 */
                //   Avg price calculate price calculate
                double full_catalog_price = 0.0;
                for (int i = 0; i < response_catalog.getProduct().length; i++) {
                    full_catalog_price += Double.parseDouble(response_catalog.getProduct()[i].getPublic_price_with_gst());
                }

                full_catalog_price += Double.parseDouble(response_catalog.getShipping_charges());
                double avg_price = 0.0;
                avg_price = full_catalog_price / response_catalog.getProduct().length;
                price = avg_price;
                txt_final_price.setText("= " + "\u20B9" + decimalFormat.format(avg_price));

                // start price breakup logic
                double avg_tax = 0.0;
                double public_catalog_price = 0.0;
                double public_catalog_price_gst = 0.0;
                for (int i = 0; i < response_catalog.getProduct().length; i++) {
                    public_catalog_price += Double.parseDouble(response_catalog.getProduct()[i].getFinal_price());
                    public_catalog_price_gst += Double.parseDouble(response_catalog.getProduct()[i].getPublic_price_with_gst());
                }
                avg_tax = (public_catalog_price_gst / response_catalog.getProduct().length) - (public_catalog_price / response_catalog.getProduct().length);
                double avg_shipping_charge = Double.parseDouble(response_catalog.getShipping_charges()) / response_catalog.getProduct().length;
                txt_catalog_price.setText("\u20B9" + decimalFormat.format((public_catalog_price / response_catalog.getProduct().length)) + " + " + decimalFormat.format(avg_tax) + "(tax)" + " + " + decimalFormat.format(avg_shipping_charge) + " (shipping)");


                if (response_catalog.getPrice_range() != null && response_catalog.getPrice_range().contains("-")) {
                    txt_price_label.setText("Avg Price/Piece: ");
                } else {
                    txt_price_label.setText("Price/Piece: ");
                }
            } else {
                // Avg price calculate
                double avg_price = 0.0;
                int selected_product = 0;
                for (int i = 0; i < response_catalog.getProduct().length; i++) {
                    if (response_catalog.getProduct()[i].isChecked()) {
                        selected_product += 1;
                        avg_price += Double.parseDouble(response_catalog.getProduct()[i].getSingle_piece_price_with_gst()) + Double.parseDouble(response_catalog.getProduct()[i].getShipping_charges());
                    }
                }
                if (selected_product > 0) {
                    txt_final_price.setText(" \u20B9" + decimalFormat.format(avg_price / selected_product));
                    price = avg_price / selected_product;
                } else {
                    txt_final_price.setText(" \u20B9" + "0.0");
                    price = 0;
                }


                // start price breakup logic
                double avg_tax = 0.0;
                double public_catalog_price = 0.0;
                double public_catalog_price_gst = 0.0;
                double avg_shipping_charge = 0.0;
                for (int i = 0; i < response_catalog.getProduct().length; i++) {
                    if (response_catalog.getProduct()[i].isChecked()) {
                        public_catalog_price += Double.parseDouble(response_catalog.getProduct()[i].getSingle_piece_price());
                        public_catalog_price_gst += Double.parseDouble(response_catalog.getProduct()[i].getSingle_piece_price_with_gst());
                        avg_shipping_charge += Double.parseDouble(response_catalog.getProduct()[i].getShipping_charges());
                    }
                }
                avg_tax = (public_catalog_price_gst / selected_product) - (public_catalog_price / selected_product);
                avg_shipping_charge = avg_shipping_charge / selected_product;
                txt_catalog_price.setText("\u20B9" + decimalFormat.format((public_catalog_price / selected_product)) + " + " + decimalFormat.format(avg_tax) + "(tax)" + " + " + decimalFormat.format(avg_shipping_charge) + " (shipping)");

                if (response_catalog.getSingle_piece_price_range() != null && response_catalog.getSingle_piece_price_range().contains("-")) {
                    txt_price_label.setText("Avg Price/Piece: ");
                } else {
                    txt_price_label.setText("Price/Piece: ");
                }
                txt_resale_price_label.setText("Resale Price/Piece: ");
            }
        } else if (response_catalog.getProduct_type() != null && response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SINGLE)) {
            if (response_catalog.getPrice_per_design_with_gst() != null) {

                double full_set_price = 0.0;
                full_set_price = Double.parseDouble(response_catalog.getPrice_per_design_with_gst()) + Double.parseDouble(response_catalog.getShipping_charges());
                txt_final_price.setText("=" + " \u20B9" + decimalFormat.format(full_set_price));
                price = full_set_price;
            }

            // start price breakup logic
            double tax = 0.0;
            double single_pc_price = 0.0;
            if (response_catalog.getSingle_piece_price_range().contains("-")) {
                single_pc_price = Double.parseDouble(response_catalog.getSingle_piece_price());
            } else {
                single_pc_price = Double.parseDouble(response_catalog.getSingle_piece_price_range());
            }
            double single_pc_price_gst = Double.parseDouble(response_catalog.getPrice_per_design_with_gst());
            double shipping_charge = Double.parseDouble(response_catalog.getShipping_charges());
            tax = single_pc_price_gst - single_pc_price;
            txt_catalog_price.setText("\u20B9" + decimalFormat.format(single_pc_price) + " + " + decimalFormat.format(tax) + "(tax)" + " + " + decimalFormat.format(shipping_charge) + " (shipping)");

            txt_price_label.setText("Price/Piece: ");
            txt_resale_price_label.setText("Resale Price/Piece: ");
        } else {
            // Set-matching price set
            if (response_catalog.getPrice_per_design_with_gst() != null) {
                double full_set_price = 0.0;
                full_set_price = (Double.parseDouble(response_catalog.getPrice_per_design_with_gst()) * Integer.parseInt(response_catalog.getNo_of_pcs_per_design()))
                        + Double.parseDouble(response_catalog.getShipping_charges());
                double avg_price = 0.0;
                avg_price = full_set_price / Integer.parseInt(response_catalog.getNo_of_pcs_per_design());
                txt_final_price.setText("=" + " \u20B9" + decimalFormat.format(avg_price));
                price = avg_price;
            }

            // start price breakup logic
            double tax = 0.0;
            double price_per_design = Double.parseDouble(response_catalog.getPrice_per_design());
            double price_per_design_gst = Double.parseDouble(response_catalog.getPrice_per_design_with_gst());
            double price_per_design_shipping_charge = Double.parseDouble(response_catalog.getShipping_charges()) / Integer.parseInt(response_catalog.getNo_of_pcs_per_design());
            tax = price_per_design_gst - price_per_design;
            txt_catalog_price.setText("\u20B9" + decimalFormat.format(price_per_design) + " + " + decimalFormat.format(tax) + "(tax)" + " + " + decimalFormat.format(price_per_design_shipping_charge) + " (shipping)");

            txt_price_label.setText("Price/Piece: ");
            txt_resale_price_label.setText("Resale Price/Piece: ");

        }

    }

    private void postShare(Context context, ProductObj[] productObjs, StaticFunctions.SHARETYPE mode) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
        RequestProductBatchUpdate requestProductBatchUpdate = new RequestProductBatchUpdate();
        ArrayList<ProductObj> shaProductObjArrayList = new ArrayList<>();
        for (ProductObj p :
                productObjs) {
            ProductObj productObj = new ProductObj();
            productObj.setProduct_id(p.getId());
            productObj.setResell_price(edit_resale_price.getText().toString());
            productObj.setShared_on(mode.name());
            productObj.setSell_full_catalog(false);
            productObj.setActual_price(String.valueOf(price));
            shaProductObjArrayList.add(productObj);
        }
        requestProductBatchUpdate.setProducts(shaProductObjArrayList);
        Log.e(TAG, "postShare: =====>" + Application_Singleton.gson.toJson(requestProductBatchUpdate));
        HttpManager.getInstance((Activity) context).requestwithObject(HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS, URLConstants.companyUrl(context, "product-share/start-sharing", ""), Application_Singleton.gson.fromJson(Application_Singleton.gson.toJson(requestProductBatchUpdate), JsonObject.class), headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    ResponseProductShare productShares = Application_Singleton.gson.fromJson(response, ResponseProductShare.class);
                    if (productShares.getB2c_product_url_list().size() > 0) {
                        product_b2c_urls = productShares.getB2c_product_url_list();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (mode == StaticFunctions.SHARETYPE.LINKSHARE) {
                    Log.e(TAG, "onServerResponse: ====>Link Share");
                    linkShare(getActivity(), response_catalog);
                } else if (mode == StaticFunctions.SHARETYPE.FBPAGE) {
                    openFbSharePage();
                } else {
                    Log.e(TAG, "onServerResponse: ====>Download Copy Share");
                    downloadAndCopyDetails(getActivity(), resellerShareAdapter.getSelectedItems(), mode, response_catalog.getTitle());
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    private void postScreenShare(Context context, Response_catalog response_catalog, StaticFunctions.SHARETYPE mode) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
        RequestProductBatchUpdate requestProductBatchUpdate = new RequestProductBatchUpdate();
        ArrayList<ProductObj> shaProductObjArrayList = new ArrayList<>();
        ProductObj productObj = new ProductObj();
        productObj.setProduct_id(response_catalog.getId());
        productObj.setResell_price(edit_resale_price.getText().toString());
        productObj.setShared_on(mode.name());
        productObj.setActual_price(String.valueOf(price));
        productObj.setSell_full_catalog(true);
        shaProductObjArrayList.add(productObj);
        requestProductBatchUpdate.setProducts(shaProductObjArrayList);
        Log.e(TAG, "postShare: =====>" + Application_Singleton.gson.toJson(requestProductBatchUpdate));
        HttpManager.getInstance((Activity) context).requestwithObject(HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS, URLConstants.companyUrl(context, "product-share/start-sharing", ""), Application_Singleton.gson.fromJson(Application_Singleton.gson.toJson(requestProductBatchUpdate), JsonObject.class), headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    ResponseProductShare productShares = Application_Singleton.gson.fromJson(response, ResponseProductShare.class);
                    if (productShares.getB2c_product_url_list().size() > 0) {
                        product_b2c_urls = productShares.getB2c_product_url_list();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (mode == StaticFunctions.SHARETYPE.LINKSHARE) {
                    linkShare(getActivity(), response_catalog);
                } else if (mode == StaticFunctions.SHARETYPE.FBPAGE) {
                    openFbSharePage();
                } else {
                    downloadAndCopyDetails(getActivity(), resellerShareAdapter.getSelectedItems(), mode, response_catalog.getTitle());
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    public ArrayList<String> getSelectedSizesDistinctEav(Response_catalog response_catalog) {
        if (response_catalog != null) {
            ArrayList<String> catalog_sizes = new ArrayList<>();
            for (int i = 0; i < response_catalog.getProduct().length; i++) {
                if (response_catalog.getProduct()[i].getAvailable_size_string() != null && !response_catalog.getProduct()[i].getAvailable_size_string().isEmpty())
                    //String[] ss = new String{response_catalog.getProduct()[i].getAvailable_size_string().split(",");};
                    catalog_sizes.addAll(Arrays.asList(response_catalog.getProduct()[i].getAvailable_size_string().split(",")));
            }
            ArrayList<String> listFromSet = new ArrayList<String>(new HashSet<String>(catalog_sizes));
            return listFromSet;
        } else {
            return null;
        }
    }


    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag);
            ft.commit();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public boolean checkB2CALLURLISSame(ArrayList<String> product_b2c_urls) {
        boolean allMatch = true;
        if (product_b2c_urls != null && product_b2c_urls.size() > 0) {
            String str = product_b2c_urls.get(0);
            for (String string : product_b2c_urls) {
                if (!string.equals(str)) {
                    allMatch = false;
                    break;
                }
            }
            return allMatch;
        }
        return allMatch;
    }

    public void openFbSharePage() {
        ArrayList<String> photos = new ArrayList<>();
        for (int i = 0; i < resellerShareAdapter.getSelectedItems().length; i++) {
            photos.add(resellerShareAdapter.getSelectedItems()[i].getImage().getThumbnail_medium());
        }
        Bundle bundle = new Bundle();
        bundle.putString("data", copyProductDetail(false, resellerShareAdapter.getSelectedItems().length, edit_resale_price.getText().toString()));
        bundle.putStringArrayList("images", photos);
        bundle.putString("name", response_catalog.getCatalog_title());
        Fragment_FBPostText fragment_fbPostText = new Fragment_FBPostText();
        fragment_fbPostText.setArguments(bundle);
        Application_Singleton.CONTAINER_TITLE = "Post on Facebook";
        Application_Singleton.CONTAINERFRAG = fragment_fbPostText;
        StaticFunctions.switchActivity(getActivity(), OpenContainer.class);
    }

    // #################### Send Wishbook Analysis Data #####################################//

    public void sendProductShareAnalysis(Context context, Response_catalog response_catalog,
                                         String resale_price, String share_type, boolean isFullCatalogShare) {
        UserInfo userInfo = UserInfo.getInstance(context);
        WishbookEvent wishbookEvent = new WishbookEvent();
        wishbookEvent.setEvent_category(WishbookEvent.PRODUCT_EVENTS_CATEGORY);
        wishbookEvent.setEvent_names("Product_Share");
        HashMap<String, String> prop = new HashMap<>();
        if (from != null) {
            if (from.equalsIgnoreCase(Fragment_BrowseProduct.class.getSimpleName()))
                prop.put("source", "Product Detail");
            else
                prop.put("source", from);
        }

        prop.put("product_type", response_catalog.getProduct_type());
        prop.put("full_catalog", response_catalog.full_catalog_orders_only);
        if (response_catalog.getBrand() != null)
            prop.put("brand", response_catalog.getBrand().getName());
        else
            prop.put("brand", "No Brand");

        if (response_catalog.getProduct_type() != null && response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN)) {
            prop.put("num_items", String.valueOf(response_catalog.getNo_of_pcs_per_design()));
        } else {
            if (response_catalog.getProduct() != null)
                prop.put("num_items", String.valueOf(response_catalog.getProduct().length));
        }
        prop.put("product_id", response_catalog.getId());
        prop.put("product_name", response_catalog.getTitle());
        if (response_catalog.getImage() != null && response_catalog.getImage().getThumbnail_small() != null)
            prop.put("product_cover_image", response_catalog.getImage().getThumbnail_small());
        if (response_catalog.getProduct_type() != null && response_catalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN))
            prop.put("set_type", response_catalog.getCatalog_multi_set_type());

        if (response_catalog.getPrice_range() != null) {
            prop.put("full_set_price", response_catalog.getPrice_range());
        }


        if (response_catalog.getSingle_piece_price_range() != null)
            prop.put("single_pc_price", response_catalog.getSingle_piece_price());

        if (response_catalog.getCategory_name() != null) {
            prop.put("category", response_catalog.getCategory_name());
        }

        if (response_catalog.getPrice_range() != null) {
            if (response_catalog.getPrice_range().contains("-")) {
                String[] priceRangeMultiple = response_catalog.getPrice_range().split("-");
                prop.put("lowest_price", priceRangeMultiple[0]);
            } else {
                prop.put("lowest_price", response_catalog.getPrice_range());
            }
        }

        prop.put("share_price", resale_price);
        prop.put("share_type", share_type);
        //prop.put("is_full_catalog_share", String.valueOf(isFullCatalogShare));
        wishbookEvent.setEvent_properties(prop);
        new WishbookTracker(getActivity(), wishbookEvent);
    }

}