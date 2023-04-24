package com.wishbook.catalog.home.orders.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wishbook.catalog.R;

import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class InvoiceViewHolder extends RecyclerView.ViewHolder {
    public TextView dispatched_items, gst_item,
            shipment_mode, shipment_transporter,
            shipment_details, shipment_number,
            shipment_date, shipment;
    public RelativeLayout relativeSellerDiscount, relativeTaxClass1, relativeTaxClass2, paid_amt_container, payble_amt_container;
    public LinearLayout invoice_catalog_container, linear_shipping_container, shipment_date_container, linear_dispatch_details;
    public AppCompatButton bntPay, btnDispatch, btnCancel, btn_order_received;

    public LinearLayout shipment_container, linear_wb_money_container, linear_reward_point_container;
    public TextView invoiceOrderNo, invoiceSellerName, invoiceOrdeDate,
            invoicePayableAmt, invoicePaidAmt, txtSellerDiscount,
            taxClass1Name, txtClass1Value, taxClass2Name, txtClass2Value, txtSupplierText,
            txt_shipping_amount, invoice_number, txt_wishbook_amount;


    public ImageView img_step_one, img_step_two, img_step_three, img_step_four;

    public View img_view_step_one, img_view_step_two, img_view_step_three, txt_view_step_one, txt_view_step_two, txt_view_step_three;

    public TextView txt_step_one, txt_step_two, txt_step_three, txt_step_four;

    public TextView btn_track_order, order_track_note, txt_reward_point;

    public LinearLayout linear_main_order_track;

    public View view_reseller;

    public LinearLayout linear_reseller_display_amount, linear_dispatch_pending;

    public TextView txt_display_amount, txt_invoice_download;

    @BindView(R.id.linear_invoice_replacement)
    public
    LinearLayout linear_invoice_replacement;

    @BindView(R.id.txt_request_supportteam_replacement)
    public TextView txt_request_supportteam_replacement;

    @BindView(R.id.txt_request_supportteam_replacement_subtext)
    public TextView txt_request_supportteam_replacement_subtext;

    @BindView(R.id.txt_request_replacement)
    public TextView txt_request_replacement;

    @BindView(R.id.txt_request_return)
    public TextView txt_request_return;

    @BindView(R.id.linear_coupon_container)
    public LinearLayout linear_coupon_container;

    @BindView(R.id.txt_coupon_price)
    public TextView txt_coupon_price;

    @BindView(R.id.txt_coupon_label)
    public TextView txt_coupon_label;


    public InvoiceViewHolder(View view) {
        super(view);
        invoice_number = (TextView) itemView.findViewById(R.id.invoice_number);
        invoiceOrderNo = (TextView) itemView.findViewById(R.id.invoice_order_no);
        linear_shipping_container = (LinearLayout) itemView.findViewById(R.id.linear_shipping_container);
        txt_shipping_amount = (TextView) itemView.findViewById(R.id.txt_shipping_amount);
        invoiceSellerName = (TextView) itemView.findViewById(R.id.invoice_seller_name);
        invoiceOrdeDate = (TextView) itemView.findViewById(R.id.invoice_order_date);
        txtSupplierText = (TextView) itemView.findViewById(R.id.txt_supplier_text);
        txtSellerDiscount = (TextView) itemView.findViewById(R.id.txt_seller_discount);
        invoicePayableAmt = (TextView) itemView.findViewById(R.id.txt_payable_amt);
        invoicePaidAmt = (TextView) itemView.findViewById(R.id.txt_paid_amt);
        paid_amt_container = (RelativeLayout) itemView.findViewById(R.id.paid_amt_container);
        payble_amt_container = (RelativeLayout) itemView.findViewById(R.id.payble_amt_container);
        relativeSellerDiscount = (RelativeLayout) itemView.findViewById(R.id.relative_seller_discount);
        relativeTaxClass1 = (RelativeLayout) itemView.findViewById(R.id.relative_tax_class_1);
        relativeTaxClass2 = (RelativeLayout) itemView.findViewById(R.id.relative_tax_class_2);
        invoice_catalog_container = (LinearLayout) itemView.findViewById(R.id.invoice_catalog_container);
        taxClass1Name = (TextView) itemView.findViewById(R.id.tax_class_1_name);
        taxClass2Name = (TextView) itemView.findViewById(R.id.tax_class_2_name);
        txtClass1Value = (TextView) itemView.findViewById(R.id.tax_class_1_value);
        txtClass2Value = (TextView) itemView.findViewById(R.id.tax_class_2_value);
        bntPay = (AppCompatButton) itemView.findViewById(R.id.btn_accept);
        btnDispatch = (AppCompatButton) itemView.findViewById(R.id.btn_dispatch);
        btnCancel = (AppCompatButton) itemView.findViewById(R.id.btn_cancel_order);


        dispatched_items = (TextView) view.findViewById(R.id.dispatched_items);
        gst_item = (TextView) view.findViewById(R.id.gst_item);
        shipment_mode = (TextView) view.findViewById(R.id.shipment_mode);
        shipment_transporter = (TextView) view.findViewById(R.id.shipment_transporter);
        shipment_details = (TextView) view.findViewById(R.id.shipment_details);
        shipment_number = (TextView) view.findViewById(R.id.shipment_number);
        shipment_date = (TextView) view.findViewById(R.id.shipment_date);
        shipment = (TextView) view.findViewById(R.id.shipment);
        shipment_container = (LinearLayout) view.findViewById(R.id.shipment_container);
        shipment_date_container = (LinearLayout) view.findViewById(R.id.shipment_date_container);
        linear_dispatch_details = (LinearLayout) view.findViewById(R.id.linear_dispatch_details);

        btn_order_received = (AppCompatButton) view.findViewById(R.id.btn_order_received);

        linear_wb_money_container = view.findViewById(R.id.linear_wb_money_container);
        txt_wishbook_amount = view.findViewById(R.id.txt_wishbook_amount);

        linear_reward_point_container = view.findViewById(R.id.linear_reward_point_container);
        txt_reward_point = view.findViewById(R.id.txt_reward_point);


        view_reseller = view.findViewById(R.id.view_reseller);
        linear_reseller_display_amount = view.findViewById(R.id.linear_reseller_display_amount);
        txt_display_amount = view.findViewById(R.id.txt_display_amount);
        linear_dispatch_pending = view.findViewById(R.id.linear_dispatch_pending);

        txt_invoice_download = view.findViewById(R.id.txt_invoice_download);

        initOrderTrack(view);
        ButterKnife.bind(this, view);
    }


    public void initOrderTrack(View view) {
        linear_main_order_track = view.findViewById(R.id.linear_main_order_track);
        img_step_one = view.findViewById(R.id.img_step_one);
        img_step_two = view.findViewById(R.id.img_step_two);
        img_step_three = view.findViewById(R.id.img_step_three);
        img_step_four = view.findViewById(R.id.img_step_four);

        txt_step_one = view.findViewById(R.id.txt_step_one);
        txt_step_two = view.findViewById(R.id.txt_step_two);
        txt_step_three = view.findViewById(R.id.txt_step_three);
        txt_step_four = view.findViewById(R.id.txt_step_four);


        img_view_step_one = view.findViewById(R.id.img_view_step_one);
        img_view_step_two = view.findViewById(R.id.img_view_step_two);
        img_view_step_three = view.findViewById(R.id.img_view_step_three);

        txt_view_step_one = view.findViewById(R.id.txt_view_step_one);
        txt_view_step_two = view.findViewById(R.id.txt_view_step_two);
        txt_view_step_three = view.findViewById(R.id.txt_view_step_three);

        btn_track_order = view.findViewById(R.id.btn_track_order);

        order_track_note = view.findViewById(R.id.order_track_note);

    }

}