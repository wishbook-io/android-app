package com.wishbook.catalog.home.cart;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wishbook.catalog.GATrackedFragment;
import com.wishbook.catalog.R;
import com.wishbook.catalog.commonmodels.CartCatalogModel;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import static com.facebook.FacebookSdk.getApplicationContext;

public class Fragment_Cart_Invoice extends GATrackedFragment {

    LinearLayoutManager mLayoutManager;
    InvoiceAdapter invoiceAdapter;
    LinearLayout layout_hide_invoice;
    TextView header_amount, header_date;

    @NonNull
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        assert v != null;
        ViewGroup ga_container = v.findViewById(R.id.ga_container);
        inflater.inflate(R.layout.fragment_cart_invoice, ga_container, true);
        initView(v);
        return v;
    }

    void initView(View v) {
        try {
            layout_hide_invoice = v.findViewById(R.id.layout_hide_invoice);
            header_amount = v.findViewById(R.id.header_amount);
            header_date = v.findViewById(R.id.header_date);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            SimpleDateFormat sdfLocal = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            header_amount.setText("\u20B9 " + ((CartCatalogModel) getArguments().getSerializable("data")).getTotal_amount());
            String date = sdfLocal.format(sdf.parse(((CartCatalogModel) getArguments().getSerializable("data")).getCreated()));
            header_date.setText(date);
            RecyclerView recyclerView = v.findViewById(R.id.invoice_items);
            if (getArguments().getSerializable("data") != null) {
                invoiceAdapter = new InvoiceAdapter(getActivity(),
                        (CartCatalogModel) getArguments().getSerializable("data"), getArguments().getBoolean("isWishbookTransport", true)
                        ,getArguments().getBoolean("isBuyonCredit",false),
                        getArguments().getBoolean("isWbMoneyApplied", false));
                mLayoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(invoiceAdapter);
            }
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (mLayoutManager.findFirstVisibleItemPosition() == 0) {
                        layout_hide_invoice.setVisibility(View.GONE);
                    } else {
                        layout_hide_invoice.setVisibility(View.VISIBLE);
                    }

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
