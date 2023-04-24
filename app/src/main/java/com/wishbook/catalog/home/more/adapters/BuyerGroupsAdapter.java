package com.wishbook.catalog.home.more.adapters;

import android.content.Intent;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wishbook.catalog.Constants;
import com.wishbook.catalog.R;
import com.wishbook.catalog.commonmodels.BuyerSeg;
import com.wishbook.catalog.home.contacts.details.ActivityBuyerGroupList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by prane on 21-03-2016.
 */
public class BuyerGroupsAdapter extends RecyclerView.Adapter<BuyerGroupsAdapter.MyViewHolder> {

    private AppCompatActivity mActivity;
    private List<BuyerSeg> mBuyerGroups;
    private Fragment fragment;



    public BuyerGroupsAdapter(AppCompatActivity mActivity, ArrayList<BuyerSeg> mBuyerGroups, Fragment fragment) {
        this.mActivity = mActivity;
        this.mBuyerGroups = mBuyerGroups;
        this.fragment = fragment;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final RelativeLayout card;
        public TextView contactName;
        private LinearLayout chat;
        private TextView txt_members;
        public MyViewHolder(View view) {
            super(view);
            contactName = (TextView) view.findViewById(R.id.contact_name);
            card = (RelativeLayout) view.findViewById(R.id.card);
            chat = (LinearLayout) view.findViewById(R.id.chat);
            txt_members = view.findViewById(R.id.txt_members);

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.groups_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final BuyerSeg bg = mBuyerGroups.get(position);


        if(bg.getActive_buyers()!=null){
            holder.txt_members.setVisibility(View.VISIBLE);
            holder.txt_members.setText(bg.getActive_buyers() +" Members");
        } else {
            holder.txt_members.setVisibility(View.GONE);
        }


      holder.contactName.setText(bg.getSegmentation_name());
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent buyerIntent = new Intent(mActivity,ActivityBuyerGroupList.class);
                buyerIntent.putExtra("id",bg.getId());
                buyerIntent.putExtra("name",bg.getSegmentation_name());
                if(!bg.getBuyer_grouping_type().equals(Constants.LOCATION_WISE_GROUP)){
                    buyerIntent.putExtra("isCustomGroup",true);
                }
               /* if(bg.getBuyers().size()>0 || bg.getState().size()>0 || bg.getCity().size()>0 || bg.getCategory().size() > 0){


                }*/
                fragment.startActivityForResult(buyerIntent,300);

            }
        });

        if (bg.getApplozic_id()!=null) {
            holder.chat.setVisibility(View.GONE);
        }
        else
        {
            holder.chat.setVisibility(View.GONE);
        }



       /* holder.chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bg.getApplozic_id()!=null) {
                    holder.chat.setVisibility(View.VISIBLE);
                    Intent intent = new Intent(mActivity, ConversationActivity.class);
                    intent.putExtra(ConversationUIService.GROUP_ID, Integer.parseInt(bg.getApplozic_id()));      //Pass group id here.
                    mActivity.startActivity(intent);
                }
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return mBuyerGroups.size();
    }



}