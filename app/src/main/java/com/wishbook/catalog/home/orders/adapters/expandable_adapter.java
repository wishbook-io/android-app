package com.wishbook.catalog.home.orders.adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;

import com.facebook.drawee.view.SimpleDraweeView;
import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonmodels.responses.Response_Product;
import com.wishbook.catalog.home.orders.expandable_recyclerview.CatalogListItem;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by root on 24/9/16.
 */
public class expandable_adapter extends ExpandableRecyclerAdapter<expandable_adapter.MyParentViewHolder, expandable_adapter.MyChildViewHolder> {
    private LayoutInflater mInflater;
    private Context mContext;

    public expandable_adapter(Context context, List<CatalogListItem> itemList) {
        super(itemList);
        mInflater = LayoutInflater.from(context);
        this.mContext=context;
    }

    @Override
    public MyParentViewHolder onCreateParentViewHolder(ViewGroup viewGroup) {
        View view = mInflater.inflate(R.layout.orderwise_catalog_layout, viewGroup, false);
        return new MyParentViewHolder(view);
    }

    @Override
    public MyChildViewHolder onCreateChildViewHolder(ViewGroup viewGroup) {
        View view = mInflater.inflate(R.layout.productitem, viewGroup, false);
        return new MyChildViewHolder(view);
    }



    @Override
    public void onBindParentViewHolder(final MyParentViewHolder parentViewHolder, int position, final ParentListItem parentListItem) {
        CatalogListItem subcategoryParentListItem = (CatalogListItem) parentListItem;
        parentViewHolder.catalog.setText(subcategoryParentListItem.getCatalog().toString());
        parentViewHolder.brand.setText("("+subcategoryParentListItem.getBrand().toString()+")");
        parentViewHolder.quantity.setText(subcategoryParentListItem.getPcs().toString()+" Pcs");

        parentViewHolder.onExpansionToggled(true);
        parentViewHolder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(parentViewHolder.isExpanded())
                {
                    parentViewHolder.image.setImageResource(R.drawable.collapse);
                }
                else
                {
                    parentViewHolder.image.setImageResource(R.drawable.expand);
                }
            }
        });


    }


    @Override
    public void onBindChildViewHolder(MyChildViewHolder childViewHolder, final int position, Object childListItem) {
        final Response_Product subcategoryChildListItem = (Response_Product) childListItem;

        if (subcategoryChildListItem.getProduct_title() != null && !subcategoryChildListItem.getProduct_title().equals("null") && !subcategoryChildListItem.getProduct_title().equals("")){
                childViewHolder.prod_name.setVisibility(View.VISIBLE);
                childViewHolder.prod_name.setText(StringUtils.capitalize(subcategoryChildListItem.getProduct_title()));
            }else{
            childViewHolder.prod_name.setVisibility(View.GONE);
            }


        childViewHolder.prod_amount.setText("\u20B9" +subcategoryChildListItem.getRate());
        childViewHolder.prod_quantity.setText(subcategoryChildListItem.getQuantity()+" Pcs");
        String image = subcategoryChildListItem.getProduct_image();
       if (image != null & !image.equals("")) {
           // StaticFunctions.loadImage(mContext,image,childViewHolder.prod_img,R.drawable.uploadempty);
           StaticFunctions.loadFresco(mContext,image,childViewHolder.prod_img);
           // Picasso.with(mContext).load(image).into(childViewHolder.prod_img);
        }
       /* childViewHolder.prod_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showImage(subcategoryChildListItem.getProduct_image());
                //hyperlinking
                StaticFunctions.hyperLinking("product",subcategoryChildListItem.getProduct(),mContext);
            }
        });
        //hyperlinking
        childViewHolder.product_main_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticFunctions.hyperLinking("product",subcategoryChildListItem.getProduct(),mContext);
            }
        });*/

    }


    public void showImage(String imagepath) {
        Dialog mSplashDialog = new Dialog(mContext, R.style.AppTheme_Dark_Dialog);
        mSplashDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mSplashDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mSplashDialog.setContentView(R.layout.imagedialog);
        SimpleDraweeView image = (SimpleDraweeView) mSplashDialog.findViewById(R.id.myimg);
        if (imagepath != null & !imagepath.equals("")) {
            //StaticFunctions.loadImage(mContext,imagepath,image,R.drawable.uploadempty);
            //Picasso.with(mContext).load(imagepath).into(image);
            StaticFunctions.loadFresco(mContext,imagepath,image);
        }
        mSplashDialog.setCancelable(true);
        mSplashDialog.show();
    }

    public class MyParentViewHolder extends ParentViewHolder {

        public TextView catalog;
        public ImageView mArrowExpandImageView;
        public ImageView image;
        public TextView brand;
        public TextView quantity;

        public MyParentViewHolder(View itemView) {
            super(itemView);
            catalog = (TextView) itemView.findViewById(R.id.catalog_name);
            image = (ImageView) itemView.findViewById(R.id.image);
            brand = (TextView) itemView.findViewById(R.id.brand_name);
            quantity = (TextView) itemView.findViewById(R.id.quantity);
            mArrowExpandImageView = (ImageView) itemView.findViewById(R.id.list_item_parent_horizontal_arrow_imageView);


        }

        @SuppressLint("NewApi")
        @Override
        public void setExpanded(boolean expanded) {
            super.setExpanded(expanded);

                if (expanded) {
                    image.setImageResource(R.drawable.collapse);
                } else {
                    image.setImageResource(R.drawable.expand);
                }
            if (expanded) {
                mArrowExpandImageView.setRotation(180);
            } else {
                mArrowExpandImageView.setRotation(0);
            }

        }

    }


    public class MyChildViewHolder extends ChildViewHolder {

        private final SimpleDraweeView prod_img;
        private final TextView prod_name;
        private final TextView prod_amount;
        private final TextView prod_quantity;
        private final LinearLayout product_main_container;

        public MyChildViewHolder(View itemView) {
            super(itemView);

            prod_img = (SimpleDraweeView) itemView.findViewById(R.id.prod_img);
            prod_name = (TextView) itemView.findViewById(R.id.prod_name);
            prod_amount = (TextView) itemView.findViewById(R.id.prod_amount);
            prod_quantity = (TextView) itemView.findViewById(R.id.prod_quantity);
            product_main_container = (LinearLayout) itemView.findViewById(R.id.product_main_container);
        }
    }
}