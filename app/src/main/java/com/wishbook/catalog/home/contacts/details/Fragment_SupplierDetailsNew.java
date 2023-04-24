package com.wishbook.catalog.home.contacts.details;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.flexbox.FlexboxLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.RecyclerViewEmptySupport;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.NameValues;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.postpatchmodels.PatchSeller;
import com.wishbook.catalog.commonmodels.postpatchmodels.RejectSeller;
import com.wishbook.catalog.commonmodels.responses.RequestAddBuyers;
import com.wishbook.catalog.commonmodels.responses.Response_BuyingCompany;
import com.wishbook.catalog.commonmodels.responses.Response_SellerFull;
import com.wishbook.catalog.commonmodels.responses.Response_buyingorder;
import com.wishbook.catalog.commonmodels.responses.Response_catalogMini;
import com.wishbook.catalog.home.contacts.adapter.CatalogGridAdapter;
import com.wishbook.catalog.home.models.BuyersList;
import com.wishbook.catalog.home.orderNew.details.Activity_BuyerSearch;
import com.wishbook.catalog.home.orders.adapters.PurchaseOrderAdapterNew;
import com.wishbook.catalog.home.orders.add.Fragment_CreatePurchaseOrder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import fisk.chipcloud.ChipCloud;
import fisk.chipcloud.ChipCloudConfig;
import fisk.chipcloud.ChipDeletedListener;


public class Fragment_SupplierDetailsNew extends GATrackedFragment {

    private View v;
    private RelativeLayout editcontainer;
    private Button updatepricebut;
    private Button but_cancel;
    private AppCompatButton btn_reject;
    private Button btn_approve;
    private Button but_create_order;
    private AppCompatButton but_save, btn_add_broker_per;
    private TextView btn_connect_buyer_broker;
    private SimpleDraweeView profpic;
    private EditText edit_fix_amount;
    private EditText edit_percentage_amount;
    private LinearLayout cont_createorder;
    private LinearLayout cont_pricerule;
    private String supplierID,sellerCompanyid;
    RecyclerViewEmptySupport recyclerCatalog, recyclerOrder;
    RelativeLayout relativeCatalog, relativeOrder;
    FrameLayout frameRecyclerOrder, frameRecyclerCatalog;
    boolean isCatalogExpand, isOrderExpand;
    ImageView order_arrow_img, arrow_img;
    LinearLayout linear_catalog_container, linear_order_container, linear_broker_user_container;
    FlexboxLayout flexbox_buyer;
    AppCompatButton btn_see_all, btn_add_buyers;
    RelativeLayout relative_brokerage;
    TextView txt_brokerage_value,txt_buyer_label;

    public static List<NameValues> selectedBuyers = new ArrayList<>();
    List<NameValues> getConnectedBuyers = new ArrayList<>();
    private RadioGroup radiogroupprice;

    public Fragment_SupplierDetailsNew() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup ga_container = (ViewGroup) v.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.details_supllier_new, ga_container, true);
        profpic = (SimpleDraweeView) v.findViewById(R.id.profpic);
        cont_pricerule = (LinearLayout) v.findViewById(R.id.cont_pricerule);
        cont_createorder = (LinearLayout) v.findViewById(R.id.cont_createorder);
        cont_pricerule.setVisibility(View.GONE);
        cont_createorder.setVisibility(View.GONE);



        btn_approve = (Button) v.findViewById(R.id.btn_approve);
        linear_catalog_container = (LinearLayout) v.findViewById(R.id.linear_catalog_container);
        linear_order_container = (LinearLayout) v.findViewById(R.id.linear_order_container);
        linear_broker_user_container = (LinearLayout) v.findViewById(R.id.linear_broker_user_container);
        relative_brokerage = (RelativeLayout) v.findViewById(R.id.relative_brokerage);
        txt_brokerage_value = (TextView) v.findViewById(R.id.txt_brokerage_value);
        txt_buyer_label = (TextView) v.findViewById(R.id.txt_buyer_label);


        updatepricebut = (Button) v.findViewById(R.id.updatepricebut);
        editcontainer = (RelativeLayout) v.findViewById(R.id.pricecontainer);
        edit_fix_amount = (EditText) v.findViewById(R.id.input_price);
        edit_percentage_amount = (EditText) v.findViewById(R.id.input_percentage);
        but_save = (AppCompatButton) v.findViewById(R.id.but_save);
        radiogroupprice=(RadioGroup)v.findViewById(R.id.radiogroupprice);


        frameRecyclerCatalog = (FrameLayout) v.findViewById(R.id.frame_recycler_catalog);
        relativeCatalog = (RelativeLayout) v.findViewById(R.id.relative_catalog);
        relativeOrder = (RelativeLayout) v.findViewById(R.id.relative_order);
        order_arrow_img = (ImageView) v.findViewById(R.id.order_arrow_img);
        arrow_img = (ImageView) v.findViewById(R.id.arrow_img);


        frameRecyclerOrder = (FrameLayout) v.findViewById(R.id.frame_recycler_order);
        recyclerOrder = (RecyclerViewEmptySupport) v.findViewById(R.id.recycler_order);
        recyclerOrder.setEmptyView(v.findViewById(R.id.list_empty1));
        isOrderExpand = true;
        order_arrow_img.setRotation(180);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerOrder.setLayoutManager(layoutManager);
        recyclerOrder.setHasFixedSize(true);
        recyclerOrder.setNestedScrollingEnabled(false);

        recyclerCatalog = (RecyclerViewEmptySupport) v.findViewById(R.id.recycler_catalog);
        recyclerCatalog.setEmptyView(v.findViewById(R.id.list_empty));
        isCatalogExpand = true;
        arrow_img.setRotation(180);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        recyclerCatalog.setLayoutManager(gridLayoutManager);
        recyclerOrder.setHasFixedSize(true);
        recyclerOrder.setNestedScrollingEnabled(false);

        relativeCatalog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isCatalogExpand) {
                    isCatalogExpand = true;
                    frameRecyclerCatalog.setVisibility(View.VISIBLE);
                    arrow_img.setRotation(180);
                } else {
                    isCatalogExpand = false;
                    frameRecyclerCatalog.setVisibility(View.GONE);
                    arrow_img.setRotation(0);
                }

            }
        });

        relativeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isOrderExpand) {
                    isOrderExpand = true;
                    frameRecyclerOrder.setVisibility(View.VISIBLE);
                    order_arrow_img.setRotation(180);
                } else {
                    isOrderExpand = false;
                    frameRecyclerOrder.setVisibility(View.GONE);
                    order_arrow_img.setRotation(0);
                }
            }
        });
        btn_reject = (AppCompatButton) v.findViewById(R.id.btn_reject);
        but_create_order = (Button) v.findViewById(R.id.createorder);
        flexbox_buyer = (FlexboxLayout) v.findViewById(R.id.flexbox_buyer);
        btn_connect_buyer_broker = (TextView) v.findViewById(R.id.btn_connect_buyer_broker);
        btn_add_broker_per = (AppCompatButton) v.findViewById(R.id.btn_add_broker_per);
        btn_add_buyers = (AppCompatButton) v.findViewById(R.id.btn_add_buyers);
        btn_see_all = (AppCompatButton) v.findViewById(R.id.btn_see_all);
        btn_connect_buyer_broker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getActivity(), Activity_BuyerSearch.class)
                                .putExtra("isMultipleSelect", true)
                                .putExtra("bottom_action", "save"),
                        Application_Singleton.BUYER_SEARCH_REQUEST_CODE);
            }
        });
        btn_add_broker_per.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBrokerageDialog();
            }
        });

        updatepricebut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editcontainer.getVisibility() == View.VISIBLE) {
                    editcontainer.setVisibility(View.GONE);
                } else {
                    editcontainer.setVisibility(View.VISIBLE);
                }

            }
        });

        btn_add_buyers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getActivity(), Activity_BuyerSearch.class)
                                .putExtra("isMultipleSelect", true)
                                .putExtra("bottom_action", "save")
                                .putExtra("previouslyArray", (Serializable) getConnectedBuyers),
                        Application_Singleton.BUYER_SEARCH_REQUEST_CODE);
            }
        });
        btn_see_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getActivity(), Activity_BuyerSearch.class)
                                .putExtra("isMultipleSelect", true)
                                .putExtra("type","connected_buyers")
                        .putExtra("selling_comapny_id",sellerCompanyid)
                        .putExtra("supplier_id",supplierID)
                                .putExtra("bottom_action", "delete"),
                        Application_Singleton.BUYER_REMOVE_REQUEST_CODE);
            }
        });



        radiogroupprice.check(R.id.check_add);
        edit_fix_amount.setEnabled(false);
        edit_percentage_amount.setSelection(1);
        edit_percentage_amount.setEnabled(true);


        radiogroupprice.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {

                        if(checkedId != R.id.check_add){
                            edit_fix_amount.setEnabled(true);
                            edit_fix_amount.requestFocus();
                            edit_fix_amount.setSelection(1);
                            edit_percentage_amount.setEnabled(false);
                            edit_percentage_amount.setText("0");
                            edit_percentage_amount.setError(null);
                            InputMethodManager imm = (InputMethodManager)   getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(edit_fix_amount, InputMethodManager.SHOW_IMPLICIT);


                        }
                        else {

                            edit_fix_amount.setEnabled(false);
                            edit_fix_amount.setText("0");
                            edit_fix_amount.setError(null);
                            edit_percentage_amount.setEnabled(true);
                            edit_percentage_amount.requestFocus();
                            edit_percentage_amount.setSelection(1);


                            InputMethodManager imm = (InputMethodManager)   getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(edit_percentage_amount, InputMethodManager.SHOW_IMPLICIT);

                        }


                    }
                }
        );


        if (getArguments() != null) {
            if (!getArguments().getString("sellerid", "").equals("")) {
                supplierID = getArguments().getString("sellerid", "");

                sellerCompanyid = getArguments().getString("sellerCompanyid");
                if (sellerCompanyid != null) {
                    getCatalogs(sellerCompanyid);
                }
                getSeller(getArguments().getString("sellerid", ""));
            }
        }

        if (UserInfo.getInstance(getActivity()).getBroker() != null && UserInfo.getInstance(getActivity()).getBroker()) {
            linear_broker_user_container.setVisibility(View.VISIBLE);
            if(sellerCompanyid!=null){
                getConnectedBrokerage(sellerCompanyid);
            }
        } else {
            linear_broker_user_container.setVisibility(View.GONE);
        }


        but_create_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*getActivity().getSupportFragmentManager().beginTransaction().replace(R.la)*/

                Application_Singleton.CONTAINER_TITLE = getResources().getString(R.string.new_purchase_order);
                Fragment_CreatePurchaseOrder purchase = new Fragment_CreatePurchaseOrder();
                Bundle bundle = new Bundle();
                bundle.putString("sellerid", supplierID);
                purchase.setArguments(bundle);
                Application_Singleton.CONTAINERFRAG = purchase;
                Intent intent = new Intent(getActivity(), OpenContainer.class);
                startActivity(intent);
            }
        });


        // getSeller(getArguments().getString("sellerid",""));
        btn_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
                RejectSeller rejectBuyer = new RejectSeller(getArguments().getString("sellerid", ""), "rejected");
                Gson gson = new Gson();
                HttpManager.getInstance(getActivity()).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(getActivity(), "sellers", "") + getArguments().getString("sellerid", "") + '/', gson.fromJson(gson.toJson(rejectBuyer), JsonObject.class), headers, new HttpManager.customCallBack() {
                    @Override
                    public void onCacheResponse(String response) {

                    }

                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {
                        getActivity().setResult(Activity.RESULT_OK);
                        getActivity().finish();
                        // progressDialog.dismiss();
                    }

                    @Override
                    public void onResponseFailed(ErrorString error) {
                        StaticFunctions.showResponseFailedDialog(error);
                    }
                });
            }
        });
        btn_approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
                RejectSeller rejectBuyer = new RejectSeller(getArguments().getString("sellerid", ""), "approved");
                Gson gson = new Gson();
                HttpManager.getInstance(getActivity()).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(getActivity(), "sellers", "") + getArguments().getString("sellerid", "") + '/', gson.fromJson(gson.toJson(rejectBuyer), JsonObject.class), headers, new HttpManager.customCallBack() {
                    @Override
                    public void onCacheResponse(String response) {

                    }

                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {
                        getActivity().setResult(Activity.RESULT_OK);
                        getActivity().finish();
                        // progressDialog.dismiss();
                    }

                    @Override
                    public void onResponseFailed(ErrorString error) {
                        new MaterialDialog.Builder(getActivity())
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
                        //  progressDialog.dismiss();
                    }
                });
            }
        });

        but_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
                Gson gson = new Gson();
                PatchSeller patchSeller = new PatchSeller();
                patchSeller.setId(getArguments().getString("sellerid", ""));
               /* patchSeller.setFix_amount(edit_fix_amount.getText().toString());
                patchSeller.setPercentage_amount(edit_percentage_amount.getText().toString());
*/

                if (radiogroupprice.getCheckedRadioButtonId() != R.id.check_add) {
                    Log.i("TAG", "onClick: Fixed Rate");
                    patchSeller.setFix_amount(edit_fix_amount.getText().toString());
                    patchSeller.setPercentage_amount("0");
                }
                else {
                    Log.i("TAG", "onClick: percentage Rate");
                    patchSeller.setFix_amount("0");
                    patchSeller.setPercentage_amount(edit_percentage_amount.getText().toString());

                }


                HttpManager.getInstance(getActivity()).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(getActivity(), "sellers", "") + getArguments().getString("sellerid", "") + '/', gson.fromJson(gson.toJson(patchSeller), JsonObject.class), headers, new HttpManager.customCallBack() {
                    @Override
                    public void onCacheResponse(String response) {

                    }

                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {
                        editcontainer.setVisibility(View.VISIBLE);
                        // progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Supplier details updated", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponseFailed(ErrorString error) {
                        StaticFunctions.showResponseFailedDialog(error);
                    }
                });
            }

        });

        return v;
    }

    private void getSeller(String id) {
        showProgress();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "sellers", "") + id + "/?expand=true", null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                Log.v("cached response", response);
                onServerResponse(response, false);

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgress();
                Log.v("sync response", response);
                final Response_SellerFull response_seller = new Gson().fromJson(response, Response_SellerFull.class);
                setupOrder(response_seller);
                setAddress(response_seller.getBuying_company());
                ((TextView) v.findViewById(R.id.det_name)).setText(response_seller.getSelling_company().getName());
                ((TextView) v.findViewById(R.id.det_email)).setText(response_seller.getSelling_company().getEmail());
                ((TextView) v.findViewById(R.id.det_num)).setText(response_seller.getSelling_company().getPhone_number());
                edit_fix_amount.setText(response_seller.getFix_amount());
                edit_percentage_amount.setText(response_seller.getPercentage_amount());
                if (response_seller.getSelling_company().getThumbnail() != null && !response_seller.getSelling_company().getThumbnail().equals("")) {
                    //StaticFunctions.loadImage(getActivity(),response_seller.getSelling_company().getThumbnail(),profpic,R.drawable.uploadempty);
                    StaticFunctions.loadFresco(getActivity(), response_seller.getSelling_company().getThumbnail(), profpic);
                    // Picasso.with(getActivity()).load(response_seller.getSelling_company().getThumbnail()).into(profpic);
                }

                if (response_seller.getStatus().equals("rejected")) {
                    btn_reject.setVisibility(View.GONE);
                    btn_approve.setVisibility(View.VISIBLE);
                    //change for supplier new UI
                    //cont_pricerule.setVisibility(View.GONE);
                    editcontainer.setVisibility(View.GONE);
                    cont_createorder.setVisibility(View.GONE);
                    linear_order_container.setVisibility(View.GONE);
                    linear_catalog_container.setVisibility(View.GONE);
                } else {
                    //change for supplier new UI
                    //cont_pricerule.setVisibility(View.VISIBLE);
                    editcontainer.setVisibility(View.VISIBLE);
                    linear_order_container.setVisibility(View.VISIBLE);
                    linear_catalog_container.setVisibility(View.VISIBLE);
                    cont_createorder.setVisibility(View.GONE);
                    btn_reject.setVisibility(View.VISIBLE);
                    btn_approve.setVisibility(View.GONE);


                }
                if (UserInfo.getInstance(getActivity()).getBroker() != null && UserInfo.getInstance(getActivity()).getBroker()) {
                    if (!response_seller.getBrokerage_fees().equals("0.00")) {
                        relative_brokerage.setVisibility(View.VISIBLE);
                        txt_brokerage_value.setText(response_seller.getBrokerage_fees() + "%");
                        btn_add_broker_per.setVisibility(View.GONE);
                    } else {
                        relative_brokerage.setVisibility(View.GONE);
                        btn_add_broker_per.setVisibility(View.VISIBLE);
                    }
                }
                v.findViewById(R.id.det_num).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + response_seller.getSelling_company().getPhone_number()));
                        getActivity().startActivity(intent);
                    }
                });
                v.findViewById(R.id.det_email).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + response_seller.getSelling_company().getEmail()));
                        getActivity().startActivity(intent);
                    }
                });
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
                Log.v("error response", error.getErrormessage());
            }
        });
    }

    static class Adapter extends FragmentStatePagerAdapter {
        private final List<GATrackedFragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(GATrackedFragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public GATrackedFragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }

    private void setupOrder(final Response_SellerFull response_sellerFull) {
        if (response_sellerFull.getBuying_company().getSelling_order() != null
                && response_sellerFull.getBuying_company().getSelling_order().size() > 0) {
            ArrayList<Response_buyingorder> orders = response_sellerFull.getBuying_company().getSelling_order();
            //BuyingOrderAdapter buyingOrderAdapter = new BuyingOrderAdapter(getActivity(), orders);
            PurchaseOrderAdapterNew buyingOrderAdapter = new PurchaseOrderAdapterNew(getActivity(), orders);
            recyclerOrder.setAdapter(buyingOrderAdapter);
        }
    }

    private void setAddress(final Response_BuyingCompany response_buyingCompany) {
        if (response_buyingCompany != null) {
            if (response_buyingCompany.getBranches() != null) {
                if (response_buyingCompany.getBranches().size() > 0) {
                    String street = response_buyingCompany.getBranches().get(0).getStreetAddress();
                    if (street == null || street.equals("null")) {
                        street = "";
                    } else {
                        street = street + ",";
                    }
                    String city = response_buyingCompany.getBranches().get(0).getCity().getCityName();
                    if (city == null || city.equals("null")) {
                        city = "";
                    } else {
                        city = city + ",";
                    }
                    String state = response_buyingCompany.getBranches().get(0).getState().getStateName();
                    if (state == null || state.equals("null")) {
                        state = "";
                    }
                    ((TextView) v.findViewById(R.id.c_loc)).setText(

                            street + city + state
                    );


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        v.findViewById(R.id.c_loc).setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                    }
                }
            }
        }
    }

    private void getCatalogs(String companyId) {
        Log.i("TAG", "===============getCatalogs: ================");
        showProgress();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "catalogs", "") + "?company=" + companyId, null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgress();
                Log.v("sync response", response);
                final Response_catalogMini[] response_catalogMinis = Application_Singleton.gson.fromJson(response, Response_catalogMini[].class);
                final ArrayList<Response_catalogMini> catalogMinis = new ArrayList<Response_catalogMini>();
                if (response_catalogMinis.length > 0) {
                    Collections.addAll(catalogMinis, response_catalogMinis);
                    CatalogGridAdapter gridAdapter = new CatalogGridAdapter(catalogMinis, getActivity());
                    recyclerCatalog.setAdapter(gridAdapter);
                } else {
                    // Toast.makeText(getActivity(),"There is no catalog in product",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Application_Singleton.BUYER_SEARCH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            selectedBuyers = new ArrayList<>();
            selectedBuyers = (List<NameValues>) data.getSerializableExtra("buyer");
            txt_buyer_label.setVisibility(View.VISIBLE);
            flexbox_buyer.setVisibility(View.VISIBLE);
            btn_connect_buyer_broker.setVisibility(View.GONE);
            if(getConnectedBuyers!=null){
                getConnectedBuyers.addAll(selectedBuyers);
            }
            flexbox_buyer.removeAllViews();
            ChipCloudConfig deleteableConfig = new ChipCloudConfig()
                    .selectMode(ChipCloud.SelectMode.multi)
                    .checkedChipColor(Color.parseColor("#ddaa00"))
                    .checkedTextColor(Color.parseColor("#ffffff"))
                    .uncheckedChipColor(Color.parseColor("#e0e0e0"))
                    .showClose(Color.parseColor("#a6a6a6"))
                    .useInsetPadding(true)
                    .uncheckedTextColor(Color.parseColor("#000000"));

            ChipCloud deleteableCloud = new ChipCloud(getActivity(), flexbox_buyer, deleteableConfig);
            ArrayList<String> companies = new ArrayList<>();
            //getPhone equals comapnyid
            for (NameValues s : selectedBuyers) {
                companies.add(s.getPhone());
            }
            addChips(deleteableCloud);
            addBuyers(supplierID, companies);
        } else if( requestCode == Application_Singleton.BUYER_REMOVE_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            getConnectedBrokerage(sellerCompanyid);
        }
    }

    public void showBrokerageDialog() {
        MaterialDialog materialDialog = new MaterialDialog.Builder(getContext()).title("Add Brokerage")
                .content("Enter Brokerage")
                .inputType(InputType.TYPE_CLASS_NUMBER).inputRange(0, 3)
                .content("You can enter brokerage % only once. Any changes in the brokerage can be made by supplier only.\n")
                .input(null, null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        if (dialog.getInputEditText().getText().toString().isEmpty()) {
                            dialog.getInputEditText().setError(getResources().getString(R.string.empty_field));
                            return;
                        }
                        addBrokerage(dialog.getInputEditText().getText().toString().trim());
                    }
                }).positiveText("Save")
                .negativeText("Cancel")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                }).cancelable(false).show();
        materialDialog.getInputEditText().setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});

        materialDialog.getContentView().setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        materialDialog.getContentView().setTextColor(getResources().getColor(R.color.purchase_dark_gray));

    }

    public void addBuyers(String supplierId, ArrayList<String> companies) {

        Log.i("TAG", "addBuyers Request: ==>" + companies + "\n Supplier id" + supplierId);
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        RequestAddBuyers requestAddBuyers = new RequestAddBuyers();
        requestAddBuyers.setBuying_companies(companies);
        HttpManager.getInstance(getActivity()).requestwithObject(HttpManager.METHOD.POSTJSONOBJECT, URLConstants.companyUrl(getActivity(), "add_buyers", supplierId), new Gson().fromJson(new Gson().toJson(requestAddBuyers), JsonObject.class), headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                Log.v("cached response", response);
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                Log.v("sync response", response);
            }


            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    public void addBrokerage(final String brokerage) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        Gson gson = new Gson();
        PatchSeller patchSeller = new PatchSeller();
        patchSeller.setId(getArguments().getString("sellerid", ""));
        patchSeller.setBrokerage_fees(brokerage);
        HttpManager.getInstance(getActivity()).requestPatch(HttpManager.METHOD.PATCHWITHPROGRESS, URLConstants.companyUrl(getActivity(), "sellers", "") +supplierID + '/', gson.fromJson(gson.toJson(patchSeller), JsonObject.class), headers, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                Log.i("TAG", "onServerResponse: " + response);
                relative_brokerage.setVisibility(View.VISIBLE);
                txt_brokerage_value.setText(brokerage + "%");
                btn_add_broker_per.setVisibility(View.GONE);
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    public void getConnectedBrokerage(String sellingComapnyId) {
        showProgress();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "get_connected_buyers_broker", sellingComapnyId), null, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                hideProgress();
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                hideProgress();
                BuyersList[] buyers = Application_Singleton.gson.fromJson(response, BuyersList[].class);
                //Response_Buyer[] response_buyer = Application_Singleton.gson.fromJson(response, Response_Buyer[].class);
                if (buyers.length > 0) {
                    txt_buyer_label.setVisibility(View.VISIBLE);
                    flexbox_buyer.setVisibility(View.VISIBLE);
                    btn_connect_buyer_broker.setVisibility(View.GONE);
                    ArrayList<BuyersList> response_buyers = new ArrayList<BuyersList>(Arrays.asList(buyers));
                    getConnectedBuyers = new ArrayList<NameValues>();
                    for (int i = 0; i < response_buyers.size(); i++) {
                        if (response_buyers.get(i).getCompany_name() != null) {
                            getConnectedBuyers.add(new NameValues(response_buyers.get(i).getCompany_name(), response_buyers.get(i).getCompany_id()));
                        } else {
                            getConnectedBuyers.add(new NameValues(response_buyers.get(i).getPhone_number(), response_buyers.get(i).getCompany_id()));
                        }
                    }
                    flexbox_buyer.removeAllViews();
                    ChipCloudConfig deleteableConfig = new ChipCloudConfig()
                            .selectMode(ChipCloud.SelectMode.multi)
                            .checkedChipColor(Color.parseColor("#ddaa00"))
                            .checkedTextColor(Color.parseColor("#ffffff"))
                            .uncheckedChipColor(Color.parseColor("#e0e0e0"))
                            .showClose(Color.parseColor("#a6a6a6"))
                            .useInsetPadding(true)
                            .uncheckedTextColor(Color.parseColor("#000000"));
                    ChipCloud deleteableCloud = new ChipCloud(getActivity(), flexbox_buyer, deleteableConfig);
                    addChips(deleteableCloud);

                }else {
                    txt_buyer_label.setVisibility(View.GONE);
                    flexbox_buyer.setVisibility(View.GONE);
                    btn_connect_buyer_broker.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                hideProgress();
                new MaterialDialog.Builder(getActivity())
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

    private void addChips(ChipCloud deleteableCloud){
        try {
            if (getConnectedBuyers.size() > 4) {
                for (int i = 0; i < 4; i++) {
                    deleteableCloud.addChip(getConnectedBuyers.get(i).getName());
                }
            } else {
                for (NameValues s : getConnectedBuyers) {
                    deleteableCloud.addChip(s.getName());
                    // companies.add(s.getPhone());
                }
            }
            deleteableCloud.setDeleteListener(new ChipDeletedListener() {
                @Override
                public void chipDeleted(int i, String s) {
                    ArrayList<String> companie = new ArrayList<String>();
                    companie.add(getConnectedBuyers.get(i).getPhone());
                    removeConnectedBuyers(supplierID,companie);
                }
            });
            flexbox_buyer.addView(btn_add_buyers);
            flexbox_buyer.addView(btn_see_all);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeConnectedBuyers(String supplierId, ArrayList<String> companies) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        RequestAddBuyers requestAddBuyers = new RequestAddBuyers();
        requestAddBuyers.setBuying_companies(companies);
        HttpManager.getInstance(getActivity()).requestwithObject(HttpManager.METHOD.POSTJSONOBJECT, URLConstants.companyUrl(getActivity(), "remove_connected_buyers", supplierId), new Gson().fromJson(new Gson().toJson(requestAddBuyers), JsonObject.class), headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                Log.v("sync response", response);

            }


            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }
}

