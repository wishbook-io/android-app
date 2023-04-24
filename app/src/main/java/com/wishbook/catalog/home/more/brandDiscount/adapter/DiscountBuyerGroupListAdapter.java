package com.wishbook.catalog.home.more.brandDiscount.adapter;


import android.content.Context;
import com.google.android.material.textfield.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;
import com.wishbook.catalog.R;
import com.wishbook.catalog.commonmodels.responses.Response_Product;
import com.wishbook.catalog.home.orders.expandable_recyclerview.CatalogListItem;

import java.util.List;

public class DiscountBuyerGroupListAdapter extends ExpandableRecyclerAdapter<DiscountBuyerGroupListAdapter.MyParentViewHolder, DiscountBuyerGroupListAdapter.MyChildViewHolder> {
    private LayoutInflater mInflater;
    private Context mContext;

    public DiscountBuyerGroupListAdapter(Context context, List<CatalogListItem> itemList) {
        super(itemList);
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
    }

    @Override
    public DiscountBuyerGroupListAdapter.MyParentViewHolder onCreateParentViewHolder(ViewGroup viewGroup) {
        View view = mInflater.inflate(R.layout.fabric_select_item, viewGroup, false);
        return new DiscountBuyerGroupListAdapter.MyParentViewHolder(view);
    }

    @Override
    public DiscountBuyerGroupListAdapter.MyChildViewHolder onCreateChildViewHolder(ViewGroup viewGroup) {
        View view = mInflater.inflate(R.layout.fabric_select_item, viewGroup, false);
        return new DiscountBuyerGroupListAdapter.MyChildViewHolder(view);
    }


    @Override
    public void onBindParentViewHolder(final DiscountBuyerGroupListAdapter.MyParentViewHolder parentViewHolder, int position, final ParentListItem parentListItem) {

    }


    @Override
    public void onBindChildViewHolder(DiscountBuyerGroupListAdapter.MyChildViewHolder childViewHolder, final int position, Object childListItem) {
        final Response_Product subcategoryChildListItem = (Response_Product) childListItem;


    }


    public class MyParentViewHolder extends ParentViewHolder {

        private CheckBox checkbox;
        private TextInputLayout txt_other_input_value;

        public MyParentViewHolder(View itemView) {
            super(itemView);
            checkbox = (CheckBox) itemView.findViewById(R.id.checkbox);
            txt_other_input_value = (TextInputLayout) itemView.findViewById(R.id.txt_other_input_value);
        }
    }


    public class MyChildViewHolder extends ChildViewHolder {

        private CheckBox checkbox;
        private TextInputLayout txt_other_input_value;
        EditText input_other;

        public MyChildViewHolder(View itemView) {
            super(itemView);
            checkbox = (CheckBox) itemView.findViewById(R.id.checkbox);
            txt_other_input_value = (TextInputLayout) itemView.findViewById(R.id.txt_other_input_value);
            input_other = (EditText) itemView.findViewById(R.id.input_other);
        }
    }
}