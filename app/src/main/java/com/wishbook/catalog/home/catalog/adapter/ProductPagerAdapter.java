package com.wishbook.catalog.home.catalog.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.ImageUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.Utils.touchImageView.ExtendedViewPager;
import com.wishbook.catalog.commonmodels.CartProductModel;
import com.wishbook.catalog.commonmodels.CompanyGroupFlag;
import com.wishbook.catalog.commonmodels.ProductMyDetail;
import com.wishbook.catalog.commonmodels.SaveCartData;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.responses.EnumGroupResponse;
import com.wishbook.catalog.frescoZoomable.ZoomableDraweeView;
import com.wishbook.catalog.home.cart.CartHandler;
import com.wishbook.catalog.home.cart.Fragment_MyCart;
import com.wishbook.catalog.home.cart.SelectSizeBottomSheet;
import com.wishbook.catalog.home.catalog.CatalogHolder;
import com.wishbook.catalog.home.catalog.ResellerCatalogShareBottomSheet;
import com.wishbook.catalog.home.catalog.StartStopHandler;
import com.wishbook.catalog.home.catalog.details.Activity_ProductPhotos;
import com.wishbook.catalog.home.catalog.share.ProductSingleShareApi;
import com.wishbook.catalog.home.catalog.social_share.BottomShareDialog;
import com.wishbook.catalog.home.models.ProductObj;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProductPagerAdapter extends PagerAdapter {


    // ### View Butterknife bind

    @BindView(R.id.container_product_sku)
    LinearLayout container_product_sku;

    @BindView(R.id.left_nav)
    ImageButton left;

    @BindView(R.id.right_nav)
    ImageButton right;

    @BindView(R.id.prod_sku)
    AppCompatTextView prod_sku;
    @BindView(R.id.prod_fabric)
    AppCompatTextView prod_fabric;
    @BindView(R.id.prod_work)
    AppCompatTextView prod_work;
    @BindView(R.id.container_product_fabric)
    LinearLayout fabric;
    @BindView(R.id.container_product_work)
    LinearLayout work;


    @BindView(R.id.full_catalog_txt)
    TextView full_catalog_txt;
    @BindView(R.id.linear_full_catalog_price)
    LinearLayout linearLayoutFullCatalog;
    @BindView(R.id.linear_single_catalog_price)
    LinearLayout linearLayoutSingleCatalog;
    @BindView(R.id.linear_received_catalog_price)
    LinearLayout linear_received_catalog_price;
    @BindView(R.id.txt_single_piece_price)
    TextView single_price;
    @BindView(R.id.txt_received_price)
    TextView txt_received_price;
    @BindView(R.id.txt_price)
    TextView txt_price;

    @BindView(R.id.txt_more_photos)
    TextView txt_more_photos;

    @BindView(R.id.linear_available_sizes)
    LinearLayout linear_available_sizes;

    @BindView(R.id.available_sizes_value)
    TextView available_sizes_value;


    @BindView(R.id.descontent)
    RelativeLayout descontent;

    @BindView(R.id.cart_info)
    TextView cart_info;

    @BindView(R.id.recyclerview_thumb_img)
    RecyclerView recyclerview_thumb_img;


    @BindView(R.id.btn_add_to_cart)
    AppCompatButton btn_add_to_cart;

    @BindView(R.id.btn_share_product)
    AppCompatButton btn_share_product;

    @BindView(R.id.btn_start_selling)
    AppCompatButton btn_start_selling;

    @BindView(R.id.btn_stop_selling)
    AppCompatButton btn_stop_selling;

    @BindView(R.id.relative_disable)
    RelativeLayout relative_disable;

    @BindView(R.id.txt_single_piece_clearance_discount)
    TextView txt_single_piece_clearance_discount;

    @BindView(R.id.txt_mwp_price)
    TextView txt_mwp_price;

    @BindView(R.id.txt_price_clearance_discount)
    TextView txt_price_clearance_discount;

    @BindView(R.id.txt_single_piece_mwp_price)
    TextView txt_single_piece_mwp_price;

    @BindView(R.id.txt_full_mwp_price)
    TextView txt_full_mwp_price;

    @BindView(R.id.txt_full_price_clearance_discount)
    TextView txt_full_price_clearance_discount;


    Context mContext;
    LayoutInflater mLayoutInflater;
    private SharedPreferences pref;
    private ProductMyDetail productMyDetail;
    private boolean full_catalog_orders_only = true;
    Toolbar toolbar;
    ImageView cart;
    int productCount, fullCount, selectedProductPostion;
    ExtendedViewPager mViewPager;
    boolean fullcatalog, singlepiece, both , isProductWithSize;
    final SharedPreferences preferences;
    String category_id;
    double full_discount;

    public static String TAG = ProductPagerAdapter.class.getSimpleName();


    public ProductPagerAdapter(Context context, Toolbar toolbar, ImageView cart, ExtendedViewPager mViewPager,
                               boolean full_catalog_orders_only,
                               String category_id,
                               ProductMyDetail productMyDetail, double full_discount) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.full_catalog_orders_only = full_catalog_orders_only;
        this.productMyDetail = productMyDetail;
        this.category_id = category_id;
        this.cart = cart;
        this.toolbar = toolbar;
        this.mViewPager = mViewPager;
        this.full_discount = full_discount;
        preferences = mContext.getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);

        productCount = 0;
        fullCount = 0;

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                selectedProductPostion = i;
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    @Override
    public int getCount() {
        return Application_Singleton.selectedCatalogProducts.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Log.e(TAG, "instantiateItem: " + position);

        // this.selectedProductPostion = selectedProductPostion;

        final View view = mLayoutInflater.inflate(R.layout.pager_item_2, container, false);
        view.setTag("Product" + position);
        ButterKnife.bind(this, view);
        pref = StaticFunctions.getAppSharedPreferences(mContext);


        final ZoomableDraweeView imageView = (ZoomableDraweeView) view.findViewById(R.id.prod_img);
        String image = Application_Singleton.selectedCatalogProducts.get(position).getImage().getThumbnail_medium();

        if (image != null && !image.equals("")) {
            ImageUtils.loadFrescoZoomableProductCarousel(mContext, image, imageView, descontent, toolbar, left, right, pref);
        }


        Application_Singleton singleton = new Application_Singleton();
        singleton.trackScreenView("Product/Detail", mContext);

        if (!Application_Singleton.selectedCatalogProducts.get(position).isEnabled()) {
            relative_disable.setVisibility(View.VISIBLE);
        } else {
            relative_disable.setVisibility(View.GONE);
        }


        initProductLevelStartStopSell(position);
        handleProductInfo(position);
        handleAddToCartButton(position);
        handleShareButton(position);
        setAdditionalImg(position, imageView,descontent);
        sendProductViewToServer(Application_Singleton.selectedCatalogProducts.get(position));


        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tab = mViewPager.getCurrentItem();
                if (tab > 0) {

                    tab--;
                    mViewPager.setCurrentItem(tab);
                } else if (tab == 0) {
                    mViewPager.setCurrentItem(tab);
                }
            }
        });


        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tab = mViewPager.getCurrentItem();
                tab++;

                mViewPager.setCurrentItem(tab);
            }
        });


        // Temporary Function Called
        hideOrderDisableConfig(btn_add_to_cart);

        container.addView(view);
        return view;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }


    private void handleProductInfo(int position) {


        if (full_catalog_orders_only) {
            linearLayoutSingleCatalog.setVisibility(View.GONE);
            full_catalog_txt.setText("Price : ");
        }

        //For Sku
        if (Application_Singleton.selectedCatalogProducts.get(position).getSku() != null && !Application_Singleton.selectedCatalogProducts.get(position).getSku().equals("null") && !Application_Singleton.selectedCatalogProducts.get(position).getSku().equals("")) {
            container_product_sku.setVisibility(View.VISIBLE);
            prod_sku.setText(Application_Singleton.selectedCatalogProducts.get(position).getSku().toUpperCase());
        } else {
            container_product_sku.setVisibility(View.GONE);
        }


        if (Application_Singleton.selectedCatalogProducts.get(position).getFabric() != null && Application_Singleton.selectedCatalogProducts.get(position).getFabric() != "") {
            prod_fabric.setVisibility(View.VISIBLE);
            fabric.setVisibility(View.VISIBLE);
            prod_fabric.setText(Application_Singleton.selectedCatalogProducts.get(position).getFabric());
        } else {
            fabric.setVisibility(View.GONE);
            prod_fabric.setVisibility(View.GONE);
        }
        if (Application_Singleton.selectedCatalogProducts.get(position).getWork() != null && Application_Singleton.selectedCatalogProducts.get(position).getWork() != "") {
            prod_work.setVisibility(View.VISIBLE);
            work.setVisibility(View.VISIBLE);
            prod_work.setText(Application_Singleton.selectedCatalogProducts.get(position).getWork());
        } else {
            prod_work.setText("Not available");
            prod_work.setVisibility(View.GONE);
            work.setVisibility(View.GONE);
        }


        if (Application_Singleton.selectedCatalogProducts.get(position).getAvailable_size_string() != null && !Application_Singleton.selectedCatalogProducts.get(position).getAvailable_size_string().isEmpty()) {
            linear_available_sizes.setVisibility(View.VISIBLE);
            available_sizes_value.setText(Application_Singleton.selectedCatalogProducts.get(position).getAvailable_size_string());
        } else {
            linear_available_sizes.setVisibility(View.GONE);
            available_sizes_value.setText("-");
        }


        if (productMyDetail.isI_am_selling_this()) {
            // For My Catalog
            if (productMyDetail.isI_am_selling_sell_full_catalog()) {
                linearLayoutFullCatalog.setVisibility(View.VISIBLE);
                linearLayoutSingleCatalog.setVisibility(View.GONE);
                if (getProductFullBillingPrice(Application_Singleton.selectedCatalogProducts.get(position)) > 0) {
                    txt_price.setText("\u20B9 " + getProductFullBillingPrice(Application_Singleton.selectedCatalogProducts.get(position)) + "/Pc.");
                } else {
                    txt_price.setText("\u20B9 " + Application_Singleton.selectedCatalogProducts.get(position).getSelling_price() + "/Pc.");
                }

                txt_full_mwp_price.setVisibility(View.GONE);
                txt_full_price_clearance_discount.setVisibility(View.GONE);
            } else {
                linearLayoutSingleCatalog.setVisibility(View.VISIBLE);
                linearLayoutFullCatalog.setVisibility(View.GONE);

                if (getProductSingleBillingPrice(Application_Singleton.selectedCatalogProducts.get(position)) > 0) {
                    single_price.setText("\u20B9 " + getProductSingleBillingPrice(Application_Singleton.selectedCatalogProducts.get(position)) + "/Pc.");
                } else {
                    single_price.setText("\u20B9 " + Application_Singleton.selectedCatalogProducts.get(position).getSingle_piece_price() + "/Pc.");
                }

                txt_single_piece_mwp_price.setVisibility(View.GONE);
                txt_single_piece_clearance_discount.setVisibility(View.GONE);
            }
        } else {
            // For Public Catalog


                Log.i(TAG, "instantiateItem: Group Status admin");
                if (Application_Singleton.selectedshareCatalog != null
                        && Application_Singleton.selectedshareCatalog.isFromPublic()
                        && Application_Singleton.selectedCatalogProducts.get(position).getPublic_price() != null &&
                        !Application_Singleton.selectedCatalogProducts.get(position).getPublic_price().equals("")) {
                    linearLayoutFullCatalog.setVisibility(View.VISIBLE);

                    String temp = Application_Singleton.selectedCatalogProducts.get(position).getPublic_price();

                    try {
                        temp = String.valueOf(temp).split("\\.")[0];
                        txt_price.setText("\u20B9 " + temp + "/Pc.");
                    } catch (Exception e) {
                        e.printStackTrace();
                        txt_price.setText("\u20B9 " + Application_Singleton.selectedCatalogProducts.get(position).getPublic_price() + "/Pc.");
                    }


                    if (Application_Singleton.selectedCatalogProducts.get(position).getSingle_piece_price() != null
                            && !Application_Singleton.selectedCatalogProducts.get(position).getSingle_piece_price().isEmpty()) {

                        temp = Application_Singleton.selectedCatalogProducts.get(position).getSingle_piece_price();
                        try {
                            temp = String.valueOf(temp).split("\\.")[0];
                            single_price.setText("\u20B9 " + temp + "/Pc.");
                        } catch (Exception e) {
                            e.printStackTrace();
                            single_price.setText("\u20B9 " + Application_Singleton.selectedCatalogProducts.get(position).getSingle_piece_price() + "/Pc.");
                        }

                        if (Application_Singleton.selectedshareCatalog.getFull_catalog_orders_only() != null && Application_Singleton.selectedshareCatalog.getFull_catalog_orders_only().equals("false")) {
                            single_price.setText(txt_price.getText().toString());
                            linearLayoutSingleCatalog.setVisibility(View.VISIBLE);
                        } else {
                            linearLayoutSingleCatalog.setVisibility(View.GONE);
                        }

                    } else {
                        if (Application_Singleton.selectedshareCatalog.getFull_catalog_orders_only() != null && Application_Singleton.selectedshareCatalog.getFull_catalog_orders_only().equals("false")) {
                            single_price.setText(txt_price.getText().toString());
                            linearLayoutSingleCatalog.setVisibility(View.VISIBLE);
                        } else {
                            linearLayoutSingleCatalog.setVisibility(View.GONE);
                        }
                    }
                } else if (Application_Singleton.selectedCatalogProducts.get(position).getFinal_price() != null && !Application_Singleton.selectedCatalogProducts.get(selectedProductPostion).getFinal_price().equals("null") && !Application_Singleton.selectedCatalogProducts.get(selectedProductPostion).getFinal_price().equals("")
                        && !Application_Singleton.selectedCatalogProducts.get(selectedProductPostion).getSelling_price().equals("0.00")
                        && !Application_Singleton.selectedCatalogProducts.get(selectedProductPostion).getSelling_price().equals("0.0")) {
                    linear_received_catalog_price.setVisibility(View.VISIBLE);
                    String temp = Application_Singleton.selectedCatalogProducts.get(position).getFinal_price();
                    try {
                        temp = String.valueOf(temp).split("\\.")[0];
                        txt_received_price.setText("\u20B9 " + temp + "/Pc.");
                    } catch (Exception e) {
                        e.printStackTrace();
                        txt_received_price.setText("\u20B9 " + Application_Singleton.selectedCatalogProducts.get(position).getFinal_price() + "/Pc.");
                    }

                    ProductObj productObj = Application_Singleton.selectedCatalogProducts.get(position);
                    if (productObj.getFull_discount() > 0) {
                        txt_mwp_price.setVisibility(View.VISIBLE);
                        txt_mwp_price.setPaintFlags(txt_mwp_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        txt_mwp_price.setText("\u20B9 " + productObj.getMwp_single_price());

                        txt_price_clearance_discount.setVisibility(View.VISIBLE);
                        txt_price_clearance_discount.setText(Math.round(productObj.getFull_discount()) + "% off");
                    } else {
                        txt_mwp_price.setVisibility(View.GONE);
                        txt_price_clearance_discount.setVisibility(View.GONE);
                    }

                    if (Application_Singleton.selectedCatalogProducts.get(position).getSingle_piece_price() != null
                            && !Application_Singleton.selectedCatalogProducts.get(position).getSingle_piece_price().isEmpty()) {
                        temp = Application_Singleton.selectedCatalogProducts.get(position).getSingle_piece_price();
                        try {
                            temp = String.valueOf(temp).split("\\.")[0];
                            single_price.setText("\u20B9 " + temp + "/Pc.");
                        } catch (Exception e) {
                            e.printStackTrace();
                            single_price.setText("\u20B9 " + Application_Singleton.selectedCatalogProducts.get(position).getSingle_piece_price() + "/Pc.");
                        }

                        if (productObj.getSingle_discount() > 0) {
                            txt_single_piece_mwp_price.setVisibility(View.VISIBLE);
                            txt_single_piece_mwp_price.setPaintFlags(txt_single_piece_mwp_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                            txt_single_piece_mwp_price.setText("\u20B9 " + productObj.getMwp_single_price());

                            txt_single_piece_clearance_discount.setVisibility(View.VISIBLE);
                            txt_single_piece_clearance_discount.setText(Math.round(productObj.getSingle_discount()) + "% off");
                        } else {
                            txt_single_piece_mwp_price.setVisibility(View.GONE);
                            txt_single_piece_clearance_discount.setVisibility(View.GONE);
                        }

                    } else {
                        linearLayoutSingleCatalog.setVisibility(View.GONE);
                    }
                } else {
                    linearLayoutFullCatalog.setVisibility(View.GONE);
                }


            if (Application_Singleton.isFromGallery != null && Application_Singleton.isFromGallery.equals(CatalogHolder.MYRECEIVED)) {
                btn_add_to_cart.setVisibility(View.GONE);
                linear_received_catalog_price.setVisibility(View.VISIBLE);
                linearLayoutSingleCatalog.setVisibility(View.GONE);
                linearLayoutFullCatalog.setVisibility(View.GONE);
            }

            try {
                if (txt_price.getText().equals("-")) {
                    linearLayoutFullCatalog.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        /**
         *Hide Full Catalog Price when only one product
         */

        if (Application_Singleton.selectedCatalogProducts.size() == 1
                && Application_Singleton.selectedCatalogProducts.get(position).getProduct_type() != null
                && !Application_Singleton.selectedCatalogProducts.get(position).getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN)) {
            if (full_catalog_orders_only) {
                linear_received_catalog_price.setVisibility(View.VISIBLE);
                linearLayoutSingleCatalog.setVisibility(View.GONE);
            } else {
                linear_received_catalog_price.setVisibility(View.GONE);
                linearLayoutSingleCatalog.setVisibility(View.VISIBLE);
            }

        } else {
            if (Application_Singleton.selectedCatalogProducts.size() > 1
                    && Application_Singleton.selectedCatalogProducts.get(position).getProduct_type() != null && !Application_Singleton.selectedCatalogProducts.get(position).getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN) && !full_catalog_orders_only) {
                linear_received_catalog_price.setVisibility(View.GONE);
            }
        }


        if (pref.getString("hidden", "no").equals("no")) {
            descontent.setVisibility(View.VISIBLE);
            toolbar.setVisibility(View.VISIBLE);
            left.setVisibility(View.VISIBLE);
            right.setVisibility(View.VISIBLE);

        } else {
            descontent.setVisibility(View.INVISIBLE);
            toolbar.setVisibility(View.GONE);
            left.setVisibility(View.GONE);
            right.setVisibility(View.GONE);

        }

    }

    public double getProductFullBillingPrice(ProductObj productObj) {
        if (productMyDetail != null) {
            if (productObj.getProduct_type() != null && productObj.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN)) {
                return productMyDetail.getFull_price();
            } else {
                for (int i = 0; i < productMyDetail.getProducts().size(); i++) {
                    if (productObj.getId().equalsIgnoreCase(productMyDetail.getProducts().get(i).getId())) {
                        return productMyDetail.getProducts().get(i).getFull_price();
                    }
                }
            }
        }
        return 0;
    }


    public double getProductSingleBillingPrice(ProductObj productObj) {
        if (productMyDetail != null) {
            if (productObj.getProduct_type() != null && productObj.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN)) {
                return productMyDetail.getFull_price();
            } else {
                for (int i = 0; i < productMyDetail.getProducts().size(); i++) {
                    if (productObj.getId().equalsIgnoreCase(productMyDetail.getProducts().get(i).getId())) {
                        return productMyDetail.getProducts().get(i).getSingle_piece_price();
                    }
                }
            }
        }
        return 0;
    }

    private void handleAddToCartButton(int position) {
        Log.i(TAG, "handleAddToCartButton: ===>" + position);
        showAlreadyAddText(cart_info, productCount);

        //  ##### Show-hide Add To Cart  Start
        btn_add_to_cart.setVisibility(View.GONE);
        if (productMyDetail != null && productMyDetail.getProducts().size() > 0 && !productMyDetail.getProducts().get(position).isIs_owner() &&
                !full_catalog_orders_only &&
                !productMyDetail.getProducts().get(position).isI_am_selling_this() &&
                Application_Singleton.selectedCatalogProducts.get(position).getSingle_piece_price() != null &&
                !Application_Singleton.selectedCatalogProducts.get(position).getSingle_piece_price().equals("null") &&
                Application_Singleton.selectedCatalogProducts.get(position).isSelling()) {
            btn_add_to_cart.setVisibility(View.VISIBLE);
        }


        final SharedPreferences preferences = mContext.getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
        if (!preferences.getString("cartdata", "").equalsIgnoreCase("")) {
            Type cartType = new TypeToken<ArrayList<SaveCartData>>() {
            }.getType();
            ArrayList<SaveCartData> saveCartData = new Gson().fromJson(preferences.getString("cartdata", ""), cartType);
            if (saveCartData != null && saveCartData.size() > 0) {
                for (int i = 0; i < saveCartData.size(); i++) {
                    if (saveCartData.get(i).getProducts().getId().contains(Application_Singleton.selectedCatalogProducts.get(position).getId())) {
                        btn_add_to_cart.setText("GO TO CART");
                        Log.i(TAG, "handleAddToCartButton: GO TO CART position====>" + position);
                        break;
                    } else {
                        btn_add_to_cart.setText("ADD TO CART");
                    }
                }
            }
        }

        if (UserInfo.getInstance(mContext).getCompanyType().equals("seller")) {
            btn_add_to_cart.setVisibility(View.GONE);
            cart.setVisibility(View.GONE);
            //cart_count.setVisibility(View.GONE);
        }
    }

    private void handleShareButton(int position) {
        Log.i(TAG, "handleShareButton: ===>" + position);

        //  ##### Show-hide Add To Cart  Start
        btn_share_product.setVisibility(View.GONE);
        if (productMyDetail != null && productMyDetail.getProducts().size() > 0 && !productMyDetail.getProducts().get(position).isIs_owner() &&
                !full_catalog_orders_only &&
                !productMyDetail.getProducts().get(position).isI_am_selling_this() &&
                Application_Singleton.selectedCatalogProducts.get(position).getSingle_piece_price() != null &&
                !Application_Singleton.selectedCatalogProducts.get(position).getSingle_piece_price().equals("null") &&
                Application_Singleton.selectedCatalogProducts.get(position).isSelling()) {
            btn_share_product.setVisibility(View.VISIBLE);
        }
        if (UserInfo.getInstance(mContext).getCompanyType().equals("seller")) {
            btn_share_product.setVisibility(View.GONE);
        }
    }

    private void sendProductViewToServer(ProductObj productObj) {
        try {
            if (Application_Singleton.getResponse_catalog() != null && Application_Singleton.getResponse_catalog().getProduct_type() != null
                    && Application_Singleton.getResponse_catalog().getProduct_type().equals(Constants.PRODUCT_TYPE_SCREEN)) {

                return;
            }
            if (productObj != null && productObj.getId() != null) {
                HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) mContext);
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("product", String.valueOf(productObj.getId()));
                params.put("catalog_type", "public");
                HttpManager.getInstance((Activity) mContext).request(HttpManager.METHOD.POST, URLConstants.companyUrl(mContext, "catalog_view_count", ""), params, headers, false, new HttpManager.customCallBack() {
                    @Override
                    public void onCacheResponse(String response) {
                        Log.v("cached response", response);
                        onServerResponse(response, false);
                    }

                    @Override
                    public void onServerResponse(String response, boolean dataUpdated) {
                        Log.v("Done", "CatalogViewCount");
                    }


                    @Override
                    public void onResponseFailed(ErrorString error) {
                        Log.i("TAG", "onResponseFailed: Error");
                    }
                });
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void setAdditionalImg(int position, final ZoomableDraweeView imageView,RelativeLayout relative_layout) {
        recyclerview_thumb_img.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        recyclerview_thumb_img.setNestedScrollingEnabled(false);
        final ProductObj productObj = Application_Singleton.selectedCatalogProducts.get(position);
        if (productObj.getPhotos() != null && productObj.getPhotos().size() > 1) {
            recyclerview_thumb_img.setVisibility(View.VISIBLE);
            final ProductThumbAdapter productThumbAdapter = new ProductThumbAdapter(mContext, productObj.getPhotos());
            productThumbAdapter.setOnThumbSelectorListener(new ProductThumbAdapter.OnThumbSelectorListener() {
                @Override
                public void onSelect(int position) {
                    String image = productObj.getPhotos().get(position).getImage().getThumbnail_medium();
                    if (image != null && !image.equals("")) {
                        ImageUtils.loadFrescoZoomableProductCarousel(mContext, image, imageView, relative_layout, toolbar, left, right, pref);
                    }
                }
            });
            recyclerview_thumb_img.setAdapter(productThumbAdapter);
        } else {
            recyclerview_thumb_img.setVisibility(View.GONE);
        }
    }


    @OnClick(R.id.btn_add_to_cart)
    public void add_to_cart(final View v) {
        if (((AppCompatButton) v).getText().toString().contains("GO")) {
            SharedPreferences preferences = mContext.getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
            int cart_count = preferences.getInt("cartcount", 0);
            Application_Singleton.CONTAINER_TITLE = "My Cart (" + cart_count + " items)";
            Application_Singleton.CONTAINERFRAG = new Fragment_MyCart();
            StaticFunctions.switchActivity((Activity) mContext, OpenContainer.class);
        } else {
            if (UserInfo.getInstance(mContext).isGuest()) {
                StaticFunctions.ShowRegisterDialog(mContext, "Product Add To Cart");
                return;
            }

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {

            } else {
                ((AppCompatButton) v).animate().rotationX(180).setDuration(600).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ((AppCompatButton) v).setText("GO TO CART");
                        ((AppCompatButton) v).setRotationX(0);
                        Animation shake = AnimationUtils.loadAnimation(mContext, R.anim.swing);
                        cart.startAnimation(shake);

                    }
                });
            }
            final CartHandler cartHandler = new CartHandler(((AppCompatActivity) mContext));
            cartHandler.new GetSize(mContext, Application_Singleton.getResponse_catalog().getCategory(), new CartHandler.SizeGetCallbackListener() {
                @Override
                public void onSuccess(boolean isSizeMandatory) {
                    try {
                        if (isSizeMandatory && Application_Singleton.selectedCatalogProducts.get(selectedProductPostion).getAvailable_size_string() == null) {
                            new MaterialDialog.Builder(Application_Singleton.getCurrentActivity())
                                    .content("Product out of stock")
                                    .positiveText("OK")
                                    .cancelable(false)
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(MaterialDialog dialog, DialogAction which) {
                                            dialog.dismiss();
                                            ((AppCompatButton) v).clearAnimation();
                                            ((AppCompatButton) v).setRotationX(0);
                                            ((AppCompatButton) v).setText("ADD TO CART");
                                        }
                                    })
                                    .show();
                            ((AppCompatButton) v).clearAnimation();
                            ((AppCompatButton) v).setRotationX(0);
                            ((AppCompatButton) v).setText("ADD TO CART");
                            return;
                        }
                        Log.i(TAG, "add_to_cart: Position" + selectedProductPostion);
                        Application_Singleton.trackEvent("Add to cart", "Click", "From Product");
                        String note = "Nan";
                        if (Application_Singleton.getResponse_catalog() != null && Application_Singleton.selectedCatalogProducts.get(selectedProductPostion).getAvailable_size_string() != null) {
                            if (Application_Singleton.selectedCatalogProducts.get(selectedProductPostion).getAvailable_size_string() != null && !Application_Singleton.selectedCatalogProducts.get(selectedProductPostion).getAvailable_size_string().isEmpty() && Application_Singleton.selectedCatalogProducts.get(selectedProductPostion).getAvailable_size_string().split(",").length > 1) {
                                SelectSizeBottomSheet bottomSheetDialog;
                                if (Application_Singleton.selectedCatalogProducts != null && Application_Singleton.selectedCatalogProducts.get(selectedProductPostion).getSingle_piece_price() != null && !Application_Singleton.selectedCatalogProducts.get(selectedProductPostion).getSingle_piece_price().equals("null")) {
                                    bottomSheetDialog = SelectSizeBottomSheet.getInstance(Application_Singleton.selectedCatalogProducts.get(selectedProductPostion).getAvailable_size_string(), Application_Singleton.getResponse_catalog().getSingle_piece_price_range(), Application_Singleton.getResponse_catalog().getTotal_products(), "product");
                                } else {
                                    bottomSheetDialog = SelectSizeBottomSheet.getInstance(Application_Singleton.selectedCatalogProducts.get(selectedProductPostion).getAvailable_size_string(), Application_Singleton.getResponse_catalog().getPrice_range(), Application_Singleton.getResponse_catalog().getTotal_products(), "product");
                                }
                                bottomSheetDialog.show(((AppCompatActivity) mContext).getSupportFragmentManager(), "Custom Bottom Sheet");
                                bottomSheetDialog.setDismissListener(new SelectSizeBottomSheet.DismissListener() {
                                    @Override
                                    public void onDismiss(HashMap<String, Integer> value) {
                                        if (value.size() > 0) {
                                            if (!preferences.getString("cartId", "").equalsIgnoreCase("")) {
                                                handleAddToCartWithSize(value, selectedProductPostion, v);
                                            } else {
                                                getCartId(value, selectedProductPostion, v);
                                            }
                                        } else {
                                            ((AppCompatButton) v).animate().rotationX(0).setDuration(0).setListener(new AnimatorListenerAdapter() {
                                                @Override
                                                public void onAnimationEnd(Animator animation) {
                                                    ((AppCompatButton) v).setText("ADD TO CART");
                                                    ((AppCompatButton) v).setRotationX(0);
                                                    Animation shake = AnimationUtils.loadAnimation(mContext, R.anim.swing);
                                                    cart.startAnimation(shake);
                                                }
                                            });

                                        }
                                    }
                                });


                                return;
                            } else {
                                note = "Size : " + Application_Singleton.selectedCatalogProducts.get(selectedProductPostion).getAvailable_size_string();
                            }
                        }

                        ++productCount;
                        singlepiece = true;
                        showAlreadyAddText(cart_info, productCount);


                        int qtys[] = new int[1];
                        qtys[0] = 1;

                        cartHandler.setAddToCartCallbackListener(new CartHandler.AddToCartCallbackListener() {
                            @Override
                            public void onSuccess(CartProductModel response) {
                                ((AppCompatButton) v).setText("GO TO CART");
                                Toast.makeText(mContext, "Product Added to cart", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure() {
                                ((AppCompatButton) v).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        ((AppCompatButton) v).setText("ADD TO CART");
                                    }
                                }, 500);
                            }
                        });
                        cartHandler.addProductToCart(Application_Singleton.getResponse_catalog().getId(),
                                Application_Singleton.selectedCatalogProducts.get(selectedProductPostion).getId(),
                                Application_Singleton.selectedCatalogProducts.get(selectedProductPostion),
                                qtys, false,
                                "", null,
                                "product", (AppCompatActivity) mContext,
                                note, Application_Singleton.selectedCatalogProducts.size());


                        for (int i = 0; i < mViewPager.getAdapter().getCount(); i++) {
                            View view1 = mViewPager.findViewWithTag("Product" + i);
                            if (view1 != null) {
                                TextView textView = (TextView) view1.findViewById(R.id.cart_info);
                                if (textView != null) {
                                    showAlreadyAddText(textView, productCount);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure() {
                    ((AppCompatButton) v).clearAnimation();
                    ((AppCompatButton) v).setRotationX(0);
                    ((AppCompatButton) v).setText("ADD TO CART");
                }
            });


        }

    }


    @OnClick(R.id.txt_more_photos)
    public void navigateToPhotos(final View view) {
        Intent photos_intent = new Intent(mContext, Activity_ProductPhotos.class);
        photos_intent.putExtra("photos", Application_Singleton.selectedCatalogProducts.get(selectedProductPostion).getPhotos());
        mContext.startActivity(photos_intent);
    }

    @OnClick(R.id.btn_share_product)
    public void shareSingleProduct(final View view) {
        CompanyGroupFlag companyGroupFlag = Application_Singleton.gson.fromJson(UserInfo.getInstance(mContext).getCompanyGroupFlag(), CompanyGroupFlag.class);
        if (companyGroupFlag != null && (companyGroupFlag.getOnline_retailer_reseller())) {
            // Open Reseller Share Bottom Sheet
            String from ="Product Pager";
            ResellerCatalogShareBottomSheet resellerCatalogShareBottomSheet = ResellerCatalogShareBottomSheet.newInstance(Application_Singleton.selectedCatalogProducts.get(selectedProductPostion).getId(),from);
            resellerCatalogShareBottomSheet.show(((AppCompatActivity) mContext).getSupportFragmentManager(), "Custom Bottom Sheet");
        } else {
            boolean is_whatsapp_business_available = StaticFunctions.appInstalledOrNot("com.whatsapp.w4b",mContext);
            Bundle bundle = new Bundle();
            bundle.putBoolean("whatsapp", true);
            bundle.putBoolean("whatsappb", is_whatsapp_business_available);
            bundle.putBoolean("fb", true);
            bundle.putBoolean("gallery", true);
            bundle.putBoolean("other", true);
            BottomShareDialog bottomSheetDialog = BottomShareDialog.getInstance(bundle);
            bottomSheetDialog.show(((AppCompatActivity) mContext).getSupportFragmentManager(), "Custom Bottom Sheet");
            bottomSheetDialog.setDismissListener(new BottomShareDialog.DismissListener() {
                @Override
                public void onDismiss(StaticFunctions.SHARETYPE type) {
                    if(type!=null) {
                        String product_id = Application_Singleton.selectedCatalogProducts.get(selectedProductPostion).getId();
                        new ProductSingleShareApi(mContext, product_id, type, null);
                    }
                }
            });
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int selectedProductPostion, Object object) {
        container.removeView((FrameLayout) object);
    }

    public void handleAddToCartWithSize(HashMap<String, Integer> sizeQtyMap, int selectedProductPostion, final View view) {
        List<String> l = new ArrayList<>(sizeQtyMap.keySet());

        CartHandler cartHandler = new CartHandler(((AppCompatActivity) mContext));
        for (int j = 0; j < sizeQtyMap.size(); j++) {
            int qtys[] = new int[1];
            qtys[0] = sizeQtyMap.get(l.get(j));
            cartHandler.setAddToCartCallbackListener(new CartHandler.AddToCartCallbackListener() {
                @Override
                public void onSuccess(CartProductModel response) {
                    ((AppCompatButton) view).setText("GO TO CART");
                    Toast.makeText(mContext, "Product Added to cart", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure() {
                    ((AppCompatButton) view).setText("ADD TO CART");
                }
            });
            cartHandler.addProductToCart(Application_Singleton.getResponse_catalog().getId(), Application_Singleton.selectedCatalogProducts.get(selectedProductPostion).getId(), Application_Singleton.selectedCatalogProducts.get(selectedProductPostion), qtys, false, "", null, "productkurti", (AppCompatActivity) mContext, "Size : " + l.get(j), Application_Singleton.selectedCatalogProducts.size());
        }

        ++productCount;
        singlepiece = true;
        showAlreadyAddText(cart_info, productCount);
    }

    public void getCartId(final HashMap<String, Integer> value, final int selectedProductPostion, final View view) {
        try {
            final HttpManager.METHOD method = HttpManager.METHOD.POSTJSONOBJECTWITHPROGRESS;
            String url = URLConstants.companyUrl(mContext, "cart", "");
            List<CartProductModel.Items> items = new ArrayList<>();
            CartProductModel cartProductModel = new CartProductModel(items);
            cartProductModel.setOrder_number("");
            HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) mContext);
            HttpManager.getInstance((Activity) mContext).requestwithObject(method, url, new Gson().fromJson(new Gson().toJson(cartProductModel), JsonObject.class), headers, false, new HttpManager.customCallBack() {

                @Override
                public void onCacheResponse(String response) {

                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    Log.d("Add-cart RESPONSE", response);
                    CartProductModel response_model = new Gson().fromJson(response, CartProductModel.class);
                    CartProductModel CartProductModel = Application_Singleton.gson.fromJson(response, CartProductModel.class);
                    SharedPreferences preferences = mContext.getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit().putString("cartId", "" + CartProductModel.getId());
                    editor.commit();
                    handleAddToCartWithSize(value, selectedProductPostion, view);

                }

                @Override
                public void onResponseFailed(ErrorString error) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void showAlreadyAddText(TextView textView, int productCount) {
        if (fullcatalog) {
            textView.setVisibility(View.VISIBLE);
            textView.setText("" + (fullCount) + " complete set of this catalog is added to your cart");
        }

        if (singlepiece) {
            textView.setVisibility(View.VISIBLE);
            if (productCount == 1) {
                textView.setText("" + (productCount) + " individual design of this catalog is added to your cart");
            } else {
                textView.setText("" + (productCount) + " individual designs of this catalog are added to your cart");
            }
        }

        if (fullcatalog && singlepiece) {
            if (productCount == 1) {
                textView.setText("" + (fullCount) + " complete set +" + (productCount) + " individual design of this catalog are added to your cart");
            } else {
                textView.setText("" + (fullCount) + " complete set +" + (productCount) + " individual designs of this catalog are added to your cart");
            }
        }

        if (fullcatalog && both) {
            textView.setVisibility(View.VISIBLE);
            if (productCount == 1) {
                textView.setText("" + (fullCount) + " complete set +" + (productCount) + " individual design of this catalog are added to your cart");
            } else {
                textView.setText("" + (fullCount) + " complete set +" + (productCount) + " individual designs of this catalog are added to your cart");
            }
        }
        if (!singlepiece && !both && !fullcatalog) {
            textView.setVisibility(View.GONE);
            textView.setText("" + productCount + " individual design of this catalog is added to your cart");
        }
    }

    public void initProductLevelStartStopSell(final int position) {
        if (productMyDetail != null && productMyDetail.getProducts().size() > 0) {
            if (!productMyDetail.getProducts().get(position).isI_am_selling_sell_full_catalog() && productMyDetail.getProducts().get(position).isI_am_selling_this()) {
                if (productMyDetail.getProducts().get(position).isCurrently_selling()) {
                    btn_start_selling.setVisibility(View.GONE);
                    btn_stop_selling.setVisibility(View.VISIBLE);
                } else {
                    btn_start_selling.setVisibility(View.VISIBLE);
                    btn_stop_selling.setVisibility(View.GONE);
                }
            } else {
                // Don't show start/stop selling button
                btn_start_selling.setVisibility(View.GONE);
                btn_stop_selling.setVisibility(View.GONE);
            }


            btn_start_selling.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getSizeEav(mContext, productMyDetail.getProducts().get(position), true);
                }
            });

            btn_stop_selling.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getSizeEav(mContext, productMyDetail.getProducts().get(position), false);
                }
            });
        }
    }

    public void updateStartStopButtton(ProductMyDetail products, AppCompatButton btn_start_selling, AppCompatButton btn_stop_selling) {
        Log.i(TAG, "initProductLevelStartStopSell:===> " + products.toString());
        if (!products.isI_am_selling_sell_full_catalog() && products.isI_am_selling_this()) {
            if (products.isCurrently_selling()) {
                btn_start_selling.setVisibility(View.GONE);
                btn_stop_selling.setVisibility(View.VISIBLE);
            } else {
                btn_start_selling.setVisibility(View.VISIBLE);
                btn_stop_selling.setVisibility(View.GONE);
            }
        } else {
            // Don't show start/stop selling button
            btn_start_selling.setVisibility(View.GONE);
            btn_stop_selling.setVisibility(View.GONE);
        }
    }


    private void getSizeEav(final Context context, final ProductMyDetail productObj, final boolean isStartSell) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) mContext);
        String url = URLConstants.companyUrl(mContext, "enumvalues", "") + "?category=" + category_id + "&attribute_slug=" + "size";
        final MaterialDialog progressdialog = StaticFunctions.showProgress(mContext);
        progressdialog.show();
        HttpManager.getInstance((Activity) mContext).request(HttpManager.METHOD.GETWITHPROGRESS, url, null, headers, true, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {
                onServerResponse(response, false);
            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                if (!((Activity) context).isFinishing()) {
                    if (progressdialog != null) {
                        progressdialog.dismiss();
                    }
                    ArrayList<String> system_sizes = new ArrayList<>();
                    EnumGroupResponse[] enumGroupResponses = Application_Singleton.gson.fromJson(response, EnumGroupResponse[].class);
                    if (enumGroupResponses != null) {
                        if (enumGroupResponses.length > 0) {
                            ArrayList<EnumGroupResponse> enumGroupResponses1 = new ArrayList<EnumGroupResponse>(Arrays.asList(enumGroupResponses));
                            for (int i = 0; i < enumGroupResponses1.size(); i++) {
                                system_sizes.add(enumGroupResponses1.get(i).getValue());
                            }
                            isProductWithSize = true;
                        }
                    } else {
                        isProductWithSize = false;
                    }
                    startStopOperation(context, productObj, isStartSell, isProductWithSize, system_sizes);
                }
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                if (progressdialog != null) {
                    progressdialog.dismiss();
                }
            }
        });
    }


    private void startStopOperation(final Context context,
                                    final ProductMyDetail productMyDetail,
                                    final boolean isStartSell,
                                    boolean isProductWithSize, ArrayList<String> system_sizes) {

        if (isProductWithSize) {
            // To Do Size code
            final ArrayList<String> selected_size = new ArrayList<>();
            final ArrayList<Integer> integers = new ArrayList<>();
            if (productMyDetail.getAvailable_sizes() != null) {
                String s[] = productMyDetail.getAvailable_sizes().split(",");
                ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(s));
                for (int i = 0; i < system_sizes.size(); i++) {
                    if (arrayList.contains(system_sizes.get(i))) {
                        integers.add(i);
                    }
                }
            }

            Integer[] integers1 = integers.toArray(new Integer[integers.size()]);


            // demo data
            CharSequence[] chars = system_sizes.toArray(new CharSequence[system_sizes.size()]);
            MaterialDialog materialDialog = new MaterialDialog.Builder(mContext).title("Select Sizes")
                    .items(chars)
                    .content(R.string.start_stop_dialog_with_size_note)
                    .itemsCallbackMultiChoice(integers1, new MaterialDialog.ListCallbackMultiChoice() {
                        @Override
                        public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {


                            for (int i = 0; i < text.length; i++) {
                                selected_size.add(text[i].toString());
                            }


                            StartStopHandler startStopHandler = new StartStopHandler((Activity) mContext);
                            startStopHandler.startStopHandler(StartStopHandler.STARTSTOP.SINGLE_WITH_SIZE, null, productMyDetail.getId(), selected_size, isStartSell);
                            startStopHandler.setStartStopDoneListener(new StartStopHandler.StartStopDoneListener() {
                                @Override
                                public void onSuccessStart() {
                                    Toast.makeText(context, "Product Successfully started", Toast.LENGTH_SHORT).show();
                                    getMyDetails(context, productMyDetail.getId());
                                }

                                @Override
                                public void onSuccessStop() {
                                    Toast.makeText(context, "Product Successfully stopped", Toast.LENGTH_SHORT).show();
                                    getMyDetails(context, productMyDetail.getId());
                                }

                                @Override
                                public void onError() {

                                }
                            });
                            return true;
                        }
                    })
                    .positiveText("Choose")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {


                        }
                    })
                    .negativeText("Cancel")
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                        }
                    }).cancelable(false).show();
        } else {
            StartStopHandler startStopHandler = new StartStopHandler((Activity) mContext);
            Log.i(TAG, "startStopOperation: =====>" + isStartSell);
            startStopHandler.startStopHandler(StartStopHandler.STARTSTOP.SINGLE_WITHOUT_SIZE, null, productMyDetail.getId(), null, isStartSell);
            startStopHandler.setStartStopDoneListener(new StartStopHandler.StartStopDoneListener() {
                @Override
                public void onSuccessStart() {
                    Toast.makeText(context, "Product Successfully started", Toast.LENGTH_SHORT).show();
                    getMyDetails(context, productMyDetail.getId());
                }

                @Override
                public void onSuccessStop() {
                    Toast.makeText(context, "Product Successfully stopped", Toast.LENGTH_SHORT).show();
                    getMyDetails(context, productMyDetail.getId());
                }

                @Override
                public void onError() {

                }
            });
        }

    }


    public void getMyDetails(final Context context, final String productId) {
        try {
            HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
            HttpManager.getInstance((Activity) context).request(HttpManager.METHOD.GETWITHPROGRESS, URLConstants.companyUrl(context, "mydetails", productId), null, headers, false, new HttpManager.customCallBack() {

                @Override
                public void onCacheResponse(String response) {
                    onServerResponse(response, false);
                }

                @Override
                public void onServerResponse(String response, boolean dataUpdated) {
                    try {
                        if (!((Activity) context).isFinishing()) {
                            ProductMyDetail temp = Application_Singleton.gson.fromJson(response, ProductMyDetail.class);
                            temp.setId(productId);
                            ArrayList<ProductMyDetail> tempArray = productMyDetail.getProducts();
                            tempArray.set(selectedProductPostion, temp);
                            productMyDetail.setProducts(tempArray);
                            View view1 = mViewPager.findViewWithTag("Product" + selectedProductPostion);
                            updateStartStopButtton(productMyDetail.getProducts().get(selectedProductPostion), (AppCompatButton) view1.findViewById(R.id.btn_start_selling), (AppCompatButton) view1.findViewById(R.id.btn_stop_selling));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onResponseFailed(ErrorString error) {
                    // hideProgress();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Temporary function for Order Disable Config
     */
    public void hideOrderDisableConfig(AppCompatButton btn_add_to_cart) {
        // add catalog button hide from catalog level
        if (StaticFunctions.checkOrderDisableConfig(mContext)) {
            btn_add_to_cart.setVisibility(View.GONE);
        }
    }
}
