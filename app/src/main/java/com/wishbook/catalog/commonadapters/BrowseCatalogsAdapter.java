package com.wishbook.catalog.commonadapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Html;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.mikepenz.actionitembadge.library.utils.BadgeStyle;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.URLConstants;
import com.wishbook.catalog.Utils.NavigationUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.networking.ErrorString;
import com.wishbook.catalog.Utils.networking.HttpManager;
import com.wishbook.catalog.commonmodels.CartProductModel;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.responses.Response_catalogMini;
import com.wishbook.catalog.home.catalog.CatalogHolderVersion2;
import com.wishbook.catalog.home.catalog.Fragment_BrowseCatalogs;
import com.wishbook.catalog.home.catalog.Fragment_BrowseProduct;
import com.wishbook.catalog.home.catalog.view_action.ViewMultipleSelectionBottomBar;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class BrowseCatalogsAdapter extends RecyclerView.Adapter<BrowseCatalogsAdapter.CustomViewHolder> {

    private ArrayList<Response_catalogMini> feedItemlist;
    private AppCompatActivity mContext;
    private final int height;
    private final int width;
    private UserInfo userinfo;
    private String layoutManager;
    private int numberofGridSize = 1;
    private boolean isRecommendation;
    private boolean isCollectionTypeProduct;
    private boolean isFullCatalogFilter;
    private boolean isWishListProduct;
    private ProductBottomBarListener productBottomBarListener;
    public ArrayList<Response_catalogMini> seleResponse_catalogMiniArrayList = new ArrayList<>();
    SharedPreferences preferences;


    public BrowseCatalogsAdapter(AppCompatActivity context, ArrayList<Response_catalogMini> feedItemList) {
        this.feedItemlist = feedItemList;
        this.mContext = context;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        this.width = size.x;
        this.height = size.y;
        userinfo = UserInfo.getInstance(mContext);
        this.layoutManager = Constants.PRODUCT_VIEW_LIST;
    }

    public BrowseCatalogsAdapter(AppCompatActivity context, ArrayList<Response_catalogMini> feedItemList, String layoutManager) {
        this.feedItemlist = feedItemList;
        this.mContext = context;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        this.width = size.x;
        this.height = size.y;
        this.layoutManager = layoutManager;
        userinfo = UserInfo.getInstance(mContext);
        preferences = mContext.getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
    }

    // Recommendation Constructor
    public BrowseCatalogsAdapter(AppCompatActivity context, ArrayList<Response_catalogMini> feedItemList, String layoutManager, int numberofGridSize, boolean isRecommendation) {
        this.feedItemlist = feedItemList;
        this.mContext = context;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        this.width = size.x;
        this.height = size.y;
        this.layoutManager = layoutManager;
        userinfo = UserInfo.getInstance(mContext);
        this.numberofGridSize = numberofGridSize;
        this.isRecommendation = isRecommendation;
        preferences = mContext.getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
    }

    public void setWishListProduct(boolean wishListProduct) {
        isWishListProduct = wishListProduct;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (layoutManager.equalsIgnoreCase(Constants.PRODUCT_VIEW_GRID)) {
            if (numberofGridSize == 3 && isRecommendation) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recommedation_catalog_item_grid, viewGroup, false);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(Math.round(width / 2.5f), Math.round(height / 3.1f));
                lp.setMargins(10, 0, 10, 0);
                view.setLayoutParams(lp);
                return new CustomViewHolder(view);
            } else {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.browse_catalog_item_grid, viewGroup, false);
                return new CustomViewHolder(view);
            }
        } else if (layoutManager.equalsIgnoreCase(Constants.PRODUCT_VIEW_LIST)) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.browse_catalog_item_version2, viewGroup, false);
            return new CustomViewHolder(view);
        } else {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.browse_catalog_item_version2, viewGroup, false);
            return new CustomViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder holder, final int i) {
        String image = null;
        final Response_catalogMini response_catalogMini = feedItemlist.get(i);
        if (isWishListProduct) {
            isCollectionTypeProduct = response_catalogMini.getProduct_type() != null && response_catalogMini.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SINGLE);
        }

        if (isCollectionTypeProduct) {
            holder.linear_design_name.setVisibility(View.VISIBLE);
            holder.txt_design_name.setText("(" + response_catalogMini.getId() + ")");
            holder.cattitle.setText(response_catalogMini.getCatalog_title());
        } else {
            holder.cattitle.setText(response_catalogMini.getTitle());
            holder.linear_design_name.setVisibility(View.GONE);
        }


        /// #### Section Image Show ######
        if (layoutManager != null && layoutManager.equalsIgnoreCase(Constants.PRODUCT_VIEW_LIST)) {
            String catimage = response_catalogMini.getImage().getThumbnail_medium();
            if (catimage != null && !catimage.equals("")) {
                StaticFunctions.loadFresco(mContext, catimage, holder.cat_img);
            }
        } else {
            String catimage = response_catalogMini.getImage().getThumbnail_small();
            if (catimage != null && !catimage.equals("")) {
                StaticFunctions.loadFresco(mContext, catimage, holder.cat_img);
            }
        }


        // ##### Section MWP_Single_Price #######
        if (response_catalogMini.getMwp_single_price() > 0) {
            holder.txt_mwp_single_price.setVisibility(View.VISIBLE);
            holder.txt_mwp_single_price.setText("\u20B9" + response_catalogMini.getMwp_single_price());
        } else {
            holder.txt_mwp_single_price.setVisibility(View.GONE);
        }


        // #### Section Full Price Range #####

        String price_range = response_catalogMini.getPrice_range();
        if (price_range != null) {
            holder.price_range.setVisibility(View.VISIBLE);
            if (price_range.contains("-")) {
                String[] priceRangeMultiple = price_range.split("-");
                holder.price_range.setText("\u20B9" + priceRangeMultiple[0] + " - " + "\u20B9" + priceRangeMultiple[1] + "/Pc."  /*" , " + response_catalogMini.getTotal_products() + " Designs"*/);
            } else {
                String priceRangeSingle = price_range;
                holder.price_range.setText("\u20B9" + priceRangeSingle + "/Pc."/*+ ", " + response_catalogMini.getTotal_products() + " Designs"*/);
            }

            if (response_catalogMini.getFull_discount() > 0) {
                holder.txt_full_discount.setVisibility(View.VISIBLE);
                holder.txt_full_discount.setText((int) response_catalogMini.getFull_discount() + "% off");
            } else {
                holder.txt_full_discount.setVisibility(View.GONE);
            }

        } else {
            holder.price_range.setVisibility(View.GONE);
        }

        if (response_catalogMini.getTotal_products() != null && Integer.parseInt(response_catalogMini.getTotal_products()) == 1 && response_catalogMini.getProduct_type() != null && !response_catalogMini.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN)) {
            holder.txt_full_price_label.setText("Price : ");
            /**
             * Changes done according to new mail April-30 by Jay patel
             */
            if (response_catalogMini.getFull_catalog_orders_only() != null && response_catalogMini.getFull_catalog_orders_only().equalsIgnoreCase("false") && response_catalogMini.getSingle_piece_price_range() != null) {
                if (response_catalogMini.getSingle_piece_price_range().contains("-")) {
                    String[] priceRangeMultiple = response_catalogMini.getSingle_piece_price_range().split("-");
                    holder.price_range.setText(" \u20B9" + priceRangeMultiple[0] + " - " + "\u20B9" + priceRangeMultiple[1] + "/Pc."  /*" , " + response_catalogMini.getTotal_products() + " Designs"*/);
                } else {
                    String priceRangeSingle = response_catalogMini.getSingle_piece_price_range();
                    holder.price_range.setText("\u20B9" + priceRangeSingle + "/Pc."/*+ ", " + response_catalogMini.getTotal_products() + " Designs"*/);
                }

                if (response_catalogMini.getSingle_discount() > 0) {
                    holder.txt_full_discount.setVisibility(View.VISIBLE);
                    holder.txt_full_discount.setText((int) response_catalogMini.getSingle_discount() + "% off");
                } else {
                    holder.txt_full_discount.setVisibility(View.GONE);
                }
            }
        } else {
            holder.txt_full_price_label.setText("Full : ");
        }





        if (response_catalogMini.isSupplier_disabled() || response_catalogMini.isBuyer_disabled()) {

            // Comment for multiple seller because any seller is selling that catalog
          /*  if (response_catalogMini.isBuyer_disabled()) {
                customViewHolder.disable_container.setAlpha(0.5f);
                customViewHolder.disable_text.setVisibility(View.VISIBLE);
                customViewHolder.disable_text.setText(mContext.getResources().getString(R.string.buyer_disable));
                customViewHolder.sharebut.setEnabled(false);
                customViewHolder.linear_share.setEnabled(false);
            } else {
                customViewHolder.disable_text.setVisibility(View.GONE);
            }
            if (response_catalogMini.isSupplier_disabled()) {
                customViewHolder.disable_container.setAlpha(0.5f);
                customViewHolder.disable_text1.setVisibility(View.VISIBLE);
                customViewHolder.disable_text1.setText(mContext.getResources().getString(R.string.supplier_disable));
                customViewHolder.sharebut.setEnabled(false);
                customViewHolder.linear_share.setEnabled(false);
            } else {
                customViewHolder.disable_text1.setVisibility(View.GONE);
            }*/
        } else {

        }

        if(isWishListProduct && response_catalogMini.isSupplier_disabled()) {
            holder.disable_container.setAlpha(0.5f);
            holder.disable_text.setVisibility(View.VISIBLE);
            holder.disable_text.setText(mContext.getResources().getString(R.string.buyer_disable));
        } else {
            holder.disable_container.setAlpha(1);
            holder.disable_text1.setVisibility(View.INVISIBLE);
            holder.disable_text.setVisibility(View.INVISIBLE);
        }


        // ### Section Brand Image ####
        if (layoutManager == null || layoutManager == Constants.PRODUCT_VIEW_LIST) {
            if (response_catalogMini.getBrand_image() != null) {
                image = response_catalogMini.getBrand_image();
                if (image != null & !image.isEmpty()) {
                    holder.brand_img.setVisibility(View.VISIBLE);
                    StaticFunctions.loadFresco(mContext, image, holder.brand_img);
                }
            } else {
                holder.brand_img.setVisibility(View.GONE);
            }


            //hyperlinking
            holder.brand_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    StaticFunctions.hyperLinking1("brand", response_catalogMini.getBrand(), mContext, response_catalogMini.getBrand_name());
                }
            });
        }







        //// ### Section Label Show  ####
        if (response_catalogMini.isReady_to_ship()) {
            holder.txt_ready_to_dispatch.setVisibility(View.GONE);
            holder.txt_pre_order.setVisibility(View.GONE);
            if ((response_catalogMini.getFull_catalog_orders_only() != null && response_catalogMini.getFull_catalog_orders_only().equals("false")) || Integer.parseInt(response_catalogMini.getTotal_products()) == 1) {
                /***
                 * Task Given by Jay patel
                 * Changes done March-27 , don't show Single Pc Label and price isFullCatalogFilter
                 */
                if (!isFullCatalogFilter && !isCollectionTypeProduct)
                    holder.txt_single_pc_available.setVisibility(View.VISIBLE);
                else
                    holder.txt_single_pc_available.setVisibility(View.GONE);
            } else {
                holder.txt_single_pc_available.setVisibility(View.GONE);
            }
        } else {
            holder.txt_ready_to_dispatch.setVisibility(View.GONE);
            holder.txt_pre_order.setVisibility(View.VISIBLE);
            holder.txt_single_pc_available.setVisibility(View.GONE);
        }

        if (response_catalogMini.getProduct_type() != null && response_catalogMini.getProduct_type().equals(Constants.PRODUCT_TYPE_SCREEN)) {
            holder.txt_ready_to_dispatch.setVisibility(View.GONE);
            holder.txt_pre_order.setVisibility(View.GONE);
            holder.txt_single_pc_available.setVisibility(View.GONE);
        }


        // ### Section show Live Stats ####
        if (response_catalogMini.getLive_stats() != null && !response_catalogMini.getLive_stats().isEmpty()) {
            holder.live_stats.setVisibility(View.VISIBLE);
            holder.live_stats.setText(Html.fromHtml(response_catalogMini.getLive_stats()), TextView.BufferType.NORMAL);
        } else {
            holder.live_stats.setVisibility(View.GONE);
        }


        // ### Section Wishlist Button #####

        boolean showWishlist = false;
        if (isWishListProduct) {
            if (!isRecommendation && isWishListProduct) {
                showWishlist = true;
            }
        } else {
            if (!isRecommendation && !isCollectionTypeProduct) {
                showWishlist = true;
            }
        }
        if (showWishlist) {
            if (!userinfo.getCompanyType().equals("seller")) {
                holder.img_wishlist.setVisibility(View.VISIBLE);
                if (StaticFunctions.checkProductInWishList(mContext, response_catalogMini.getId())) {
                    holder.img_wishlist.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_bookmart_white_24dp));
                    holder.img_wishlist.setSelected(true);
                } else {
                    holder.img_wishlist.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_bookmark_border_white_24dp));
                    holder.img_wishlist.setSelected(false);
                }
            } else {
                holder.img_wishlist.setVisibility(View.GONE);
            }
        } else {
            holder.img_wishlist.setVisibility(View.GONE);
        }


        // ##### Section Click Listener #####


        holder.mainContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isWishListProduct) {
                    isCollectionTypeProduct = response_catalogMini.getProduct_type() != null
                            && response_catalogMini.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SINGLE);
                }
                if (isCollectionTypeProduct) {
                    Bundle bundle = new Bundle();
                    bundle.putString("product_id", response_catalogMini.getId());
                    bundle.putString("from", "Public List");
                    new NavigationUtils().navigateSingleProductDetailPage(mContext, bundle);
                } else {
                    Bundle bundle = new Bundle();
                    if (isRecommendation) {
                        bundle.putString("from", "Recommended Product List");
                    } else {
                        bundle.putString("from", Fragment_BrowseCatalogs.class.getSimpleName());
                    }
                    bundle.putString("product_id", response_catalogMini.getId());
                    new NavigationUtils().navigateDetailPage(mContext, bundle);
                }
            }
        });


        holder.img_wishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UserInfo.getInstance(mContext).isGuest()) {
                    StaticFunctions.ShowRegisterDialog(mContext, "Product List");
                    return;
                }
                if (holder.img_wishlist.isSelected()) {
                    // Remove from WishList
                    removeWishlist(mContext, response_catalogMini.getId(), holder);
                } else {
                    // Add in WishList
                    saveWishlist(mContext, response_catalogMini.getId(), holder);
                }
            }
        });


        boolean isShowSinglePrice = false;

        // #### Section Single Price Range Show #####

        /***
         * Changes done Feb-8 2019 remove single pc price and for whose catalog has 1 products
         */

        /***
         * Task Given by Jay patel
         * Changes done March-27 2019 , don't show Single Pc Label and price isFullCatalogFilter
         */
        if (Integer.parseInt(response_catalogMini.getTotal_products()) > 1 && !response_catalogMini.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN) && !isFullCatalogFilter) {
            if (response_catalogMini.getFull_catalog_orders_only().equals("false") && response_catalogMini.getSingle_piece_price_range() != null) {
                if (layoutManager != Constants.PRODUCT_VIEW_LIST) {
                    holder.linear_price_range.setPadding(StaticFunctions.dpToPx(mContext, 0), 0, 0, 0);
                }
                holder.linear_single_price_range.setVisibility(View.VISIBLE);
                isShowSinglePrice = true;
                String single_range = response_catalogMini.getSingle_piece_price_range();
                if (single_range != null && !single_range.isEmpty()) {
                    if (single_range.contains("-")) {
                        String[] priceRangeMultiple = single_range.split("-");
                        holder.single_price_range.setText(" \u20B9" + priceRangeMultiple[0] + " - " + "\u20B9" + priceRangeMultiple[1] + "/Pc.");
                    } else {
                        String priceRangeSingle = single_range;
                        holder.single_price_range.setText("\u20B9" + priceRangeSingle + "/Pc.");
                    }
                }

                if (response_catalogMini.getSingle_discount() > 0) {
                    holder.txt_single_discount.setVisibility(View.VISIBLE);
                    holder.txt_single_discount.setText((int) response_catalogMini.getSingle_discount() + "% off");
                } else {
                    holder.txt_single_discount.setVisibility(View.GONE);
                }
            } else {
                if (layoutManager != Constants.PRODUCT_VIEW_LIST) {
                    holder.linear_price_range.setPadding(StaticFunctions.dpToPx(mContext, 0), 0, 0, StaticFunctions.dpToPx(mContext, 5));
                }
                holder.linear_single_price_range.setVisibility(View.GONE);
                isShowSinglePrice = false;
            }
        } else {
            holder.linear_single_price_range.setVisibility(View.GONE);
            isShowSinglePrice = false;
        }





        if (!isRecommendation && !userinfo.getCompanyType().equals("seller")) {
            if (isCollectionTypeProduct) {
                // Show Add To Cart Button
                // show Radio Button

                if (response_catalogMini.getSingle_piece_price() != null) {
                    holder.linear_collection_product_price.setVisibility(View.VISIBLE);
                    holder.txt_collection_product_price.setText("\u20B9" + response_catalogMini.getSingle_piece_price());
                    if (response_catalogMini.getSingle_discount() > 0) {
                        holder.txt_collection_product_single_discount.setVisibility(View.VISIBLE);
                        holder.txt_collection_product_single_discount.setText((int) response_catalogMini.getSingle_discount() + "% off");
                    } else {
                        holder.txt_collection_product_single_discount.setVisibility(View.GONE);
                    }

                }
                holder.linear_price_range.setVisibility(View.GONE);
                holder.linear_single_price_range.setVisibility(View.GONE);
                if (!isWishListProduct) {
                    holder.btn_add_to_cart.setVisibility(View.VISIBLE);
                    holder.chk_product_selection.setVisibility(View.VISIBLE);
                    holder.img_whatsapp_share.setVisibility(View.VISIBLE);
                }


                holder.chk_product_selection.setOnCheckedChangeListener(null);
                checkAlreadyAddedSinglePcToCart(holder.btn_add_to_cart, response_catalogMini.getId());

                holder.btn_add_to_cart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (holder.btn_add_to_cart.getText().toString().contains("GO")) {
                            new NavigationUtils().navigateMyCart(mContext);
                        } else {
                            ArrayList<Response_catalogMini> temp = new ArrayList<>();
                            temp.add(response_catalogMini);
                            ViewMultipleSelectionBottomBar viewMultipleSelectionBottomBar = new ViewMultipleSelectionBottomBar(mContext, temp, null);
                            viewMultipleSelectionBottomBar.setTaskCompleteListener(new ViewMultipleSelectionBottomBar.TaskCompleteListener() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onFailure() {

                                }

                                @Override
                                public void onSuccessAddToCart(CartProductModel response) {
                                    holder.btn_add_to_cart.setText("GO TO CART");
                                    Toast.makeText(mContext, "Product Added to cart", Toast.LENGTH_SHORT).show();
                                    if (mContext instanceof OpenContainer) {
                                        updateToolbarCart();
                                    }
                                }
                            });
                            viewMultipleSelectionBottomBar.addToCart();
                        }
                    }
                });

                if (seleResponse_catalogMiniArrayList != null && seleResponse_catalogMiniArrayList.size() > 0 && seleResponse_catalogMiniArrayList.contains(response_catalogMini)) {
                    holder.chk_product_selection.setChecked(true);
                } else {
                    holder.chk_product_selection.setChecked(false);
                }

                holder.chk_product_selection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean ischecked) {
                        if (ischecked) {
                            seleResponse_catalogMiniArrayList.add(response_catalogMini);
                        } else {
                            if (seleResponse_catalogMiniArrayList.contains(response_catalogMini)) {
                                seleResponse_catalogMiniArrayList.remove(response_catalogMini);
                            }
                        }
                        if (productBottomBarListener != null) {
                            productBottomBarListener.showHide(seleResponse_catalogMiniArrayList);
                        }
                    }
                });

                holder.img_whatsapp_share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ArrayList<Response_catalogMini> temp = new ArrayList<>();
                        temp.add(response_catalogMini);
                        ViewMultipleSelectionBottomBar viewMultipleSelectionBottomBar = new ViewMultipleSelectionBottomBar(mContext, temp, null);
                        viewMultipleSelectionBottomBar.setTaskCompleteListener(new ViewMultipleSelectionBottomBar.TaskCompleteListener() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onFailure() {

                            }

                            @Override
                            public void onSuccessAddToCart(CartProductModel response) {

                            }
                        });
                        viewMultipleSelectionBottomBar.whatsappShare();

                    }
                });


                // Temporary function
                hideOrderDisableConfig( holder.btn_add_to_cart);

            } else {
                holder.btn_add_to_cart.setVisibility(View.GONE);
                holder.chk_product_selection.setVisibility(View.GONE);
                holder.linear_collection_product_price.setVisibility(View.GONE);
                holder.img_whatsapp_share.setVisibility(View.GONE);

                holder.linear_price_range.setVisibility(View.VISIBLE);
                if (isShowSinglePrice)
                    holder.linear_single_price_range.setVisibility(View.VISIBLE);
                else
                    holder.linear_single_price_range.setVisibility(View.GONE);
            }
        }


    }

    public void checkAlreadyAddedSinglePcToCart(TextView btn_add_to_cart, String productId) {
        btn_add_to_cart.setText("ADD TO CART");
        SharedPreferences preferences = mContext.getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
        if (!preferences.getString("cartdataProducts", "").equalsIgnoreCase("")) {
            Type cartType = new TypeToken<ArrayList<String>>() {
            }.getType();
            ArrayList<String> saveCartData = new Gson().fromJson(preferences.getString("cartdataProducts", ""), cartType);
            if (saveCartData != null && saveCartData.size() > 0) {
                for (int j = 0; j < saveCartData.size(); j++) {
                    if (saveCartData.get(j).contains(productId)) {
                        btn_add_to_cart.setText("GO TO CART");
                        break;
                    }
                }
            } else {
                btn_add_to_cart.setText("ADD TO CART");
            }
        } else {
            btn_add_to_cart.setText("ADD TO CART");
        }
    }

    public void saveWishlist(final Context context, final String productId, final BrowseCatalogsAdapter.CustomViewHolder holder) {
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
                updateToolbarWishlist();
                holder.img_wishlist.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_bookmart_white_24dp));
                holder.img_wishlist.setSelected(true);
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    public void removeWishlist(final Context context, final String product_id, final BrowseCatalogsAdapter.CustomViewHolder holder) {
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
                updateToolbarWishlist();
                holder.img_wishlist.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_bookmark_border_white_24dp));
                holder.img_wishlist.setSelected(false);
            }

            @Override
            public void onResponseFailed(ErrorString error) {
                StaticFunctions.showResponseFailedDialog(error);
            }
        });
    }

    public void updateToolbarWishlist() {
        try {
            if (!UserInfo.getInstance(mContext).getCompanyType().equals("seller")) {
                if (mContext instanceof OpenContainer) {
                    if (((OpenContainer) mContext).toolbar.getMenu() != null) {
                        int wishcount = UserInfo.getInstance(mContext).getWishlistCount();
                        if (wishcount == 0) {
                            BadgeStyle badgeStyleTransparent = new BadgeStyle(BadgeStyle.Style.DEFAULT, com.mikepenz.actionitembadge.library.R.layout.menu_action_item_badge, Color.parseColor("#00000000"), Color.parseColor("#00000000"));
                            ActionItemBadge.update(mContext, ((OpenContainer) mContext).toolbar.getMenu().findItem(R.id.action_wishlist), mContext.getResources().getDrawable(R.drawable.ic_bookmark_gold_24dp), badgeStyleTransparent, "");
                        } else {
                            ActionItemBadge.update(mContext, ((OpenContainer) mContext).toolbar.getMenu().findItem(R.id.action_wishlist), mContext.getResources().getDrawable(R.drawable.ic_bookmark_gold_24dp), ActionItemBadge.BadgeStyles.RED, wishcount);
                        }
                    }
                } else {

                    try {
                        if (CatalogHolderVersion2.toolbar.getMenu() != null) {
                            int wishcount = UserInfo.getInstance(mContext).getWishlistCount();
                            if (wishcount == 0) {
                                BadgeStyle badgeStyleTransparent = new BadgeStyle(BadgeStyle.Style.DEFAULT, com.mikepenz.actionitembadge.library.R.layout.menu_action_item_badge, Color.parseColor("#00000000"), Color.parseColor("#00000000"));
                                ActionItemBadge.update(mContext, CatalogHolderVersion2.toolbar.getMenu().findItem(R.id.action_wishlist), mContext.getResources().getDrawable(R.drawable.ic_bookmark_gold_24dp), badgeStyleTransparent, "");
                            } else {
                                ActionItemBadge.update(mContext, CatalogHolderVersion2.toolbar.getMenu().findItem(R.id.action_wishlist), mContext.getResources().getDrawable(R.drawable.ic_bookmark_gold_24dp), ActionItemBadge.BadgeStyles.RED, wishcount);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    try {
                        if (Fragment_BrowseProduct.toolbar.getMenu() != null) {
                            int wishcount = UserInfo.getInstance(mContext).getWishlistCount();
                            if (wishcount == 0) {
                                BadgeStyle badgeStyleTransparent = new BadgeStyle(BadgeStyle.Style.DEFAULT, com.mikepenz.actionitembadge.library.R.layout.menu_action_item_badge, Color.parseColor("#00000000"), Color.parseColor("#00000000"));
                                ActionItemBadge.update(mContext, Fragment_BrowseProduct.toolbar.getMenu().findItem(R.id.action_wishlist), mContext.getResources().getDrawable(R.drawable.ic_bookmark_gold_24dp), badgeStyleTransparent, "");
                            } else {
                                ActionItemBadge.update(mContext, Fragment_BrowseProduct.toolbar.getMenu().findItem(R.id.action_wishlist), mContext.getResources().getDrawable(R.drawable.ic_bookmark_gold_24dp), ActionItemBadge.BadgeStyles.RED, wishcount);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void updateToolbarCart() {
        if (!UserInfo.getInstance(mContext).getCompanyType().equals("seller")) {
            try {
                SharedPreferences preferences = mContext.getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
                int cartcount = preferences.getInt("cartcount", 0);
                if (cartcount == 0) {
                    BadgeStyle badgeStyleTransparent = new BadgeStyle(BadgeStyle.Style.DEFAULT, com.mikepenz.actionitembadge.library.R.layout.menu_action_item_badge, Color.parseColor("#00000000"), Color.parseColor("#00000000"));
                    ActionItemBadge.update(mContext, Fragment_BrowseProduct.toolbar.getMenu().findItem(R.id.action_cart), mContext.getResources().getDrawable(R.drawable.ic_shopping_cart_24dp), badgeStyleTransparent, "");
                } else {
                    ActionItemBadge.update(mContext, Fragment_BrowseProduct.toolbar.getMenu().findItem(R.id.action_cart), mContext.getResources().getDrawable(R.drawable.ic_shopping_cart_24dp), ActionItemBadge.BadgeStyles.RED, cartcount);
                }
            } catch (Exception e) {
                // e.printStackTrace();
            }
        } else {
            Fragment_BrowseProduct.toolbar.getMenu().findItem(R.id.action_cart).setVisible(false);
        }
    }

    public void setCollectionTypeProduct(boolean collectionTypeProduct) {
        isCollectionTypeProduct = collectionTypeProduct;
        if (!isCollectionTypeProduct) {
            seleResponse_catalogMiniArrayList.clear();
            if (productBottomBarListener != null) {
                productBottomBarListener.showHide(seleResponse_catalogMiniArrayList);
            }
        }
    }

    public void setFullCatalogFilter(boolean fullCatalogFilter) {
        isFullCatalogFilter = fullCatalogFilter;
    }

    public interface ProductBottomBarListener {
        void showHide(ArrayList<Response_catalogMini> response_catalogMinis);
    }

    public void setProductBottomBarListener(ProductBottomBarListener productBottomBarListener) {
        this.productBottomBarListener = productBottomBarListener;
    }

    @Override
    public int getItemCount() {
        return feedItemlist.size();
    }



    /**
     * Temporary function for Order Disable
     */
    public void hideOrderDisableConfig(TextView  btn_add_to_cart) {
        // add catalog button hide for single product
        if (StaticFunctions.checkOrderDisableConfig(mContext)) {
            btn_add_to_cart.setVisibility(View.GONE);
        }
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder {

        private final SimpleDraweeView cat_img, brand_img;
        private final TextView cattitle;
        //  private final ImageButton addprod;
        private final RelativeLayout disable_container;
        private final TextView disable_text;
        private final TextView disable_text1;
        private TextView price_range;
        private final RelativeLayout mainContainer;
        private LinearLayout linear_view;
        private LinearLayout linear_single_price_range, linear_price_range, linear_collection_product_price, linear_design_name;
        ImageButton img_wishlist, img_whatsapp_share;
        private TextView txt_pre_order, txt_ready_to_dispatch, txt_single_pc_available,
                single_price_range, txt_full_price_label, txt_collection_product_price, txt_design_name,
                txt_mwp_single_price, txt_single_discount, txt_full_discount, txt_collection_product_single_discount;
        private TextView btn_add_to_cart;
        CheckBox chk_product_selection;
        TextView live_stats;


        public CustomViewHolder(View view) {
            super(view);
            brand_img = (SimpleDraweeView) view.findViewById(R.id.brand_img);
            cattitle = (TextView) view.findViewById(R.id.cattitle);
            cat_img = (SimpleDraweeView) view.findViewById(R.id.cat_img);
            disable_container = view.findViewById(R.id.disable_container);
            disable_text = (TextView) view.findViewById(R.id.disable_text);
            disable_text1 = (TextView) view.findViewById(R.id.disable_text1);
            price_range = (TextView) view.findViewById(R.id.price_range);
            mainContainer = (RelativeLayout) view.findViewById(R.id.MainContainer);
            img_wishlist = view.findViewById(R.id.img_wishlist);
            txt_pre_order = view.findViewById(R.id.txt_pre_order);
            txt_ready_to_dispatch = view.findViewById(R.id.txt_ready_to_dispatch);
            txt_single_pc_available = view.findViewById(R.id.txt_single_pc_available);
            linear_price_range = view.findViewById(R.id.linear_price_range);
            linear_single_price_range = view.findViewById(R.id.linear_single_price_range);
            single_price_range = view.findViewById(R.id.single_price_range);
            linear_collection_product_price = view.findViewById(R.id.linear_collection_product_price);
            txt_collection_product_price = view.findViewById(R.id.txt_collection_product_price);
            txt_full_price_label = view.findViewById(R.id.txt_full_price_label);
            btn_add_to_cart = view.findViewById(R.id.btn_add_to_cart);
            chk_product_selection = view.findViewById(R.id.chk_product_selection);

            linear_design_name = view.findViewById(R.id.linear_design_name);
            txt_design_name = view.findViewById(R.id.txt_design_name);
            img_whatsapp_share = view.findViewById(R.id.img_whatsapp_share);
            live_stats = view.findViewById(R.id.live_stats);
            txt_mwp_single_price = view.findViewById(R.id.txt_mwp_single_price);
            txt_single_discount = view.findViewById(R.id.txt_single_discount);
            txt_full_discount = view.findViewById(R.id.txt_full_discount);
            txt_collection_product_single_discount = view.findViewById(R.id.txt_collection_product_single_discount);

            if (txt_mwp_single_price != null) {
                txt_mwp_single_price.setPaintFlags(txt_mwp_single_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }

        }
    }
}