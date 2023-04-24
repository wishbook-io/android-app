package com.wishbook.catalog.home.home_groupie.view;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.NavigationUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonadapters.HomeCategoryItemsAdapter;
import com.wishbook.catalog.commonmodels.CartProductModel;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.responses.CatalogMinified;
import com.wishbook.catalog.commonmodels.responses.Response_catalogMini;
import com.wishbook.catalog.home.cart.CartHandler;
import com.wishbook.catalog.home.cart.Fragment_MyCart;
import com.xwray.groupie.Item;
import com.xwray.groupie.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeWishListItem extends Item<HomeWishListItem.WishListViewHolder> {


    Fragment fragment;
    Context mContext;
    private Response_catalogMini data;
    private int span_grid = 2;


    public HomeWishListItem(Response_catalogMini data, Context context, Fragment fragment, int span_grid) {
        this.data = data;
        this.mContext = context;
        this.fragment = fragment;
        this.span_grid = span_grid;
    }


    public interface ItemClickListener {
        void onClick(View view, int position, boolean isLongClick);
    }


    @Override
    public int getLayout() {
        return R.layout.home_wishlist_item;
    }


    @NonNull
    @Override
    public HomeWishListItem.WishListViewHolder createViewHolder(@NonNull View itemView) {
        return new HomeWishListItem.WishListViewHolder(itemView);
    }

    @Override
    public void bind(@NonNull WishListViewHolder holder, int position) {
        Log.e("WISH", "onBindViewHolder 1 : " + position);
        if (data instanceof Response_catalogMini) {
            Log.e("WISH", "onBindViewHolder 2 : " + position);
            final Response_catalogMini wish = data;
            if (position == 0) {
                holder.spacer.setVisibility(View.VISIBLE);
            } else {
                holder.spacer.setVisibility(View.GONE);
            }
            if (wish.getImage().getThumbnail_small() != null) {
                StaticFunctions.loadFresco(mContext, wish.getImage().getThumbnail_small(), holder.item_img);
            }
            if (wish.getBrand_name() != null)
                holder.txt_brand_name.setText(wish.getBrand_name());

            if (wish.getSupplier_details() != null) {
                if (wish.getSupplier_details().getTotal_suppliers() != 0) {
                    if (wish.getSupplier_details().getTotal_suppliers() == 1) {
                        if (wish.getSupplier_name() != null && !wish.getSupplier_name().equals("") && !wish.getSupplier_name().equals("null")) {
                            holder.txt_seller_name.setVisibility(View.INVISIBLE);
                            //((WishListViewHolder) holder).txt_seller_name.setText("Sold by : " + wish.getSupplier_name());
                        }
                    } else {
                        // multiple Seller
                        holder.txt_seller_name.setVisibility(View.INVISIBLE);
                        //((WishListViewHolder) holder).txt_seller_name.setText("Sold by : "+wish.getSupplier_details().getTotal_suppliers() + " Sellers");
                    }
                } else {
                    holder.txt_seller_name.setVisibility(View.INVISIBLE);
                }
            } else {
                if (wish.getSupplier_name() != null && !wish.getSupplier_name().equals("") && !wish.getSupplier_name().equals("null")) {
                    holder.txt_seller_name.setVisibility(View.INVISIBLE);
                    // ((WishListViewHolder) holder).txt_seller_name.setText("Sold by : " + wish.getSupplier_name());
                } else {
                    holder.txt_seller_name.setVisibility(View.INVISIBLE);
                }
            }

            //full catalog only
            if (wish.getFull_catalog_orders_only() != null && wish.getFull_catalog_orders_only().equals("true")) {
                holder.txt_only_full_catalog.setText("Only full catalog for sale");
            } else {
                holder.txt_only_full_catalog.setText("Single piece available");
            }

            if (wish.getProduct_type() != null &&
                    !wish.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN)
                    && wish.getFull_catalog_orders_only().equalsIgnoreCase("false") &&
                    wish.getTotal_products() != null && Integer.parseInt(wish.getTotal_products()) == 1) {
                // show single pc price range
                holder.txt_price.setVisibility(View.VISIBLE);
                if (wish.getSingle_piece_price_range().contains("-")) {
                    String[] priceRangeMultiple = wish.getSingle_piece_price_range().split("-");
                    holder.txt_price.setText(wish.getTotal_products() + " designs, " + " \u20B9" + priceRangeMultiple[0] + " - " + "\u20B9" + priceRangeMultiple[1] + "/Pc."  /*" , " + response_catalogMini.getTotal_products() + " Designs"*/);
                } else {
                    String priceRangeSingle = wish.getSingle_piece_price_range();
                    holder.txt_price.setText(wish.getTotal_products() + " designs, " + "\u20B9" + priceRangeSingle + "/Pc.");
                }
            } else {
                //price_range
                String price_range = wish.getPrice_range();
                if (price_range != null) {
                    holder.txt_price.setVisibility(View.VISIBLE);
                    if (price_range.contains("-")) {
                        String[] priceRangeMultiple = price_range.split("-");
                        holder.txt_price.setText(" \u20B9" + priceRangeMultiple[0] + " - " + "\u20B9" + priceRangeMultiple[1] + "/Pc."  /*" , " + response_catalogMini.getTotal_products() + " Designs"*/);
                    } else {
                        String priceRangeSingle = price_range;
                        holder.txt_price.setText("\u20B9" + priceRangeSingle + "/Pc.");
                    }
                } else {
                    holder.txt_price.setVisibility(View.GONE);
                }
            }


            if (wish.getTotal_products() != null) {
                holder.txt_number_design.setVisibility(View.VISIBLE);
                holder.txt_number_design.setText(wish.getTotal_products() + " Designs");
            } else {
                holder.txt_number_design.setVisibility(View.GONE);
            }
            holder.txt_catalog_name.setText(wish.getTitle());

            if (StaticFunctions.checkCatalogIsInCart(mContext, wish.getId())) {
                holder.btn_purchase.setText("GO TO CART");
            } else {
                holder.btn_purchase.setText("ADD TO CART");
            }


            holder.btn_purchase.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (UserInfo.getInstance(mContext).isGuest()) {
                        StaticFunctions.ShowRegisterDialog(mContext, "Home Page");
                        return;
                    }

                    if (holder.btn_purchase.getText().toString().contains("GO")) {
                        try {
                            Application_Singleton.CONTAINER_TITLE = "My Cart";
                            Application_Singleton.CONTAINERFRAG = new Fragment_MyCart();
                            StaticFunctions.switchActivity(fragment.getActivity(), OpenContainer.class);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {

                        CatalogMinified response = new CatalogMinified(wish.getId(), "catalog", wish.isBuyer_disabled(), wish.getTitle(), wish.getBrand_name(), wish.getView_permission());
                        response.setIs_supplier_approved(wish.getIs_supplier_approved());
                        response.setSupplier(wish.getSupplier());
                        response.setFull_catalog_orders_only(wish.getFull_catalog_orders_only());
                        response.setSupplier_name(wish.getSupplier_name());
                        response.setSupplier_chat_user(wish.getSupplier_chat_user());
                        response.setBuyer_disabled(wish.isBuyer_disabled());
                        response.setSupplier_disabled(wish.isSupplier_disabled());
                        response.setPrice_range(wish.getPrice_range());
                        response.setSupplier_details(wish.getSupplier_details());
                        response.setIs_owner(wish.is_owner());
                        response.setIs_addedto_wishlist(wish.getIs_addedto_wishlist());
                        response.setFromPublic(true);
                        if (wish.getSupplier_details() != null) {
                            response.setNear_by_sellers(wish.getSupplier_details().getNear_by_sellers());
                        }
                        Application_Singleton.selectedshareCatalog = response;


                        try {
                            CartHandler cartHandler = new CartHandler(((AppCompatActivity) mContext));
                            List<Integer> q = new ArrayList<>();
                            for (int i = 0; i < Integer.parseInt(wish.getTotal_products()); i++) {
                                q.add(1);
                            }
                            int a[] = new int[q.size()];
                            for (int i = 0; i < a.length; i++) {
                                a[i] = q.get(i).intValue();
                            }
                            cartHandler.setAddToCartCallbackListener(new CartHandler.AddToCartCallbackListener() {
                                @Override
                                public void onSuccess(CartProductModel response) {
                                    holder.btn_purchase.setText("GO TO CART");
                                }

                                @Override
                                public void onFailure() {
                                    holder.btn_purchase.setText("ADD TO CART");
                                }
                            });
                            cartHandler.addCatalogToCart(response.getId(), "", fragment, "home2", null, holder.btn_purchase, "Nan");
                            holder.btn_purchase.setText("GO TO CART");
                            Application_Singleton.trackEvent("Add to cart", "Click", "From Wishlist");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                }
            });

            holder.setClickListener(new HomeCategoryItemsAdapter.ItemClickListener() {
                @Override
                public void onClick(View view, int position, boolean isLongClick) {

                    Bundle bundle = new Bundle();
                    bundle.putString("from", "Home Wishlist");
                    bundle.putString("product_id", wish.getId());
                    new NavigationUtils().navigateDetailPage(mContext, bundle);
                }
            });

        }
    }


    @Override
    public int getSpanSize(int spanCount, int position) {
        return spanCount / span_grid;
    }


    public class WishListViewHolder extends ViewHolder implements View.OnClickListener {

        @BindView(R.id.card_view)
        CardView cardView;

        @BindView(R.id.item_img)
        SimpleDraweeView item_img;

        @BindView(R.id.txt_brand_name)
        TextView txt_brand_name;

        @BindView(R.id.txt_catalog_name)
        TextView txt_catalog_name;

        @BindView(R.id.txt_seller_name)
        TextView txt_seller_name;

        @BindView(R.id.txt_only_full_catalog)
        TextView txt_only_full_catalog;

        @BindView(R.id.txt_price)
        TextView txt_price;

        @BindView(R.id.btn_purchase)
        AppCompatButton btn_purchase;

        @BindView(R.id.txt_number_design)
        TextView txt_number_design;

        @BindView(R.id.spacer)
        View spacer;

        private HomeCategoryItemsAdapter.ItemClickListener clickListener;

        public WishListViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        public void setClickListener(HomeCategoryItemsAdapter.ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getPosition(), false);
        }
    }

}