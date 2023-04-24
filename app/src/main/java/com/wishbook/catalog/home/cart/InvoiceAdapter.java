package com.wishbook.catalog.home.cart;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wishbook.catalog.R;
import com.wishbook.catalog.commonmodels.CartCatalogModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InvoiceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    private CartCatalogModel fulldata;
    private ArrayList<CartCatalogModel.Catalogs> catalogs = new ArrayList<>();
    private int HEADER = 0;
    private int ITEMS = 1;
    private boolean isWishbookTransport;
    private boolean isBuyonCredit, isWBMoneyApplied;

    InvoiceAdapter(Context context, @NonNull CartCatalogModel fulldata, boolean isWishbookTransport, boolean isBuyonCredit, boolean isWbmoneyApplied) {

        this.fulldata = fulldata;
        this.context = context;
        this.isWishbookTransport = isWishbookTransport;
        this.isBuyonCredit = isBuyonCredit;
        this.isWBMoneyApplied = isWbmoneyApplied;
        catalogs.addAll(fulldata.getCatalogs());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.total_invoice_detail, parent, false);
            view.setBackgroundColor(context.getResources().getColor(R.color.white));
            final float scale = context.getResources().getDisplayMetrics().density;
            int padding_in_px = (int) (5 * scale + 0.5f);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, padding_in_px, 0, padding_in_px);
            view.setLayoutParams(params);
            padding_in_px = (int) (10 * scale + 0.5f);
            view.setPadding(padding_in_px, padding_in_px, padding_in_px, padding_in_px);
            return new HeaderViewHolder(view);
        } else {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycler_invoice_detail, parent, false);
            return new InvoiceHolder(itemView);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            if (holder instanceof InvoiceHolder) {

                CartCatalogModel.Catalogs data = catalogs.get(position - 1);
                ((InvoiceHolder) holder).invoice_supplier_name.setText(data.getSelling_company_name());

                if (data.getIs_full_catalog()) {
                    ((InvoiceHolder) holder).invoice_catalog_name.setVisibility(View.GONE);
                    if (data.getCatalog_title() != null && data.getCatalog_title().length() > 12) {
                        ((InvoiceHolder) holder).invoice_product_name.setText(data.getCatalog_title().substring(0, 12) + "... (" + data.getTotal_products() + "Pcs.)");
                    } else if (data.getCatalog_title() != null) {
                        ((InvoiceHolder) holder).invoice_product_name.setText(data.getCatalog_title() + " (" + data.getTotal_products() + "Pcs.)");
                    } else {
                        ((InvoiceHolder) holder).invoice_product_name.setText("-" + " (" + data.getTotal_products() + "Pcs.)");
                    }
                } else {
                    ((InvoiceHolder) holder).invoice_catalog_name.setVisibility(View.VISIBLE);
                    ((InvoiceHolder) holder).invoice_catalog_name.setText("From " + data.getCatalog_title());
                    if (data.getProducts().get(0).getProduct_sku() != null && data.getProducts().get(0).getProduct_sku().length() > 12) {
                        ((InvoiceHolder) holder).invoice_product_name.setText(data.getProducts().get(0).getProduct_sku().substring(0, 12) + "... (" + data.getTotal_products() + "Pcs.)");
                    } else if (data.getProducts().get(0).getProduct_sku() != null) {
                        ((InvoiceHolder) holder).invoice_product_name.setText(data.getProducts().get(0).getProduct_sku() + " (" + data.getTotal_products() + "Pcs.)");
                    } else {
                        ((InvoiceHolder) holder).invoice_product_name.setText("-" + " (" + data.getTotal_products() + "Pcs.)");
                    }
                }


                if (data.getTax_class_1() != null && !data.getTax_class_1().equals("null")) {
                    ((InvoiceHolder) holder).layout_tax1.setVisibility(View.VISIBLE);
                    ((InvoiceHolder) holder).invoice_tax1.setVisibility(View.VISIBLE);
                    ((InvoiceHolder) holder).invoice_tax1_text.setVisibility(View.VISIBLE);
                    ((InvoiceHolder) holder).invoice_tax1.setText("+ \u20B9 " + data.getCatalog_tax_value_1());
                    ((InvoiceHolder) holder).invoice_tax1_text.setText(data.getTax_class_1() + " (" + data.getCatalog_tax_class_1_percentage() + "%):");
                } else {
                    ((InvoiceHolder) holder).layout_tax1.setVisibility(View.GONE);
                    ((InvoiceHolder) holder).invoice_tax1.setVisibility(View.GONE);
                    ((InvoiceHolder) holder).invoice_tax1_text.setVisibility(View.GONE);
                }

                if (data.getTax_class_2() != null && !data.getTax_class_2().equals("null")) {
                    ((InvoiceHolder) holder).layout_tax2.setVisibility(View.VISIBLE);
                    ((InvoiceHolder) holder).invoice_tax2.setVisibility(View.VISIBLE);
                    ((InvoiceHolder) holder).invoice_tax2_text.setVisibility(View.VISIBLE);
                    ((InvoiceHolder) holder).invoice_tax2.setText("+ \u20B9 " + data.getCatalog_tax_value_2());
                    ((InvoiceHolder) holder).invoice_tax2_text.setText(data.getTax_class_2() + " (" + data.getCatalog_tax_class_2_percentage() + "%):");
                } else {
                    ((InvoiceHolder) holder).layout_tax2.setVisibility(View.GONE);
                    ((InvoiceHolder) holder).invoice_tax2.setVisibility(View.GONE);
                    ((InvoiceHolder) holder).invoice_tax2_text.setVisibility(View.GONE);
                }

                ((InvoiceHolder) holder).invoice_total_amount.setText("\u20B9 " + data.getCatalog_amount());

                if (!isBuyonCredit) {
                    if (data.getCatalog_discount_percent() != null && !data.getCatalog_discount_percent().equals("null") && !data.getCatalog_discount_percent().equals("0.0")) {
                        ((InvoiceHolder) holder).layout_discount.setVisibility(View.VISIBLE);
                        ((InvoiceHolder) holder).invoice_discount_percent.setVisibility(View.VISIBLE);
                        ((InvoiceHolder) holder).invoice_discount_percent.setText("Discount (" + data.getCatalog_discount_percent() + "%):");
                        ((InvoiceHolder) holder).invoice_discount_amount.setText("- \u20B9 " + data.getCatalog_discount());
                    } else {
                        ((InvoiceHolder) holder).layout_discount.setVisibility(View.GONE);
                        ((InvoiceHolder) holder).invoice_discount_percent.setVisibility(View.GONE);
                        ((InvoiceHolder) holder).invoice_discount_amount.setVisibility(View.GONE);
                    }
                } else {
                    ((InvoiceHolder) holder).layout_discount.setVisibility(View.GONE);
                }


                if (!isBuyonCredit) {
                    ((InvoiceHolder) holder).invoice_payable_amount.setText("\u20B9 " + data.getCatalog_total_amount());
                } else {
                    if (data.getCatalog_discount_percent() != null && !data.getCatalog_discount_percent().equals("null") && !data.getCatalog_discount_percent().equals("0.0")) {
                        ((InvoiceHolder) holder).invoice_payable_amount.setText("\u20B9 " + String.format("%.2f", (Double.parseDouble(data.getCatalog_total_amount()) + Double.parseDouble(data.getCatalog_discount()))));
                    } else {
                        ((InvoiceHolder) holder).invoice_payable_amount.setText("\u20B9 " + data.getCatalog_total_amount());
                    }
                }


                if (data.getIs_full_catalog()) {
                    ((InvoiceHolder) holder).invoice_type.setText("(Full Catalog)");
                } else {
                    ((InvoiceHolder) holder).invoice_type.setText("(Single Design)");
                }


                if (data.getProducts().size() > 0 && data.getProducts().get(0).getNote() != null) {
                    ((InvoiceHolder) holder).invoice_note.setVisibility(View.VISIBLE);
                    ((InvoiceHolder) holder).invoice_note.setText(data.getProducts().get(0).getNote());
                } else {
                    ((InvoiceHolder) holder).invoice_note.setVisibility(View.GONE);
                }

            } else if (holder instanceof HeaderViewHolder) {
                ((HeaderViewHolder) holder).total_amount.setText("\u20B9 " + fulldata.getAmount());

                if (!isBuyonCredit) {
                    if (fulldata.getSeller_discount() != null && !fulldata.getSeller_discount().equals("null") && !fulldata.getSeller_discount().equals("0.0")) {
                        ((HeaderViewHolder) holder).layout_discount.setVisibility(View.VISIBLE);
                        ((HeaderViewHolder) holder).discount_amount.setText("- \u20B9 " + fulldata.getSeller_discount());
                    } else {
                        ((HeaderViewHolder) holder).layout_discount.setVisibility(View.GONE);
                    }
                } else {
                    ((HeaderViewHolder) holder).layout_discount.setVisibility(View.GONE);
                }


                //((HeaderViewHolder) holder).discount_amount.setText("- \u20B9 " + fulldata.getSeller_discount());
                ((HeaderViewHolder) holder).gst_amount.setText("+ \u20B9 " + fulldata.getTaxes());

                if (isWishbookTransport) {
                    if (fulldata.getShipping_charges() != null && !fulldata.getShipping_charges().equals("null") && !fulldata.getShipping_charges().equals("0.0")) {
                        ((HeaderViewHolder) holder).layout_delivery.setVisibility(View.VISIBLE);
                        ((HeaderViewHolder) holder).delivery_amount.setText("+ \u20B9 " + fulldata.getShipping_charges());
                    } else {
                        ((HeaderViewHolder) holder).layout_delivery.setVisibility(View.GONE);
                    }
                } else {
                    ((HeaderViewHolder) holder).layout_delivery.setVisibility(View.GONE);
                }

                if (isBuyonCredit) {
                    if (fulldata.getSeller_discount() != null && !fulldata.getSeller_discount().equals("null") && !fulldata.getSeller_discount().equals("0.0")) {
                        ((HeaderViewHolder) holder).payable_amount.setText("\u20B9 " + String.format("%.2f", (fulldata.getPending_amount() + fulldata.getWb_money_used() + Double.parseDouble(fulldata.getSeller_discount()))));
                    } else {
                        ((HeaderViewHolder) holder).payable_amount.setText("\u20B9 " + String.format("%.2f", (fulldata.getPending_amount() + fulldata.getWb_money_used())));
                    }
                } else {
                    ((HeaderViewHolder) holder).payable_amount.setText("\u20B9 " + String.format("%.2f", (fulldata.getPending_amount() + fulldata.getWb_money_used())));
                }


                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                SimpleDateFormat sdfLocal = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

                ((HeaderViewHolder) holder).cart_date.setText(new SimpleDateFormat("dd MMM yyyy", Locale.US).format(new Date()));


                if (isWBMoneyApplied) {
                    ((HeaderViewHolder) holder).linear_invoice_wb_money.setVisibility(View.VISIBLE);
                    ((HeaderViewHolder) holder).linear_now_pay.setVisibility(View.VISIBLE);
                    ((HeaderViewHolder) holder).invoice_wb_money.setText("- " + "\u20B9 " + fulldata.getWb_money_used());
                    ((HeaderViewHolder) holder).now_pay_amount.setText("\u20B9 " + (fulldata.getPending_amount()));
                } else {
                    ((HeaderViewHolder) holder).linear_invoice_wb_money.setVisibility(View.VISIBLE);
                    ((HeaderViewHolder) holder).linear_now_pay.setVisibility(View.VISIBLE);
                    ((HeaderViewHolder) holder).invoice_wb_money.setText("- " + "\u20B9 " + fulldata.getWb_money_used());
                    ((HeaderViewHolder) holder).now_pay_amount.setText("\u20B9 " + (fulldata.getPending_amount()));
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return catalogs.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEADER;
        } else {
            return ITEMS;
        }
    }

    class InvoiceHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.invoice_supplier_name)
        TextView invoice_supplier_name;

        @BindView(R.id.invoice_total_amount)
        TextView invoice_total_amount;

        @BindView(R.id.invoice_discount_amount)
        TextView invoice_discount_amount;

        @BindView(R.id.invoice_tax1)
        TextView invoice_tax1;

        @BindView(R.id.invoice_tax2)
        TextView invoice_tax2;

        @BindView(R.id.invoice_payable_amount)
        TextView invoice_payable_amount;

        @BindView(R.id.invoice_catalog_name)
        TextView invoice_catalog_name;

        @BindView(R.id.invoice_product_name)
        TextView invoice_product_name;

        @BindView(R.id.invoice_discount_percent)
        TextView invoice_discount_percent;

        @BindView(R.id.invoice_tax1_text)
        TextView invoice_tax1_text;

        @BindView(R.id.invoice_tax2_text)
        TextView invoice_tax2_text;

        @BindView(R.id.invoice_type)
        TextView invoice_type;

        @BindView(R.id.layout_discount)
        LinearLayout layout_discount;

        @BindView(R.id.layout_tax1)
        LinearLayout layout_tax1;

        @BindView(R.id.layout_tax2)
        LinearLayout layout_tax2;

        @BindView(R.id.invoice_note)
        TextView invoice_note;

        InvoiceHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }


    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cart_date)
        TextView cart_date;

        @BindView(R.id.total_amount)
        TextView total_amount;

        @BindView(R.id.discount_amount)
        TextView discount_amount;

        @BindView(R.id.gst_amount)
        TextView gst_amount;

        @BindView(R.id.delivery_amount)
        TextView delivery_amount;

        @BindView(R.id.payable_amount)
        TextView payable_amount;

        @BindView(R.id.layout_discount)
        LinearLayout layout_discount;

        @BindView(R.id.layout_delivery)
        LinearLayout layout_delivery;


        @BindView(R.id.final_amount)
        TextView now_pay_amount;

        @BindView(R.id.linear_now_pay)
        LinearLayout linear_now_pay;

        @BindView(R.id.linear_invoice_wb_money)
        LinearLayout linear_invoice_wb_money;

        @BindView(R.id.invoice_wb_money)
        TextView invoice_wb_money;


        public HeaderViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

        }
    }


}