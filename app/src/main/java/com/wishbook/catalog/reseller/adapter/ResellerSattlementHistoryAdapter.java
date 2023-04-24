package com.wishbook.catalog.reseller.adapter;

import android.content.Context;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wishbook.catalog.R;
import com.wishbook.catalog.SplashScreen;
import com.wishbook.catalog.Utils.DeepLinkFunction;
import com.wishbook.catalog.commonadapters.WBMoneyAdapter;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.responses.ResponseResellerSattelmentHistory;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ResellerSattlementHistoryAdapter extends RecyclerView.Adapter<ResellerSattlementHistoryAdapter.CustomViewHolder> {

    private Context context;
    private ArrayList<ResponseResellerSattelmentHistory> historyArrayList;
    UserInfo userInfo;

    public int ITEMS = 1;


    public ResellerSattlementHistoryAdapter(Context context, ArrayList<ResponseResellerSattelmentHistory> historyArrayList) {
        this.context = context;
        this.historyArrayList = historyArrayList;
        userInfo = UserInfo.getInstance(context);
    }


    @Override
    public ResellerSattlementHistoryAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wb_money_item2, parent, false);
        return new ResellerSattlementHistoryAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResellerSattlementHistoryAdapter.CustomViewHolder holder, int position) {
        final ResponseResellerSattelmentHistory history = historyArrayList.get(position);
        if (history.getAmount() != null) {
            holder.txt_point.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
            holder.txt_point.setTextColor(context.getResources().getColor(R.color.green));
            holder.txt_point.setText("+" + history.getAmount());
        }

        holder.txt_point_desc.setText(history.getDisplay_text_log());
        holder.linear_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (history.getDeep_link_log() != null) {
                    HashMap<String, String> hashMap = SplashScreen.getQueryString(Uri.parse(history.getDeep_link_log()));
                    hashMap.put("from", "Reseller Hub Page");
                    new DeepLinkFunction(hashMap, context);
                }
            }
        });


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


    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        @BindView(R.id.txt_point)
        TextView txt_point;

        @BindView(R.id.txt_point_desc)
        TextView txt_point_desc;

        @BindView(R.id.linear_root)
        LinearLayout linear_root;

        private WBMoneyAdapter.ItemClickListener clickListener;

        public CustomViewHolder(View view) {
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
