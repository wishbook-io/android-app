package com.wishbook.catalog.home.more.visits.SecondLevelExpandableView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;
import com.wishbook.catalog.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by root on 28/10/16.
 */
public class CityExpandableView extends ExpandableRecyclerAdapter<CityExpandableView.MyParentViewHolder, CityExpandableView.MyChildViewHolder> {

    private LayoutInflater mInflater;
    private Context mContext;
    private Dialog dialog;
    private AdapterCallback callback;

    public CityExpandableView(Context context, List<Cities> itemList, Dialog dialog, AdapterCallback callback) {
        super(itemList);
        mInflater = LayoutInflater.from(context);
        this.mContext=context;
        this.dialog = dialog;
        this.callback = callback;
    }

    @Override
    public MyParentViewHolder onCreateParentViewHolder(ViewGroup viewGroup) {
        View view = mInflater.inflate(R.layout.citywise_buyer_layout, viewGroup, false);
        return new MyParentViewHolder(view);
    }

    @Override
    public MyChildViewHolder onCreateChildViewHolder(ViewGroup viewGroup) {
        View view = mInflater.inflate(R.layout.buyer_list, viewGroup, false);
        return new MyChildViewHolder(view);
    }

    @Override
    public void onBindParentViewHolder(CityExpandableView.MyParentViewHolder parentViewHolder, int position, ParentListItem parentListItem) {
        Cities city_list = (Cities) parentListItem;
        parentViewHolder.City.setText(city_list.getName());
    }

    @Override
    public void onBindChildViewHolder(CityExpandableView.MyChildViewHolder childViewHolder, int position, Object childListItem) {
            final Buyers buyer_list = (Buyers) childListItem;
            childViewHolder.buyer.setText(buyer_list.getBuying_company__name());
        childViewHolder.buyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("buyer_id",buyer_list.getBuying_company__id());
                params.put("buyer_name",buyer_list.getBuying_company__name());

                if(callback != null) {

                    callback.getData(params);
                }

              //  Toast.makeText(mContext,"id : "+ buyer_list.getBuying_company__id() + " name : " + buyer_list.getBuying_company__name(),Toast.LENGTH_LONG).show();
            }
        });
    }

    public class MyParentViewHolder extends ParentViewHolder {

        public TextView City;
        public ImageView image;
        public ImageView mArrowExpandImageView;

        public MyParentViewHolder(View itemView) {
            super(itemView);

            City = (TextView) itemView.findViewById(R.id.city_name);
            image = (ImageView) itemView.findViewById(R.id.image);
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

        public TextView buyer;

        public MyChildViewHolder(View itemView) {
            super(itemView);
            buyer = (TextView) itemView.findViewById(R.id.buyer_name);
        }
    }


}
