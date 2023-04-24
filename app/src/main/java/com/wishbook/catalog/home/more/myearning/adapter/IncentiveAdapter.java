package com.wishbook.catalog.home.more.myearning.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wishbook.catalog.R;
import com.wishbook.catalog.Utils.DateUtils;
import com.wishbook.catalog.Utils.StaticFunctions;
import com.wishbook.catalog.commonadapters.WBMoneyAdapter;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.responses.IncentiveHistory;

import java.text.DecimalFormat;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class IncentiveAdapter extends RecyclerView.Adapter<IncentiveAdapter.IncentiveViewHolder> {

    private Context context;
    private ArrayList<IncentiveHistory> historyArrayList;
    UserInfo userInfo;

    public int HEADER = 0;
    public int ITEMS = 1;


    public IncentiveAdapter(Context context, ArrayList<IncentiveHistory> historyArrayList) {
        this.context = context;
        this.historyArrayList = historyArrayList;
        userInfo = UserInfo.getInstance(context);
    }


    @Override
    public IncentiveViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.incentive_item, parent, false);
        return new IncentiveAdapter.IncentiveViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IncentiveViewHolder holder, int position) {
        IncentiveHistory history = historyArrayList.get(position);
        String from_date = DateUtils.changeDateFormat(StaticFunctions.SERVER_POST_FORMAT, StaticFunctions.CLIENT_DISPLAY_FORMAT1, history.getFrom_date());
        String to_date = DateUtils.changeDateFormat(StaticFunctions.SERVER_POST_FORMAT, StaticFunctions.CLIENT_DISPLAY_FORMAT1, history.getTo_date());
        holder.txt_date.setText(from_date + " - " + to_date);
        holder.txt_target_value.setText("\u20B9 " + history.getPurchase_target_amount());
        holder.txt_achieved_value.setText("\u20B9 " + history.getPurchase_actual_amount());
        holder.txt_incentive_earned_value.setText("\u20B9 " + history.getIncentive_amount());

        try {
            if(history.getPurchase_actual_amount() > 0 && history.getIncentive_amount() > 0) {
                double per = (history.getIncentive_amount()* (100/ history.getPurchase_actual_amount()));
                DecimalFormat decimalFormat = new DecimalFormat("#.##");
                //holder.txt_incentive_earned_label.setText("Incentive Earned("+ decimalFormat.format(per) +"% of Sales Achieved)");
                holder.txt_incentive_earned_label.setText("Incentive Earned("+ decimalFormat.format(Math.round(per)) +"% of Sales Achieved)");
            } else {
                holder.txt_incentive_earned_label.setText("Incentive Earned");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @Override
    public int getItemCount() {
        return historyArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return ITEMS;
    }


    public interface ItemClickListener {
        void onClick(View view, int position, boolean isLongClick);
    }


    public class IncentiveViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        @BindView(R.id.txt_date)
        TextView txt_date;

        @BindView(R.id.realative_target)
        RelativeLayout realative_target;

        @BindView(R.id.txt_target_value)
        TextView txt_target_value;

        @BindView(R.id.realative_achieved)
        RelativeLayout realative_achieved;

        @BindView(R.id.txt_achieved_value)
        TextView txt_achieved_value;

        @BindView(R.id.realative_incentived_earned)
        RelativeLayout realative_incentived_earned;

        @BindView(R.id.txt_incentive_earned_value)
        TextView txt_incentive_earned_value;

        @BindView(R.id.txt_incentive_earned_label)
        TextView txt_incentive_earned_label;

        private WBMoneyAdapter.ItemClickListener clickListener;

        public IncentiveViewHolder(View view) {
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
}

