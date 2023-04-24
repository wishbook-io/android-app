package com.wishbook.catalog.home.orderNew.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.Constants;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.ChatCallUtils;
import com.wishbook.catalog.Utils.DateUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.Utils.util.LocaleHelper;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.postpatchmodels.Invoice;
import com.wishbook.catalog.commonmodels.postpatchmodels.Items;
import com.wishbook.catalog.commonmodels.postpatchmodels.Order_item;
import com.wishbook.catalog.commonmodels.postpatchmodels.Shipments;
import com.wishbook.catalog.commonmodels.responses.CatalogGroup;
import com.wishbook.catalog.commonmodels.responses.OrderStep;
import com.wishbook.catalog.commonmodels.responses.Response_sellingoder_catalog;
import com.wishbook.catalog.home.orderNew.details.Activity_OrderDetailsNew;
import com.wishbook.catalog.home.orderNew.details.Activity_PaymentOrder;
import com.wishbook.catalog.home.orderNew.details.Fragment_ShipTrack;
import com.wishbook.catalog.home.orders.viewholder.InvoiceViewHolder;
import com.wishbook.catalog.home.rrc.Fragment_RRC_OrderItemSelection;
import com.wishbook.catalog.home.rrc.RRCHandler;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class InvoiceAdapter extends RecyclerView.Adapter<InvoiceViewHolder> {

    private Context mcContext;
    private ArrayList<Invoice> invoices;
    private boolean isPurchased = false;
    private String orderId, ordernumber, companyName, date, orderType, orderStatus;
    private ArrayList<Response_sellingoder_catalog> catalogs;
    private boolean isBroker;


    public InvoiceAdapter(Context mcContext, ArrayList<Invoice> invoices, String orderId,
                          String ordernumber,
                          String orderType,
                          String companyName, String date, boolean isPurchased, ArrayList<Response_sellingoder_catalog> catalogs,
                          boolean isBroker,
                          String orderStatus) {
        this.mcContext = mcContext;
        this.invoices = invoices;
        this.orderId = orderId;
        this.ordernumber = ordernumber;
        this.orderType = orderType;
        this.companyName = companyName;
        this.date = date;
        this.isPurchased = isPurchased;
        this.catalogs = catalogs;
        this.isBroker = isBroker;
        this.orderStatus = orderStatus;
    }

    public static String currentISTDate(String dateFromServer) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat sdfLocal = new SimpleDateFormat("MMMM dd,yyyy", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            return sdfLocal.format(sdf.parse(dateFromServer));
        } catch (ParseException e) {
            e.printStackTrace();
            try {
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                sdf1.setTimeZone(TimeZone.getTimeZone("GMT"));
                return sdfLocal.format(sdf1.parse(dateFromServer));

            } catch (Exception e1) {

            }

        }

        return "";

    }

    @Override
    public InvoiceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.purchase_invoice_item, parent, false);
        return new InvoiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final InvoiceViewHolder holder, final int position) {
        holder.invoiceOrderNo.setText(ordernumber);
        holder.invoice_number.setText("#" + (position + 1));
        if (isPurchased) {
            holder.txtSupplierText.setText("Supplier name");
        } else {
            holder.txtSupplierText.setText("Buyer name");
        }
        if (companyName != null) {
            holder.invoiceSellerName.setText(StringUtils.capitalize(companyName.toLowerCase().trim()));
        }
        String temp_date = DateUtils.changeDateFormat(StaticFunctions.SERVER_POST_FORMAT1, StaticFunctions.CLIENT_DISPLAY_FORMAT1, this.date);
        holder.invoiceOrdeDate.setText(temp_date);

        try {
            if (Float.parseFloat(invoices.get(position).getPending_amount()) > 1) {
                holder.invoicePayableAmt.setText("\u20B9" + " " + invoices.get(position).getPending_amount());
                holder.payble_amt_container.setVisibility(View.VISIBLE);
            } else {
                holder.payble_amt_container.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            holder.payble_amt_container.setVisibility(View.GONE);
        }

        try {
            if (Float.parseFloat(invoices.get(position).getPaid_amount()) > 0) {
                holder.invoicePaidAmt.setText("\u20B9" + " " + invoices.get(position).getPaid_amount());
                holder.paid_amt_container.setVisibility(View.VISIBLE);
            } else
                holder.paid_amt_container.setVisibility(View.GONE);

        } catch (Exception e) {
            holder.paid_amt_container.setVisibility(View.GONE);
        }

        try {
            if (Float.parseFloat(invoices.get(position).getPaid_amount()) < 1 && Float.parseFloat(invoices.get(position).getPending_amount()) < 1) {
                holder.invoicePayableAmt.setText("\u20B9" + " " + "0");
                holder.payble_amt_container.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        //set Discount tax
        if (invoices.get(position).getTax_class_1() != null) {
            holder.relativeTaxClass1.setVisibility(View.VISIBLE);
            holder.taxClass1Name.setText(invoices.get(position).getTax_class_1());
            holder.txtClass1Value.setText("+ " + "\u20B9" + invoices.get(position).getTotal_tax_value_1());
        } else {
            holder.relativeTaxClass1.setVisibility(View.GONE);
        }

        //addCatalogList(catalogs, holder.invoice_catalog_container);
        ArrayList<Items> arrayList = new ArrayList<Items>(Arrays.asList(invoices.get(position).getItems()));
        addCatalogLis2(arrayList, holder.invoice_catalog_container);
        if (invoices.get(position).getTax_class_2() != null) {
            holder.relativeTaxClass2.setVisibility(View.VISIBLE);
            holder.taxClass2Name.setText(invoices.get(position).getTax_class_2());
            holder.txtClass2Value.setText("+ " + "\u20B9" + invoices.get(position).getTotal_tax_value_2());
        } else {
            holder.relativeTaxClass2.setVisibility(View.GONE);
        }

        if (invoices.get(position).getSeller_discount() != null && !invoices.get(position).getSeller_discount().equals("0.00")) {
            holder.relativeSellerDiscount.setVisibility(View.VISIBLE);
            holder.txtSellerDiscount.setText("- " + "\u20B9" + invoices.get(position).getSeller_discount());
        } else {
            holder.relativeSellerDiscount.setVisibility(View.GONE);
        }

        if (invoices.get(position).getShipping_charges() != null && !invoices.get(position).getShipping_charges().equals("0.00")) {
            holder.linear_shipping_container.setVisibility(View.VISIBLE);
            holder.txt_shipping_amount.setText("+ " + "\u20B9" + invoices.get(position).getShipping_charges());
        } else {
            holder.linear_shipping_container.setVisibility(View.GONE);
        }

        if (!isBroker) {

            if (invoices.get(position).getStatus().equals("Invoiced")) {

                if (!isPurchased) {
                    /**
                     *   https://wishbook.atlassian.net/browse/WB-2580
                     *   hide cancel button for sales order
                     */
                    holder.btnCancel.setVisibility(View.GONE);

                    if (invoices.get(position).getPayment_status().contains("Paid") || orderType.equalsIgnoreCase(Constants.ORDER_TYPE_CREDIT)) {
                        holder.btnDispatch.setVisibility(View.GONE);
                    }

                } else {

                    holder.btnDispatch.setVisibility(View.GONE);

                    if (invoices.get(position).getPayment_status().contains("Paid")) {
                        /**
                         * WB-2751
                         */
                        holder.btnCancel.setVisibility(View.GONE);
                        holder.bntPay.setVisibility(View.GONE);

                    } else {
                        Log.i("TAG", "onBindViewHolder: 2");
                        holder.btnCancel.setVisibility(View.VISIBLE);
                        holder.bntPay.setVisibility(View.GONE);
                    }
                }
            } else if ((invoices.get(position).getStatus().equals("Dispatched") ||
                    invoices.get(position).getStatus().equals("Delivered"))
                    && !invoices.get(position).getPayment_status().contains("Paid")) {

                holder.bntPay.setVisibility(View.GONE);
                holder.btnCancel.setVisibility(View.GONE);
                holder.btnDispatch.setVisibility(View.GONE);
                if (isPurchased) {
                    // holder.bntPay.setVisibility(View.VISIBLE);
                    /**
                     * Hide Order Received https://wishbook.atlassian.net/browse/WB-3294
                     */
                    holder.btn_order_received.setVisibility(View.GONE);
                }
            } else {
                holder.btnCancel.setVisibility(View.GONE);
                holder.bntPay.setVisibility(View.GONE);
                holder.btnDispatch.setVisibility(View.GONE);
                if (isPurchased) {
                    if (invoices.get(position).getStatus().equals("Dispatched")) {
                        /**
                         * Hide Order Received https://wishbook.atlassian.net/browse/WB-3294
                         */
                        holder.btn_order_received.setVisibility(View.GONE);
                    } else {
                        holder.btn_order_received.setVisibility(View.GONE);
                    }
                } else {
                    holder.btn_order_received.setVisibility(View.GONE);
                }
            }


        }


        //ShipMent Details

        holder.dispatched_items.setText(invoices.get(position).getTotal_qty() + " Pcs.");

        if (invoices.get(position).getTaxes() != null && !invoices.get(position).getTaxes().equals("null") && !invoices.get(position).getTaxes().equals("")) {
            holder.gst_item.setText("\u20B9" + invoices.get(position).getTaxes());
        } else {
            holder.gst_item.setText("\u20B9" + "0");
        }


        if (invoices.get(position).getShipments() != null && invoices.get(position).getShipments().length > 0) {
            holder.shipment_container.setVisibility(View.VISIBLE);
            Shipments shipments = invoices.get(position).getShipments()[0];
            holder.shipment.setText("Shipment" + "(" + shipments.getStatus() + ")");






            /*
            //mode
            if (validate(shipments.getMode())) {
                holder.shipment_mode.setVisibility(View.VISIBLE);
                holder.shipment_mode.setText("Mode : " + shipments.getMode());
            } else {
                holder.shipment_mode.setVisibility(View.GONE);
            }

            //transporter
            if (validate(shipments.getTransporter_courier())) {
                holder.shipment_transporter.setVisibility(View.VISIBLE);
                holder.shipment_transporter.setText("Transporter/Courier : " + shipments.getTransporter_courier());
            } else {
                holder.shipment_transporter.setVisibility(View.GONE);
            }

            //number
            if (validate(shipments.getTracking_number())) {
                holder.shipment_number.setVisibility(View.VISIBLE);
                holder.shipment_number.setText("Tracking-Number : " + shipments.getTracking_number());
            } else {
                holder.shipment_number.setVisibility(View.GONE);
            }

            //details
            if (validate(shipments.getDetails())) {
                holder.shipment_details.setVisibility(View.VISIBLE);
                holder.shipment_details.setText("Tracking-Details : " + shipments.getDetails());
            } else {
                holder.shipment_details.setVisibility(View.GONE);
            }

            if (!validate(shipments.getDetails())
                    && !validate(shipments.getTracking_number())
                    && !validate(shipments.getTransporter_courier())
                    && !validate(shipments.getMode())) {
                holder.linear_dispatch_details.setVisibility(View.GONE);
            } else {
                holder.linear_dispatch_details.setVisibility(View.VISIBLE);
            }*/

            holder.linear_dispatch_details.setVisibility(View.GONE);


            //date
            if (shipments.getDatetime() != null && !currentISTDate(shipments.getDatetime()).equals("")) {
                holder.shipment_date_container.setVisibility(View.VISIBLE);
                holder.shipment_date.setVisibility(View.VISIBLE);
                holder.shipment_date.setText("" + currentISTDate(shipments.getDatetime()));
            } else {
                holder.shipment_date_container.setVisibility(View.GONE);
                holder.shipment_date.setVisibility(View.GONE);
            }
        } else {
            holder.shipment_container.setVisibility(View.GONE);
        }

        if (isPurchased) {
            setOrderTrack(invoices.get(position).getShipments(), holder, orderStatus);
            showHideReplacementView(invoices.get(position).getShipments(), holder, orderStatus);
        } else {
            holder.linear_main_order_track.setVisibility(View.GONE);
            holder.btn_track_order.setVisibility(View.GONE);
            holder.order_track_note.setVisibility(View.GONE);
            holder.linear_invoice_replacement.setVisibility(View.GONE);
        }


        if (invoices.get(position).getWbmoney_points_used() > 0) {
            holder.linear_wb_money_container.setVisibility(View.VISIBLE);
            holder.txt_wishbook_amount.setText("- "+"\u20B9" + invoices.get(position).getWbmoney_points_used());
        } else {
            holder.linear_wb_money_container.setVisibility(View.GONE);
        }

        if (invoices.get(position).getWbpoints_used() > 0) {
            holder.linear_reward_point_container.setVisibility(View.VISIBLE);
            holder.txt_reward_point.setText("- "+"\u20B9" + invoices.get(position).getWbpoints_used());
        } else {
            holder.linear_reward_point_container.setVisibility(View.GONE);
        }

        // Coupon UI Start

        if(invoices.get(position).getWb_coupon_discount() > 0 && invoices.get(position).getWb_coupon()!=null) {
            holder.linear_coupon_container.setVisibility(View.VISIBLE);
            holder.txt_coupon_label.setText("Coupon Applied("+invoices.get(position).getWb_coupon().getCode()+")");
            holder.txt_coupon_price.setText("- "+"\u20B9"+invoices.get(position).getWb_coupon_discount());
        }

        // Reseller UI Start

        if (isPurchased) {
            if (invoices.get(position).isReseller_order() && invoices.get(position).getDisplay_amount() > 0) {
                holder.view_reseller.setVisibility(View.VISIBLE);
                holder.linear_reseller_display_amount.setVisibility(View.VISIBLE);
                holder.txt_display_amount.setText("\u20B9 " + invoices.get(position).getDisplay_amount());
            } else {
                holder.view_reseller.setVisibility(View.GONE);
                holder.linear_reseller_display_amount.setVisibility(View.GONE);
            }
        }

        // Download PDF
        if (isPurchased && invoices.get(position).getUniware_invoice_pdf() != null && !invoices.get(position).getUniware_invoice_pdf().isEmpty()) {
            holder.txt_invoice_download.setVisibility(View.VISIBLE);
            holder.txt_invoice_download.setPaintFlags(holder.txt_invoice_download.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            holder.txt_invoice_download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mcContext instanceof Activity_OrderDetailsNew) {
                        ((Activity_OrderDetailsNew) mcContext).checkWritePermissionAndDownloadInvoice(invoices.get(position).getUniware_invoice_pdf(), invoices.get(position).getId());
                    }
                }
            });
        } else {
            holder.txt_invoice_download.setVisibility(View.GONE);
        }

        holder.btnDispatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Activity_OrderDetailsNew) mcContext).dispatchOrder(invoices.get(holder.getAdapterPosition()).getId(), "invoice");
            }
        });

        holder.bntPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Todo When pay Click
                Application_Singleton.purchaseInvoice = invoices.get(holder.getAdapterPosition());
                if (invoices.get(holder.getAdapterPosition()) != null) {
                    Intent intent = new Intent(mcContext, Activity_PaymentOrder.class);
                    ((Activity) mcContext).startActivityForResult(intent, 1000);
                    ((Activity) mcContext).finish();
                }
            }
        });

        holder.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Activity_OrderDetailsNew) mcContext).cancelInvoice("create_invoice", invoices.get(holder.getAdapterPosition()).getId());
            }
        });

        holder.btn_order_received.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Activity_OrderDetailsNew) mcContext).showConfirmOrderReviewDialog(invoices.get(holder.getAdapterPosition()).getOrder());
            }
        });


    }

    @Override
    public int getItemCount() {
        return invoices.size();
    }

    private String getformatedDate(String dat) {
        String format = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        String toformat = "MMMM dd,yyyy";
        SimpleDateFormat tosdf = new SimpleDateFormat(toformat, Locale.US);

        try {
            Date date = sdf.parse(dat);
            return tosdf.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }


    private void addCatalogList(ArrayList<Order_item> catalogs, LinearLayout root) {
      /*  Log.d("TAG", "addCatalogList: " + catalogs.size());
        LayoutInflater vi = (LayoutInflater) mcContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        root.removeAllViews();
        for (int i = 0; i < catalogs.size(); i++) {
            View v = vi.inflate(R.layout.catalog_row, null);
            TextView catalogName = (TextView) v.findViewById(R.id.invoice_prod_name);
            TextView catalogValue = (TextView) v.findViewById(R.id.invoice_prod_value);
            float totalPriceCatalog = 0;
            int totalQty = 0;
            for (int j = 0; j < catalogs.get(i).getProducts().size(); j++) {
                Response_Product product = catalogs.get(i).getProducts().get(j);
                totalQty += Integer.parseInt(product.getQuantity());
                totalPriceCatalog += Float.parseFloat(product.getRate()) * Integer.parseInt(product.getQuantity());
            }
            String cname = "" + (i + 1) + ". " + catalogs.get(i).getName() + " (" + totalQty + "Pcs.)";
            catalogValue.setText("\u20B9" + String.valueOf(totalPriceCatalog));
            catalogName.setText(cname);
            root.addView(v);
        }*/
    }


    private void setOrderTrack(final Shipments[] shipments, InvoiceViewHolder holder, final String orderStatus) {
        holder.linear_main_order_track.setVisibility(View.VISIBLE);
        ArrayList<OrderStep> orderSteps = new ArrayList<>();
        orderSteps.add(new OrderStep("Order Placed", OrderStep.STEP_COMPLETED));
        if (orderStatus != null &&
                (orderStatus.equalsIgnoreCase("Transferred")
                        || orderStatus.equalsIgnoreCase("Cancelled"))) {
            orderSteps.add(new OrderStep(orderStatus, OrderStep.STEP_COMPLETED));
        } else {
            if (shipments.length > 0) {
                int step = 0;
                if (shipments[0].getStatus() != null && (shipments[0].getStatus().equalsIgnoreCase(Constants.SHIPMENT_READYTOSHIP)
                        || shipments[0].getStatus().equalsIgnoreCase(Constants.MANIFESTED))) {
                    step = 1;
                    orderSteps.add(new OrderStep("Ready to ship", OrderStep.STEP_COMPLETED));
                    orderSteps.add(new OrderStep("Dispatched", OrderStep.STEP_REMAIN));
                    orderSteps.add(new OrderStep("Delivered", OrderStep.STEP_REMAIN));
                } else if (shipments[0].getStatus() != null && shipments[0].getStatus().equalsIgnoreCase(Constants.SHIPMENT_DISPATCHED) ||
                        shipments[0].getStatus().equalsIgnoreCase(Constants.SHIPMENT_OUT_FOR_DELIVERY) ) {
                    step = 2;
                    orderSteps.add(new OrderStep("Ready to ship", OrderStep.STEP_COMPLETED));
                    orderSteps.add(new OrderStep("Dispatched", OrderStep.STEP_COMPLETED));
                    orderSteps.add(new OrderStep("Delivered", OrderStep.STEP_REMAIN));
                } else if (shipments[0].getStatus() != null && shipments[0].getStatus().equalsIgnoreCase(Constants.SHIPMENT_DELIVERED)) {
                    step = 3;
                    orderSteps.add(new OrderStep("Ready to ship", OrderStep.STEP_COMPLETED));
                    orderSteps.add(new OrderStep("Dispatched", OrderStep.STEP_COMPLETED));
                    orderSteps.add(new OrderStep("Delivered", OrderStep.STEP_COMPLETED));
                } else if (shipments[0].getStatus() !=null && shipments[0].getStatus().equalsIgnoreCase(Constants.SHIPMENT_RETURNED)) {
                    orderSteps.add(new OrderStep("Returned", OrderStep.STEP_COMPLETED));
                } else {
                    orderSteps.add(new OrderStep("Ready to ship", OrderStep.STEP_REMAIN));
                    orderSteps.add(new OrderStep("Dispatched", OrderStep.STEP_REMAIN));
                    orderSteps.add(new OrderStep("Delivered", OrderStep.STEP_REMAIN));
                }

            } else {
                orderSteps.add(new OrderStep("Ready to ship", OrderStep.STEP_REMAIN));
                orderSteps.add(new OrderStep("Dispatched", OrderStep.STEP_REMAIN));
                orderSteps.add(new OrderStep("Delivered", OrderStep.STEP_REMAIN));
            }
        }

        for (int i = 0; i < orderSteps.size(); i++) {
            if (i == 0) {
                if (orderSteps.get(i).getState() == OrderStep.STEP_REMAIN) {
                    holder.txt_step_one.setText(orderSteps.get(i).getName());
                    holder.txt_step_one.setTextColor(ContextCompat.getColor(mcContext, R.color.purchase_medium_gray));
                    holder.img_step_one.setImageDrawable(ContextCompat.getDrawable(mcContext, R.drawable.ic_order_track_disable_round));
                } else if (orderSteps.get(i).getState() == OrderStep.STEP_COMPLETED) {
                    holder.txt_step_one.setText(orderSteps.get(i).getName());
                    holder.txt_step_one.setTextColor(ContextCompat.getColor(mcContext, R.color.color_primary));
                    holder.img_step_one.setImageDrawable(ContextCompat.getDrawable(mcContext, R.drawable.ic_order_track_active_round));
                }
            } else if (i == 1) {
                if (orderSteps.get(i).getState() == OrderStep.STEP_REMAIN) {
                    holder.img_view_step_one.setBackgroundColor(ContextCompat.getColor(mcContext, R.color.purchase_medium_gray));
                    holder.txt_step_two.setText(orderSteps.get(i).getName());
                    holder.txt_step_two.setTextColor(ContextCompat.getColor(mcContext, R.color.purchase_medium_gray));
                    holder.img_step_two.setImageDrawable(ContextCompat.getDrawable(mcContext, R.drawable.ic_order_track_disable_round));
                } else if (orderSteps.get(i).getState() == OrderStep.STEP_COMPLETED) {
                    holder.img_view_step_one.setBackgroundColor(ContextCompat.getColor(mcContext, R.color.color_primary));
                    holder.txt_step_two.setText(orderSteps.get(i).getName());
                    holder.txt_step_two.setTextColor(ContextCompat.getColor(mcContext, R.color.color_primary));
                    holder.img_step_two.setImageDrawable(ContextCompat.getDrawable(mcContext, R.drawable.ic_order_track_active_round));
                }
            } else if (i == 2) {
                if (orderSteps.get(i).getState() == OrderStep.STEP_REMAIN) {

                    holder.img_view_step_two.setVisibility(View.VISIBLE);
                    holder.img_step_three.setVisibility(View.VISIBLE);
                    holder.txt_step_three.setVisibility(View.VISIBLE);
                    holder.txt_view_step_two.setVisibility(View.VISIBLE);

                    holder.img_view_step_two.setBackgroundColor(ContextCompat.getColor(mcContext, R.color.purchase_medium_gray));
                    holder.txt_step_three.setText(orderSteps.get(i).getName());
                    holder.txt_step_three.setTextColor(ContextCompat.getColor(mcContext, R.color.purchase_medium_gray));
                    holder.img_step_three.setImageDrawable(ContextCompat.getDrawable(mcContext, R.drawable.ic_order_track_disable_round));
                } else if (orderSteps.get(i).getState() == OrderStep.STEP_COMPLETED) {
                    holder.img_view_step_two.setVisibility(View.VISIBLE);
                    holder.img_step_three.setVisibility(View.VISIBLE);
                    holder.txt_step_three.setVisibility(View.VISIBLE);
                    holder.txt_view_step_two.setVisibility(View.VISIBLE);

                    holder.img_view_step_two.setBackgroundColor(ContextCompat.getColor(mcContext, R.color.color_primary));
                    holder.txt_step_three.setText(orderSteps.get(i).getName());
                    holder.txt_step_three.setTextColor(ContextCompat.getColor(mcContext, R.color.color_primary));
                    holder.img_step_three.setImageDrawable(ContextCompat.getDrawable(mcContext, R.drawable.ic_order_track_active_round));
                }
            } else if (i == 3) {
                if (orderSteps.get(i).getState() == OrderStep.STEP_REMAIN) {
                    holder.img_view_step_three.setVisibility(View.VISIBLE);
                    holder.img_step_four.setVisibility(View.VISIBLE);
                    holder.txt_step_four.setVisibility(View.VISIBLE);
                    holder.txt_view_step_three.setVisibility(View.VISIBLE);

                    holder.img_view_step_three.setBackgroundColor(ContextCompat.getColor(mcContext, R.color.purchase_medium_gray));
                    holder.txt_step_four.setText(orderSteps.get(i).getName());
                    holder.txt_step_four.setTextColor(ContextCompat.getColor(mcContext, R.color.purchase_medium_gray));
                    holder.img_step_four.setImageDrawable(ContextCompat.getDrawable(mcContext, R.drawable.ic_order_track_disable_round));
                } else if (orderSteps.get(i).getState() == OrderStep.STEP_COMPLETED) {
                    holder.img_view_step_three.setVisibility(View.VISIBLE);
                    holder.img_step_four.setVisibility(View.VISIBLE);
                    holder.txt_step_four.setVisibility(View.VISIBLE);
                    holder.txt_view_step_three.setVisibility(View.VISIBLE);

                    holder.img_view_step_three.setBackgroundColor(ContextCompat.getColor(mcContext, R.color.color_primary));
                    holder.txt_step_four.setText(orderSteps.get(i).getName());
                    holder.txt_step_four.setTextColor(ContextCompat.getColor(mcContext, R.color.color_primary));
                    holder.img_step_four.setImageDrawable(ContextCompat.getDrawable(mcContext, R.drawable.ic_order_track_active_round));
                }
            }

        }


        if (shipments.length > 0) {
            if (shipments[0].getStatus() != null && shipments[0].getStatus().contains(Constants.SHIPMENT_DISPATCHED)) {
                holder.btn_track_order.setVisibility(View.VISIBLE);
                holder.order_track_note.setVisibility(View.GONE);

                holder.btn_track_order.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        Fragment_ShipTrack fragment_shipTrack = new Fragment_ShipTrack();
                        bundle.putString("track_url", shipments[0].getAws_url());
                        fragment_shipTrack.setArguments(bundle);
                        Application_Singleton.CONTAINER_TITLE = "Tracking Details";
                        Application_Singleton.CONTAINERFRAG = fragment_shipTrack;
                        mcContext.startActivity(new Intent(mcContext, OpenContainer.class));
                    }
                });

            } else {
                holder.btn_track_order.setVisibility(View.GONE);
                holder.order_track_note.setVisibility(View.VISIBLE);
            }
        } else {
            holder.btn_track_order.setVisibility(View.GONE);
            holder.order_track_note.setVisibility(View.GONE);
        }
    }


    private void addCatalogLis2(ArrayList<Items> catalogs, LinearLayout root) {

        ArrayList<CatalogGroup> groups = new ArrayList<>();
        for (int i = 0; i < catalogs.size(); i++) {
            Items items = catalogs.get(i);
            CatalogGroup tempGroup = new CatalogGroup(items.getOrder_item().getProduct_catalog(), items.getQty());
            int position = groups.indexOf(tempGroup);
            if (position != -1) {
                groups.get(position).setPieces(String.valueOf(Integer.parseInt(groups.get(position).getPieces()) + Integer.parseInt(items.getQty())));
                float rate = 0;
                if (items.getQty() != null && items.getRate() != null) {
                    rate = Integer.parseInt(items.getQty()) * Float.parseFloat(items.getRate());
                }
                groups.get(position).setTotal(String.valueOf(Float.parseFloat(groups.get(position).getTotal()) + rate));
            } else {
                if (items.getQty() != null && items.getRate() != null) {
                    tempGroup.setTotal(String.valueOf(Integer.parseInt(items.getQty()) * Float.parseFloat(items.getRate())));
                } else {
                    tempGroup.setTotal("0");
                }
                tempGroup.setPkg_type(items.getOrder_item().getPacking_type());
                groups.add(tempGroup);
            }
        }

        LayoutInflater vi = (LayoutInflater) mcContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        root.removeAllViews();
        for (int j = 0; j < groups.size(); j++) {
            View v = vi.inflate(R.layout.catalog_row, null);
            TextView catalogName = (TextView) v.findViewById(R.id.invoice_prod_name);
            TextView catalogValue = (TextView) v.findViewById(R.id.invoice_prod_value);
            RelativeLayout relative_pkg_type = (RelativeLayout) v.findViewById(R.id.relative_pkg_type);
            relative_pkg_type.setVisibility(View.VISIBLE);
            TextView txt_pkg_type = (TextView) v.findViewById(R.id.txt_pkg_type);
            String cname = "" + (j + 1) + ". " + groups.get(j).getProduct_catalog() + " (" + groups.get(j).getPieces() + " Pcs.)";
            catalogValue.setText("\u20B9" + groups.get(j).getTotal());
            catalogName.setText(cname);
            //txt_pkg_type.setText(groups.get(j).getPkg_type());
            // Remove by bhavik WB-1602
            relative_pkg_type.setVisibility(View.GONE);
            root.addView(v);
        }

    }


    private Boolean validate(String data) {
        return data != null && !data.equals("") && !data.equals("");
    }

    private void showHideReplacementView(final Shipments[] shipments, InvoiceViewHolder holder, final String orderStatus) {

        if (!UserInfo.getInstance(mcContext).isOrderDisabled() && shipments.length > 0) {
            if (shipments[0].getStatus().equalsIgnoreCase(Constants.SHIPMENT_DISPATCHED)) {
                holder.linear_invoice_replacement.setVisibility(View.VISIBLE);
                if (!shipments[0].isBuyer_req_to_mark_delivered()) {
                    Log.e("TAG", "showHideReplacementView:  Dispatched False" );
                    holder.txt_request_supportteam_replacement.setVisibility(View.VISIBLE);
                    holder.txt_request_supportteam_replacement_subtext.setVisibility(View.VISIBLE);
                    holder.txt_request_replacement.setVisibility(View.GONE);
                    holder.txt_request_return.setVisibility(View.GONE);
                    ClickableSpan clickableSpan = new ClickableSpan() {
                        @Override
                        public void onClick(View textView) {
                            try {
                                Log.e("TAG", "onClick: " );
                                String msg = "Please mark the shipment delivered as I need to create a return request. Order ID:" + orderId;
                                ((Activity_OrderDetailsNew) mcContext).callMarkBuyerReqDelivered(shipments[0].getId());
                                new ChatCallUtils(mcContext, ChatCallUtils.WB_CHAT_TYPE, msg);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            super.updateDrawState(ds);
                            ds.setUnderlineText(true);
                        }
                    };
                    SpannableString ss = null;
                    if (LocaleHelper.getLanguage(mcContext).equalsIgnoreCase("hi")) {
                        ss = new SpannableString(mcContext.getResources().getString(R.string.order_replace_note1));
                        try {
                            ss.setSpan(clickableSpan, ss.toString().indexOf("Support Team"), ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        ss = new SpannableString(mcContext.getResources().getString(R.string.order_replace_note1));
                        String s_link = "Request our Support Team";
                        ss.setSpan(clickableSpan, ss.toString().indexOf(s_link), ss.toString().indexOf(s_link) + s_link.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    holder.txt_request_supportteam_replacement.setMovementMethod(LinkMovementMethod.getInstance());
                    holder.txt_request_supportteam_replacement.setText(ss);
                    holder.txt_request_supportteam_replacement_subtext.setText(mcContext.getResources().getString(R.string.order_replace_note1_sub_text));
                } else {
                    holder.txt_request_supportteam_replacement.setVisibility(View.VISIBLE);
                    holder.txt_request_supportteam_replacement_subtext.setVisibility(View.VISIBLE);
                    holder.txt_request_replacement.setVisibility(View.GONE);
                    holder.txt_request_return.setVisibility(View.GONE);

                    holder.txt_request_supportteam_replacement.setText(mcContext.getResources().getString(R.string.order_replace_note2));
                    holder.txt_request_supportteam_replacement_subtext.setText(mcContext.getResources().getString(R.string.order_replace_note2_sb_text));
                }

            } else if (shipments[0].getStatus().equalsIgnoreCase(Constants.SHIPMENT_DELIVERED) && shipments[0].isRrc_eligible()) {
                holder.linear_invoice_replacement.setVisibility(View.VISIBLE);
                holder.txt_request_supportteam_replacement.setVisibility(View.GONE);
                holder.txt_request_supportteam_replacement_subtext.setVisibility(View.GONE);
                holder.txt_request_replacement.setVisibility(View.GONE);
                holder.txt_request_return.setVisibility(View.VISIBLE);
                holder.txt_request_replacement.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        createRRC(orderId, RRCHandler.RRCREQUESTTYPE.REPLACEMENT);
                    }
                });

                holder.txt_request_return.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        createRRC(orderId, RRCHandler.RRCREQUESTTYPE.RETURN);
                    }
                });
            }


        } else {
            holder.linear_invoice_replacement.setVisibility(View.GONE);
        }
    }

    public void createRRC(String orderId, RRCHandler.RRCREQUESTTYPE rrcrequesttype) {
        Bundle bundle = new Bundle();
        bundle.putString("order_id", orderId);
        Fragment_RRC_OrderItemSelection fragment_rrc_orderItemSelection = new Fragment_RRC_OrderItemSelection();
        fragment_rrc_orderItemSelection.setArguments(bundle);
        bundle.putSerializable("request_type", rrcrequesttype);
        if (rrcrequesttype == RRCHandler.RRCREQUESTTYPE.REPLACEMENT) {
            Application_Singleton.CONTAINER_TITLE = "Select items to replacement";
        } else if (rrcrequesttype == RRCHandler.RRCREQUESTTYPE.RETURN) {
            Application_Singleton.CONTAINER_TITLE = "Select items to return";
        }
        Application_Singleton.CONTAINERFRAG = fragment_rrc_orderItemSelection;
        if(mcContext instanceof Activity_OrderDetailsNew) {
            new RRCHandler((Activity) mcContext).setRrcHandlerListner((Activity_OrderDetailsNew)mcContext);
        }

        mcContext.startActivity(new Intent(mcContext, OpenContainer.class));
    }


}
