package com.wishbook.catalog.home.catalog.adapter;

import android.content.Context;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.NavigationUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonadapters.BrowseCatalogsAdapter;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.responses.Response_catalogMini;
import com.wishbook.catalog.home.catalog.Fragment_BrowseCatalogs;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class HomeRecommedationAdapter extends RecyclerView.Adapter<HomeRecommedationAdapter.CustomViewHolder> {

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
    private BrowseCatalogsAdapter.ProductBottomBarListener productBottomBarListener;
    public ArrayList<Response_catalogMini> seleResponse_catalogMiniArrayList = new ArrayList<>();


    public HomeRecommedationAdapter(AppCompatActivity context, ArrayList<Response_catalogMini> feedItemList, String layoutManager, int numberofGridSize, boolean isRecommendation) {
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
    }


    @Override
    public HomeRecommedationAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_recommded_list, viewGroup, false);
        return new HomeRecommedationAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final HomeRecommedationAdapter.CustomViewHolder holder, final int i) {
        String image = null;
        final Response_catalogMini response_catalogMini = feedItemlist.get(i);
        if (isCollectionTypeProduct) {
            holder.linear_design_name.setVisibility(View.VISIBLE);
            holder.txt_design_name.setText("(" + response_catalogMini.getId() + ")");
            holder.cattitle.setText(response_catalogMini.getCatalog_title());
        } else {
            holder.cattitle.setText(response_catalogMini.getTitle());
            holder.linear_design_name.setVisibility(View.GONE);
        }


        /// #### Section Image Show ######


        String catimage = response_catalogMini.getImage().getThumbnail_small();
        if (catimage != null && !catimage.equals("")) {
            StaticFunctions.loadFresco(mContext, catimage, holder.cat_img);
        }


        // ##### Section MWP_Single_Price #######
        if (response_catalogMini.getMwp_single_price() > 0) {
            holder.txt_mwp_single_price.setVisibility(View.VISIBLE);
            holder.txt_mwp_single_price.setText("\u20B9" + response_catalogMini.getMwp_single_price());
        } else {
            holder.txt_mwp_single_price.setVisibility(View.GONE);
        }


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


        //// ### Section Label Show  ####
        if (response_catalogMini.isReady_to_ship()) {
            holder.txt_ready_to_dispatch.setVisibility(View.GONE);
            holder.txt_pre_order.setVisibility(View.GONE);
            if ((response_catalogMini.getFull_catalog_orders_only() != null && response_catalogMini.getFull_catalog_orders_only().equals("false")) || Integer.parseInt(response_catalogMini.getTotal_products()) == 1) {


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


        // ##### Section Click Listener #####


        holder.mainContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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


        boolean isShowSinglePrice = false;

        // #### Section Single Price Range Show #####


        if (Integer.parseInt(response_catalogMini.getTotal_products()) > 1 && !response_catalogMini.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN) && !isFullCatalogFilter) {
            if (response_catalogMini.getFull_catalog_orders_only().equals("false") && response_catalogMini.getSingle_piece_price_range() != null) {
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


    public void setProductBottomBarListener(BrowseCatalogsAdapter.ProductBottomBarListener productBottomBarListener) {
        this.productBottomBarListener = productBottomBarListener;
    }

    @Override
    public int getItemCount() {
        return feedItemlist.size();
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder {

        private final SimpleDraweeView cat_img, brand_img;
        private final TextView cattitle;
        //  private final ImageButton addprod;

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
            price_range = (TextView) view.findViewById(R.id.price_range);
            mainContainer = (RelativeLayout) view.findViewById(R.id.MainContainer);
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
