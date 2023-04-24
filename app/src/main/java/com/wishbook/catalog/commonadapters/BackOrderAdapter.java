package com.wishbook.catalog.commonadapters;

import android.content.Context;
import android.graphics.Color;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wishbook.catalog.R;
import com.wishbook.catalog.commonmodels.responses.Response_sellingorder;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by root on 6/2/17.
 */
public class BackOrderAdapter extends RecyclerView.Adapter<BackOrderAdapter.CustomViewHolder> {
    private ArrayList<Response_sellingorder> feedItemList;
    private Context mContext;

    public BackOrderAdapter(Context context, ArrayList<Response_sellingorder> feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;
    }
    public void setData(ArrayList<Response_sellingorder> filteredModelList) {
        this.feedItemList=filteredModelList;
        notifyDataSetChanged();
    }
    public class CustomViewHolder extends RecyclerView.ViewHolder {


        private final TextView order_num;
        private final TextView totalrate;
        private final TextView companyname;
        private final TextView processstatus;
        private final TextView customerstatus;
        private final TextView date;
        private final TextView totalproducts;
        private final LinearLayout checkbox_container;
        private final LinearLayout order_container;
        private final LinearLayout container;
        private final CheckBox selected;

        public CustomViewHolder(View view) {
            super(view);
            order_num = (TextView) view.findViewById(R.id.order_num);
            totalrate = (TextView) view.findViewById(R.id.totalrate);
            companyname = (TextView) view.findViewById(R.id.companyname);
            processstatus = (TextView) view.findViewById(R.id.processstatus);
            date = (TextView) view.findViewById(R.id.date);
            totalproducts = (TextView) view.findViewById(R.id.totalproducts);
            container = (LinearLayout) view.findViewById(R.id.container);
            checkbox_container = (LinearLayout) view.findViewById(R.id.checkbox_container);
            order_container = (LinearLayout) view.findViewById(R.id.order_container);
            customerstatus = (TextView) view.findViewById(R.id.customerstatus);
            selected = (CheckBox) view.findViewById(R.id.selection);
        }
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.backorderitem, viewGroup, false);

        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder customViewHolder,  int i) {
        if(feedItemList.get(i).getCompany_name()!=null) {
            customViewHolder.companyname.setText("For : "+ StringUtils.capitalize(feedItemList.get(i).getCompany_name().toLowerCase().trim()));
        }


        customViewHolder.order_container.setBackgroundColor(Color.parseColor("#ffffff"));
        customViewHolder.checkbox_container.setBackgroundColor(Color.parseColor("#ffffff"));

        customViewHolder.date.setText("Received On "+getformatedDate(StringUtils.capitalize(feedItemList.get(i).getDate().toLowerCase().trim())));
        customViewHolder.order_num.setText("#"+StringUtils.capitalize(feedItemList.get(i).getOrder_number().toLowerCase().trim()));
        customViewHolder.totalproducts.setText(StringUtils.capitalize(feedItemList.get(i).getTotal_products() .toLowerCase().trim())+ " Pcs");
        customViewHolder.totalrate.setText("\u20B9" + feedItemList.get(i).getTotal_rate()+ " For ");
        customViewHolder.processstatus.setText(StringUtils.capitalize(StringUtils.capitalize(feedItemList.get(i).getProcessing_status().toLowerCase().trim())));
        customViewHolder.customerstatus.setText(StringUtils.capitalize(feedItemList.get(i).getCustomer_status().toLowerCase().trim()));
        customViewHolder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeScaleUpAnimation(customViewHolder.container, 0,0,customViewHolder.container.getWidth(),customViewHolder.container.getHeight());

                if(customViewHolder.selected.isChecked())
                {
                    customViewHolder.selected.setChecked(false);
                    customViewHolder.order_container.setBackgroundColor(Color.parseColor("#ffffff"));
                    customViewHolder.checkbox_container.setBackgroundColor(Color.parseColor("#ffffff"));
                    feedItemList.get(customViewHolder.getAdapterPosition()).setSelected(false);

                }
                else
                {
                    feedItemList.get(customViewHolder.getAdapterPosition()).setSelected(true);
                    customViewHolder.order_container.setBackgroundColor(Color.parseColor("#E0E0E0"));
                    customViewHolder.checkbox_container.setBackgroundColor(Color.parseColor("#E0E0E0"));
                    customViewHolder.selected.setChecked(true);
                }


            }
        });

        customViewHolder.selected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    feedItemList.get(customViewHolder.getAdapterPosition()).setSelected(true);
                    customViewHolder.order_container.setBackgroundColor(Color.parseColor("#E0E0E0"));
                    customViewHolder.checkbox_container.setBackgroundColor(Color.parseColor("#E0E0E0"));
                }else
                {
                    feedItemList.get(customViewHolder.getAdapterPosition()).setSelected(false);
                    customViewHolder.order_container.setBackgroundColor(Color.parseColor("#ffffff"));
                    customViewHolder.checkbox_container.setBackgroundColor(Color.parseColor("#ffffff"));

                }
            }
        });

        if(feedItemList.get(i).getSelected()){
            customViewHolder.order_container.setBackgroundColor(Color.parseColor("#E0E0E0"));
            customViewHolder.checkbox_container.setBackgroundColor(Color.parseColor("#E0E0E0"));
            customViewHolder.selected.setChecked(true);
        }else{
            customViewHolder.order_container.setBackgroundColor(Color.parseColor("#ffffff"));
            customViewHolder.checkbox_container.setBackgroundColor(Color.parseColor("#ffffff"));
            customViewHolder.selected.setChecked(false);
        }

    }

    private String getformatedDate(String dat) {
        String format = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        String toformat = "MMMM dd,yyyy";
        SimpleDateFormat tosdf = new SimpleDateFormat(toformat, Locale.US);

        try {
            Date date = sdf.parse(dat);
            return tosdf.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public ArrayList<String> getAllSelected(){
        ArrayList<String> orders = new ArrayList<>();
        for(int i=0;i<feedItemList.size();i++)
        {
            if(feedItemList.get(i).getSelected()){
                orders.add(feedItemList.get(i).getId());
            }
        }
        return  orders;
    }

    @Override
    public int getItemCount() {
        return feedItemList.size();
    }
}