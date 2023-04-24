package com.wishbook.catalog.commonadapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.responses.Response_catalog;
import com.wishbook.catalog.commonmodels.responses.Response_catalogMini;
import com.wishbook.catalog.home.catalog.Fragment_Catalogs;
import com.wishbook.catalog.home.catalog.details.Fragment_CatalogsGallery;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class CatalogsAdapter extends RecyclerView.Adapter<CatalogsAdapter.CustomViewHolder> {
    private ArrayList<Response_catalogMini> feedItemlist;
    private AppCompatActivity mContext;
    UserInfo userInfo;
    Fragment fragment;
    boolean isMyCatalog;
    private int layoutManager;
    UpdateListPageListener updateListPageListener;

    public CatalogsAdapter(AppCompatActivity activity, ArrayList<Response_catalogMini> allCatalogs, Fragment fragment_catalogs, boolean isMyCatalog, int layoutManager) {
        this.feedItemlist = allCatalogs;
        this.mContext = activity;
        userInfo = new UserInfo(mContext);
        this.fragment = fragment_catalogs;
        this.isMyCatalog = isMyCatalog;
        this.layoutManager = layoutManager;
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder {

        private final SimpleDraweeView cat_img;
        private final TextView cattitle;
        private final SimpleDraweeView brand_img;
        private final TextView disable_text;
        private final TextView disable_text1;
        private final TextView price_range;
        private final TextView catalog_type;
        private final RelativeLayout mainContainer;
        private LinearLayout linear_price_range;
        private TextView txt_pre_order, txt_ready_to_dispatch, txt_single_pc_available, txt_full_price_label;


        private LinearLayout linear_enable_disable, linear_single_price_range;
        private TextView btn_enable_disable, single_price_range;
        private RelativeLayout relative_bottom, relative_top;
        ImageButton img_wishlist;

        public CustomViewHolder(View view) {
            super(view);
            brand_img = (SimpleDraweeView) view.findViewById(R.id.brand_img);
            cattitle = (TextView) view.findViewById(R.id.cattitle);
            cat_img = (SimpleDraweeView) view.findViewById(R.id.cat_img);
            disable_text = (TextView) view.findViewById(R.id.disable_text);
            disable_text1 = (TextView) view.findViewById(R.id.disable_text1);
            price_range = (TextView) view.findViewById(R.id.price_range);
            catalog_type = (TextView) view.findViewById(R.id.catalog_type);
            mainContainer = (RelativeLayout) view.findViewById(R.id.MainContainer);
            txt_pre_order = view.findViewById(R.id.txt_pre_order);
            txt_ready_to_dispatch = view.findViewById(R.id.txt_ready_to_dispatch);

            linear_price_range = view.findViewById(R.id.linear_price_range);


            btn_enable_disable = view.findViewById(R.id.btn_enable_disable);
            linear_enable_disable = view.findViewById(R.id.linear_enable_disable);
            relative_bottom = view.findViewById(R.id.relative_bottom);
            relative_top = view.findViewById(R.id.relative_top);
            txt_single_pc_available = view.findViewById(R.id.txt_single_pc_available);
            img_wishlist = view.findViewById(R.id.img_wishlist);
            linear_single_price_range = view.findViewById(R.id.linear_single_price_range);
            single_price_range = view.findViewById(R.id.single_price_range);
            txt_full_price_label = view.findViewById(R.id.txt_full_price_label);

        }
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (layoutManager == 1) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.browse_catalog_item_grid, viewGroup, false);
            return new CustomViewHolder(view);
        } else if (layoutManager == 0) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.browse_catalog_item_version2, viewGroup, false);
            return new CustomViewHolder(view);
        } else {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.browse_catalog_item_version2, viewGroup, false);
            return new CustomViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder customViewHolder, int i) {
        String image = null;
        final Response_catalogMini response_catalogMini = feedItemlist.get(i);


        if (checkPriceRange(response_catalogMini)) {
            if (response_catalogMini.isBuyer_disabled()) {
                customViewHolder.relative_top.setAlpha(0.5f);
                customViewHolder.disable_text.setVisibility(View.VISIBLE);
                customViewHolder.disable_text.setText(mContext.getResources().getString(R.string.buyer_disable));
                customViewHolder.relative_bottom.setAlpha(1f);
            } else {
                customViewHolder.relative_top.setAlpha(1);
                customViewHolder.disable_text1.setVisibility(View.INVISIBLE);
                customViewHolder.disable_text.setVisibility(View.INVISIBLE);
                customViewHolder.relative_bottom.setAlpha(1f);
            }
        }


        //price_range
        String price_range = response_catalogMini.getPrice_range();
        if (checkPriceRange(response_catalogMini)) {
            if (price_range.contains("-")) {
                String[] priceRangeMultiple = price_range.split("-");
                customViewHolder.price_range.setText(" \u20B9" + priceRangeMultiple[0] + " - " + "\u20B9" + priceRangeMultiple[1] + "/Pc."  /*" , " + response_catalogMini.getTotal_products() + " Designs"*/);
            } else {
                String priceRangeSingle = price_range;
                customViewHolder.price_range.setText("\u20B9" + priceRangeSingle + "/Pc."/*+ ", " + response_catalogMini.getTotal_products() + " Designs"*/);
            }
            customViewHolder.linear_price_range.setVisibility(View.VISIBLE);
        } else {
            customViewHolder.linear_price_range.setVisibility(View.GONE);
        }


        if (response_catalogMini.getBrand() != null) {
            image = response_catalogMini.getBrand_image();
        }
        if (image != null && !image.equals("")) {
            customViewHolder.brand_img.setVisibility(View.VISIBLE);
            StaticFunctions.loadFresco(mContext, image, customViewHolder.brand_img);
        } else {
            customViewHolder.brand_img.setVisibility(View.GONE);
        }
        //hyperlinking
        customViewHolder.brand_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticFunctions.hyperLinking1("brand", response_catalogMini.getBrand(), mContext, response_catalogMini.getBrand_name());
            }
        });


        String catimage = response_catalogMini.getImage().getThumbnail_medium();
        if (catimage != null && !catimage.equals("")) {
            StaticFunctions.loadFresco(mContext, catimage, customViewHolder.cat_img);
        }

        if (response_catalogMini.isReady_to_ship()) {
            customViewHolder.txt_ready_to_dispatch.setVisibility(View.GONE);
            customViewHolder.txt_pre_order.setVisibility(View.GONE);
            if (response_catalogMini.getFull_catalog_orders_only() != null && response_catalogMini.getFull_catalog_orders_only().equals("false")) {
                customViewHolder.txt_single_pc_available.setVisibility(View.VISIBLE);
            } else {
                customViewHolder.txt_single_pc_available.setVisibility(View.GONE);
            }
        } else {
            customViewHolder.txt_ready_to_dispatch.setVisibility(View.GONE);
            customViewHolder.txt_pre_order.setVisibility(View.VISIBLE);
            customViewHolder.txt_single_pc_available.setVisibility(View.GONE);
        }

        if (response_catalogMini.getProduct_type() != null && response_catalogMini.getProduct_type().equals(Constants.PRODUCT_TYPE_SCREEN)) {
            customViewHolder.txt_ready_to_dispatch.setVisibility(View.GONE);
            customViewHolder.txt_pre_order.setVisibility(View.GONE);
            customViewHolder.txt_single_pc_available.setVisibility(View.GONE);
        }
        customViewHolder.mainContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("from", "My Catalog List");
                bundle.putString("product_id", response_catalogMini.getId());
                Fragment_CatalogsGallery gallery = new Fragment_CatalogsGallery();
                gallery.setArguments(bundle);
                Application_Singleton.CONTAINER_TITLE = "";
                Application_Singleton.CONTAINERFRAG = gallery;
                Intent intent = new Intent(mContext, OpenContainer.class);
                intent.putExtra("toolbarCategory", OpenContainer.BROWSECATALOG);
                mContext.startActivity(intent);


            }
        });

        customViewHolder.cattitle.setText(response_catalogMini.getTitle());




        /**
         * WB-3490 - In My Catalog Show Start selling/Stop Selling btn and hide share
         */
        if (isMyCatalog) {
            if(isSellerApprove()) {
                customViewHolder.img_wishlist.setVisibility(View.GONE);
                customViewHolder.linear_enable_disable.setVisibility(View.VISIBLE);
                if (response_catalogMini.isSupplier_disabled() || response_catalogMini.isBuyer_disabled()) {
                    //  (enable catalog)
                    customViewHolder.btn_enable_disable.setBackground(mContext.getResources().getDrawable(R.drawable.btn_purchase_blue));
                    customViewHolder.btn_enable_disable.setVisibility(View.VISIBLE);
                    customViewHolder.btn_enable_disable.setText("Start Selling");
                } else {
                    // stop selling (disable catalog)
                    customViewHolder.btn_enable_disable.setBackground(mContext.getResources().getDrawable(R.drawable.btn_redfill));
                    customViewHolder.btn_enable_disable.setVisibility(View.VISIBLE);
                    customViewHolder.btn_enable_disable.setText("Stop Selling");
                }


                customViewHolder.btn_enable_disable.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Fragment_CatalogsGallery fragment_catalogsGallery = new Fragment_CatalogsGallery();
                        final Response_catalog response_catalog = new Response_catalog();
                        response_catalog.setId(response_catalogMini.getId());
                        String label = "";
                        if (customViewHolder.btn_enable_disable.getText().toString().equalsIgnoreCase("Start Selling")) {
                            label = Fragment_CatalogsGallery.ENABLE_LABEL;
                        } else {
                            label = Fragment_CatalogsGallery.DISABLE_LABEL;
                        }
                        fragment_catalogsGallery.setUpdateListEnableDisable(new Fragment_CatalogsGallery.updateListEnableDisable() {
                            @Override
                            public void successEnableDisable(boolean isEnable) {
                              /*  if (isEnable) {
                                    // Enable Successfully
                                    response_catalogMini.setBuyer_disabled(false);
                                    response_catalogMini.setSupplier_disabled(false);
                                    customViewHolder.btn_enable_disable.setText("Stop Selling");
                                    notifyItemChanged(customViewHolder.getAdapterPosition());


                                } else {
                                    // Disable Successfully
                                    response_catalogMini.setBuyer_disabled(true);
                                    customViewHolder.btn_enable_disable.setText("Start Selling");
                                    notifyItemChanged(customViewHolder.getAdapterPosition());
                                }*/

                                if(updateListPageListener!=null) {
                                    updateListPageListener.updateList(customViewHolder.getAdapterPosition());
                                }
                            }
                        });
                        fragment_catalogsGallery.getCatalogDataBeforeEnableDisable(false, mContext, response_catalog, label, Fragment_Catalogs.class.getSimpleName());

                    }
                });

                // Temporary Function call
                hideOrderDisableConfig(customViewHolder.linear_enable_disable);
            } else {
                customViewHolder.img_wishlist.setVisibility(View.GONE);
                customViewHolder.linear_enable_disable.setVisibility(View.GONE);
            }
        } else {
            customViewHolder.linear_enable_disable.setVisibility(View.GONE);
            customViewHolder.img_wishlist.setVisibility(View.VISIBLE);
            if (StaticFunctions.checkProductInWishList(mContext, response_catalogMini.getId())) {
                customViewHolder.img_wishlist.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_bookmart_white_24dp));
                customViewHolder.img_wishlist.setSelected(true);
            } else {
                customViewHolder.img_wishlist.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_bookmark_border_white_24dp));
                customViewHolder.img_wishlist.setSelected(false);
            }
        }

        customViewHolder.img_wishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (customViewHolder.img_wishlist.isSelected()) {
                    // Remove from WishList
                    removeWishlist(mContext, response_catalogMini.getId(), customViewHolder);
                } else {
                    // Add in WishList
                    saveWishlist(mContext, response_catalogMini.getId(), customViewHolder);
                }
            }
        });


        /***
         * Changes done Feb-8 2019 show single pc price and NA for whose catalog has 1 products
         */

        if (Integer.parseInt(response_catalogMini.getTotal_products()) > 1 && !response_catalogMini.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN)) {
            if (response_catalogMini.getFull_catalog_orders_only().equals("false") && response_catalogMini.getSingle_piece_price_range() != null) {
                customViewHolder.linear_single_price_range.setVisibility(View.VISIBLE);
                String single_range = response_catalogMini.getSingle_piece_price_range();
                if (single_range.contains("-")) {
                    String[] priceRangeMultiple = single_range.split("-");
                    customViewHolder.single_price_range.setText(" \u20B9" + priceRangeMultiple[0] + " - " + "\u20B9" + priceRangeMultiple[1] + "/Pc.");
                } else {
                    String priceRangeSingle = single_range;
                    customViewHolder.single_price_range.setText("\u20B9" + priceRangeSingle + "/Pc.");
                }
            } else {
                customViewHolder.linear_single_price_range.setVisibility(View.GONE);
            }
        } else {
            customViewHolder.linear_single_price_range.setVisibility(View.GONE);
        }

        if (response_catalogMini.getTotal_products() != null && Integer.parseInt(response_catalogMini.getTotal_products()) == 1 && response_catalogMini.getProduct_type() != null && !response_catalogMini.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN)) {
            customViewHolder.txt_full_price_label.setText("Price : ");
        } else {
            customViewHolder.txt_full_price_label.setText("Full : ");
        }

        if(isMyCatalog) {
            customViewHolder.linear_price_range.setVisibility(View.GONE);
            customViewHolder.linear_single_price_range.setVisibility(View.GONE);
            customViewHolder.cattitle.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
            customViewHolder.cattitle.setPadding(StaticFunctions.dpToPx(mContext,8),0,StaticFunctions.dpToPx(mContext,8),StaticFunctions.dpToPx(mContext,18));
        }

    }


    /**
     * Temporary function for Order Disable
     */
    public void hideOrderDisableConfig(View  btn_add_to_cart) {
        // add catalog button hide for single product
        if (StaticFunctions.checkOrderDisableConfig(mContext)) {
            btn_add_to_cart.setVisibility(View.GONE);
        }
    }

    private boolean checkPriceRange(Response_catalogMini responseCatalogMinified) {
        if (responseCatalogMinified.getPrice_range() != null && !responseCatalogMinified.getPrice_range().equals("0.00") && !responseCatalogMinified.getPrice_range().equals("0.0") && !responseCatalogMinified.getPrice_range().equals("0")) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return feedItemlist.size();
    }

    public boolean isSellerApprove() {
        String supplier_approval_status = UserInfo.getInstance(mContext).getSupplierApprovalStatus();
        if (supplier_approval_status != null) {
            if (supplier_approval_status.equals(Constants.SELLER_APPROVAL_PENDING)) {
                return false;
            } else if (supplier_approval_status.equals(Constants.SELLER_APPROVAL_APPROVED)) {
               return true;
            } else if (supplier_approval_status.equals(Constants.SELLER_APPROVAL_REJECTED)) {
                return false;
            } else if (supplier_approval_status.equals(Constants.SELLER_APPROVAL_FIRSTTIME_UPLOAD)) {
                return false;
            }
        }
        return false;
    }

    public  interface UpdateListPageListener  {
        void updateList(int position);
    }

    public  void setUpdateListPageListener(UpdateListPageListener updateListPageListener) {
        this.updateListPageListener = updateListPageListener;
    }

    public void saveWishlist(final Context context, final String productId, final CustomViewHolder holder) {
        Application_Singleton.trackEvent("Wishlist", "Save Wishlist", productId);
        String userid = UserInfo.getInstance(mContext).getUserId();
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("user", userid);
        params.put("product", productId);
        HttpManager.getInstance((Activity) context).methodPost(HttpManager.METHOD.POSTWITHPROGRESS, URLConstants.userUrl(context, "wishlist-catalog", userid), params, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                StaticFunctions.addWishList(context, productId);
                int wishcount = UserInfo.getInstance(context).getWishlistCount() + 1;
                UserInfo.getInstance(context).setWishlistCount(wishcount);
                Toast.makeText(context, "Catalog successfully added to wishlist", Toast.LENGTH_SHORT).show();
                StaticFunctions.updateStatics(context);
                holder.img_wishlist.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_bookmart_white_24dp));
                holder.img_wishlist.setSelected(true);
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    public void removeWishlist(final Context context, final String product_id, final CustomViewHolder holder) {
        HashMap<String, String> headers = StaticFunctions.getAuthHeader((Activity) context);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("product", product_id);
        HttpManager.getInstance((Activity) context).methodPost(HttpManager.METHOD.POSTWITHPROGRESS, URLConstants.userUrl(context, "wishlist-delete-product", ""), hashMap, headers, false, new HttpManager.customCallBack() {
            @Override
            public void onCacheResponse(String response) {

            }

            @Override
            public void onServerResponse(String response, boolean dataUpdated) {
                StaticFunctions.removeWishList(context, product_id);
                int wishcount = UserInfo.getInstance(context).getWishlistCount() - 1;
                UserInfo.getInstance(context).setWishlistCount(wishcount);
                Toast.makeText(mContext, "Catalog successfully removed from wishlist", Toast.LENGTH_SHORT).show();
                holder.img_wishlist.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_bookmark_border_white_24dp));
                holder.img_wishlist.setSelected(false);
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }
}