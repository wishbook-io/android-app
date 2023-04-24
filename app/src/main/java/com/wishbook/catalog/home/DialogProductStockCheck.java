package com.wishbook.catalog.home;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wishbook.catalog.R;
import com.wishbook.catalog.commonadapters.ProductStockCheckAdapter;
import com.wishbook.catalog.commonmodels.responses.Response_catalogMini;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DialogProductStockCheck extends DialogFragment {

    @BindView(R.id.recycler_view_stock)
    RecyclerView recycler_view_stock;

    public DialogProductStockCheck() {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_product_stock_check);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        setCancelable(false);
        ButterKnife.bind(this, dialog);
        initView();
        setData();
        return dialog;
    }


    public void initView(){
        recycler_view_stock.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler_view_stock.setHasFixedSize(true);
        recycler_view_stock.setNestedScrollingEnabled(false);
    }


    public void setData(){
        ArrayList<Response_catalogMini> arrayList = (ArrayList<Response_catalogMini>) getArguments().getSerializable("catalog");
        ProductStockCheckAdapter productStockCheckAdapter = new ProductStockCheckAdapter(arrayList,getActivity(),this);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(500);
        itemAnimator.setRemoveDuration(500);
        recycler_view_stock.setItemAnimator(itemAnimator);
        recycler_view_stock.setAdapter(productStockCheckAdapter);
    }



}
