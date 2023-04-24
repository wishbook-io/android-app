package com.wishbook.catalog.home.more.myearning.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wishbook.catalog.Application_Singleton;
import com.wishbook.catalog.OpenContainer;
import com.wishbook.catalog.R;
import com.wishbook.catalog.SplashScreen;
import com.wishbook.catalog.Utils.DateUtils;
import com.wishbook.catalog.Utils.DeepLinkFunction;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonadapters.WBMoneyAdapter;
import com.wishbook.catalog.commonmodels.ResponseWBMoneyDashboard;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.responses.ResponseWBHistory;
import com.wishbook.catalog.home.more.myearning.RewardTcBottomSheetFragment;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RewardPointAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<ResponseWBHistory> historyArrayList;
    private ResponseWBMoneyDashboard responseWBMoneyDashboard;
    UserInfo userInfo;

    public int HEADER = 0;
    public int ITEMS = 1;


    public RewardPointAdapter(Context context, ArrayList<ResponseWBHistory> historyArrayList, ResponseWBMoneyDashboard dashboard) {
        this.context = context;
        this.historyArrayList = historyArrayList;
        this.responseWBMoneyDashboard = dashboard;
        userInfo = UserInfo.getInstance(context);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reward_header_view, parent, false);
            return new RewardPointAdapter.RewardHeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reward_point_item, parent, false);
            return new RewardPointAdapter.RewardViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof RewardViewHolder) {
            final ResponseWBHistory history = historyArrayList.get(position - 1);
            if(!history.getId().equalsIgnoreCase("-1")) {
                if (history.getWb_money_rule() != null) {
                    if (history.getWb_money_rule().getLog_type().equals("Positive")) {
                        ((RewardViewHolder) holder).txt_point.setTextColor(context.getResources().getColor(R.color.green));
                        ((RewardViewHolder) holder).txt_point.setText(history.getPoints());
                    } else {
                        ((RewardViewHolder) holder).txt_point.setTextColor(context.getResources().getColor(R.color.purchase_medium_gray));
                        ((RewardViewHolder) holder).txt_point.setText(history.getPoints());
                    }
                }
                if (history.isHeader()) {
                    ((RewardViewHolder) holder).list_item_section_text.setVisibility(View.VISIBLE);
                    ((RewardViewHolder) holder).list_item_section_text.setText(DateUtils.changeDateFormat(StaticFunctions.SERVER_POST_FORMAT1, StaticFunctions.CLIENT_DISPLAY_FORMAT1, history.getCreated()));
                } else {
                    ((RewardViewHolder) holder).list_item_section_text.setVisibility(View.GONE);
                }
                ((RewardViewHolder) holder).txt_point_desc.setText(history.getDisplay_text_log());
                if (history.getExpiry_date() != null) {
                    ((RewardViewHolder) holder).txt_expiry_date.setText("Expires on " + DateUtils.changeDateFormat(StaticFunctions.SERVER_POST_FORMAT1, StaticFunctions.CLIENT_DISPLAY_FORMAT1, history.getExpiry_date()));
                }
                ((RewardViewHolder) holder).linear_root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (history.getDeep_link_log() != null) {
                            HashMap<String, String> hashMap = SplashScreen.getQueryString(Uri.parse(history.getDeep_link_log()));
                            hashMap.put("from", "WB Rewards Page");
                            new DeepLinkFunction(hashMap, context);
                        }
                    }
                });
            } else {
                // Set Empty Layout
                ((RewardViewHolder) holder).linear_root.setVisibility(View.GONE);
                ((RewardViewHolder) holder).txt_empty_wb_reward.setVisibility(View.VISIBLE);
                ((RewardViewHolder) holder).txt_empty_wb_reward.setText(history.getDisplay_text_log());

            }

        } else if (holder instanceof RewardHeaderViewHolder) {
            bindHeaderView(holder);
        }

    }


    public void bindHeaderView(RecyclerView.ViewHolder holder) {

        if (responseWBMoneyDashboard.getTotal_available() != null) {
            ((RewardHeaderViewHolder) holder).txt_total_earn.setText("\u20B9 " + String.valueOf(responseWBMoneyDashboard.getTotal_available()));
        }

        if (responseWBMoneyDashboard.getTotal_received() != null) {
            ((RewardHeaderViewHolder) holder).txt_received_money.setText(String.valueOf(responseWBMoneyDashboard.getTotal_received()));
        }

        if (responseWBMoneyDashboard.getTotal_redeemed() != null) {
            ((RewardHeaderViewHolder) holder).txt_due_money.setText(String.valueOf(responseWBMoneyDashboard.getTotal_redeemed()));
        }

        ((RewardHeaderViewHolder) holder).txt_tc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle reseller_bundle = new Bundle();
                reseller_bundle.putString("type", "reward");
                RewardTcBottomSheetFragment rewardTcBottomSheetFragment = new RewardTcBottomSheetFragment();
                rewardTcBottomSheetFragment.setArguments(reseller_bundle);
                Application_Singleton.CONTAINER_TITLE = "Reward program T&C";
                Application_Singleton.CONTAINERFRAG = rewardTcBottomSheetFragment;
                context.startActivity(new Intent(context, OpenContainer.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        if(historyArrayList!=null)
            return historyArrayList.size()+1;
        else
           return  1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEADER;
        } else {
            return ITEMS;
        }
    }


    public interface ItemClickListener {
        void onClick(View view, int position, boolean isLongClick);
    }


    public class RewardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        @BindView(R.id.txt_point)
        TextView txt_point;

        @BindView(R.id.txt_point_desc)
        TextView txt_point_desc;

        @BindView(R.id.linear_root)
        LinearLayout linear_root;

        @BindView(R.id.txt_expiry_date)
        TextView txt_expiry_date;

        @BindView(R.id.list_item_section_text)
        TextView list_item_section_text;

        @BindView(R.id.txt_empty_wb_reward)
        TextView txt_empty_wb_reward;

        private WBMoneyAdapter.ItemClickListener clickListener;

        public RewardViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setClickListener(WBMoneyAdapter.ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getPosition(), false);
        }
    }

    public class RewardHeaderViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.txt_payout_notified)
        TextView txt_payout_notified;

        @BindView(R.id.txt_received_money)
        TextView txt_received_money;

        @BindView(R.id.txt_due_money)
        TextView txt_due_money;

        @BindView(R.id.txt_total_earn)
        TextView txt_total_earn;

        @BindView(R.id.txt_tc)
        TextView txt_tc;


        public RewardHeaderViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

    }
}
