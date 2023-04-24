package com.wishbook.catalog.home.orderNew.details;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.responses.Feedback_by;
import com.wishbook.catalog.commonmodels.responses.ResponseCompanyRating;
import com.wishbook.catalog.home.contacts.details.Fragment_SupplierDetailsNew2;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BuyerCreditRatingBottomSheetDialogFragment extends BottomSheetDialogFragment {

    String mBuyerId;

    @BindView(R.id.linear_toolbar)
    LinearLayout linear_toolbar;

    @BindView(R.id.txt_toolbar_tile)
    TextView txt_toolbar_tile;

    @BindView(R.id.img_close)
    ImageView img_close;

    @BindView(R.id.img_overall_rating)
    ImageView img_overall_rating;


    @BindView(R.id.linear_supplier_list_container)
    LinearLayout linear_supplier_list_container;
    @BindView(R.id.txt_overall_rating_value)
    TextView txt_overall_rating_value;
    @BindView(R.id.txt_bereau_value)
    TextView txt_bereau_value;
    @BindView(R.id.txt_finacial_statement_value)
    TextView txt_finacial_statement_value;
    @BindView(R.id.txt_feedback_suppliers_value)
    TextView txt_feedback_suppliers_value;
    @BindView(R.id.txt_avg_payment_value)
    TextView txt_avg_payment_value;
    @BindView(R.id.txt_goods_return_value)
    TextView txt_goods_return_value;

    @BindView(R.id.total_feedback)
    LinearLayout total_feedback;

    @BindView(R.id.linear_overall_rating)
    LinearLayout linear_overall_rating;

    @BindView(R.id.linear_bureau_report)
    LinearLayout linear_bureau_report;

    @BindView(R.id.linear_finacial_statement)
    LinearLayout linear_finacial_statement;

    @BindView(R.id.linear_avg_payment_time)
    LinearLayout linear_avg_payment_time;


    @BindView(R.id.linear_goods_retrun_rate)
    LinearLayout linear_goods_retrun_rate;

    @BindView(R.id.relative_bottom_progress)
    RelativeLayout relative_bottom_progress;

    @BindView(R.id.linear_credit_container)
    LinearLayout linear_credit_container;


    public static BuyerCreditRatingBottomSheetDialogFragment newInstance(String string, String buyer_company_name) {
        BuyerCreditRatingBottomSheetDialogFragment f = new BuyerCreditRatingBottomSheetDialogFragment();
        if (string != null) {
            Bundle args = new Bundle();
            args.putString("buyer_company_id", string);
            args.putString("buyer_company_name", buyer_company_name);
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
        View v = inflater.inflate(R.layout.bottom_sheet_buyer_credit_rationg_info, container, false);
        ButterKnife.bind(this, v);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        if (getArguments().getString("buyer_company_id") != null) {
            getCompanyRating(getArguments().getString("buyer_company_id"));
        }
        if (getArguments().getString("buyer_company_name") != null) {
            txt_toolbar_tile.setText(getArguments().getString("buyer_company_name"));
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
                bottomSheetBehavior.setPeekHeight((int) (size.y / 1.70));
                bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                    @Override
                    public void onStateChanged(@NonNull View bottomSheet, int newState) {
                        if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                            setupToolbarBlue();
                        } else if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                            dismiss();
                        } else {
                            setupToolbarWhite();
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

    public void setupToolbarWhite() {
        linear_toolbar.setBackgroundColor(getResources().getColor(R.color.white));
        txt_toolbar_tile.setTextColor(getResources().getColor(R.color.purchase_dark_gray));
        img_close.setImageDrawable(getResources().getDrawable(R.drawable.ic_close_black_24dp));
        img_close.setColorFilter(ContextCompat.getColor(getActivity(), R.color.purchase_medium_gray), android.graphics.PorterDuff.Mode.MULTIPLY);
    }

    public void setupToolbarBlue() {
        linear_toolbar.setBackgroundColor(getResources().getColor(R.color.color_primary));
        txt_toolbar_tile.setTextColor(getResources().getColor(R.color.white));
        img_close.setImageDrawable(getResources().getDrawable(R.drawable.ic_close_white_24dp));
        img_close.setColorFilter(ContextCompat.getColor(getActivity(), R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
    }


    public void getCompanyRating(final String companyId) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        showBottomProgress();
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "credit-rating", companyId), null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                try {
                    hideBottomProgress();
                    if (isAdded() && !isDetached()) {
                        ArrayList<ResponseCompanyRating> responseData = Application_Singleton.gson.fromJson(response, new TypeToken<ArrayList<ResponseCompanyRating>>() {
                        }.getType());
                        if (responseData.size() > 0) {
                            final ResponseCompanyRating companyRating = responseData.get(0);
                            if (companyRating.getRating() != null) {
                                if (companyRating.getRating().equals(Constants.BUYER_CREDIT_RATING_UNRATED)) {
                                    txt_overall_rating_value.setText("Not Available");
                                    img_overall_rating.setVisibility(View.GONE);
                                }
                                else if (companyRating.getRating().equals(Constants.BUYER_CREDIT_RATING_GOOD)){
                                    txt_overall_rating_value.setText("Good");
                                    img_overall_rating.setVisibility(View.VISIBLE);
                                }

                            }
                            if (companyRating.getBureau_report_rating() != null) {
                                txt_bereau_value.setText(companyRating.getBureau_report_rating());
                            } else {
                                linear_bureau_report.setVisibility(View.GONE);
                            }

                            if (companyRating.getFinancial_statement_rating() != null) {
                                txt_finacial_statement_value.setText(companyRating.getFinancial_statement_rating());
                            } else {
                                linear_finacial_statement.setVisibility(View.GONE);
                            }

                            if (companyRating.getTotal_feedback() != null) {
                                if(companyRating.getTotal_feedback().equals("1")){
                                    txt_feedback_suppliers_value.setText(companyRating.getTotal_feedback() + " Supplier");
                                } else {
                                    txt_feedback_suppliers_value.setText(companyRating.getTotal_feedback() + " Suppliers");
                                }
                            } else {
                                total_feedback.setVisibility(View.GONE);
                            }

                            if (companyRating.getFeedback_by_names().size() > 0) {
                                //addSupplierList(companyRating.getFeedback_by_names(), linear_supplier_list_container);
                                total_feedback.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                       /* Bundle bundle = new Bundle();
                                        bundle.putString("buying_company_id", getArguments().getString("buyer_company_id"));
                                        Fragment_Credit_Reference_Suppliers fragment_credit_reference_suppliers = new Fragment_Credit_Reference_Suppliers();
                                        fragment_credit_reference_suppliers.setArguments(bundle);
                                        Application_Singleton.CONTAINER_TITLE = "Feedback by Suppliers ";
                                        Application_Singleton.CONTAINERFRAG = fragment_credit_reference_suppliers;
                                        StaticFunctions.switchActivity(getActivity(), OpenContainer.class);*/
                                    }
                                });
                            } else {
                            }

                            if (companyRating.getAverage_payment_duration() != null) {
                                txt_avg_payment_value.setText(companyRating.getAverage_payment_duration());
                            } else {
                                linear_avg_payment_time.setVisibility(View.GONE);
                            }

                            if (companyRating.getAverage_gr_rate() != null) {
                                txt_goods_return_value.setText(companyRating.getAverage_gr_rate());
                            } else {
                                linear_goods_retrun_rate.setVisibility(View.GONE);
                            }


                        } else {
                            // NO DATA AVAILABLE
                        }
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideBottomProgress();
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }


    private void addSupplierList(final ArrayList<Feedback_by> feedbackSupplierList, LinearLayout root) {
        LayoutInflater vi = (LayoutInflater) getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        root.removeAllViews();
        for (int i = 0; i < feedbackSupplierList.size(); i++) {
            final Feedback_by supplier = feedbackSupplierList.get(i);
            View v = vi.inflate(R.layout.feedback_supplier_credit_item, null);
            TextView supplierName = (TextView) v.findViewById(R.id.txt_supplier_name);
            supplierName.setText(feedbackSupplierList.get(i).getName());
            LinearLayout supplier_container = v.findViewById(R.id.supplier_data_root);
            supplier_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    Fragment_SupplierDetailsNew2 fragment_supplier = new Fragment_SupplierDetailsNew2();
                    Application_Singleton.CONTAINER_TITLE = "Supplier Details";
                    Application_Singleton.TOOLBARSTYLE = "WHITE";
                    if (supplier.getCompany_id() != null && supplier.getRelation_id() != null) {
                        bundle.putString("sellerid", supplier.getRelation_id());
                        bundle.putString("sellerCompanyid", supplier.getCompany_id());
                    } else if (supplier.getCompany_id() != null) {
                        // for public details
                        bundle.putString("sellerid", supplier.getCompany_id());
                        bundle.putBoolean("isHideAll", true);
                    }
                    fragment_supplier.setArguments(bundle);
                    Application_Singleton.CONTAINERFRAG = fragment_supplier;
                    Intent intent = new Intent(getActivity(), OpenContainer.class);
                    getActivity().startActivity(intent);
                }
            });
            root.addView(v);
        }
    }


    private void showBottomProgress() {
        relative_bottom_progress.setVisibility(View.VISIBLE);
        linear_credit_container.setVisibility(View.GONE);

    }

    private void hideBottomProgress() {
        relative_bottom_progress.setVisibility(View.GONE);
        linear_credit_container.setVisibility(View.VISIBLE);
    }


}
