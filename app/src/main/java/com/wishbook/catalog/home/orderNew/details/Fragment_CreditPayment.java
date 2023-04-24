package com.wishbook.catalog.home.orderNew.details;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.commonmodels.postpatchmodels.Invoice;
import com.wishbook.catalog.commonmodels.responses.Response_Product;
import com.wishbook.catalog.commonmodels.responses.Response_buyingorder;
import com.wishbook.catalog.commonmodels.responses.Response_sellingoder_catalog;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


public class Fragment_CreditPayment extends GATrackedFragment {


    private View view;
    // invoice Detail
    @BindView(R.id.invoice_order_no)
    TextView txtInvoiceOrderNo;
    @BindView(R.id.invoice_supplier_name)
    TextView txtInvoiceSellerName;
    @BindView(R.id.invoice_order_date)
    TextView txtInvoiceOrderDate;
    @BindView(R.id.invoice_catalog_container)
    LinearLayout invoice_catalog_container;
    @BindView(R.id.txt_payable_amt)
    TextView txtPayableAmt;

    // payment Detail
    @BindView(R.id.radio_onCredit)
    RadioButton radioOnCredit;

    @BindView(R.id.btn_payment_credit)
    AppCompatButton btnPaymentCredit;


    Invoice invoice;

    public Fragment_CreditPayment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_credit_payment, container, false);
        ButterKnife.bind(this, view);
        radioOnCredit.setChecked(true);
        if (Application_Singleton.purchaseInvoice != null) {
            invoice = Application_Singleton.purchaseInvoice;
        }
        if (Application_Singleton.selectedOrder instanceof Response_buyingorder) {
            Response_buyingorder order = (Response_buyingorder) Application_Singleton.selectedOrder;
            txtInvoiceOrderNo.setText(order.getOrder_number());
            if (order.getSeller_company_name() != null)
                txtInvoiceSellerName.setText(StringUtils.capitalize(order.getSeller_company_name().toLowerCase().trim()));
            txtInvoiceOrderDate.setText(getformatedDate(order.getDate()));
            addCatalogList(order.getCatalogs(), invoice_catalog_container);
            if (order.getInvoice() != null && order.getInvoice().size() > 0) {
                txtPayableAmt.setText(invoice.getTotal_amount());
            }
        }
        return view;
    }

    private void addCatalogList(ArrayList<Response_sellingoder_catalog> catalogs, LinearLayout root) {
        LayoutInflater vi = (LayoutInflater) getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.catalog_row, null);
        for (int i = 0; i < catalogs.size(); i++) {
            TextView catalogName = (TextView) v.findViewById(R.id.invoice_prod_name);
            TextView catalogValue = (TextView) v.findViewById(R.id.invoice_prod_value);
            String cname = "" + (i + 1) + ". " + catalogs.get(i).getName() + " (" + catalogs.get(i).getTotal_products() + "Pcs.)";
            float totalPriceCatalog = 0;
            for (int j = 0; j < catalogs.get(i).getProducts().size(); j++) {
                Response_Product product = catalogs.get(i).getProducts().get(j);
                totalPriceCatalog += Float.parseFloat(product.getRate()) * Integer.parseInt(product.getQuantity());
            }
            catalogValue.setText(String.valueOf(totalPriceCatalog) + "\u20B9");
            catalogName.setText(cname);
            root.removeAllViews();
            root.addView(v);
        }
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
}
