package com.wishbook.catalog.home.more.brandDiscount.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wishbook.catalog.R;
import com.wishbook.catalog.commonmodels.responses.Brand;

import java.util.ArrayList;
import java.util.Locale;

public class DiscountBrandChildListAdapter extends RecyclerView.Adapter<DiscountBrandChildListAdapter.CustomViewHolder>{


    private Context context;
    private ArrayList<Brand> arraylist;
    private ArrayList<Brand> list;
    private CallParentListener callParentListener;
    private int Parentposition;

    public DiscountBrandChildListAdapter(Context context, ArrayList<Brand> list, ArrayList<Brand> list1,int Parentposition) {
        this.context = context;
        this.list = list;
        this.Parentposition = Parentposition;
        this.arraylist = new ArrayList<>();
        arraylist.addAll(list1);
    }

    @Override
    public DiscountBrandChildListAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.discount_child_item, parent, false);
        return new DiscountBrandChildListAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiscountBrandChildListAdapter.CustomViewHolder holder, final int position) {
        holder.checkbox.setOnCheckedChangeListener(null);
        if (list.get(position).isChecked()) {
            holder.checkbox.setChecked(true);
        } else {
            holder.checkbox.setChecked(false);
        }


        holder.checkbox.setText(list.get(position).getName());

        if(list.get(position).isDisable()){

            holder.checkbox.setEnabled(false);
            holder.checkbox.setClickable(true);
            holder.txt_applied_discount.setVisibility(View.VISIBLE);
            StringBuffer discount_rule = new StringBuffer();
            if(list.get(position).getDiscount_rules()!=null) {
                if(list.get(position).getDiscount_rules().getCash_discount()!=null)
                    discount_rule.append("Full catalog discount: "+list.get(position).getDiscount_rules().getCash_discount()+"%");
                if(list.get(position).getDiscount_rules().getSingle_pcs_discount() > 0)
                    discount_rule.append(",Single pc. discount: "+list.get(position).getDiscount_rules().getSingle_pcs_discount()+"%");

                holder.txt_applied_discount.setText(discount_rule);
            }

            holder.linear_child_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context,"You have already selected this in one of your discount rules.",Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        list.get(position).setChecked(true);
                    } else {
                        list.get(position).setChecked(false);
                        if(callParentListener!=null){
                            callParentListener.changeChecked(Parentposition);
                        }
                    }

                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface CallParentListener{
        void changeChecked(int position);
    }

    public void setcallParentListener(CallParentListener callParentListener){
        this.callParentListener = callParentListener;
    }


    // Filter Class
    public ArrayList<Brand> filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        list.clear();
        if (charText.length() == 0) {
            list.addAll(arraylist);
        } else {
            for (Brand wp : arraylist) {
                if (wp.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    list.add(wp);
                }
            }
        }

        return list;
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder {
        private CheckBox checkbox;
        private TextView txt_applied_discount;
        private LinearLayout linear_child_container;
        public CustomViewHolder(View view) {
            super(view);
            checkbox = (CheckBox) itemView.findViewById(R.id.checkbox);
            txt_applied_discount = itemView.findViewById(R.id.txt_applied_discount);
            linear_child_container = itemView.findViewById(R.id.linear_child_container);
        }
    }
}
