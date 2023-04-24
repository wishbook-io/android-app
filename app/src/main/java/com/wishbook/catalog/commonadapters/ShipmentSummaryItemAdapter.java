package com.wishbook.catalog.commonadapters;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wishbook.catalog.R;
import com.wishbook.catalog.commonmodels.Payments;
import com.wishbook.catalog.commonmodels.postpatchmodels.Invoice;
import com.wishbook.catalog.commonmodels.postpatchmodels.Shipments;
import com.wishbook.catalog.home.orders.details.Activity_OrderDetails;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by root on 10/8/17.
 */

public class ShipmentSummaryItemAdapter extends RecyclerView.Adapter<ShipmentSummaryItemAdapter.MyViewHolder> {

    private static final float AMOUNT_LIMIT_PAYMENT_OPTION = 10000;
    private AppCompatActivity mActivity;
    private ArrayList<Invoice> mlist;
    private String type;


    public ShipmentSummaryItemAdapter(AppCompatActivity mActivity, ArrayList<Invoice> mBuyerGroups, String purchase) {
        this.mActivity = mActivity;
        this.mlist = mBuyerGroups;
        type = purchase;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView dispatched_items, gst_item,
                shipment_mode, shipment_transporter,
                shipment_details, shipment_number,
                shipment_date, shipment;
        public TextView payment_mode, payment_amount,
                payment_transaction_reference, payment_success,
                payment_date, payment,invoiced_items,total_amount,invoice_gst,invoice;
        LinearLayout payment_container, button_container,shipment_container;
        AppCompatButton invoice_pay, invoice_cancel,invoice_dispatch;
        FrameLayout container_pay_dispatch;

        public MyViewHolder(View view) {
            super(view);
            dispatched_items = (TextView) view.findViewById(R.id.dispatched_items);
            gst_item = (TextView) view.findViewById(R.id.gst_item);
            shipment_mode = (TextView) view.findViewById(R.id.shipment_mode);
            shipment_transporter = (TextView) view.findViewById(R.id.shipment_transporter);
            shipment_details = (TextView) view.findViewById(R.id.shipment_details);
            shipment_number = (TextView) view.findViewById(R.id.shipment_number);
            shipment_date = (TextView) view.findViewById(R.id.shipment_date);
            shipment = (TextView) view.findViewById(R.id.shipment);

            payment = (TextView) view.findViewById(R.id.payment);
            payment_date = (TextView) view.findViewById(R.id.payment_date);
            payment_success = (TextView) view.findViewById(R.id.payment_success);
            payment_transaction_reference = (TextView) view.findViewById(R.id.payment_transaction_reference);
            payment_amount = (TextView) view.findViewById(R.id.payment_amount);
            payment_mode = (TextView) view.findViewById(R.id.payment_mode);

            invoice = (TextView) view.findViewById(R.id.invoice);

            invoiced_items = (TextView) view.findViewById(R.id.invoiced_items);
            total_amount = (TextView) view.findViewById(R.id.total_amount);
            invoice_gst = (TextView) view.findViewById(R.id.invoice_gst);


            invoice_pay = (AppCompatButton) view.findViewById(R.id.invoice_pay);
            invoice_cancel = (AppCompatButton) view.findViewById(R.id.invoice_cancel);
            invoice_dispatch = (AppCompatButton) view.findViewById(R.id.invoice_dispatch);

            payment_container = (LinearLayout) view.findViewById(R.id.payment_container);


            button_container = (LinearLayout) view.findViewById(R.id.button_container);
            shipment_container = (LinearLayout) view.findViewById(R.id.shipment_container);
            container_pay_dispatch = (FrameLayout) view.findViewById(R.id.container_pay_dispatch);

        }
    }

    @Override
    public ShipmentSummaryItemAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shipment_item_list, parent, false);

        return new ShipmentSummaryItemAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ShipmentSummaryItemAdapter.MyViewHolder holder, int position) {


        final Invoice invoice = mlist.get(position);

        String status = invoice.getStatus();

        holder.invoice.setText("Invoice #" + (position+1));
        holder.invoiced_items.setText(invoice.getTotal_qty());
        holder.invoice_gst.setText("\u20B9"+invoice.getTaxes());
        holder.total_amount.setText("\u20B9"+invoice.getAmount());


        holder.container_pay_dispatch.setVisibility(View.VISIBLE);


        if (status.equals("Invoiced")) {



            if(type.equals("sales")) {

                holder.invoice_dispatch.setVisibility(View.VISIBLE);
                holder.invoice_cancel.setVisibility(View.VISIBLE);
            }
            else {
                if(invoice.getPayment_status().equals("Paid")){
                    holder.invoice_cancel.setVisibility(View.VISIBLE);
                    holder.invoice_pay.setVisibility(View.GONE);
                }
                else {
                    holder.invoice_cancel.setVisibility(View.VISIBLE);
                    holder.invoice_pay.setVisibility(View.VISIBLE);
                    holder.container_pay_dispatch.setVisibility(View.VISIBLE);
                }

            }



        } else if (status.equals("Dispatched") && !invoice.getPayment_status().equals("Paid")) {
            holder.invoice_cancel.setVisibility(View.GONE);
            holder.invoice_dispatch.setVisibility(View.GONE);
            if(!type.equals("sales")) {
                holder.invoice_pay.setVisibility(View.VISIBLE);
            }

        }else{
            holder.invoice_cancel.setVisibility(View.GONE);
            holder.invoice_pay.setVisibility(View.GONE);
            holder.invoice_dispatch.setVisibility(View.GONE);
        }

        holder.dispatched_items.setText(invoice.getTotal_qty());

        if(invoice.getTaxes()!=null && !invoice.getTaxes().equals("null") && !invoice.getTaxes().equals("")) {
            holder.gst_item.setText("\u20B9" + invoice.getTaxes());
        }else{
            holder.gst_item.setText("\u20B9" +"0");
        }


        if (invoice.getShipments() != null && invoice.getShipments().length > 0) {
            holder.shipment_container.setVisibility(View.VISIBLE);
            Shipments shipments = invoice.getShipments()[0];
            holder.shipment.setText("Shipment #" + (position+1));

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

            //date
            if (!currentISTDate(shipments.getDatetime()).equals("")) {
                holder.shipment_date.setVisibility(View.VISIBLE);
                holder.shipment_date.setText("Date : " + currentISTDate(shipments.getDatetime()));
            } else {
                holder.shipment_date.setVisibility(View.GONE);
            }
        }else{
            holder.shipment_container.setVisibility(View.GONE);
        }


        if (invoice.getPayments() != null && invoice.getPayments().length > 0) {
            holder.payment_container.setVisibility(View.VISIBLE);

            Payments payments = invoice.getPayments()[0];

            holder.payment.setText("Payment #" + (position+1));
            //mode
            if (validate(payments.getMode())) {
                holder.payment_mode.setVisibility(View.VISIBLE);
                holder.payment_mode.setText("Mode : " + payments.getMode());
            } else {
                holder.payment_mode.setVisibility(View.GONE);
            }

            //transporter
            if (validate(payments.getAmount())) {
                holder.payment_amount.setVisibility(View.VISIBLE);
                holder.payment_amount.setText("Amount : \u20B9" + payments.getAmount());
            } else {
                holder.payment_amount.setVisibility(View.GONE);
            }

            //number
            if (validate(payments.getStatus())) {
                holder.payment_success.setVisibility(View.VISIBLE);
                holder.payment_success.setText("Status : " + payments.getStatus());
            } else {
                holder.payment_success.setVisibility(View.GONE);
            }

            //details
            if (validate(payments.getTransaction_reference())) {
                holder.payment_transaction_reference.setVisibility(View.VISIBLE);
                holder.payment_transaction_reference.setText("Transaction-Reference : " + payments.getTransaction_reference());
            } else {
                holder.payment_transaction_reference.setVisibility(View.GONE);
            }

            //date
            if (!currentISTDate(payments.getDatetime()).equals("")) {
                holder.payment_date.setVisibility(View.VISIBLE);
                holder.payment_date.setText("Date : " + currentISTDate(payments.getDatetime()));
            } else {
                holder.payment_date.setVisibility(View.GONE);
            }
        } else {
            holder.payment_container.setVisibility(View.GONE);
        }

        holder.invoice_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Activity_OrderDetails)mActivity).paymentOptions("invoice",invoice.getId(),invoice.getTotal_amount());
            }
        });

        holder.invoice_dispatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Activity_OrderDetails)mActivity).dispatchOrder(invoice.getId(),"invoice");
            }
        });

        holder.invoice_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Activity_OrderDetails)mActivity).cancelInvoice("create_invoice",invoice.getId());
            }
        });


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


    private Boolean validate(String data) {
        return data != null && !data.equals("") && !data.equals("");
    }


    @Override
    public int getItemCount() {
        return mlist.size();
    }


}
