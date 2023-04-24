package com.wishbook.catalog.commonadapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeWishListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context context;
    private ArrayList<?> items;
    private int type;
    private Fragment fragment;

    public static int DEEPLINKWISHBOOK = 1;


    public HomeWishListAdapter(Context context, ArrayList<?> items, int type, Fragment fragment) {
        this.context = context;
        this.items = items;
        this.type = type;
        this.fragment = fragment;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == DEEPLINKWISHBOOK) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_wishlist_item, parent, false);
            return new WishListViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        Log.e("WISH", "onBindViewHolder: " + position);
        if (holder instanceof WishListViewHolder) {
            if (type == DEEPLINKWISHBOOK) {
                Log.e("WISH", "onBindViewHolder 1 : " + position);
                if (items.get(0) instanceof Response_catalogMini) {
                    Log.e("WISH", "onBindViewHolder 2 : " + position);
                    final Response_catalogMini wish = (Response_catalogMini) items.get(position);
                    if (position == 0) {
                        ((HomeWishListAdapter.WishListViewHolder) holder).spacer.setVisibility(View.VISIBLE);
                    } else {
                        ((HomeWishListAdapter.WishListViewHolder) holder).spacer.setVisibility(View.GONE);
                    }
                    if (wish.getImage().getThumbnail_small() != null) {
                        StaticFunctions.loadFresco(context, wish.getImage().getThumbnail_small(), ((WishListViewHolder) holder).item_img);
                    }
                    if(wish.getBrand_name()!=null)
                        ((WishListViewHolder) holder).txt_brand_name.setText(wish.getBrand_name());

                    if (wish.getSupplier_details() != null) {
                        if (wish.getSupplier_details().getTotal_suppliers() != 0) {
                            if (wish.getSupplier_details().getTotal_suppliers() == 1) {
                                if (wish.getSupplier_name() != null && !wish.getSupplier_name().equals("") && !wish.getSupplier_name().equals("null")) {
                                    ((WishListViewHolder) holder).txt_seller_name.setVisibility(View.INVISIBLE);
                                    //((WishListViewHolder) holder).txt_seller_name.setText("Sold by : " + wish.getSupplier_name());
                                }
                            } else {
                                // multiple Seller
                                ((WishListViewHolder) holder).txt_seller_name.setVisibility(View.INVISIBLE);
                                //((WishListViewHolder) holder).txt_seller_name.setText("Sold by : "+wish.getSupplier_details().getTotal_suppliers() + " Sellers");
                            }
                        } else {
                            ((WishListViewHolder) holder).txt_seller_name.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        if (wish.getSupplier_name() != null && !wish.getSupplier_name().equals("") && !wish.getSupplier_name().equals("null")) {
                            ((WishListViewHolder) holder).txt_seller_name.setVisibility(View.INVISIBLE);
                            // ((WishListViewHolder) holder).txt_seller_name.setText("Sold by : " + wish.getSupplier_name());
                        } else {
                            ((WishListViewHolder) holder).txt_seller_name.setVisibility(View.INVISIBLE);
                        }
                    }

                    //full catalog only
                    if (wish.getFull_catalog_orders_only() != null && wish.getFull_catalog_orders_only().equals("true")) {
                        ((WishListViewHolder) holder).txt_only_full_catalog.setText("Only full catalog for sale");
                    } else {
                        ((WishListViewHolder) holder).txt_only_full_catalog.setText("Single piece available");
                    }

                    if(wish.getProduct_type()!=null &&
                            !wish.getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN)
                            && wish.getFull_catalog_orders_only().equalsIgnoreCase("false") &&
                             wish.getTotal_products()!=null && Integer.parseInt(wish.getTotal_products()) == 1) {
                        // show single pc price range
                        ((WishListViewHolder) holder).txt_price.setVisibility(View.VISIBLE);
                        if (wish.getSingle_piece_price_range().contains("-")) {
                            String[] priceRangeMultiple = wish.getSingle_piece_price_range().split("-");
                            ((WishListViewHolder) holder).txt_price.setText(wish.getTotal_products() + " designs, " + " \u20B9" + priceRangeMultiple[0] + " - " + "\u20B9" + priceRangeMultiple[1] + "/Pc."  /*" , " + response_catalogMini.getTotal_products() + " Designs"*/);
                        } else {
                            String priceRangeSingle = wish.getSingle_piece_price_range();
                            ((WishListViewHolder) holder).txt_price.setText(wish.getTotal_products() + " designs, " + "\u20B9" + priceRangeSingle + "/Pc.");
                        }
                    } else {
                        //price_range
                        String price_range = wish.getPrice_range();
                        if (price_range != null) {
                            ((WishListViewHolder) holder).txt_price.setVisibility(View.VISIBLE);
                            if (price_range.contains("-")) {
                                String[] priceRangeMultiple = price_range.split("-");
                                ((WishListViewHolder) holder).txt_price.setText(" \u20B9" + priceRangeMultiple[0] + " - " + "\u20B9" + priceRangeMultiple[1] + "/Pc."  /*" , " + response_catalogMini.getTotal_products() + " Designs"*/);
                            } else {
                                String priceRangeSingle = price_range;
                                ((WishListViewHolder) holder).txt_price.setText("\u20B9" + priceRangeSingle + "/Pc.");
                            }
                        } else {
                            ((WishListViewHolder) holder).txt_price.setVisibility(View.GONE);
                        }
                    }


                    if (wish.getTotal_products() != null) {
                        ((WishListViewHolder) holder).txt_number_design.setVisibility(View.VISIBLE);
                        ((WishListViewHolder) holder).txt_number_design.setText(wish.getTotal_products() + " Designs");
                    } else {
                        ((WishListViewHolder) holder).txt_number_design.setVisibility(View.GONE);
                    }
                    ((WishListViewHolder) holder).txt_catalog_name.setText(wish.getTitle());

                    if (StaticFunctions.checkCatalogIsInCart(context, wish.getId())) {
                        ((WishListViewHolder) holder).btn_purchase.setText("GO TO CART");
                    } else {
                        ((WishListViewHolder) holder).btn_purchase.setText("ADD TO CART");
                    }


                    ((WishListViewHolder) holder).btn_purchase.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (UserInfo.getInstance(context).isGuest()) {
                                StaticFunctions.ShowRegisterDialog(context,"Home Page");
                                return;
                            }

                            if (((WishListViewHolder) holder).btn_purchase.getText().toString().contains("GO")) {
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
                                    CartHandler cartHandler = new CartHandler(((AppCompatActivity) context));
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
                                            ((WishListViewHolder) holder).btn_purchase.setText("GO TO CART");
                                        }

                                        @Override
                                        public void onFailure() {
                                            ((WishListViewHolder) holder).btn_purchase.setText("ADD TO CART");
                                        }
                                    });
                                    cartHandler.addCatalogToCart(response.getId(), "", fragment, "summary", null, ((WishListViewHolder) holder).btn_purchase, "Nan");
                                    ((WishListViewHolder) holder).btn_purchase.setText("GO TO CART");
                                    Application_Singleton.trackEvent("Add to cart", "Click", "From Wishlist");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            /*if (wish.getSupplier_details() != null) {
                                if (wish.getSupplier_details().getTotal_suppliers() > 1) {
                                   fragment.startActivityForResult(new Intent(context, ActivityMultipleSeller.class)
                                            .putExtra("catalog_name", wish.getTitle())
                                            .putExtra("catalog_id", wish.getId())
                                            .putExtra("catalog_price", wish.getPrice_range())
                                            .putExtra("action", Fragment_CatalogsGallery.PURCHASE), Application_Singleton.SELECT_SUPPLIER_REQUEST_CODE);
                                } else {
                                    createPurchaseOrder(response, response.getSupplier());
                                }
                            } else {
                                createPurchaseOrder(response, response.getSupplier());
                            }*/

                            }
                        }
                    });

                    ((HomeWishListAdapter.WishListViewHolder) holder).setClickListener(new HomeCategoryItemsAdapter.ItemClickListener() {
                        @Override
                        public void onClick(View view, int position, boolean isLongClick) {

                            Bundle bundle = new Bundle();
                            bundle.putString("from", "Home Wishlist");
                            bundle.putString("product_id", wish.getId());
                            new NavigationUtils().navigateDetailPage(context,bundle);
                        }
                    });

                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    @Override
    public int getItemViewType(int position) {
        return type;
    }


    public interface ItemClickListener {
        void onClick(View view, int position, boolean isLongClick);
    }

    public void createPurchaseOrder(CatalogMinified response_catalog, String supplier) {
        if (response_catalog.getPush_user_id() != null) {
            // private shared catalog
            Application_Singleton.CONTAINER_TITLE = context.getResources().getString(R.string.new_purchase_order);
            //Fragment_CreatePurchaseOrderNew purchase = new Fragment_CreatePurchaseOrderNew();
            Fragment_CreatePurchaseOrderVersion2 purchase = new Fragment_CreatePurchaseOrderVersion2();

            Application_Singleton.selectedshareCatalog.setSupplier(supplier);
            Application_Singleton.selectedshareCatalog.setIs_product_price_null(response_catalog.getIs_supplier_approved());
            Application_Singleton.selectedshareCatalog.setFull_catalog_orders_only(response_catalog.getFull_catalog_orders_only());
            Application_Singleton.selectedshareCatalog.setIs_trusted_seller((response_catalog.getIs_trusted_seller()));
            //Fragment_CreatePurchaseOrderNew purchase = new Fragment_CreatePurchaseOrderNew();
            Bundle bundle = new Bundle();
            if (Application_Singleton.selectedshareCatalog != null) {

                bundle.putString("ordertype", Application_Singleton.selectedshareCatalog.getType());
                bundle.putString("ordervalue", Application_Singleton.selectedshareCatalog.getId());
            }
            purchase.setArguments(bundle);
            Application_Singleton.CONTAINERFRAG = purchase;
            Intent intent = new Intent(context, OpenContainer.class);
            context.startActivity(intent);
        } else {
            // for public catalog
            createPublicPurchaseOrder(response_catalog, supplier, response_catalog.getSupplier_name(), response_catalog.getIs_supplier_approved(), response_catalog.getIs_trusted_seller());
        }
    }

    public void createPublicPurchaseOrder(CatalogMinified response_catalog, String supplier, String supplierName, boolean isSupplierApproved, boolean isTrusted) {

        //Fragment_CreatePurchaseOrderNew createOrderFrag = new Fragment_CreatePurchaseOrderNew();
        Application_Singleton.selectedshareCatalog = new CatalogMinified();
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

    public class WishListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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