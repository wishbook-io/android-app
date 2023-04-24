package com.wishbook.catalog.home.more.invoice;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.widget.TextView;

import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.RecyclerViewEmptySupport;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.home.more.adapters.treeview.Invoice_details_item_Adapter;

import org.apache.commons.lang3.text.WordUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by root on 10/10/16.
 */
public class InvoiceDetails extends AppCompatActivity {

    @BindView(R.id.invoice_value)
    TextView invoice_number;


    @BindView(R.id.date)
    TextView date;

    @BindView(R.id.total_credit)
    TextView total_credit;

    @BindView(R.id.billing_amount)
    TextView billing_amount;

    @BindView(R.id.balancing_amount)
    TextView balancing_amount;

    @BindView(R.id.status)
    TextView status;


    private RecyclerViewEmptySupport mRecyclerView;
    private Invoice_details_item_Adapter detailAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_invoice_details);

        StaticFunctions.initializeAppsee();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Invoice Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        ButterKnife.bind(this);
        mRecyclerView = (RecyclerViewEmptySupport) findViewById(R.id.recycler_view);
        mRecyclerView.setEmptyView(findViewById(R.id.list_empty1));
        LinearLayoutManager layoutManager = new LinearLayoutManager(InvoiceDetails.this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        detailAdapter = new Invoice_details_item_Adapter(InvoiceDetails.this,Application_Singleton.selectedInvoice.getInvoiceitem_set());
        mRecyclerView.setAdapter(detailAdapter);
        setValues();
    }


    private void setValues() {
        invoice_number.setText("#" + Application_Singleton.selectedInvoice.getId());
        //invoice_period.setText(Application_Singleton.selectedInvoice.get);
        date.setText("From " + getformatedDate(Application_Singleton.selectedInvoice.getStart_date())+" To "+ getformatedDate(Application_Singleton.selectedInvoice.getEnd_date()));
        status.setText(WordUtils.capitalize(Application_Singleton.selectedInvoice.getStatus()));
        total_credit.setText(Application_Singleton.selectedInvoice.getTotal_credit());
        balancing_amount.setText("\u20B9" +Application_Singleton.selectedInvoice.getBalance_amount());
        billing_amount.setText("\u20B9" +Application_Singleton.selectedInvoice.getBilled_amount());

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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
