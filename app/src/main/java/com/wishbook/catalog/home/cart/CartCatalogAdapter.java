package com.wishbook.catalog.home.cart;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.DateUtils;
import com.wishbook.catalog.Utils.NavigationUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.CartCatalogModel;
import com.wishbook.catalog.commonmodels.CartProductModel;
import com.wishbook.catalog.commonmodels.DeleteCartItem;
import com.wishbook.catalog.home.orderNew.details.Activity_MultipleSeller;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class CartCatalogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static int qty;
    private TextView pre_discount, post_discount;
    private int grand_total;
    private boolean flag = true;
    private Context context;
    public CartCatalogModel fullList;
    private ArrayList<CartCatalogModel.Catalogs> cartList;
    private RecyclerView recyclerView_catalog;
    private RelativeLayout my_cart_layout;
    private AdapterListener mListener;
    private Fragment_MyCart fragment_myCart;
    private int HEADER = 0;
    private int FOOTER = 2;

    private static String TAG = CartCatalogAdapter.class.getSimpleName();

    public static int count;

    CartCatalogAdapter(@NonNull ArrayList<CartCatalogModel.Catalogs> cartList, Context context, @NonNull RecyclerView catalog_item, @NonNull RelativeLayout my_cart_layout, @NonNull Fragment_MyCart fragment_myCart) {
        this.context = context;
        this.recyclerView_catalog = catalog_item;
        if (cartList != null) {
            setCartList(cartList);
        }
        this.my_cart_layout = my_cart_layout;
        this.fragment_myCart = fragment_myCart;


        try {
            SharedPreferences preferences = context.getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
            count = preferences.getInt("cartcount", 0);
        } catch (Exception e) {
            e.printStackTrace();
            count = 0;
        }
    }

    public static void collapse(final @NonNull View v, int pos, @NonNull RecyclerView recyclerView) {
        v.setVisibility(View.GONE);

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) throws NullPointerException {
        if (viewType == HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_cart_header, parent, false);
            try {
                pre_discount = view.findViewById(R.id.pre_discount);
                post_discount = view.findViewById(R.id.post_discount);

                if (cartList.size() == 0) {
                    view.setVisibility(View.GONE);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return new HeaderViewHolder(view);
        }/* else if(viewType == FOOTER){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.catalog_footer_item, parent, false);
            if (cartList.size() == 0) {
                view.setVisibility(View.GONE);
            }
            return new FooterViewHolder(view);
        } */ else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_recycler_mycart, parent, false);
            return new CartCatalogHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        DecimalFormat decimalFormat = new DecimalFormat("#0.##");
        try {
            if (holder instanceof CartCatalogHolder) {
                ArrayList<CartCatalogModel.Products> products;
                CartProductAdapter cartProductAdapter;
                CartCatalogModel.Catalogs catalog = cartList.get(position - 1);
                if (!catalog.isIs_full_catalog()) {
                    ((CartCatalogHolder) holder).cart_catalog_name.setText(catalog.getProducts().get(0).getProduct_title());
                    if (catalog.getProducts().get(0).getNote() != null) {
                        ((CartCatalogHolder) holder).cart_catalog_size.setText(catalog.getProducts().get(0).getNote());
                    } else {
                        ((CartCatalogHolder) holder).size_layout.setVisibility(View.GONE);
                    }
                    if (catalog.getSellers().size() > 1) {
                        ((CartCatalogHolder) holder).btn_catalog_change.setVisibility(View.VISIBLE);
                    }
                    ((CartCatalogHolder) holder).cart_catalog_amount.setTextColor(context.getResources().getColor(R.color.purchase_medium_gray));
                    ((CartCatalogHolder) holder).cart_catalog_amount.setText("Single design only");
                    String temp = "<font color='#777777' size='16'>From </font><font color='#3a3a3a' size='16'>" + catalog.getCatalog_title() + "</font>";
                    ((CartCatalogHolder) holder).cart_catalog_desgin.setText(Html.fromHtml(temp), TextView.BufferType.SPANNABLE);

                    temp = catalog.getProducts().get(0).getRate();
                    try {
                        temp = String.valueOf(temp).split("\\.")[0];
                        ((CartCatalogHolder) holder).cart_catalog_priceperpcs.setText("\u20B9 " + temp + "/Pc.");
                    } catch (Exception e) {
                        e.printStackTrace();
                        ((CartCatalogHolder) holder).cart_catalog_priceperpcs.setText("\u20B9 " + catalog.getProducts().get(0).getRate() + "/Pc.");
                    }

                    if(catalog.getProducts().get(0).getMwp_single_price() > 0) {
                        ((CartCatalogHolder) holder).txt_mwp_single_price.setVisibility(View.VISIBLE);
                        ((CartCatalogHolder) holder).txt_mwp_single_price.setText("\u20B9 " + catalog.getProducts().get(0).getMwp_single_price() + "/Pc.");
                    } else {
                        ((CartCatalogHolder) holder).txt_mwp_single_price.setVisibility(View.GONE);
                    }




                    ((CartCatalogHolder) holder).layout_catalog_details.setVisibility(View.INVISIBLE);
                    if(catalog.getProducts().get(0).getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN)) {
                        ((CartCatalogHolder) holder).number_txt.setText("No. of sets");
                        ((CartCatalogHolder) holder).cart_catalog_setpcs.setVisibility(View.VISIBLE);
                        String no_of_design = String.valueOf(cartList.get(position - 1).getProducts().get(0).getNo_of_pcs());
                        ((CartCatalogHolder) holder).cart_catalog_setpcs.setText("1 Set = " + no_of_design + " Pcs.");
                    } else {
                        ((CartCatalogHolder) holder).number_txt.setText("No. of pcs.");
                        ((CartCatalogHolder) holder).cart_catalog_setpcs.setVisibility(View.GONE);
                    }

                    ((CartCatalogHolder) holder).cart_catalog_fullprice.setText("\u20B9 " + (int) Double.parseDouble(catalog.getProducts().get(0).getAmount()) + ".0");
                    ((CartCatalogHolder) holder).cart_catalog_after_discount_price.setText("\u20B9 " + String.valueOf((int) Double.parseDouble(catalog.getProducts().get(0).getAmount()) - (int) Double.parseDouble(catalog.getProducts().get(0).getDiscount())) + ".0");

                    int temp_qunatity = (cartList.get(holder.getAdapterPosition() - 1).getProducts().get(0).getQuantity());
                    double total_amount = ((Double.parseDouble(cartList.get(position - 1).getCatalog_total_amount()) / (temp_qunatity)) * temp_qunatity);
                    ((CartCatalogHolder) holder).cart_catalog_discountprice.setText("\u20B9 " + decimalFormat.format(total_amount));

                    ((CartCatalogHolder) holder).discount_percent.setText(String.valueOf((int) (catalog.getProducts().get(0).getDiscount_percent())) + "% Discount");
                    StaticFunctions.loadFresco(context, catalog.getProducts().get(0).getProduct_image(), ((CartCatalogHolder) holder).cart_img);
                    ((CartCatalogHolder) holder).edit_qty.setText(String.valueOf(catalog.getProducts().get(0).getQuantity()));
                    cartList.get(position - 1).getProducts().get(0).setCount(catalog.getProducts().get(0).getQuantity());
                    ((CartCatalogHolder) holder).cart_catalog_seller.setText(catalog.getSelling_company_name());
                    if (catalog.getTrusted_seller()) {
                        ((CartCatalogHolder) holder).isTrustedseller.setVisibility(View.GONE);
                    } else {
                        ((CartCatalogHolder) holder).isTrustedseller.setVisibility(View.GONE);
                    }


                    if(catalog.getSingle_discount() > 0) {
                        ((CartCatalogHolder) holder).txt_full_single_discount.setVisibility(View.VISIBLE);
                        ((CartCatalogHolder) holder).txt_full_single_discount.setText(catalog.getSingle_discount() +"% off");
                    } else {
                        ((CartCatalogHolder) holder).txt_full_single_discount.setVisibility(View.GONE);
                    }



                } else {
                    cartList.get(position - 1).getProducts().get(0).setCount(catalog.getProducts().get(0).getQuantity());
                    String no_of_design = catalog.getTotal_products();
                    if (cartList.get(position - 1).getProducts().get(0).getProduct_type() != null
                            && cartList.get(position - 1).getProducts().get(0).getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN)) {
                        ((CartCatalogHolder) holder).layout_catalog_details.setVisibility(View.INVISIBLE);
                        StaticFunctions.loadFresco(context, catalog.getProducts().get(0).getProduct_image(), ((CartCatalogHolder) holder).cart_img);
                        ((CartCatalogHolder) holder).cart_catalog_amount.setVisibility(View.GONE);
                        no_of_design = String.valueOf(cartList.get(position - 1).getProducts().get(0).getNo_of_pcs());
                        String temp = "<font color='#3a3a3a' size='16'>" + catalog.getProducts().get(0).getProduct_title() +"</font>" + "<font color='#777777' size='16'> From </font><font color='#3a3a3a' size='16'>" + catalog.getCatalog_title() + "</font>";
                        ((CartCatalogHolder) holder).cart_catalog_name.setText(Html.fromHtml(temp), TextView.BufferType.SPANNABLE);
                    } else {
                        ((CartCatalogHolder) holder).cart_catalog_name.setText(catalog.getCatalog_title());
                        ((CartCatalogHolder) holder).layout_catalog_details.setVisibility(View.VISIBLE);
                        StaticFunctions.loadFresco(context, catalog.getCatalog_image(), ((CartCatalogHolder) holder).cart_img);
                        ((CartCatalogHolder) holder).cart_catalog_amount.setVisibility(View.VISIBLE);
                        ((CartCatalogHolder) holder).cart_catalog_amount.setTextColor(context.getResources().getColor(R.color.red));
                        ((CartCatalogHolder) holder).cart_catalog_amount.setText("Full Catalog");
                        no_of_design = catalog.getTotal_products();
                    }

                    if (catalog.getProducts().get(0).getNote() != null) {
                        ((CartCatalogHolder) holder).cart_catalog_size.setText(catalog.getProducts().get(0).getNote());
                    } else {
                        ((CartCatalogHolder) holder).size_layout.setVisibility(View.GONE);
                    }
                    if (catalog.getSellers().size() > 1) {
                        ((CartCatalogHolder) holder).btn_catalog_change.setVisibility(View.GONE);
                    }

                    if (Integer.parseInt(no_of_design) == 1) {
                        ((CartCatalogHolder) holder).cart_catalog_desgin.setText(no_of_design + " Design");
                    } else {
                        ((CartCatalogHolder) holder).cart_catalog_desgin.setText(no_of_design + " Designs");
                    }
                    ((CartCatalogHolder) holder).cart_catalog_setpcs.setText("1 Set = " + no_of_design + " Pcs.");
                    ((CartCatalogHolder) holder).cart_catalog_seller.setText(catalog.getSelling_company_name());


                    if (catalog.getTrusted_seller()) {
                        ((CartCatalogHolder) holder).isTrustedseller.setVisibility(View.VISIBLE);
                    } else {
                        ((CartCatalogHolder) holder).isTrustedseller.setVisibility(View.INVISIBLE);
                    }
                    ((CartCatalogHolder) holder).cart_catalog_priceperpcs.setText("\u20B9 " + catalog.getPrice_range() + "/Pc.");
                    if(catalog.getProducts().get(0).getMwp_single_price() > 0) {
                        ((CartCatalogHolder) holder).txt_mwp_single_price.setVisibility(View.VISIBLE);
                        ((CartCatalogHolder) holder).txt_mwp_single_price.setText("\u20B9 " + catalog.getProducts().get(0).getMwp_single_price() + "/Pc.");
                    } else {
                        ((CartCatalogHolder) holder).txt_mwp_single_price.setVisibility(View.GONE);
                    }
                    ((CartCatalogHolder) holder).discount_percent.setText(String.valueOf((int) (catalog.getProducts().get(0).getDiscount_percent())) + "% Discount");

                    products = catalog.getProducts();
                    cartProductAdapter = new CartProductAdapter(products, (AppCompatActivity) context, position - 1, ((CartCatalogHolder) holder).cart_catalog_amount.getText().toString());
                    if (catalog.getCartProductAdapter() == null) {
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
                        ((CartCatalogHolder) holder).recycler_products.setLayoutManager(mLayoutManager);
                        ((CartCatalogHolder) holder).recycler_products.setAdapter(cartProductAdapter);
                        catalog.setCartProductAdapter(cartProductAdapter);
                        ((CartProductAdapter) catalog.getCartProductAdapter()).setProductQTY(products.get(0).getQuantity());
                        cartProductAdapter.setProductChangeListener(new CartProductAdapter.ProductChangeListener() {
                            @Override
                            public void onChange(int prodcut_position) {
                                double price = ((CartProductAdapter) cartList.get(holder.getAdapterPosition() - 1).getCartProductAdapter()).getTotalPrice();
                                double discount = cartList.get(holder.getAdapterPosition() - 1).getProducts().get(0).getDiscount_percent();
                                if (discount == 0.0) {
                                    double total_amount = ((Double.parseDouble(cartList.get(holder.getAdapterPosition() - 1).getCatalog_total_amount()) / ((cartList.get(holder.getAdapterPosition() - 1).getProducts().get(0).getQuantity()))) * (cartList.get(holder.getAdapterPosition() - 1).getProducts().get(0).getQuantity()));
                                    ((CartCatalogHolder) holder).cart_catalog_discountprice.setText("\u20B9 " + total_amount);
                                    ((CartCatalogHolder) holder).cart_catalog_after_discount_price.setText("\u20B9 " + price);
                                    ((CartCatalogHolder) holder).cart_catalog_fullprice.setText("\u20B9 " + decimalFormat.format(price));
                                } else {
                                    double discount_price = (price * discount) / 100;
                                    discount_price = price - discount_price;
                                    ((CartCatalogHolder) holder).cart_catalog_fullprice.setText("\u20B9 " + decimalFormat.format(price));
                                    double total_amount = ((Double.parseDouble(cartList.get(holder.getAdapterPosition() - 1).getCatalog_total_amount()) / ((cartList.get(holder.getAdapterPosition() - 1).getProducts().get(0).getQuantity()))) * (cartList.get(holder.getAdapterPosition() - 1).getProducts().get(0).getQuantity()));
                                    ((CartCatalogHolder) holder).cart_catalog_discountprice.setText("\u20B9 " + total_amount);
                                }
                            }
                        });
                        ((CartCatalogHolder) holder).edit_qty.setText(String.valueOf(cartList.get(position - 1).getProducts().get(0).getQuantity()));
                        ((CartProductAdapter) catalog.getCartProductAdapter()).setProductQTY(Integer.parseInt(String.valueOf(cartList.get(position - 1).getProducts().get(0).getQuantity())));
                        double price = ((CartProductAdapter) catalog.getCartProductAdapter()).getTotalPrice();
                        double discount = catalog.getProducts().get(0).getDiscount_percent();
                        if (discount == 0.0) {
                            int temp_qunatity = (cartList.get(holder.getAdapterPosition() - 1).getProducts().get(0).getQuantity());
                            double total_amount = ((Double.parseDouble(cartList.get(position - 1).getCatalog_total_amount()) / (temp_qunatity)) * temp_qunatity);
                            ((CartCatalogHolder) holder).cart_catalog_discountprice.setText("\u20B9 " + decimalFormat.format(total_amount));
                            ((CartCatalogHolder) holder).cart_catalog_fullprice.setText("\u20B9 " + decimalFormat.format(price));
                        } else {
                            double discount_price = (price * discount) / 100;
                            discount_price = price - discount_price;
                            ((CartCatalogHolder) holder).cart_catalog_fullprice.setText("\u20B9 " + decimalFormat.format(price));
                            int temp_qunatity = (cartList.get(holder.getAdapterPosition() - 1).getProducts().get(0).getQuantity());
                            double total_amount = ((Double.parseDouble(cartList.get(position - 1).getCatalog_total_amount()) / (temp_qunatity)) * temp_qunatity);
                            ((CartCatalogHolder) holder).cart_catalog_discountprice.setText("\u20B9 " + decimalFormat.format(total_amount));
                            ((CartCatalogHolder) holder).cart_catalog_after_discount_price.setText("\u20B9 " + decimalFormat.format(discount_price));
                        }

                        if (position != 0) {
                            if (cartList.get(position - 1).getExpanded()) {
                                ((CartCatalogHolder) holder).txt_details.setText("Hide Details");
                                ((CartCatalogHolder) holder).btn_catalog_details.setRotation(180);
                                expand(((CartCatalogHolder) holder).recycler_products);
                            } else {
                                ((CartCatalogHolder) holder).txt_details.setText("Item Details");
                                ((CartCatalogHolder) holder).btn_catalog_details.setRotation(360);
                                collapse(((CartCatalogHolder) holder).recycler_products, position - 1, recyclerView_catalog);
                            }
                        }
                    } else {
                        ((CartCatalogHolder) holder).edit_qty.setText(String.valueOf(cartList.get(position - 1).getProducts().get(0).getQuantity()));
                        ((CartProductAdapter) catalog.getCartProductAdapter()).setProductQTY(Integer.parseInt(String.valueOf(cartList.get(position - 1).getProducts().get(0).getQuantity())));
                    }



                    if(catalog.getFull_discount() > 0) {
                        ((CartCatalogHolder) holder).txt_full_single_discount.setVisibility(View.VISIBLE);
                        ((CartCatalogHolder) holder).txt_full_single_discount.setText(catalog.getFull_discount() +"% off");
                    } else {
                        ((CartCatalogHolder) holder).txt_full_single_discount.setVisibility(View.GONE);
                    }
                }


                //mListener.onUpdateGrandTotal(String.valueOf(decimalFormat.format(fullList.getTotal_amount() - Double.parseDouble(fullList.getShipping_charges()))));
                double discount = catalog.getProducts().get(0).getDiscount_percent();
                if (discount == 0.0) {
                    ((CartCatalogHolder) holder).cart_catalog_fullprice.setVisibility(View.INVISIBLE);
                    ((CartCatalogHolder) holder).cart_catalog_after_discount_price.setVisibility(View.INVISIBLE);
                    ((CartCatalogHolder) holder).discount_percent.setVisibility(View.INVISIBLE);
                }
                ((CartCatalogHolder) holder).cart_catalog_fullprice.setPaintFlags(((CartCatalogHolder) holder).cart_catalog_fullprice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                initListener(((CartCatalogHolder) holder), position);
                if (catalog.isReady_to_ship()) {
                    ((CartCatalogHolder) holder).linear_pre_order.setVisibility(View.GONE);
                    ((CartCatalogHolder) holder).txt_ready_to_dispatch.setVisibility(View.GONE);
                } else {
                    ((CartCatalogHolder) holder).linear_pre_order.setVisibility(View.VISIBLE);
                    try {
                        String converted_date = DateUtils.changeDateFormat(StaticFunctions.SERVER_POST_FORMAT, StaticFunctions.CLIENT_DISPLAY_FORMAT2, catalog.getDispatch_date());
                        ((CartCatalogHolder) holder).txt_dispatch_date.setText(converted_date);
                    } catch (Exception e) {
                        ((CartCatalogHolder) holder).txt_dispatch_date.setText(catalog.getDispatch_date());
                        e.printStackTrace();
                    }

                    ((CartCatalogHolder) holder).txt_ready_to_dispatch.setVisibility(View.GONE);
                }

                if(catalog.getProducts()!=null && catalog.getProducts().get(0).getProduct_type().equalsIgnoreCase(Constants.PRODUCT_TYPE_SCREEN)) {
                    ((CartCatalogHolder) holder).linear_pre_order.setVisibility(View.GONE);
                    ((CartCatalogHolder) holder).txt_ready_to_dispatch.setVisibility(View.GONE);
                }

            } else if (holder instanceof HeaderViewHolder) {
                if (Application_Singleton.configResponse.get(0).getValue() != null) {
                    try {
                        for (int i = 0; i < Application_Singleton.configResponse.size(); i++) {
                            if (Application_Singleton.configResponse.get(i).getKey().equalsIgnoreCase(Constants.WB_CART_CASHBACK_HEADER)) {
                                if (Application_Singleton.configResponse.get(i).getValue().equals("html")) {
                                    post_discount.setText(Html.fromHtml(Application_Singleton.configResponse.get(i).getDisplay_text()), TextView.BufferType.SPANNABLE);
                                } else {
                                    post_discount.setText(Application_Singleton.configResponse.get(i).getValue());
                                }
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                holder.itemView.setLayoutParams(new LinearLayout.LayoutParams(0,0));

            } else if (holder instanceof FooterViewHolder) {
                ((FooterViewHolder) holder).btn_continue_shopping.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((Activity) context).finish();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return cartList.size() + 1;
    }

    public ArrayList<CartCatalogModel.Catalogs> getCartList() {
        return cartList;
    }

    public void setCartList(ArrayList<CartCatalogModel.Catalogs> cartList) {
        this.cartList = cartList;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEADER;
        } /*else if(position == cartList.size()+1){
            return FOOTER;
        }*/ else {
            return 1;
        }
    }

    public void initListener(final @NonNull CartCatalogHolder holder, final int position) {


        holder.cart_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cartList.get(position - 1).getIs_full_catalog()) {
                    String catalog_bundleId;
                    if (cartList.get(position - 1).getProduct_id() != null)
                        catalog_bundleId = cartList.get(position - 1).getProduct_id();
                    else
                        catalog_bundleId = cartList.get(position - 1).getCatalog_id();

                    Bundle bundle = new Bundle();
                    bundle.putString("from","My Cart");
                    bundle.putString("product_id",catalog_bundleId);
                    new NavigationUtils().navigateDetailPage(context,bundle);
                } else {
                    ((AppCompatActivity) context).startActivity(new Intent((AppCompatActivity) context, CartProductView.class)
                            .putExtra("id", cartList.get(position - 1).getProducts().get(0).getProduct()));
                }
            }
        });


        holder.btn_catalog_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    deleteCartItem(holder.getAdapterPosition() - 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        holder.layout_catalog_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (holder.txt_details.getText().toString().contains("Item")) {

                        cartList.get(position - 1).setExpanded(true);
                        holder.txt_details.setText("Hide Details");
                        holder.btn_catalog_details.setRotation(180);
                        expand(holder.recycler_products);
                    } else {
                        cartList.get(position - 1).setExpanded(false);
                        collapse(holder.recycler_products, position - 1, recyclerView_catalog);
                        holder.txt_details.setText("Item Details");
                        holder.btn_catalog_details.setRotation(360);
                        recyclerView_catalog.smoothScrollToPosition(position);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        holder.btn_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    qty = Integer.parseInt(holder.edit_qty.getText().toString());
                    holder.edit_qty.setText("" + (++qty));
                    cartItemQtyHanlder(holder.getAdapterPosition() - 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });


        holder.btn_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (count > 1) {
                        SharedPreferences preferences = context.getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
                        preferences.edit().putInt("cartcount", count).commit();
                    }
                    qty = Integer.parseInt(holder.edit_qty.getText().toString());
                    if((qty - 1) > 0){
                        holder.edit_qty.setText("" + (--qty));
                        cartItemQtyHanlder(holder.getAdapterPosition() - 1);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        holder.btn_catalog_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    fragment_myCart.patchData((AppCompatActivity) context, false, "cart");
                    Activity_MultipleSeller activity_multipleSeller = new Activity_MultipleSeller();
                    Intent i = new Intent(context, activity_multipleSeller.getClass())
                            .putExtra("position", cartList.get(position - 1).toString())
                            .putExtra("catalogId", cartList.get(position - 1).getCatalog_id())
                            .putExtra("isFullCatalog", cartList.get(position - 1).getIs_full_catalog())
                            .putExtra("sellers", cartList.get(position - 1).getSellers())
                            .putExtra("current_seller", cartList.get(position - 1).getSelling_company_id())
                            .putExtra("quantity", cartList.get(position - 1).getProducts())
                            .putExtra("price", cartList.get(position - 1).getProducts().get(0).getRate())
                            .putExtra("note", cartList.get(position - 1).getProducts().get(0).getNote())
                            .putExtra("productId", cartList.get(position - 1).getProducts().get(0).getProduct());
                    if (cartList.get(position - 1).getProducts().get(0).getNote() != null) {
                        i.putExtra("note", cartList.get(position - 1).getProducts().get(0).getNote());
                    } else {
                        i.putExtra("note", "");
                    }
                    fragment_myCart.startActivityForResult(i, Fragment_MyCart.CHANGE_SUPPLIER_REQUEST_CODE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


     /*   holder.change_size.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    SelectSizeBottomSheet bottomSheetDialog = SelectSizeBottomSheet.getInstance1(cartList.get(position - 1).getCatalog_eavdata(), cartList.get(position - 1).getProducts().get(0).getNote());
                    bottomSheetDialog.show(fragment_myCart.getFragmentManager(), "Custom Bottom Sheet");
                    bottomSheetDialog.setDismissListener(new SelectSizeBottomSheet.DismissListener() {
                        @Override
                        public void onDismiss(String type) {

                            if (type.contains("continue")) {
                                String size = type.substring(type.indexOf(":") + 1);
                                ((CartCatalogHolder) holder).cart_catalog_size.setText(size);


                                for (int i = 0; i < cartList.get(position - 1).getProducts().size(); i++) {
                                    cartList.get(position - 1).getProducts().get(i).setNote(size);
                                }
                                fragment_myCart.patchData((AppCompatActivity) context, false, "cart");
                            }

                        }


                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });*/
    }

    public void deleteCartItem(final int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View alertLayout = inflater.inflate(R.layout.dialog_cart_delete, null);
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setView(alertLayout);
        alert.setCancelable(false);
        alert.setNegativeButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                try {

                    final CartCatalogModel.Catalogs temp = cartList.get(position);
                    fragment_myCart.showProgress();
                    DeleteCartItem deleteCartItem = new DeleteCartItem();
                    ArrayList<Integer> item_ids = new ArrayList<>();
                    for (int i = 0; i < temp.getProducts().size(); i++) {
                        item_ids.add(Integer.parseInt(temp.getProducts().get(i).getId()));
                    }
                    deleteCartItem.setItem_ids(item_ids);
                    CartHandler cartHandler = new CartHandler((AppCompatActivity) context);
                    cartHandler.deleteCartItem(deleteCartItem);
                    try {
                        SharedPreferences preferences = context.getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
                        count = preferences.getInt("cartcount", 0);
                        StaticFunctions.deleteCartData(context, temp.getProduct_id(), temp.isIs_full_catalog());
                        int counttoremove = temp.getProducts().get(0).getCount();
                        count = count - counttoremove;
                        if (count < 0) {
                            count = 0;
                        }
                        preferences.edit().putInt("cartcount", count).commit();
                        fragment_myCart.refreshCart();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        alert.setPositiveButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        final AlertDialog dialog = alert.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.purchase_medium_gray));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.color_primary));

            }
        });
        dialog.show();

    }

    public void cartItemQtyHanlder(int position) {
        if (cartList.get(position).getProducts().get(0).getQuantity() > -1) {
            int updatedQty = qty - cartList.get(position).getProducts().get(0).getQuantity();
            count = count + updatedQty;
            SharedPreferences preferences = context.getSharedPreferences("wishbookprefs", Context.MODE_PRIVATE);
            preferences.edit().putInt("cartcount", count).commit();
        }

        if (qty == 0) {
            deleteCartItem(position);
            return;
        }

        List<CartProductModel.Items> items = new ArrayList<>();
        CartProductModel.Items cartItem = null;
        for (CartCatalogModel.Products product : cartList.get(position).getProducts()) {

            if (cartItem != null && cartItem.getProduct().equals(product.getProduct())) {
                Log.e(TAG, "cartItemQtyHanlder: ====> Equals bundleProduct");
                //cartItem.setQuantity(String.valueOf(Integer.parseInt(cartItem.getQuantity()) + qty));
                items.clear();
            }

            cartItem = new CartProductModel.Items(product.getRate(), 0 + "", product.getProduct(), product.getIs_full_catalog(), product.getNote());


            if (product.getNo_of_pcs() > 0) {
                Log.e(TAG, "cartItemQtyHanlder: ====> no of pcs :" + product.getNo_of_pcs());
                cartItem.setQuantity(String.valueOf(qty * product.getNo_of_pcs()));
            } else {
                Log.e(TAG, "cartItemQtyHanlder: ====> no of pcs else");
                cartItem.setQuantity(String.valueOf(qty));
            }


            items.add(cartItem);
        }


        CartProductModel products = new CartProductModel(items);
        products.setFinalize(false);
        products.setAdd_quantity(false);
        fragment_myCart.patchQuantity((AppCompatActivity) context, products, position);

    }

    public void expand(final @NonNull View v) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();
        v.getLayoutParams().height = 1;

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1 ? ViewGroup.LayoutParams.WRAP_CONTENT : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
                v.setVisibility(View.VISIBLE);
                recyclerView_catalog.post(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView_catalog.scrollTo(0, 5);
                    }
                });
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
        v.setVisibility(View.VISIBLE);
    }

    public void setListener(@NonNull AdapterListener listener) {
        this.mListener = listener;
    }

    public interface AdapterListener {
        void onUpdateGrandTotal(@NonNull String name);
    }

    public class CartCatalogHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.btn_catalog_delete)
        ImageView btn_catalog_delete;

        @BindView(R.id.txt_details)
        TextView txt_details;

        @BindView(R.id.btn_catalog_details)
        ImageView btn_catalog_details;
        @BindView(R.id.isTrustedseller)
        ImageView isTrustedseller;
        @BindView(R.id.layout_catalog_details)
        LinearLayout layout_catalog_details;

        @BindView(R.id.cart_catalog_name)
        TextView cart_catalog_name;

        @BindView(R.id.cart_catalog_amount)
        TextView cart_catalog_amount;

        @BindView(R.id.cart_catalog_desgin)
        TextView cart_catalog_desgin;

        @BindView(R.id.cart_catalog_priceperpcs)
        TextView cart_catalog_priceperpcs;

        @BindView(R.id.btn_catalog_change)
        TextView btn_catalog_change;

        @BindView(R.id.cart_catalog_setpcs)
        TextView cart_catalog_setpcs;

        @BindView(R.id.cart_catalog_seller)
        TextView cart_catalog_seller;

        @BindView(R.id.cart_catalog_fullprice)
        TextView cart_catalog_fullprice;

        @BindView(R.id.cart_catalog_discountprice)
        TextView cart_catalog_discountprice;

        @BindView(R.id.recycler_products)
        RecyclerView recycler_products;

        @BindView(R.id.btn_minus)
        TextView btn_minus;

        @BindView(R.id.btn_plus)
        TextView btn_plus;

        @BindView(R.id.edit_qty)
        EditText edit_qty;
        @BindView(R.id.cart_img)
        SimpleDraweeView cart_img;

        @BindView(R.id.number_txt)
        TextView number_txt;

        @BindView(R.id.discount_percent)
        TextView discount_percent;

        @BindView(R.id.cart_catalog_size)
        TextView cart_catalog_size;

        @BindView(R.id.size_layout)
        RelativeLayout size_layout;

        @BindView(R.id.change_size)
        TextView change_size;

        @BindView(R.id.txt_dispatch_date)
        TextView txt_dispatch_date;

        @BindView(R.id.txt_pre_order)
        TextView txt_pre_order;

        @BindView(R.id.txt_ready_to_dispatch)
        TextView txt_ready_to_dispatch;

        @BindView(R.id.linear_pre_order)
        LinearLayout linear_pre_order;

        @BindView(R.id.cart_catalog_after_discount_price)
        TextView cart_catalog_after_discount_price;


        @BindView(R.id.cart_item)
        LinearLayout cart_item;

        @BindView(R.id.txt_mwp_single_price)
        TextView txt_mwp_single_price;

        @BindView(R.id.txt_full_single_discount)
        TextView txt_full_single_discount;

        private CartCatalogHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            txt_mwp_single_price.setPaintFlags(txt_mwp_single_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        HeaderViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

        }
    }


    class FooterViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.btn_continue_shopping)
        AppCompatButton btn_continue_shopping;

        @BindView(R.id.linear_catalog_footer)
        LinearLayout linear_catalog_footer;

        FooterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

        }
    }

}