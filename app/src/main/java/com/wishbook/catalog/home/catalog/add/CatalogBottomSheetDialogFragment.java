package com.wishbook.catalog.home.catalog.add;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wishbook.catalog.Activity_AddCatalog;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.NavigationUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.ProductMyDetail;
import com.wishbook.catalog.commonmodels.responses.Eavdata;
import com.wishbook.catalog.commonmodels.responses.Response_catalog;
import com.wishbook.catalog.home.catalog.details.Fragment_CatalogsGallery;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CatalogBottomSheetDialogFragment extends BottomSheetDialogFragment implements Activity_AddCatalog.NavigateListener {


    String mCatalogId;

    @BindView(R.id.txt_catalog_name)
    TextView txt_catalog_name;

    @BindView(R.id.txt_price_range)
    TextView txt_price_range;

    @BindView(R.id.btn_become_seller)
    TextView btn_become_seller;

    @BindView(R.id.number_designs)
    TextView number_designs;

    @BindView(R.id.txt_size)
    TextView txt_size;

    @BindView(R.id.txt_stiching_details)
    TextView txt_stiching_details;

    @BindView(R.id.txt_single_piece)
    TextView txt_single_piece;

    @BindView(R.id.txt_kurti_set)
    TextView txt_kurti_set;

    @BindView(R.id.catalog_img)
    SimpleDraweeView catalog_img;

    @BindView(R.id.txt_category)
    TextView txt_category;

    @BindView(R.id.img_close)
    ImageView img_close;

    @BindView(R.id.txt_catalog_sub_text)
    TextView txt_catalog_sub_text;

    @BindView(R.id.btn_enable_disable)
    TextView btn_enable_disable;

    @BindView(R.id.btn_see_detail)
    TextView btn_see_detail;

    Response_catalog response_catalog = null;
    ProductMyDetail productMyDetail;


    static CatalogBottomSheetDialogFragment newInstance(String string) {
        CatalogBottomSheetDialogFragment f = new CatalogBottomSheetDialogFragment();
        Bundle args = new Bundle();
        args.putString("catalog_id", string);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCatalogId = getArguments().getString("catalog_id");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.catalog_bottom_sheet_modal, container, false);
        ButterKnife.bind(this, v);
        fetchCatalogDetails(mCatalogId);
        if (getActivity() instanceof Activity_AddCatalog) {
            ((Activity_AddCatalog) getActivity()).setNavigateListner(this);
        }
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        return v;
    }


    public void fetchCatalogDetails(String mCatalogId) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader(getActivity());
        HttpManager.getInstance(getActivity()).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(getActivity(), "catalogs_expand_true_id", mCatalogId), null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override

            public void onServerResponse(String response, boolean dataUpdated) {

                if (isAdded() && !isDetached()) {
                    response_catalog = Application_Singleton.gson.fromJson(response, Response_catalog.class);
                    getMyDetails(getActivity(), response_catalog.getId());
                }
            }


            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    public void getMyDetails(Context context, String catalogID) {
        try {
            HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
            HttpManager.getInstance((Activity) context).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(context, "mydetails", catalogID), null, headers, false, new HttpManager.customCallBack() {

                @Override
                public void onCacheResponse(String response) {
                    onServerResponse(response, false);
                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {

                    try {
                        if (isAdded() && !isDetached()) {
                            productMyDetail = Application_Singleton.gson.fromJson(response, ProductMyDetail.class);
                            if (productMyDetail.isIs_owner()) {
                                response_catalog.setIs_owner(true);
                            }

                            if (productMyDetail.isI_am_selling_this()) {
                                response_catalog.setIs_seller(true);
                            }

                            if (productMyDetail.isCurrently_selling()) {
                                response_catalog.setIs_currently_selling(true);
                            }

                            if (productMyDetail.isI_am_selling_sell_full_catalog()) {
                                response_catalog.setI_am_selling_sell_full_catalog(true);
                            }

                            updateCatalogUI();

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
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

    private void setCatalogFlags(Response_catalog response_catalog) {


        if (response_catalog.getView_permission().toLowerCase().equals("public")) {
            response_catalog.setIs_public(true);
        }
    }

    private void updateCatalogUI() {
        if (response_catalog.getEavdatajson() != null) {
            response_catalog.setEavdata(Application_Singleton.gson.fromJson(response_catalog.getEavdatajson(), Eavdata.class));
        }
        if (response_catalog.getImage() != null) {
            if (response_catalog.getImage().getThumbnail_medium() != null) {
                StaticFunctions.loadFresco(getActivity(), response_catalog.getImage().getThumbnail_medium(), catalog_img);
            }
        }
        setCatalogFlags(response_catalog);

        if (response_catalog.getSupplier_details().isI_am_selling_this()) {
            txt_catalog_sub_text.setText(getResources().getString(R.string.already_seller_popup));
        } else {
            txt_catalog_sub_text.setText(getResources().getString(R.string.modal_bottom_subtext));
        }
        txt_category.setText("Category : " + response_catalog.getCategory_name());
        txt_catalog_name.setText(response_catalog.getTitle());
        String price_range = response_catalog.getPrice_range();
        if (price_range.contains("-")) {
            String[] priceRangeMultiple = price_range.split("-");
            txt_price_range.setText("Price : " + "\u20B9" + priceRangeMultiple[0] + " - " + "\u20B9" + priceRangeMultiple[1]);
        } else {
            txt_price_range.setText("Price : " + "\u20B9" + price_range);
        }

        if (response_catalog.getSupplier_details() != null) {
            if (response_catalog.getSupplier_details().isI_am_selling_this()) {
                btn_become_seller.setVisibility(View.GONE);
                if (response_catalog.is_seller() && response_catalog.is_currently_selling()) {
                    // stop selling (disable catalog)
                    btn_enable_disable.setVisibility(View.GONE);
                    btn_see_detail.setVisibility(View.VISIBLE);

                }
                if (response_catalog.is_seller() && !response_catalog.is_currently_selling()) {
                    // resume selling (enable catalog)
                    btn_see_detail.setVisibility(View.GONE);
                    txt_catalog_sub_text.setText(getResources().getString(R.string.start_selling_popup));
                    btn_enable_disable.setVisibility(View.VISIBLE);
                    btn_enable_disable.setText(Fragment_CatalogsGallery.ENABLE_LABEL);
                }

                btn_enable_disable.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (btn_enable_disable.getText().equals(Fragment_CatalogsGallery.ENABLE_LABEL)) {
                            Application_Singleton.trackEvent("Add Catalog", "Click", "Start Selling Again");
                        } else {
                            Application_Singleton.trackEvent("Add Catalog", "Click", "Stop Selling");
                        }
                        Fragment_CatalogsGallery fragment_catalogsGallery = new Fragment_CatalogsGallery();

                        fragment_catalogsGallery.setUpdateListEnableDisable(new Fragment_CatalogsGallery.updateListEnableDisable() {
                            @Override
                            public void successEnableDisable(boolean isEnable) {
                                if (isEnable) {

                                }
                            }
                        });
                        fragment_catalogsGallery.getCatalogDataBeforeEnableDisable(false, getActivity(), response_catalog, btn_enable_disable.getText().toString(), CatalogBottomSheetDialogFragment.class.getSimpleName());
                    }
                });

                btn_see_detail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goToDetail(response_catalog);
                    }
                });
            } else {
                btn_become_seller.setVisibility(View.VISIBLE);

                btn_become_seller.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Application_Singleton.trackEvent("Add Catalog", "Click", "Become Seller");
                        Fragment_CatalogsGallery fragment_catalogsGallery = new Fragment_CatalogsGallery();
                        fragment_catalogsGallery.getBrandPermission(response_catalog, false, getActivity());
                    }
                });
            }
        } else {
            btn_become_seller.setVisibility(View.GONE);
        }


        if (response_catalog.getFull_catalog_orders_only() != null && response_catalog.getFull_catalog_orders_only().equals("true")) {
            txt_single_piece.setText(R.string.only_full_catalog_sale);
        } else {
            txt_single_piece.setText(R.string.single_catalog_sale);
        }

        if (response_catalog.getProducts() != null) {
            number_designs.setText("Designs : " + String.valueOf(response_catalog.getProducts().size()));
        }


        if (response_catalog.getEavdata() != null) {
            if (response_catalog.getEavdata().getStitching_type() != null) {
                txt_stiching_details.setVisibility(View.VISIBLE);
                txt_stiching_details.setText("Stiching Details : " + response_catalog.getEavdata().getStitching_type());
            } else {
                txt_stiching_details.setVisibility(View.GONE);
            }

            if (response_catalog.getEavdata().getNumber_pcs_design_per_set() != null && !response_catalog.getEavdata().getNumber_pcs_design_per_set().equals("0")) {
                txt_kurti_set.setVisibility(View.VISIBLE);
                String s = response_catalog.getEavdata().getNumber_pcs_design_per_set() + " Pcs./Design = " + response_catalog.getProducts().size() * Integer.parseInt(response_catalog.getEavdata().getNumber_pcs_design_per_set()) + " Pcs.";
                txt_kurti_set.setText(s);
            } else {
                txt_kurti_set.setVisibility(View.GONE);
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(StaticFunctions.dpToPx(getActivity(), 5), 0, 0, 0);
            if (response_catalog.getAvailable_sizes() != null && !response_catalog.getAvailable_sizes().isEmpty()) {
                txt_size.setVisibility(View.VISIBLE);
                txt_size.setText("Size : " + response_catalog.getAvailable_sizes());
            } else {
                txt_size.setVisibility(View.GONE);
            }

        }


    }

    public void goToDetail(Response_catalog response_catalog) {
        if (response_catalog != null) {
            Bundle bundle = new Bundle();
            bundle.putString("from", "Add Catalog");
            bundle.putString("product_id", response_catalog.getId());
            new NavigationUtils().navigateDetailPage(getContext(), bundle);
            getActivity().finish();
        }
    }


    @Override
    public void navigateDetail() {
        goToDetail(response_catalog);
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
}

