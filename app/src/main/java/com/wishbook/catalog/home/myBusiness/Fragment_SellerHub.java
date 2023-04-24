package com.wishbook.catalog.home.myBusiness;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.Activity_AddCatalog;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.DeepLinkFunction;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.analysis.WishbookTracker;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.Utils.util.LocaleHelper;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.WishbookEvent;
import com.wishbook.catalog.commonmodels.responses.ResponseSellerDashBoard;
import com.wishbook.catalog.home.catalog.CatalogHolder;
import com.wishbook.catalog.home.catalog.Fragment_Catalogs;
import com.wishbook.catalog.home.more.Fragment_AddBrand;
import com.wishbook.catalog.home.more.Fragment_Mybrands;
import com.wishbook.catalog.home.more.brandDiscount.ActivityBrandwiseDiscountList;
import com.wishbook.catalog.home.orders.ActivityEnquiryHolder;
import com.wishbook.catalog.home.orders.ActivityOrderHolder;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Fragment_SellerHub extends GATrackedFragment implements View.OnClickListener {


    View view;

    @BindView(R.id.txt_myproducts)
    TextView txt_myproducts;

    @BindView(R.id.txt_addproduct)
    TextView txt_addproduct;

    @BindView(R.id.txt_mybrand)
    TextView txt_mybrand;


    @BindView(R.id.txt_discount_setting)
    TextView txt_discount_setting;

    @BindView(R.id.txt_return_policy)
    TextView txt_return_policy;

    @BindView(R.id.txt_payouts)
    TextView txt_payouts;

    @BindView(R.id.txt_pending_order_items)
    TextView txt_pending_order_items;

    @BindView(R.id.txt_cancellation_rate_value)
    TextView txt_cancellation_rate_value;

    @BindView(R.id.txt_avg_order_delay_value)
    TextView txt_avg_order_delay_value;

    @BindView(R.id.liner_live_catalog)
    LinearLayout liner_live_catalog;

    @BindView(R.id.linear_live_noncatalog)
    LinearLayout linear_live_noncatalog;

    @BindView(R.id.linear_live_set)
    LinearLayout linear_live_set;

    @BindView(R.id.txt_live_catalog_value)
    TextView txt_live_catalog_value;

    @BindView(R.id.txt_live_noncatalog_value)
    TextView txt_live_noncatalog_value;

    @BindView(R.id.txt_live_set_value)
    TextView txt_live_set_value;

    @BindView(R.id.txt_leads)
    TextView txt_leads;

    @BindView(R.id.txt_sales_orders)
    TextView txt_sales_orders;

    @BindView(R.id.txt_leads_value)
    TextView txt_leads_value;

    @BindView(R.id.txt_sales_order_value)
    TextView txt_sales_order_value;

    @BindView(R.id.linear_sales_order)
    LinearLayout linear_sales_order;

    @BindView(R.id.linear_leads)
    LinearLayout linear_leads;

    public static SharedPreferences pref;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) view.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.fragment_seller_hub, ga_container, true);
        ButterKnife.bind(this, view);
        pref = StaticFunctions.getAppSharedPreferences(getActivity());
        initView();
        // Temporary Function
        hideOrderDisableConfig();
        return view;

    }

    private void initView() {

        txt_myproducts.setOnClickListener(this);
        txt_addproduct.setOnClickListener(this);
        txt_mybrand.setOnClickListener(this);
        txt_discount_setting.setOnClickListener(this);
        txt_return_policy.setOnClickListener(this);

        txt_payouts.setOnClickListener(this);
        txt_pending_order_items.setOnClickListener(this);

        liner_live_catalog.setOnClickListener(this);
        linear_live_noncatalog.setOnClickListener(this);
        linear_live_set.setOnClickListener(this);

        txt_leads.setOnClickListener(this);
        txt_sales_orders.setOnClickListener(this);


        txt_leads_value.setOnClickListener(this);
        txt_sales_order_value.setOnClickListener(this);


        linear_sales_order.setOnClickListener(this);
        linear_leads.setOnClickListener(this);

        getSellerDashboards();
    }


    @Override
    public void onClick(View view) {
        HashMap<String, String> param = new HashMap<>();
        switch (view.getId()) {
            case R.id.txt_myproducts: {
                Bundle bundle = new Bundle();
                Fragment_Catalogs fragmentCatalogs = new Fragment_Catalogs(CatalogHolder.MYCATALOGS);
                fragmentCatalogs.setArguments(bundle);
                Application_Singleton.CONTAINER_TITLE = "My Products";
                Application_Singleton.CONTAINERFRAG = fragmentCatalogs;
                Intent intent2 = new Intent(getActivity(), OpenContainer.class);
                startActivity(intent2);
            }
            break;

            case R.id.txt_addproduct: {
                if (!UserInfo.getInstance(getActivity()).isCompanyProfileSet()
                        && UserInfo.getInstance(getActivity()).getCompanyname().isEmpty()) {
                    param.put("type", "profile_update");
                    new DeepLinkFunction(param, getActivity());
                    return;
                }
                StaticFunctions.switchActivity(getActivity(), Activity_AddCatalog.class);
                WishbookTracker.sendScreenEvents(getActivity(), WishbookEvent.SELLER_EVENTS_CATEGORY, "CatalogItem_Add_screen", "My business page", null);
            }
            break;

            case R.id.txt_mybrand: {
                if (pref.getString("groupstatus", "0").equals("1") && UserInfo.getInstance(getActivity()).getCompanyType().equals("buyer")) {
                    if (pref.getString("brandadded", "no").equals("no")) {
                        Application_Singleton.CONTAINER_TITLE = "My Brands";
                        Application_Singleton.CONTAINERFRAG = new Fragment_AddBrand();
                        StaticFunctions.switchActivity(getActivity(), OpenContainer.class);

                    } else {
                        Application_Singleton.CONTAINER_TITLE = "My Brands";
                        Application_Singleton.CONTAINERFRAG = new Fragment_Mybrands();
                        StaticFunctions.switchActivity(getActivity(), OpenContainer.class);

                    }
                } else {
                    Application_Singleton.CONTAINER_TITLE = "My Brands";
                    Application_Singleton.CONTAINERFRAG = new Fragment_Mybrands();
                    StaticFunctions.switchActivity(getActivity(), OpenContainer.class);
                }
            }
            break;

            case R.id.txt_discount_setting: {
                StaticFunctions.switchActivity(getActivity(), ActivityBrandwiseDiscountList.class);
            }

            break;

            case R.id.txt_payouts: {
                param.put("type", "tab");
                param.put("page", "sellerpayout");
                param.put("from", "Seller Hub");
                new DeepLinkFunction(param, getActivity());

            }
            break;

            case R.id.txt_pending_order_items:
                param.put("type", "tab");
                param.put("page", "sellerpendingitem");
                param.put("from", "Seller Hub");
                new DeepLinkFunction(param, getActivity());
                break;

            case R.id.linear_live_set:
                navigateToMyCatalog(Constants.PRODUCT_TYPE_SCREEN);
                break;

            case R.id.linear_live_noncatalog:
                navigateToMyCatalog(Constants.PRODUCT_TYPE_NON);
                break;

            case R.id.liner_live_catalog:
                navigateToMyCatalog(Constants.PRODUCT_TYPE_CAT);
                break;

            case R.id.txt_sales_orders:
            case R.id.linear_sales_order:
                startActivity(new Intent(getActivity(), ActivityOrderHolder.class).putExtra("type", "sale").putExtra("position", 0).putExtra("date_range_type", "all orders"));
                break;

            case R.id.txt_leads:
            case R.id.linear_leads:
                startActivity(new Intent(getActivity(), ActivityEnquiryHolder.class).putExtra("type", "leads").putExtra("position", 1));
                break;

            case R.id.txt_return_policy: {
                param.put("type", "webview");
                param.put("page", "https://www.wishbook.io/seller-return-policy/");
                new DeepLinkFunction(param, getActivity());
            }
                break;
        }
    }

    private void getSellerDashboards() {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        showProgress();
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "seller-dashboard", ""), null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }


            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgress();
                try {
                    final ResponseSellerDashBoard dashBoard = Application_Singleton.gson.fromJson(response, new TypeToken<ResponseSellerDashBoard>() {
                    }.getType());
                    if (dashBoard.getCancellation_rate() != null)
                        txt_cancellation_rate_value.setText(dashBoard.getCancellation_rate() + " %");
                    if (dashBoard.getAvg_order_delay() != null)
                        txt_avg_order_delay_value.setText(dashBoard.getAvg_order_delay() + " days");

                    if (dashBoard.getLive_catalogs() != null)
                        txt_live_catalog_value.setText(dashBoard.getLive_catalogs());
                    if (dashBoard.getLive_non_catalogs() != null)
                        txt_live_noncatalog_value.setText(dashBoard.getLive_non_catalogs());
                    if (dashBoard.getLive_sets() != null)
                        txt_live_set_value.setText(dashBoard.getLive_sets());

                    if(dashBoard.getLeads()!=null && !dashBoard.getLeads().isEmpty()) {
                        txt_leads_value.setText(dashBoard.getLeads());
                    }

                    if(dashBoard.getSales_orders()!=null && !dashBoard.getSales_orders().isEmpty()) {
                        txt_sales_order_value.setText(dashBoard.getSales_orders());
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

    }

    public void navigateToMyCatalog(String product_type) {
        Bundle bundle = new Bundle();
        Fragment_Catalogs fragmentCatalogs = new Fragment_Catalogs(CatalogHolder.MYCATALOGS);
        bundle.putString("product_type", product_type);
        fragmentCatalogs.setArguments(bundle);
        Application_Singleton.CONTAINER_TITLE = "My Products";
        Application_Singleton.CONTAINERFRAG = fragmentCatalogs;
        Intent intent2 = new Intent(getActivity(), OpenContainer.class);
       // intent2.putExtra("toolbarCategory", OpenContainer.ADD_CATALOG);
        String supplier_approval_status = UserInfo.getInstance(getActivity()).getSupplierApprovalStatus();
        if (supplier_approval_status == null) {
            UserInfo.getInstance(getActivity()).setSupplierApprovalStatus(Constants.SELLER_APPROVAL_FIRSTTIME_UPLOAD);
        }
        startActivity(intent2);
    }


    /**
     * Temp Function for Hide Order Disable Config
     */
    public void hideOrderDisableConfig() {
        // Hide Leads
        // Hide Add Products
        if(StaticFunctions.checkOrderDisableConfig(getActivity())) {
            txt_addproduct.setVisibility(View.GONE);
            txt_leads.setVisibility(View.GONE);
            linear_leads.setOnClickListener(null);
        }

    }
}
