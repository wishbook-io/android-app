package com.wishbook.catalog.commonadapters;

import android.content.Context;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wishbook.catalog.R;
import com.wishbook.catalog.SplashScreen;
import com.wishbook.catalog.Utils.DeepLinkFunction;
import com.wishbook.catalog.commonmodels.UserInfo;
import com.wishbook.catalog.commonmodels.responses.ResponseWBHistory;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WBMoneyAdapter extends RecyclerView.Adapter<WBMoneyAdapter.CustomViewHolder> {

    private Context context;
    private ArrayList<ResponseWBHistory> historyArrayList;
    UserInfo userInfo;

    public int HEADER = 0;
    public int ITEMS = 1;


    public WBMoneyAdapter(Context context, ArrayList<ResponseWBHistory> historyArrayList) {
        this.context = context;
        this.historyArrayList = historyArrayList;
        userInfo = UserInfo.getInstance(context);
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wb_money_item2, parent, false);
        return new WBMoneyAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        final ResponseWBHistory history = historyArrayList.get(position);
        if(history.getWb_money_rule()!=null){
            if(history.getWb_money_rule().getLog_type().equals("Positive")){
                holder.txt_point.setTextColor(context.getResources().getColor(R.color.green));
                holder.txt_point.setText("+" + history.getPoints());
            } else {
                holder.txt_point.setTextColor(context.getResources().getColor(R.color.purchase_medium_gray));
                holder.txt_point.setText(history.getPoints());
            }
        }

        holder.txt_point_desc.setText(history.getDisplay_text_log());
        holder.linear_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(history.getDeep_link_log()!=null){
                    HashMap<String, String> hashMap = SplashScreen.getQueryString(Uri.parse(history.getDeep_link_log()));
                    hashMap.put("from","WB Money Page");
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

        private ItemClickListener clickListener;

        public CustomViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

        }

        public void setClickListener(ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getPosition(), false);
        }
    }
}
