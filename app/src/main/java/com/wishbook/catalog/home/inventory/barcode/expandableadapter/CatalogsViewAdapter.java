package com.wishbook.catalog.home.inventory.barcode.expandableadapter;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;
import com.wishbook.catalog.R;
import com.wishbook.catalog.commonadapters.CatalogsAdapter;
import com.wishbook.catalog.commonmodels.responses.Response_catalog;
import com.wishbook.catalog.home.inventory.barcode.Fragment_Barcode_Dialog;
import com.wishbook.catalog.home.models.ProductObj;

import java.util.ArrayList;

/**
 * Created by root on 18/11/16.
 */
public class CatalogsViewAdapter extends ExpandableRecyclerAdapter<CatalogsViewAdapter.MyParentViewHolder,CatalogsViewAdapter.MyChildViewHolder> {
    private LayoutInflater mInflater;
    private ArrayList<Response_catalog> catalogs =new ArrayList<>();
    private Context context;
    private CatalogsAdapter catalogsAdapter;
    private FragmentManager manager;

    public CatalogsViewAdapter(Context context, ArrayList<Response_catalog> itemList,FragmentManager manager) {
        super(itemList);
        mInflater = LayoutInflater.from(context);
        catalogs=itemList;
        this.context=context;
        this.manager=manager;
    }


    @Override
    public MyParentViewHolder onCreateParentViewHolder(ViewGroup parentViewGroup) {
        View view = mInflater.inflate(R.layout.catalog_list_item, parentViewGroup, false);
        return new MyParentViewHolder(view);
    }

    @Override
    public MyChildViewHolder onCreateChildViewHolder(ViewGroup childViewGroup) {
        View view = mInflater.inflate(R.layout.product_list_item, childViewGroup, false);
        return new MyChildViewHolder(view);
    }

    @Override
    public void onBindParentViewHolder(MyParentViewHolder parentViewHolder, int position, ParentListItem parentListItem) {
        Response_catalog catalog = (Response_catalog) parentListItem;
        parentViewHolder.catalog_name.setText(catalog.getTitle());
    }

    @Override
    public void onBindChildViewHolder(MyChildViewHolder childViewHolder, int position, Object childListItem) {
        final ProductObj product = (ProductObj) childListItem;
        if(product.getBarcode()!=null)
        {
            childViewHolder.barcode.setText(product.getBarcode().toString());
            childViewHolder.add_barcode.setVisibility(View.GONE);
        }
        else
        {
            childViewHolder.add_barcode.setVisibility(View.VISIBLE);
        }

        childViewHolder.product_name.setText(product.getTitle().toString());

        childViewHolder.add_barcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Fragment_Barcode_Dialog dialog = new Fragment_Barcode_Dialog();
                Bundle bundle=new Bundle();
                bundle.putString("id",product.getId());
                dialog.setArguments(bundle);
                dialog.show(manager,"dialog");
            }
        });

    }



    public class MyParentViewHolder extends ParentViewHolder {

        private TextView catalog_name;
        private ImageView arrow;

        public MyParentViewHolder(View itemView) {
            super(itemView);
            catalog_name = (TextView) itemView.findViewById(R.id.catalog_name);
            arrow = (ImageView) itemView.findViewById(R.id.arrow);
        }

    }

    public class MyChildViewHolder extends ChildViewHolder {

        private TextView product_name;
        private TextView barcode;
        private LinearLayout container;
        private AppCompatButton add_barcode;
        public MyChildViewHolder(View itemView) {
            super(itemView);
            product_name = (TextView) itemView.findViewById(R.id.product_name);
            barcode = (TextView) itemView.findViewById(R.id.barcode_name);
            container = (LinearLayout) itemView.findViewById(R.id.barcode_name_container);
            add_barcode = (AppCompatButton) itemView.findViewById(R.id.add_barcode);

        }
    }
}
