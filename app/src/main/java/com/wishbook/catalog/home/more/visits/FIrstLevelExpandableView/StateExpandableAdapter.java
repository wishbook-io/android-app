package com.wishbook.catalog.home.more.visits.FIrstLevelExpandableView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import com.wishbook.catalog.home.more.visits.SecondLevelExpandableView.AdapterCallback;
import com.wishbook.catalog.home.more.visits.SecondLevelExpandableView.Cities;
import com.wishbook.catalog.home.more.visits.SecondLevelExpandableView.CityExpandableView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 28/10/16.
 */
public class StateExpandableAdapter extends ExpandableRecyclerAdapter<StateExpandableAdapter.MyParentViewHolder, StateExpandableAdapter.MyChildViewHolder> {

    private LayoutInflater mInflater;
    private Context mContext;
    private CityExpandableView city_view_adapter;
    private List<Cities> cities = new ArrayList<>();
    private Dialog dialog;
    private AdapterCallback callback;

    public StateExpandableAdapter(Context context, List<StateListItem> itemList, Dialog dialog) {
        super(itemList);
        mInflater = LayoutInflater.from(context);
        this.mContext=context;
        this.dialog=dialog;
    }

    @Override
    public MyParentViewHolder onCreateParentViewHolder(ViewGroup parentViewGroup) {
        View view = mInflater.inflate(R.layout.statewise_layout, parentViewGroup, false);
        return new MyParentViewHolder(view);
    }

    public void setCallbackAdapter(AdapterCallback callback){
        this.callback = callback;
    }

    @Override
    public MyChildViewHolder onCreateChildViewHolder(ViewGroup childViewGroup) {
        View view = mInflater.inflate(R.layout.citywise_list_buyer_layout, childViewGroup, false);
        return new MyChildViewHolder(view);
    }

    @Override
    public void onBindParentViewHolder(final MyParentViewHolder parentViewHolder, int position, ParentListItem parentListItem) {
        StateListItem city_list = (StateListItem) parentListItem;
        parentViewHolder.State.setText(city_list.getState().toString());
    }

    @Override
    public void onBindChildViewHolder(final MyChildViewHolder childViewHolder, int position, Object childListItem) {
        Cities city = (Cities) childListItem;
        cities = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        childViewHolder.recyclerView.setLayoutManager(layoutManager);
        cities.add(city);
        city_view_adapter = new CityExpandableView(mContext,cities,dialog,callback);
        childViewHolder.recyclerView.setAdapter(city_view_adapter);
    }


    public class MyParentViewHolder extends ParentViewHolder {

        public TextView State;
        public ImageView mArrowExpandImageView;
        public ImageView image;

        public MyParentViewHolder(View itemView) {
            super(itemView);
            State = (TextView) itemView.findViewById(R.id.state_name);
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

        private RecyclerView recyclerView;

        public MyChildViewHolder(View itemView) {
            super(itemView);

          recyclerView = (RecyclerView) itemView.findViewById(R.id.recycler_view);


        }
    }
}
