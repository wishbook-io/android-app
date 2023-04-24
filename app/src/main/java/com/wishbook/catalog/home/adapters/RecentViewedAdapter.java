package com.wishbook.catalog.home.adapters;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.NavigationUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.CartProductModel;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.responses.CatalogMinified;
import com.wishbook.catalog.commonmodels.responses.Response_catalogMini;
import com.wishbook.catalog.home.cart.CartHandler;
import com.wishbook.catalog.home.cart.Fragment_MyCart;
import com.wishbook.catalog.home.orderNew.add.Fragment_CreatePurchaseOrderVersion2;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RecentViewedAdapter extends RecyclerView.Adapter<RecentViewedAdapter.ViewHolder> {


    Fragment fragment;
    Context context;
    private List<Response_catalogMini> data;


    public RecentViewedAdapter(List<Response_catalogMini> data, Context context, Fragment fragment) {
        this.data = data;
        this.context = context;
        this.fragment = fragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.fragment_recycler_recent, parent, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (position == data.size() - 1) {
            holder.divider.setVisibility(View.GONE);
        }

        if (data != null) {
            final Response_catalogMini recentCatalog = data.get(position);
            if(recentCatalog.getProduct_type()!=null &&
                    !recentCatalog.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN) &&
                    recentCatalog.getFull_catalog_orders_only().equalsIgnoreCase("false") &&
                    recentCatalog.getTotal_products()!=null && Integer.parseInt(recentCatalog.getTotal_products()) == 1) {
                holder.total_products_price_range.setVisibility(View.VISIBLE);
                if (recentCatalog.getSingle_piece_price_range().contains("-")) {
                    String[] priceRangeMultiple = recentCatalog.getSingle_piece_price_range().split("-");
                    holder.total_products_price_range.setText(data.get(position).getTotal_products() + " designs, " + " \u20B9" + priceRangeMultiple[0] + " - " + "\u20B9" + priceRangeMultiple[1] + "/Pc."  /*" , " + response_catalogMini.getTotal_products() + " Designs"*/);
                } else {
                    String priceRangeSingle = recentCatalog.getSingle_piece_price_range();
                    holder.total_products_price_range.setText(data.get(position).getTotal_products() + " designs, " + "\u20B9" + priceRangeSingle + "/Pc.");
                }
            } else {
                //price_range
                String price_range = recentCatalog.getPrice_range();
                if (price_range != null) {
                    holder.total_products_price_range.setVisibility(View.VISIBLE);
                    if (price_range.contains("-")) {
                        String[] priceRangeMultiple = price_range.split("-");
                        holder.total_products_price_range.setText(data.get(position).getTotal_products() + " designs, " + " \u20B9" + priceRangeMultiple[0] + " - " + "\u20B9" + priceRangeMultiple[1] + "/Pc."  /*" , " + response_catalogMini.getTotal_products() + " Designs"*/);
                    } else {
                        String priceRangeSingle = price_range;
                        holder.total_products_price_range.setText(data.get(position).getTotal_products() + " designs, " + "\u20B9" + priceRangeSingle + "/Pc.");
                    }
                } else {
                    holder.total_products_price_range.setVisibility(View.GONE);
                }
            }




            holder.title_category.setText(data.get(position).getTitle() + ", " + data.get(position).getCategory_name());
            StaticFunctions.loadFresco(context, data.get(position).getImage().getThumbnail_small(), holder.thumbnail);

            if (StaticFunctions.checkCatalogIsInCart(context, recentCatalog.getId())) {
                holder.btn_buy_now.setText("GO TO CART");
            } else {
                holder.btn_buy_now.setText("ADD TO CART");
            }

            holder.btn_buy_now.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (UserInfo.getInstance(context).isGuest()) {
                        StaticFunctions.ShowRegisterDialog(context,"Home Page");
                        return;
                    }
                    if (holder.btn_buy_now.getText().toString().contains("GO")) {
                        try {
                            SharedPreferences preferences = context.getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
                            int cart_count = preferences.getInt("cartcount", 0);
                            Application_Singleton.CONTAINER_TITLE = "My Cart (" + cart_count + " items)";
                            Application_Singleton.CONTAINERFRAG = new Fragment_MyCart();
                            StaticFunctions.switchActivity(fragment.getActivity(), OpenContainer.class);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        CatalogMinified response = new CatalogMinified(recentCatalog.getProduct_id(), "catalog", recentCatalog.isBuyer_disabled(), recentCatalog.getTitle(), recentCatalog.getBrand_name(), recentCatalog.getView_permission());
                        response.setIs_supplier_approved(recentCatalog.getIs_supplier_approved());
                        response.setSupplier(recentCatalog.getSupplier());
                        response.setFull_catalog_orders_only(recentCatalog.getFull_catalog_orders_only());
                        response.setSupplier_name(recentCatalog.getSupplier_name());
                        response.setSupplier_chat_user(recentCatalog.getSupplier_chat_user());
                        response.setBuyer_disabled(recentCatalog.isBuyer_disabled());
                        response.setSupplier_disabled(recentCatalog.isSupplier_disabled());
                        response.setPrice_range(recentCatalog.getPrice_range());
                        response.setSupplier_details(recentCatalog.getSupplier_details());
                        response.setIs_owner(recentCatalog.is_owner());
                        response.setIs_addedto_wishlist(recentCatalog.getIs_addedto_wishlist());
                        response.setFromPublic(true);
                        if (recentCatalog.getSupplier_details() != null) {
                            response.setNear_by_sellers(recentCatalog.getSupplier_details().getNear_by_sellers());
                        }
                        Application_Singleton.selectedshareCatalog = response;
                        try {
                            CartHandler cartHandler = new CartHandler(((AppCompatActivity) context));
                            List<Integer> q = new ArrayList<>();
                            for (int i = 0; i < Integer.parseInt(recentCatalog.getTotal_products()); i++) {
                                q.add(1);
                            }
                            int a[] = new int[q.size()];
                            for (int i = 0; i < a.length; i++) {
                                a[i] = q.get(i);
                            }
                            cartHandler.setAddToCartCallbackListener(new CartHandler.AddToCartCallbackListener() {
                                @Override
                                public void onSuccess(CartProductModel response) {
                                    holder.btn_buy_now.setText("GO TO CART");
                                }

                                @Override
                                public void onFailure() {
                                    holder.btn_buy_now.setText("ADD TO CART");
                                }
                            });
                            cartHandler.addCatalogToCart(recentCatalog.getId(), "", fragment, "recent", null, holder.btn_buy_now, "Nan");
                            holder.btn_buy_now.setText("GO TO CART");
                            Application_Singleton.trackEvent("Add to cart", "Click", "From Recent");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            holder.card_seller_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("from", "Home Recent");
                    bundle.putString("product_id", recentCatalog.getId());
                  new NavigationUtils().navigateDetailPage(context,bundle);
                }
            });
        }
    }



    public void createPublicPurchaseOrder(CatalogMinified response_catalog, String supplier, String supplierName, boolean isSupplierApproved, boolean isTrusted) {

        //Fragment_CreatePurchaseOrderNew createOrderFrag = new Fragment_CreatePurchaseOrderNew();
        Fragment_CreatePurchaseOrderVersion2 createOrderFrag = new Fragment_CreatePurchaseOrderVersion2();
        Application_Singleton.selectedshareCatalog.setSupplier(supplier);
        Application_Singleton.selectedshareCatalog.setSupplier_name(supplierName);
        Application_Singleton.selectedshareCatalog.setIs_supplier_approved(isSupplierApproved);
        if (response_catalog.getEavdata() != null) {
            Application_Singleton.selectedshareCatalog.setEavdata(response_catalog.getEavdata());
        }
        Application_Singleton.selectedshareCatalog.setIs_trusted_seller((isTrusted));
        Bundle bundle = new Bundle();
        bundle.putString("ordertype", "catalog");
        bundle.putString("ordervalue", response_catalog.getId());
        bundle.putString("selling_company", supplier);
        bundle.putBoolean("is_public", true);
        bundle.putBoolean("is_supplier_approved", isSupplierApproved);
        bundle.putString("full_catalog_order", response_catalog.getFull_catalog_orders_only());
        createOrderFrag.setArguments(bundle);
        Application_Singleton.CONTAINER_TITLE = context.getResources().getString(R.string.new_purchase_order);
        Application_Singleton.CONTAINERFRAG = createOrderFrag;
        Intent intent = new Intent(context, OpenContainer.class);
        context.startActivity(intent);
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.card_seller_container)
        CardView card_seller_container;
        @BindView(R.id.title_category)
        TextView title_category;


        @BindView(R.id.divider)
        View divider;

        @BindView(R.id.total_products_price_range)
        TextView total_products_price_range;

        @BindView(R.id.btn_buy_now)
        Button btn_buy_now;

        @BindView(R.id.thumbnail)
        SimpleDraweeView thumbnail;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }


}
