package com.wishbook.catalog.home.inventory.barcode.expandableadapter;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;
import com.wishbook.catalog.R;
import com.wishbook.catalog.home.inventory.barcode.Fragment_Barcode_Dialog;
import com.wishbook.catalog.home.models.ProductObj;

import java.util.ArrayList;

/**
 * Created by root on 18/11/16.
 */
public class ProductAdapterExpandable extends RecyclerView.Adapter<ProductAdapterExpandable.MyParentViewHolder> {
    private LayoutInflater mInflater;
    private ArrayList<ProductObj> products =new ArrayList<>();
    private Context context;
    private FragmentManager manager;
    private Fragment fragment;

    public ProductAdapterExpandable(Context context, ArrayList<ProductObj> itemList,FragmentManager manager) {
        mInflater = LayoutInflater.from(context);
        products=itemList;
        this.context=context;
        this.manager=manager;


    }


    @Override
    public MyParentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.product_list_item, parent, false);
        return new MyParentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyParentViewHolder childViewHolder, int position) {
        if(products.get(position).getBarcode()!=null)
        {
            childViewHolder.barcode.setText(products.get(position).getBarcode().toString());
            childViewHolder.barcode_name_container.setVisibility(View.VISIBLE);
            childViewHolder.add_barcode.setVisibility(View.GONE);
        }
        else
        {
            childViewHolder.barcode_name_container.setVisibility(View.GONE);
            childViewHolder.add_barcode.setVisibility(View.VISIBLE);
        }

        childViewHolder.product_name.setText(products.get(position).getTitle().toString());

        childViewHolder.add_barcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                Fragment_Barcode_Dialog dialog = new Fragment_Barcode_Dialog();
                Bundle bundle=new Bundle();
                bundle.putString("id",products.get(childViewHolder.getAdapterPosition()).getId());
                dialog.setArguments(bundle);
                dialog.setTargetFragment(fragment,1);
                dialog.show(manager,"dialog");
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }


    public class MyParentViewHolder extends ParentViewHolder{

        private TextView product_name;
        public TextView barcode;
        private LinearLayout barcode_name_container;
        private AppCompatButton add_barcode;
        public MyParentViewHolder(View itemView) {
            super(itemView);

            product_name = (TextView) itemView.findViewById(R.id.product_name);
            barcode = (TextView) itemView.findViewById(R.id.barcode_name);
            barcode_name_container = (LinearLayout) itemView.findViewById(R.id.barcode_name_container);
            add_barcode = (AppCompatButton) itemView.findViewById(R.id.add_barcode);

        }

    }


}
