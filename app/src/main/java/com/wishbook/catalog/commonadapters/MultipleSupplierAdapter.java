package com.wishbook.catalog.commonadapters;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.responses.MultipleSuppliers;
import com.wishbook.catalog.commonmodels.responses.ResponseSellerPolicy;
import com.wishbook.catalog.home.catalog.details.Fragment_CatalogsGallery;
import com.wishbook.catalog.home.contacts.details.Fragment_SupplierDetailsNew2;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MultipleSupplierAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<MultipleSuppliers> supplierses;
    private String action;
    private String price;
    UserInfo userInfo;
    private boolean fromBroker;

    public int HEADER = 0;
    public int ITEMS = 1;


    public MultipleSupplierAdapter(Context context, ArrayList<MultipleSuppliers> supplierses, String action, String price, boolean fromBroker) {
        this.context = context;
        this.supplierses = supplierses;
        this.action = action;
        this.price = price;
        userInfo = UserInfo.getInstance(context);
        this.fromBroker = fromBroker;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_mutiple_seller, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.see_all_seller_item, parent, false);
            return new CustomViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CustomViewHolder) {
            final MultipleSuppliers suppliers = supplierses.get(position-1);
            if (action.equals(Fragment_CatalogsGallery.ALLACTION)) {
                ((CustomViewHolder) holder).linear_button_flow.setVisibility(View.VISIBLE);
                if (!userInfo.getCompany_id().equals(suppliers.getCompany_id())) {
                    if (userInfo.getCompanyType().equals("seller")) {
                        ((CustomViewHolder) holder).btn_send_enquiry.setVisibility(View.GONE);
                        ((CustomViewHolder) holder).btn_chat_supplier.setVisibility(View.GONE);
                        ((CustomViewHolder) holder).btn_buy_as_broker.setVisibility(View.GONE);
                        ((CustomViewHolder) holder).linear_button_flow.setVisibility(View.GONE);
                    } else {
                        /*if (suppliers.getRelation_id() != null) {
                            ((CustomViewHolder) holder).btn_chat_supplier.setVisibility(View.VISIBLE);
                            ((CustomViewHolder) holder).btn_send_enquiry.setVisibility(View.GONE);

                        } else {
                            ((CustomViewHolder) holder).btn_chat_supplier.setVisibility(View.GONE);
                            ((CustomViewHolder) holder).btn_send_enquiry.setVisibility(View.VISIBLE);
                        }*/

                        if (suppliers.getEnquiry_id() != null) {
                            ((CustomViewHolder) holder).btn_chat_supplier.setVisibility(View.VISIBLE);
                            ((CustomViewHolder) holder).btn_send_enquiry.setVisibility(View.GONE);

                        } else {
                            ((CustomViewHolder) holder).btn_chat_supplier.setVisibility(View.GONE);
                            ((CustomViewHolder) holder).btn_send_enquiry.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    // Own Company
                    ((CustomViewHolder) holder).linear_button_flow.setVisibility(View.GONE);
                    ((CustomViewHolder) holder).own_company_subtext.setVisibility(View.VISIBLE);
                }

            } else if (action.equals(Fragment_CatalogsGallery.CHATENQUIRY)) {
                ((CustomViewHolder) holder).linear_button_flow.setVisibility(View.VISIBLE);
                if (!userInfo.getCompany_id().equals(suppliers.getCompany_id())) {

                    if (userInfo.getCompanyType().equals("seller")) {
                        ((CustomViewHolder) holder).btn_send_enquiry.setVisibility(View.GONE);
                        ((CustomViewHolder) holder).btn_chat_supplier.setVisibility(View.GONE);
                        ((CustomViewHolder) holder).btn_buy_as_broker.setVisibility(View.GONE);
                        ((CustomViewHolder) holder).linear_button_flow.setVisibility(View.GONE);
                    } else {
                       /* if (suppliers.getRelation_id() != null) {
                            ((CustomViewHolder) holder).btn_chat_supplier.setVisibility(View.VISIBLE);
                            ((CustomViewHolder) holder).btn_send_enquiry.setVisibility(View.GONE);

                        } else {
                            ((CustomViewHolder) holder).btn_chat_supplier.setVisibility(View.GONE);
                            ((CustomViewHolder) holder).btn_send_enquiry.setVisibility(View.VISIBLE);
                        }*/

                        if (suppliers.getEnquiry_id() != null) {
                            ((CustomViewHolder) holder).btn_chat_supplier.setVisibility(View.VISIBLE);
                            ((CustomViewHolder) holder).btn_send_enquiry.setVisibility(View.GONE);

                        } else {
                            ((CustomViewHolder) holder).btn_chat_supplier.setVisibility(View.GONE);
                            ((CustomViewHolder) holder).btn_send_enquiry.setVisibility(View.VISIBLE);
                        }
                        ((CustomViewHolder) holder).btn_buy_as_broker.setVisibility(View.GONE);
                    }
                } else {

                    // Own Company
                    ((CustomViewHolder) holder).linear_button_flow.setVisibility(View.GONE);
                    ((CustomViewHolder) holder).own_company_subtext.setVisibility(View.VISIBLE);
                }
            } else {
                if (!userInfo.getCompany_id().equals(suppliers.getCompany_id())) {
                    ((CustomViewHolder) holder).own_company_subtext.setVisibility(View.GONE);
                } else {
                    ((CustomViewHolder) holder).own_company_subtext.setVisibility(View.VISIBLE);
                }
                ((CustomViewHolder) holder).linear_button_flow.setVisibility(View.GONE);

            }

            if (price != null) {
                ((CustomViewHolder) holder).linear_price_range.setVisibility(View.VISIBLE);
                if (price.contains("-")) {
                    String[] priceRangeMultiple = price.split("-");
                    ((CustomViewHolder) holder).txt_price_range.setText(" \u20B9" + priceRangeMultiple[0] + " - " + "\u20B9" + priceRangeMultiple[1]);
                } else {
                    String priceRangeSingle = price;
                    ((CustomViewHolder) holder).txt_price_range.setText("\u20B9" + priceRangeSingle);
                }
            } else {
                ((CustomViewHolder) holder).linear_price_range.setVisibility(View.VISIBLE);
            }
            if (suppliers.getSeller_score() != null) {
                float rating = Float.parseFloat(suppliers.getSeller_score());
                if (Math.round(rating) == 0) {
                    ((CustomViewHolder) holder).linear_seller_rating.setVisibility(View.GONE);
                } else {
                    if (Double.parseDouble(suppliers.getSeller_score()) <= 2) {
                        ((CustomViewHolder) holder).txt_rating.setBackground(ContextCompat.getDrawable(context, R.drawable.rating_summary_red));
                    } else {
                        ((CustomViewHolder) holder).txt_rating.setBackground(ContextCompat.getDrawable(context, R.drawable.rating_summary));
                    }
                    String rateString = String.format("%.1f", Double.parseDouble(suppliers.getSeller_score()));
                    ((CustomViewHolder) holder).txt_rating.setText(rateString);
                }
            } else {
                ((CustomViewHolder) holder).linear_seller_rating.setVisibility(View.GONE);
            }


            if (suppliers.getTrusted_seller()) {
                ((CustomViewHolder) holder).img_trusted.setVisibility(View.VISIBLE);
            } else {
                ((CustomViewHolder) holder).img_trusted.setVisibility(View.GONE);
            }
            ((CustomViewHolder) holder).txt_sold_by.setText(suppliers.getName());
            String stateLocation = suppliers.getState_name() != null ? suppliers.getState_name() + ", " : "";
            String cityLocation = suppliers.getCity_name() != null ? suppliers.getCity_name() : "";
            ((CustomViewHolder) holder).txt_location.setText(stateLocation + cityLocation);


            if (suppliers.getSeller_policy() != null && suppliers.getSeller_policy().size() > 0) {
                ArrayList<ResponseSellerPolicy> policies = suppliers.getSeller_policy();
                for (int i = 0; i < policies.size(); i++) {
                    if (policies.get(i).getPolicy_type().equals(Constants.SELLER_POLICY_DISPATCH_DURATION)) {
                        ((CustomViewHolder) holder).linear_delivery_time.setVisibility(View.VISIBLE);
                        ((CustomViewHolder) holder).txt_delivery_value.setText(policies.get(i).getPolicy().toString());
                    } else if (policies.get(i).getPolicy_type().equals(Constants.SELLER_POLICY_RETURN)) {
                        ((CustomViewHolder) holder).linear_return_policy.setVisibility(View.VISIBLE);
                        ((CustomViewHolder) holder).txt_return_value.setText(policies.get(i).getPolicy().toString());
                    }
                }
            } else {
                ((CustomViewHolder) holder).linear_delivery_time.setVisibility(View.GONE);
                ((CustomViewHolder) holder).linear_return_policy.setVisibility(View.GONE);
            }


            ((CustomViewHolder) holder).btn_chat_supplier.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (UserInfo.getInstance(context).isGuest()) {
                        StaticFunctions.ShowRegisterDialog(context,"Multiple Supplier");
                        return;
                    }
                    Intent supplierIntent = new Intent();
                    supplierIntent.putExtra("supplier", suppliers);
                    supplierIntent.putExtra("action", Fragment_CatalogsGallery.CHATSUPPLIER);
                    ((Activity) context).setResult(Application_Singleton.SELECT_SUPPLIER_RESPONSE_CODE, supplierIntent);
                    ((Activity) context).finish();




                }
            });

            ((CustomViewHolder) holder).btn_send_enquiry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (UserInfo.getInstance(context).isGuest()) {
                        StaticFunctions.ShowRegisterDialog(context,"Multiple Supplier");
                        return;
                    }
                    Intent supplierIntent = new Intent();
                    supplierIntent.putExtra("supplier", suppliers);
                    supplierIntent.putExtra("action", Fragment_CatalogsGallery.SENDENQUIRY);
                    ((Activity) context).setResult(Application_Singleton.SELECT_SUPPLIER_RESPONSE_CODE, supplierIntent);
                    ((Activity) context).finish();
                }
            });
            if (UserInfo.getInstance(context).getBroker() == null || !UserInfo.getInstance(context).getBroker()) {
                ((CustomViewHolder) holder).btn_buy_as_broker.setVisibility(View.GONE);
            }
            ((CustomViewHolder) holder).btn_buy_as_broker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (UserInfo.getInstance(context).isGuest()) {
                        StaticFunctions.ShowRegisterDialog(context,"Multiple Supplier");
                        return;
                    }
                    if (UserInfo.getInstance(context).getBroker() != null && UserInfo.getInstance(context).getBroker()) {
                        Intent supplierIntent = new Intent();
                        supplierIntent.putExtra("supplier", suppliers);
                        supplierIntent.putExtra("action", Fragment_CatalogsGallery.BROKERAGE);
                        ((Activity) context).setResult(Application_Singleton.SELECT_SUPPLIER_RESPONSE_CODE, supplierIntent);
                        ((Activity) context).finish();
                     /*   final PopupMenu popupMenu = new PopupMenu(context, view);
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                if (item.getItemId() == R.id.create_purchase) {
                                    Intent supplierIntent = new Intent();
                                    supplierIntent.putExtra("supplier", suppliers);
                                    supplierIntent.putExtra("action", Fragment_CatalogsGallery.PURCHASE);
                                    ((Activity) context).setResult(Application_Singleton.SELECT_SUPPLIER_RESPONSE_CODE, supplierIntent);
                                    ((Activity) context).finish();
                                } else if (item.getItemId() == R.id.create_brokerage) {
                                    Intent supplierIntent = new Intent();
                                    supplierIntent.putExtra("supplier", suppliers);
                                    supplierIntent.putExtra("action", Fragment_CatalogsGallery.BROKERAGE);
                                    ((Activity) context).setResult(Application_Singleton.SELECT_SUPPLIER_RESPONSE_CODE, supplierIntent);
                                    ((Activity) context).finish();
                                }
                                return false;
                            }
                        });
                        popupMenu.inflate(R.menu.menu_order_popup);
                        popupMenu.getMenu().getItem(0).setVisible(true);
                        popupMenu.getMenu().getItem(1).setVisible(true);
                        popupMenu.getMenu().getItem(2).setVisible(false);
                        popupMenu.show();*/
                    } else {
                        ((CustomViewHolder) holder).btn_buy_as_broker.setVisibility(View.GONE);
                    }/*else {
                        Intent supplierIntent = new Intent();
                        supplierIntent.putExtra("supplier", suppliers);
                        supplierIntent.putExtra("action", Fragment_CatalogsGallery.PURCHASE);
                        ((Activity) context).setResult(Application_Singleton.SELECT_SUPPLIER_RESPONSE_CODE, supplierIntent);
                        ((Activity) context).finish();
                    }*/
                }
            });
            ((CustomViewHolder) holder).card_seller_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!action.equals(Fragment_CatalogsGallery.ALLACTION)) {
                        if (userInfo.getCompany_id().equals(suppliers.getCompany_id())) {
                            // own company Order
                            new MaterialDialog.Builder(context)
                                    .content("You can't create own company order")
                                    .positiveText("OK")
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(MaterialDialog dialog, DialogAction which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .show();
                        } else {
                            Intent supplierIntent = new Intent();
                            supplierIntent.putExtra("supplier", suppliers);
                            supplierIntent.putExtra("action", action);
                            ((Activity) context).setResult(Application_Singleton.SELECT_SUPPLIER_RESPONSE_CODE, supplierIntent);
                            ((Activity) context).finish();
                        }
                    } else {
                        if (UserInfo.getInstance(context).isGuest()) {
                            StaticFunctions.ShowRegisterDialog(context,"Multiple Supplier");
                        } else {
                            Bundle bundle = new Bundle();
                            if (suppliers.getCompany_id() != null && suppliers.getRelation_id() != null) {
                                bundle.putString("sellerid", suppliers.getRelation_id());
                                bundle.putString("sellerCompanyid", suppliers.getCompany_id());
                                Application_Singleton.CONTAINER_TITLE = "Supplier Details";
                                Application_Singleton.TOOLBARSTYLE = "WHITE";
                                Fragment_SupplierDetailsNew2 supplier = new Fragment_SupplierDetailsNew2();
                                supplier.setArguments(bundle);
                                Application_Singleton.CONTAINERFRAG = supplier;
                                Intent intent = new Intent(context, OpenContainer.class);
                                context.startActivity(intent);
                            } else if (suppliers.getCompany_id() != null) {
                                // for public details
                                bundle.putString("sellerid", suppliers.getCompany_id());
                                bundle.putBoolean("isHideAll", true);
                                Application_Singleton.CONTAINER_TITLE = "Supplier Details";
                                Application_Singleton.TOOLBARSTYLE = "WHITE";
                                Fragment_SupplierDetailsNew2 supplier = new Fragment_SupplierDetailsNew2();
                                supplier.setArguments(bundle);
                                Application_Singleton.CONTAINERFRAG = supplier;
                                Intent intent = new Intent(context, OpenContainer.class);
                                context.startActivity(intent);
                            }
                        }

                    }
                }
            });
        } else if (holder instanceof HeaderViewHolder) {
            if(fromBroker){
                if(supplierses.size() == 1) {
                    ((HeaderViewHolder) holder).txt_number_seller.setText("Showing seller connected with you.");
                } else {
                    ((HeaderViewHolder) holder).txt_number_seller.setText("Showing sellers connected with you.");
                }
            } else {
                if(supplierses.size() == 1) {
                    ((HeaderViewHolder) holder).txt_number_seller.setText(String.format(context.getResources().getString(R.string.multiple_seller_header1), supplierses.size()));
                } else {
                    ((HeaderViewHolder) holder).txt_number_seller.setText(String.format(context.getResources().getString(R.string.multiple_seller_header), supplierses.size()));
                }
            }

        }
    }

    @Override
    public int getItemCount() {
        return supplierses.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEADER;
        } else {
            return ITEMS;
        }
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txt_sold_by)
        TextView txt_sold_by;

        @BindView(R.id.img_trusted)
        ImageView img_trusted;

        @BindView(R.id.txt_location)
        TextView txt_location;

        @BindView(R.id.linear_seller_rating)
        LinearLayout linear_seller_rating;

        @BindView(R.id.txt_rating)
        TextView txt_rating;

        @BindView(R.id.card_seller_container)
        CardView card_seller_container;

        @BindView(R.id.linear_button_flow)
        LinearLayout linear_button_flow;

        @BindView(R.id.txt_price_range)
        TextView txt_price_range;

        @BindView(R.id.btn_purchase)
        AppCompatButton btn_purchase;

        @BindView(R.id.btn_chat_supplier)
        AppCompatButton btn_chat_supplier;

        @BindView(R.id.btn_send_enquiry)
        AppCompatButton btn_send_enquiry;

        @BindView(R.id.linear_price_range)
        LinearLayout linear_price_range;

        @BindView(R.id.own_company_subtext)
        TextView own_company_subtext;

        @BindView(R.id.linear_return_policy)
        LinearLayout linear_return_policy;

        @BindView(R.id.linear_delivery_time)
        LinearLayout linear_delivery_time;

        @BindView(R.id.txt_delivery_value)
        TextView txt_delivery_value;

        @BindView(R.id.txt_return_value)
        TextView txt_return_value;

        @BindView(R.id.btn_buy_as_broker)
        AppCompatButton btn_buy_as_broker;

        public CustomViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txt_number_seller)
        TextView txt_number_seller;


        public HeaderViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

        }
    }
}
